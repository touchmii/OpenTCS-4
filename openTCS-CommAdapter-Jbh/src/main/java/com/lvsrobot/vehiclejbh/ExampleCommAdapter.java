package com.lvsrobot.vehiclejbh;

import com.google.common.util.concurrent.Uninterruptibles;
import com.google.inject.assistedinject.Assisted;

import java.util.Iterator;
import java.util.List;

import static java.util.Objects.requireNonNull;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import javax.inject.Inject;

import org.opentcs.customizations.kernel.KernelExecutor;
import org.opentcs.data.model.Path;
import org.opentcs.data.model.Point;
import org.opentcs.data.model.Triple;
import org.opentcs.data.model.Vehicle;
import org.opentcs.data.model.Vehicle.Orientation;
import org.opentcs.data.order.DriveOrder;
import org.opentcs.data.order.Route;
import org.opentcs.data.order.Route.Step;
import org.opentcs.drivers.vehicle.BasicVehicleCommAdapter;
import org.opentcs.drivers.vehicle.MovementCommand;
import org.opentcs.drivers.vehicle.management.VehicleProcessModelTO;
import org.opentcs.drivers.vehicle.messages.SetSpeedMultiplier;
import org.opentcs.util.CyclicTask;
import org.opentcs.util.ExplainedBoolean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An example implementation for a communication adapter.
 *
 * @author Mats Wilhelm (Fraunhofer IML)
 */
public class ExampleCommAdapter extends BasicVehicleCommAdapter {
    /**
     * The name of the load handling device set by this adapter.
     */
    public static final String LHD_NAME = "default";
    /**
     * This class's Logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger(ExampleCommAdapter.class);
    /**
     * An error code indicating that there's a conflict between a load operation and the vehicle's
     * current load state.
     */
    private static final String LOAD_OPERATION_CONFLICT = "cannotLoadWhenLoaded";
    /**
     * An error code indicating that there's a conflict between an unload operation and the vehicle's
     * current load state.
     */
    private static final String UNLOAD_OPERATION_CONFLICT = "cannotUnloadWhenNotLoaded";
    /**
     * The time by which to advance the velocity controller per step (in ms).
     */
    private static final int ADVANCE_TIME = 100;
    /**
     * The adapter components factory.
     */
    private final ExampleAdapterComponentsFactory componentsFactory;
    /**
     * The kernel's executor.
     */
    private final ExecutorService kernelExecutor;
    /**
     * The task simulating the virtual vehicle's behaviour.
     */
    private CyclicTask vehicleSimulationTask;
    /**
     * The boolean flag to check if execution of the next command is allowed.
     */
    private boolean singleStepExecutionAllowed;
    /**
     * The vehicle to this comm adapter instance.
     */
    private final Vehicle vehicle;
    /**
     * The vehicle's load state.
     */
    private LoadState loadState = LoadState.EMPTY;
    /**
     * Whether the loopback adapter is initialized or not.
     */
    private boolean initialized;


    private AgvTelegram agv;

    private ConfigRoute configRoute = new ConfigRoute();

//    private Route = route;

    /**
     * Creates a new instance.
     *
     * @param vehicle           The attached vehicle.
     * @param componentsFactory The components factory.
     */
    @Inject
    public ExampleCommAdapter(@Assisted Vehicle vehicle, ExampleAdapterComponentsFactory componentsFactory, @KernelExecutor ExecutorService kernelExecutor) {
        super(new ExampleProcessModel(vehicle), 30, 30, "Charge");
        this.componentsFactory = requireNonNull(componentsFactory, "componentsFactory");
        this.vehicle = requireNonNull(vehicle, "vehicle");
        this.kernelExecutor = requireNonNull(kernelExecutor, "kernelExecutor");
    }


    @Override
    public void initialize() {
        super.initialize();
    }

    @Override
    public void terminate() {
        super.terminate();
    }


    @Override
    public synchronized void enable() {
        if (isEnabled()) {
            return;
        }
        agv = new AgvTelegram(getProcessModel().getIp(), getProcessModel().getPort());

        getProcessModel().getVelocityController().addVelocityListener(getProcessModel());
        getProcessModel().setVehiclePosition("010");
//                if (currentStatus == 0) {
        getProcessModel().setVehicleState(Vehicle.State.IDLE);
        getProcessModel().setVehicleOrientationAngle(90);
        // Create task for vehicle simulation.
        vehicleSimulationTask = new VehicleSimulationTask();
        Thread simThread = new Thread(vehicleSimulationTask, getName() + "-simulationTask");
        simThread.start();
        super.enable();
    }

    @Override
    public synchronized void disable() {
        if (!isEnabled()) {
            return;
        }
        agv = null;
        vehicleSimulationTask.terminate();
        vehicleSimulationTask = null;
        getProcessModel().getVelocityController().removeVelocityListener(getProcessModel());
        super.disable();
    }

    @Override
    public final ExampleProcessModel getProcessModel() {
        return (ExampleProcessModel) super.getProcessModel();
    }


    @Override
    public synchronized void sendCommand(MovementCommand cmd) {
        requireNonNull(cmd, "cmd");

        // Reset the execution flag for single-step mode.
        singleStepExecutionAllowed = false;
        // Don't do anything else - the command will be put into the sentQueue
        // automatically, where it will be picked up by the simulation task.
    }

    @Override
    public void processMessage(Object message) {
        // Process LimitSpeeed message which might pause the vehicle.
        if (message instanceof SetSpeedMultiplier) {
            SetSpeedMultiplier lsMessage = (SetSpeedMultiplier) message;
            int multiplier = lsMessage.getMultiplier();
            getProcessModel().setVehiclePaused(multiplier == 0);
        }
    }

    @Override
    public synchronized ExplainedBoolean canProcess(List<String> operations) {
        requireNonNull(operations, "operations");

        LOG.debug("{}: Checking processability of {}...", getName(), operations);
        boolean canProcess = true;
        String reason = "";

        // Do NOT require the vehicle to be IDLE or CHARGING here!
        // That would mean a vehicle moving to a parking position or recharging location would always
        // have to finish that order first, which would render a transport order's dispensable flag
        // useless.
        boolean loaded = loadState == LoadState.FULL;
        Iterator<String> opIter = operations.iterator();
        while (canProcess && opIter.hasNext()) {
            final String nextOp = opIter.next();
            // If we're loaded, we cannot load another piece, but could unload.
            if (loaded) {
                if (nextOp.startsWith(getProcessModel().getLoadOperation())) {
                    canProcess = false;
                    reason = LOAD_OPERATION_CONFLICT;
                } else if (nextOp.startsWith(getProcessModel().getUnloadOperation())) {
                    loaded = false;
                }
            } // If we're not loaded, we could load, but not unload.
            else if (nextOp.startsWith(getProcessModel().getLoadOperation())) {
                loaded = true;
            } else if (nextOp.startsWith(getProcessModel().getUnloadOperation())) {
                canProcess = false;
                reason = UNLOAD_OPERATION_CONFLICT;
            }
        }
        if (!canProcess) {
            LOG.debug("{}: Cannot process {}, reason: '{}'", getName(), operations, reason);
        }
        return new ExplainedBoolean(canProcess, reason);
    }

    @Override
    protected synchronized boolean canSendNextCommand() {
        return super.canSendNextCommand()
                && (!getProcessModel().isSingleStepModeEnabled() || singleStepExecutionAllowed);
    }

    @Override
    protected synchronized void connectVehicle() {
    }

    @Override
    protected synchronized void disconnectVehicle() {
    }

    @Override
    protected synchronized boolean isVehicleConnected() {
        return true;
    }

    public Point approachPosition(DriveOrder driveOrder, Triple point_1, int distenceX, int distenceY) {
        List<Step> listPoint = driveOrder.getRoute().getSteps().stream().filter(point -> Math.abs(point.getDestinationPoint().getPosition().getX() - point_1.getX()) < distenceX && Math.abs(point.getDestinationPoint().getPosition().getY() - point_1.getY()) < distenceY).collect(Collectors.toList());
        if (listPoint.size() < 1) {return null;}
        return listPoint.get(0).getDestinationPoint();
    }

    @Override
    protected VehicleProcessModelTO createCustomTransferableProcessModel() {
        return new ExampleProcessModelTO()
                .setLoadOperation(getProcessModel().getLoadOperation())
                .setMaxAcceleration(getProcessModel().getMaxAcceleration())
                .setMaxDeceleration(getProcessModel().getMaxDecceleration())
                .setMaxFwdVelocity(getProcessModel().getMaxFwdVelocity())
                .setMaxRevVelocity(getProcessModel().getMaxRevVelocity())
                .setOperatingTime(getProcessModel().getOperatingTime())
                .setSingleStepModeEnabled(getProcessModel().isSingleStepModeEnabled())
                .setUnloadOperation(getProcessModel().getUnloadOperation())
                .setVehiclePaused(getProcessModel().isVehiclePaused());
    }


    /**
     * Triggers a step in single step mode.
     */
    public synchronized void trigger() {
        singleStepExecutionAllowed = true;
    }

    private class VehicleSimulationTask extends CyclicTask {
        private int simAdvanceTime;

        private VehicleSimulationTask() {
            super(200);
        }

        @Override
        protected void runActualTask() {
            try {
                //获取状态  位置  速度  方向等
                AgvInfo agvInfo = agv.getAgvInfo();
                if (agvInfo == null) {
                    Thread.sleep(200);
                    return;
                }
                String currentPoint = String.valueOf(agvInfo.getPosition());
                int currentStatus = agvInfo.getStatus();
                if (currentStatus == 0) {
                    getProcessModel().setVehicleState(Vehicle.State.IDLE);
                } else if (currentStatus == 1) {
                    getProcessModel().setVehicleState(Vehicle.State.EXECUTING);
                }



//                final MovementCommand curCommand;
//                synchronized (ExampleCommAdapter.this) {
//                    curCommand = getSentQueue().peek();
//
//                }

                if(currentDriveOrder != null && !getSentQueue().isEmpty()) {
                    configRoute.setRoute(currentDriveOrder);
                    configRoute.setAngle(90);
                    int[] path = configRoute.getPath();
                    agv.sendPath(path);
                    //使用agvinfo 发送path
                    LOG.info("send path to vehicle : {}", path);
                    simulateMove(currentDriveOrder.getRoute());
                }

//                simAdvanceTime = (int) (ADVANCE_TIME * 1.0);
//                if (curCommand == null) {
//                    Uninterruptibles.sleepUninterruptibly(ADVANCE_TIME, TimeUnit.MILLISECONDS);
//                    getProcessModel().getVelocityController().advanceTime(simAdvanceTime);
//                } else {
//                    // If we were told to move somewhere, simulate the journey.
//                    LOG.debug("Processing MovementCommand...");
//                    final Route.Step curStep = curCommand.getStep();
//                    // Simulate the movement.
//                    simulateMovement(curStep);
//                    // Simulate processing of an operation.
//                    if (!curCommand.isWithoutOperation()) {
//                        simulateOperation(curCommand.getOperation());
//                    }
//                    LOG.debug("Processed MovementCommand.");
//                    if (!isTerminated()) {
//                        // Set the vehicle's state back to IDLE, but only if there aren't
//                        // any more movements to be processed.
//                        if (getSentQueue().size() <= 1 && getCommandQueue().isEmpty()) {
//                            getProcessModel().setVehicleState(Vehicle.State.IDLE);
//                        }
//                        // Update GUI.
//                        synchronized (ExampleCommAdapter.this) {
//                            MovementCommand sentCmd = getSentQueue().poll();
//                            // If the command queue was cleared in the meantime, the kernel
//                            // might be surprised to hear we executed a command we shouldn't
//                            // have, so we only peek() at the beginning of this method and
//                            // poll() here. If sentCmd is null, the queue was probably cleared
//                            // and we shouldn't report anything back.
//                            if (sentCmd != null && sentCmd.equals(curCommand)) {
//                                // Let the vehicle manager know we've finished this command.
//                                getProcessModel().commandExecuted(curCommand);
//                                ExampleCommAdapter.this.notify();
//                            }
//                        }
//                    }
//                }
            } catch (Exception ex) {
                LOG.error(ex.getMessage());
            }
        }

        private void simulateMove(Route _route) {
            getProcessModel().setVehicleState(Vehicle.State.EXECUTING);
            for(int i=0; i < _route.getSteps().size(); i++) {
                final MovementCommand curCommandd;
                synchronized (ExampleCommAdapter.this) {
                    curCommandd = getSentQueue().poll();

                }
                int path_leng = (int)(_route.getSteps().get(i).getPath().getLength())/20;
                org.opentcs.data.model.Triple point1 = _route.getSteps().get(i).getSourcePoint().getPosition();
                org.opentcs.data.model.Triple point2 = _route.getSteps().get(i).getDestinationPoint().getPosition();
                long A1 = point2.getX()-point1.getX();
                long A2 = point2.getY()-point1.getY();
                for (int n=0; n<path_leng; n++){
                    org.opentcs.data.model.Triple current_point = _route.getSteps().get(i).getSourcePoint().getPosition();
                    org.opentcs.data.model.Triple new_point = new Triple(current_point.getX(), current_point.getY(), current_point.getZ());
                    //往右
                    if(A1 > 500 && Math.abs(A2) < 200) {
                        new_point.setX(current_point.getX() + 20*n);
                     //往左
                    } else if (A1 < -500 && Math.abs(A2) < 200) {
                        new_point.setX(current_point.getX() - 20*n);
                    //往上
                    } else if (Math.abs(A1) < 200 && A2 > 500) {
                        new_point.setY(current_point.getY() + 20*n);
                    //往下
                    } else if (Math.abs(A1) < 200 && A2 < -500) {
                        new_point.setY(current_point.getY() - 20*n);
                    } else {

                    }
//                    new_point.setY(current_point.getY()+100);
                    getProcessModel().setVehiclePrecisePosition(new_point);
//                    Thread.sleep(200);
                    try {
                        Thread.sleep(20);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                    }
                }
                getProcessModel().setVehiclePosition(_route.getSteps().get(i).getDestinationPoint().getName());
                getProcessModel().commandExecuted(curCommandd);
            }
            getProcessModel().setVehicleState(Vehicle.State.IDLE);
        }
        private void simulateMovement(Step step) throws Exception {
            if (step.getPath() == null) {
                return;
            }
            Orientation orientation = step.getVehicleOrientation();
            long pathLength = step.getPath().getLength();
            int maxVelocity;
            switch (orientation) {
                case BACKWARD:
                    maxVelocity = step.getPath().getMaxReverseVelocity();
                    break;
                default:
                    maxVelocity = step.getPath().getMaxVelocity();
                    break;
            }
            String pointName = step.getDestinationPoint().getName();

            getProcessModel().setVehicleState(Vehicle.State.EXECUTING);
            String currentPoint = "";
            int currentStatus = 0;

//            agv.sendPath(Integer.parseInt(pointName));
            while (!currentPoint.equals(pointName) && !isTerminated()) {
                AgvInfo agvInfo = agv.getAgvInfo();
                if (agvInfo == null) {
                    Thread.sleep(200);
                    continue;
                }
                currentPoint = String.valueOf(agvInfo.getPosition());
                currentStatus = agvInfo.getStatus();
                getProcessModel().setVehiclePosition(currentPoint);
                if (currentStatus == 0) {
                    getProcessModel().setVehicleState(Vehicle.State.IDLE);
                } else if (currentStatus == 1) {
                    getProcessModel().setVehicleState(Vehicle.State.EXECUTING);
                }
            }
        }

        /**
         * Simulates an operation.
         *
         * @param operation A operation
         * @throws InterruptedException If an exception occured while simulating
         */
        private void simulateOperation(String operation) {
            requireNonNull(operation, "operation");
            if (isTerminated()) {
                return;
            }
            agv.sendWork(operation);
        }
    }


    private enum LoadState {
        EMPTY,
        FULL;
    }
}
