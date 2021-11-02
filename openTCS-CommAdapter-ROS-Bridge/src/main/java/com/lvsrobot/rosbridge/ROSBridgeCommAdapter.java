package com.lvsrobot.rosbridge;

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
import org.opentcs.data.model.Point;
import org.opentcs.data.model.Triple;
import org.opentcs.data.model.Vehicle;
import org.opentcs.data.model.Vehicle.Orientation;
import org.opentcs.data.notification.UserNotification;
import org.opentcs.data.order.DriveOrder;
import org.opentcs.data.order.Route.Step;
import org.opentcs.drivers.vehicle.AdapterCommand;
import org.opentcs.drivers.vehicle.BasicVehicleCommAdapter;
import org.opentcs.drivers.vehicle.MovementCommand;
import org.opentcs.drivers.vehicle.commands.PublishEventCommand;
import org.opentcs.drivers.vehicle.management.VehicleProcessModelTO;
import org.opentcs.drivers.vehicle.messages.SetSpeedMultiplier;
import org.opentcs.util.CyclicTask;
import org.opentcs.util.ExplainedBoolean;
//import org.opentcs.virtualvehicle.ConfigRoute;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.helpers.MessageFormatter;

/**
 * An example implementation for a communication adapter.
 *
 * @author Mats Wilhelm (Fraunhofer IML)
 */
public class ROSBridgeCommAdapter extends BasicVehicleCommAdapter {
    /**
     * The name of the load handling device set by this adapter.
     */
    public static final String LHD_NAME = "default";
    /**
     * This class's Logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger(ROSBridgeCommAdapter.class);
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
    private final ROSBridgeAdapterComponentsFactory componentsFactory;
    /**
     * The kernel's executor.
     */
    private final ExecutorService kernelExecutor;
    /**
     * The task simulating the virtual vehicle's behaviour.
     */
    private CyclicTask vehicleROSBridgeTask;
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


    private AgvTelegramROS agv;

    private ConfigRoute configRoute = new ConfigRoute();

    private Point currentPoint;

    private Point previousPoint;

//    private  sendDriverOrder;

    /**
     * Creates a new instance.
     *
     * @param vehicle           The attached vehicle.
     * @param componentsFactory The components factory.
     * 父类BasicVehicleCommAdapter引用了ExampleProcessModel的父类
     * ProcessModel，此处将自己实现的类关联在以前，可以让内核调用到。
     */
    @Inject
    public ROSBridgeCommAdapter(@Assisted Vehicle vehicle, ROSBridgeAdapterComponentsFactory componentsFactory, @KernelExecutor ExecutorService kernelExecutor) {
        //父类BasicVehicleCommAdapter实例需要的参数
        super(new ROSBridgeProcessModel(vehicle), 30, 20, "Charge");
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
        agv = new AgvTelegramROS(getProcessModel().getIp(), getProcessModel().getPort(), this.getProcessModel());



        getProcessModel().getVelocityController().addVelocityListener(getProcessModel());

//        getProcessModel().setVehiclePosition(getInitialPosition());

        // Create task for vehicle simulation.
        vehicleROSBridgeTask = new VehicleROSBridgeTask();
        Thread adapterThread = new Thread(vehicleROSBridgeTask, getName() + "-ROSBridgeTask");
        adapterThread.start();
        super.enable();
//        getProcessModel().setVehiclePosition("4");
        try {
            Thread.sleep(2000);
        } catch (Exception e) {}
        String init_point = getInitialPosition();
        getProcessModel().setVehiclePosition(init_point);
        getProcessModel().publishUserNotification(new UserNotification(MessageFormatter.format("adapter init finish, vehicle current point: {}", init_point).getMessage(), UserNotification.Level.INFORMATIONAL));
    }

    @Override
    public synchronized void disable() {
        if (!isEnabled()) {
            return;
        }
        agv.disConnecte();
        currentDriveOrder = null;
        sendDriveOrder = null;
        currentPoint = null;
        previousPoint = null;
        vehicleROSBridgeTask.terminate();
        vehicleROSBridgeTask = null;
        getProcessModel().getVelocityController().removeVelocityListener(getProcessModel());
        super.disable();
    }

    @Override
    public final ROSBridgeProcessModel getProcessModel() {
        return (ROSBridgeProcessModel) super.getProcessModel();
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
    public void execute(AdapterCommand command) {
        PublishEventCommand publishCommand = (PublishEventCommand) command;
        switch (publishCommand.getEventAppendix().toString()) {
            case "pausePath":
                agv.pausePath();
                getProcessModel().publishUserNotification(new UserNotification("send pause path command to vehicle", UserNotification.Level.INFORMATIONAL));
                break;
            case "resumePath":
                agv.resumePath();
                getProcessModel().publishUserNotification(new UserNotification("send resume path command to vehicle", UserNotification.Level.INFORMATIONAL));
                break;
            case "abortPath":
                agv.abortPath();
                getProcessModel().publishUserNotification(new UserNotification("send abort path command to vehicle", UserNotification.Level.INFORMATIONAL));
                break;
            case "resetAlarm":
                agv.resetAlarm();
                getProcessModel().publishUserNotification(new UserNotification("send reset alarm command to vehicle", UserNotification.Level.INFORMATIONAL));
                break;
            case "forkLoad":
                String point_name = this.getInitialPosition();
                if (point_name != null) {
                    agv.forkAction(getProcessModel().getVehiclePrecisePosition(), 1, Integer.parseInt(new String(point_name)));
                }
                getProcessModel().publishUserNotification(new UserNotification(MessageFormatter.format("forkLoad in point: {}", getProcessModel().getVehiclePosition()).getMessage(), UserNotification.Level.INFORMATIONAL));
                break;
            case "forkUnload":
                String point_name2 = this.getInitialPosition();
                if (point_name2 != null) {
                    agv.forkAction(getProcessModel().getVehiclePrecisePosition(), 2, Integer.parseInt(new String(point_name2)));
                }
                getProcessModel().publishUserNotification(new UserNotification(MessageFormatter.format("forkUnload int point: {}", getProcessModel().getVehiclePosition()).getMessage(), UserNotification.Level.INFORMATIONAL));
                break;
            default:
                break;
        }

        LOG.info("controlcenter action: '{}'", publishCommand.getEventAppendix());
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
        return new ROSBridgeProcessModelTO()
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

    public String getInitialPosition() {
        Triple triple = getProcessModel().getVehiclePrecisePosition();
//        while (triple.getX() == 0.0) { triple = getProcessModel().getVehiclePrecisePosition();}

        getProcessModel().setVehicleState(Vehicle.State.IDLE);
//        Triple precisePosition = new Triple((long)currentPosition[0], (long)currentPosition[1], 0);
        Triple precisePosition = getProcessModel().getVehiclePrecisePosition();
        List<Point> PointList = getPointLists().stream().filter(point -> Math.abs(point.getPosition().getX() - precisePosition.getX()) < 500).filter(point -> Math.abs(point.getPosition().getY() - precisePosition.getY()) < 500).collect(Collectors.toList());

        switch( PointList.size() ) {
            case 0:
                return null;
            case 1:
                currentPoint = PointList.get(0);
                getProcessModel().setVehiclePosition(currentPoint.getName());
                LOG.info("PointList: {}", PointList);
                return currentPoint.getName();
            default:
                currentPoint = PointList.get(0);
                getProcessModel().setVehiclePosition(currentPoint.getName());
                return PointList.get(0).getName();
        }
    }

    public Point approachPosition(DriveOrder driveOrder, Triple point_1, int distenceX, int distenceY) {
        List<Step> listPoint = driveOrder.getRoute().getSteps().stream().filter(point -> Math.abs(point.getDestinationPoint().getPosition().getX() - point_1.getX()) < distenceX && Math.abs(point.getDestinationPoint().getPosition().getY() - point_1.getY()) < distenceY).collect(Collectors.toList());
        if (listPoint.size() < 1) {return null;}
        return listPoint.get(0).getDestinationPoint();
    }

    public int[] gotoNearPosition() {
        AgvInfo agvInfo = agv.getAgvInfo();
        int path[] = {0, 0, 3, 0, (int)agvInfo.getPrecisePosition().getX()/10, (int)agvInfo.getPrecisePosition().getY()/10, 365, 0, (int)currentPoint.getPosition().getX()/10, (int)agvInfo.getPrecisePosition().getY()/10, 365, 0, (int)currentPoint.getPosition().getX()/10, (int)currentPoint.getPosition().getY()/10, 365};
        return path;
    }
   @Override
   public void abortDriveOrder() {
        LOG.info("abort path");
        agv.abortPath();
   }

    private class VehicleROSBridgeTask extends CyclicTask {
        private int simAdvanceTime;

        private VehicleROSBridgeTask() {
            super(500);
        }

        private MovementCommand curCommand;

        private Point pathStartPosition;

        Triple current_precise;

        Triple dist_precise;
        double dist_angle;

        double current_angle;

        double previous_angle;

        @Override
        protected void runActualTask() {
            try {
                if(curCommand == null && getSentQueue().size() > 0) {

                    synchronized (ROSBridgeCommAdapter.this) {
                        curCommand = getSentQueue().poll();
                    }
                    if (curCommand.getStep().getSourcePoint().equals(getcurrentDriveOrder().getRoute().getSteps().get(0).getSourcePoint())) {
                        //起始地点
                        sendDriveOrder = getcurrentDriveOrder();
                        getProcessModel().setVehicleState(Vehicle.State.EXECUTING);
                        pathStartPosition = curCommand.getStep().getSourcePoint();
                        dist_precise = curCommand.getFinalDestination().getPosition();
                        dist_angle = curCommand.getFinalDestination().getVehicleOrientationAngle();
                    }
                    agv.sendGoalPose(curCommand);
                    LOG.info("目标点车辆角度: {}", curCommand.getStep().getDestinationPoint().getVehicleOrientationAngle());
                }
                if (curCommand == null) {
                    Uninterruptibles.sleepUninterruptibly(ADVANCE_TIME, TimeUnit.MILLISECONDS);
                    getProcessModel().getVelocityController().advanceTime(simAdvanceTime);
                } else {
                    // If we were told to move somewhere, simulate the journey.
                    LOG.debug("Processing MovementCommand...");
//                    final Route.Step curStep = curCommand.getStep();

                    Point p;
                    if (getSentQueue().size() != 0) {
//                        p = approachPosition(sendDriveOrder, agvInfo.getPrecisePosition(), 300, 300);
                        p = approachPosition(sendDriveOrder, getProcessModel().getVehiclePrecisePosition(), 300, 300);
                    } else {
//                        p = approachPosition(sendDriveOrder, agvInfo.getPrecisePosition(), 300, 100);
                        p = approachPosition(sendDriveOrder, getProcessModel().getVehiclePrecisePosition(), 300, 300);
                    }
                    if (p != null && p != pathStartPosition && p != currentPoint) {
                        currentPoint = p;
//                        if( currentPoint == p)
                        getProcessModel().setVehiclePosition(p.getName());
                        current_precise = getProcessModel().getVehiclePrecisePosition();
                        if (curCommand.getFinalDestination().getName().equals(p.getName())) {
//                            if (Math.abs(current_precise.getX() - dist_precise.getX()) < 50 && Math.abs(current_precise.getY() - dist_precise.getY()) < 50 && Math.abs(current_angle - dist_angle) < 2) {
                                getProcessModel().commandExecuted(curCommand);
                                getProcessModel().publishUserNotification(new UserNotification(MessageFormatter.format("reach to end point: {}", p).getMessage(), UserNotification.Level.INFORMATIONAL));

                                curCommand = null;
                                getProcessModel().setVehicleState(Vehicle.State.IDLE);
//                            }
                            Thread.sleep(500);
                        } else {

                            getProcessModel().commandExecuted(curCommand);
                            getProcessModel().publishUserNotification(new UserNotification(MessageFormatter.format("reach to point: {}", p).getMessage(), UserNotification.Level.INFORMATIONAL));

                            curCommand = null;
                        }
                    }
                }
                Thread.sleep(200);
            } catch (Exception ex) {
                LOG.error(ex.getMessage());
            }
        }
    }


    private enum LoadState {
        EMPTY,
        FULL;
    }
}
