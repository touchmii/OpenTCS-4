package com.lvsrobot.vehicle;

import com.google.common.util.concurrent.Uninterruptibles;
import com.google.inject.assistedinject.Assisted;

import java.util.Iterator;
import java.util.List;

import static java.util.Objects.requireNonNull;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import javax.inject.Inject;

import org.opentcs.customizations.kernel.KernelExecutor;
import org.opentcs.data.model.Vehicle;
import org.opentcs.data.model.Vehicle.Orientation;
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

    /**
     * Creates a new instance.
     *
     * @param vehicle           The attached vehicle.
     * @param componentsFactory The components factory.
     * 父类BasicVehicleCommAdapter引用了ExampleProcessModel的父类
     * ProcessModel，此处将自己实现的类关联在以前，可以让内核调用到。
     */
    @Inject
    public ExampleCommAdapter(@Assisted Vehicle vehicle, ExampleAdapterComponentsFactory componentsFactory, @KernelExecutor ExecutorService kernelExecutor) {
        //父类BasicVehicleCommAdapter实例需要的参数
        super(new ExampleProcessModel(vehicle), 3, 2, "Charge");
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
        this.agv.Connecte();
    }

    @Override
    protected synchronized void disconnectVehicle() {
        this.agv.disConnecte();
    }

    @Override
    protected synchronized boolean isVehicleConnected() {
        return this.agv.isConnected();
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
            super(500);
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
                String currentPoint = "Point-00"+String.valueOf(agvInfo.getPosition());
                int currentStatus = agvInfo.getStatus();

                getProcessModel().setVehiclePosition(currentPoint);
                if (currentStatus == 0) {
                    getProcessModel().setVehicleState(Vehicle.State.IDLE);
                } else if (currentStatus == 1) {
                    getProcessModel().setVehicleState(Vehicle.State.EXECUTING);
                }


                final MovementCommand curCommand;
                synchronized (ExampleCommAdapter.this) {
                    curCommand = getSentQueue().peek();
                }
                simAdvanceTime = (int) (ADVANCE_TIME * 1.0);
                if (curCommand == null) {
                    Uninterruptibles.sleepUninterruptibly(ADVANCE_TIME, TimeUnit.MILLISECONDS);
                    getProcessModel().getVelocityController().advanceTime(simAdvanceTime);
                } else {
                    // If we were told to move somewhere, simulate the journey.
                    LOG.debug("Processing MovementCommand...");
                    final Route.Step curStep = curCommand.getStep();
                    // Simulate the movement.
//                    simulateMovement(curStep);

                    getProcessModel().commandExecuted(curCommand);
                    
                    // Simulate processing of an operation.
                    if (!curCommand.isWithoutOperation()) {
                        simulateOperation(curCommand.getOperation());
                    }
                    LOG.debug("Processed MovementCommand.");
                    if (!isTerminated()) {
                        // Set the vehicle's state back to IDLE, but only if there aren't
                        // any more movements to be processed.
                        if (getSentQueue().size() <= 1 && getCommandQueue().isEmpty()) {
                            getProcessModel().setVehicleState(Vehicle.State.IDLE);
                        }
                        // Update GUI.
                        synchronized (ExampleCommAdapter.this) {
                            MovementCommand sentCmd = getSentQueue().poll();
                            // If the command queue was cleared in the meantime, the kernel
                            // might be surprised to hear we executed a command we shouldn't
                            // have, so we only peek() at the beginning of this method and
                            // poll() here. If sentCmd is null, the queue was probably cleared
                            // and we shouldn't report anything back.
                            if (sentCmd != null && sentCmd.equals(curCommand)) {
                                // Let the vehicle manager know we've finished this command.
                                getProcessModel().commandExecuted(curCommand);
                                ExampleCommAdapter.this.notify();
                            }
                        }
                    }
                }
            } catch (Exception ex) {
                LOG.error(ex.getMessage());
            }
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
                currentPoint = "Point-00"+String.valueOf(agvInfo.getPosition());
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
