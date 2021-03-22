package com.lvsrobot.vehicletcp;

import com.google.common.util.concurrent.Uninterruptibles;
import com.google.inject.assistedinject.Assisted;
import org.apache.commons.codec.binary.StringUtils;
import org.opentcs.components.kernel.services.TCSObjectService;
import org.opentcs.components.kernel.services.TransportOrderService;
import org.opentcs.customizations.kernel.KernelExecutor;
import org.opentcs.data.model.Point;
import org.opentcs.data.model.Triple;
import org.opentcs.data.model.Vehicle;
import org.opentcs.data.model.Vehicle.Orientation;
import org.opentcs.data.notification.UserNotification;
import org.opentcs.data.order.Route.Step;
import org.opentcs.drivers.vehicle.AdapterCommand;
import org.opentcs.drivers.vehicle.BasicVehicleCommAdapter;
import org.opentcs.drivers.vehicle.MovementCommand;
import org.opentcs.drivers.vehicle.commands.PublishEventCommand;
import org.opentcs.drivers.vehicle.management.VehicleProcessModelTO;
import org.opentcs.drivers.vehicle.messages.SetSpeedMultiplier;
import org.opentcs.util.CyclicTask;
import org.opentcs.util.ExplainedBoolean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.helpers.MessageFormatter;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import static java.util.Objects.requireNonNull;

//import org.opentcs.virtualvehicle.ConfigRoute;

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


    private AgvTelegramNew agv;

    private ConfigRoute configRoute = new ConfigRoute();

    private Point currentPoint;
    private String currentID;

    private Point previousPoint;
    private String previousID;

    private static AgvInfo agvInfo_callback = null;

    private MovementCommand currentCommand;

    private MovementCommand previousCommand;

    private final TransportOrderService orderService;

    private final TCSObjectService objectService;

    private int operate_point = 0;

//    private

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
    public ExampleCommAdapter(@Assisted Vehicle vehicle, ExampleAdapterComponentsFactory componentsFactory, @KernelExecutor ExecutorService kernelExecutor, TransportOrderService orderService, @Nonnull TCSObjectService objectService) {
        //父类BasicVehicleCommAdapter实例需要的参数
        super(new ExampleProcessModel(vehicle), 30, 30, "Charge");
        this.componentsFactory = requireNonNull(componentsFactory, "componentsFactory");
        this.vehicle = requireNonNull(vehicle, "vehicle");
        this.kernelExecutor = requireNonNull(kernelExecutor, "kernelExecutor");
        this.orderService = requireNonNull(orderService, "orderService");
        this.objectService = requireNonNull(objectService, "objectService");
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
        agv = new AgvTelegramNew(getProcessModel().getIp(), getProcessModel().getPort());
//        agv.Connect();
        agv.getAgvInfo();



        getProcessModel().getVelocityController().addVelocityListener(getProcessModel());

//        getProcessModel().setVehiclePosition(getInitialPosition());

        // Create task for vehicle simulation.
        vehicleSimulationTask = new VehicleSimulationTask();
        Thread simThread = new Thread(vehicleSimulationTask, getName() + "-simulationTask");
        simThread.start();
        super.enable();
//        getProcessModel().setVehiclePosition("4");
//        String init_point = getInitialPosition();
//        getProcessModel().setVehiclePosition(init_point);
//        getProcessModel().publishUserNotification(new UserNotification(MessageFormatter.format("adapter init finish, vehicle current point: {}", init_point).getMessage(), UserNotification.Level.INFORMATIONAL));
    }

    @Override
    public synchronized void disable() {
        if (!isEnabled()) {
            return;
        }
        agv.Terminal();
        currentDriveOrder = null;
        sendDriveOrder = null;
        currentPoint = null;
        currentID = null;
        previousPoint = null;
        previousID = null;
        currentCommand = null;
        previousCommand = null;
//        this.setcurrentDriveOrder(null);
        vehicleSimulationTask.terminate();
        vehicleSimulationTask = null;
        operate_point = 0;
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
    public void execute(AdapterCommand command) {
        PublishEventCommand publishCommand = (PublishEventCommand) command;
        String command_str = publishCommand.getEventAppendix().toString();
        try {
            operate_point = Integer.parseInt(command_str);
            LOG.info("Operate action in Point: {}", operate_point);
//            getProcessModel().publishUserNotification(new UserNotification(MessageFormatter.format("Operate confirm in Point: {}", operate_point), UserNotification.Level.INFORMATIONAL));
            getProcessModel().publishUserNotification(new UserNotification(MessageFormatter.format("forkUnload int point: {}", operate_point).getMessage(), UserNotification.Level.INFORMATIONAL));

            return;
        } catch (NumberFormatException e) {
//            LOG.info("");
        }
        switch (command_str) {
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
        this.agv.Connect();
    }

    @Override
    protected synchronized void disconnectVehicle() {

        this.agv.disConnect();
    }

    @Override
    protected synchronized boolean isVehicleConnected() {
//        return true;
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

    public String getInitialPosition() {
        AgvInfo agvInfo = agv.getAgvInfo();

        getProcessModel().setVehiclePosition(String.valueOf(agvInfo.getPosition()));
        getProcessModel().setVehicleState(Vehicle.State.IDLE);
        return String.valueOf(agvInfo.getPosition());

    }

//    public Point approachPosition(DriveOrder driveOrder, Triple point_1, int distenceX, int distenceY) {
//        List<Step> listPoint = driveOrder.getRoute().getSteps().stream().filter(point -> Math.abs(point.getDestinationPoint().getPosition().getX() - point_1.getX()) < distenceX && Math.abs(point.getDestinationPoint().getPosition().getY() - point_1.getY()) < distenceY).collect(Collectors.toList());
//        if (listPoint.size() < 1) {return null;}
//        return listPoint.get(0).getDestinationPoint();
//    }

//    public int[] gotoNearPosition() {
//        AgvInfo agvInfo = agv.getAgvInfo();
//        int path[] = {0, 0, 3, 0, (int)agvInfo.getPrecisePosition().getX()/10, (int)agvInfo.getPrecisePosition().getY()/10, 365, 0, (int)currentPoint.getPosition().getX()/10, (int)agvInfo.getPrecisePosition().getY()/10, 365, 0, (int)currentPoint.getPosition().getX()/10, (int)currentPoint.getPosition().getY()/10, 365};
//        return path;
//    }
   @Override
   public void abortDriveOrder() {
        LOG.info("abort path");
        agv.abortPath();
        getSentQueue().clear();
   }


   public static void callback(AgvInfo agvInfo) {
        agvInfo_callback = agvInfo;
   }

    private class VehicleSimulationTask extends CyclicTask {
        private int simAdvanceTime;

        private VehicleSimulationTask() {
            super(500);
        }

        private MovementCommand curCommand;

        private Point pathStartPosition;
        private String pathStartID;

        Triple current_precise;

        Triple previous_precise;

        double current_angle;

        double previous_angle;

        String currentPoint;
        int currentPoint_int;
        int currentStatus;
        int currentCharge;
        double currentAngle;
        long chargeTime = 0;

        String action = "";

        String ppp = null;

        private String wait_point = "";
        private String wait_oprate = "";

        @Override
        protected void runActualTask() {
            try {
                //获取状态  位置  速度  方向等
                AgvInfo agvInfo = agv.getAgvInfo();
                if (agvInfo_callback != null) {
//                    Thread.sleep(500);
//                    return;
                    currentPoint = String.valueOf(agvInfo_callback.getPosition());
                    currentPoint_int = agvInfo_callback.getPosition();
                    currentStatus = agvInfo_callback.getStatus();
                    currentAngle = agvInfo_callback.getAngle();
                    currentCharge = agvInfo_callback.getCharge();
    //                int[] currentPosition = agvInfo.getCurrentPosition();
    //                Triple precisePosition = new Triple((long)currentPosition[0], (long)currentPosition[1], 0);
    //                previous_precise = current_precise;
    //                current_precise = agvInfo.getPrecisePosition();
    //                currentID = v
//                    previous_angle = current_angle;
//                    current_angle = agvInfo_callback.getAngle();

//                    LOG.info("BiZhang dis : {}", agvInfo_callback.getBizhang());
                    if(currentPoint_int == 522 && currentAngle == 0 && agvInfo_callback.getBizhang() > 10) {
                        agv.radarDis(10, 35, 15);
                        agv.radarDis(10, 35, 15);
                        LOG.warn("关避障 522");
                    } else if(currentPoint_int == 121 && currentAngle == 270 && agvInfo_callback.getBizhang() > 10) {
                        agv.radarDis(10, 35, 15);
                        agv.radarDis(10, 35, 15);
                        LOG.warn("关避障 121");
                    } else if(currentPoint_int == 806 && currentAngle == 180 && agvInfo_callback.getBizhang() > 10) {
                        agv.radarDis(10, 35, 15);
                        agv.radarDis(10, 35, 15);
                        LOG.warn("关避障 充电点");
                    } else if((currentPoint_int == 129 || currentPoint_int == 169 || currentPoint_int == 806 || currentPoint_int == 808) && currentAngle == 270 && agvInfo_callback.getBizhang() > 10) {
                        agv.radarDis(10, 35, 15);
                        agv.radarDis(10, 35, 15);
                        LOG.warn("关避障 出X号工位");
                    } else if((currentPoint_int == 543 || currentPoint_int == 701 || currentPoint_int == 716 || currentPoint_int == 826) && currentAngle == 270 && agvInfo_callback.getBizhang() < 20) {
                        agv.radarDis(70, 35, 15);
                        agv.radarDis(70, 35, 15);
                        LOG.warn("开避障 出X号工位");
                    }
                    else if (currentPoint_int != 121 && currentPoint_int != 522 && currentPoint_int != 129 && currentPoint_int != 169 && currentPoint_int != 806 && currentPoint_int != 808 && currentPoint_int != 207 && agvInfo_callback.getBizhang() < 20) {
                        agv.radarDis(70, 35, 15);
                        agv.radarDis(70, 35, 15);
                        LOG.warn("开避障 所有点位");
                    }

                    getProcessModel().setVehiclePosition(currentPoint);
                    getProcessModel().setVehicleOrientationAngle(currentAngle);
                    getProcessModel().setVehicleEnergyLevel(agvInfo_callback.getBattery());
                    agvInfo_callback = null;
                }
//                LOG.info("xxxx");
//                if(agvInfo.getLoadStatus() == 1) {
//                    loadState = LoadState.FULL;
//                } else {
//                    loadState = LoadState.EMPTY;
//                }
//                getProcessModel().setMaxFwdVelocity(agvInfo.getSpeed());
//                getProcessModel().setMaxRevVelocity(agvInfo.getSpeed());
//                getProcessModel().setVehicleMaxVelocity(agvInfo.getSpeed());
//                LOG.info("get vehicle max speed {}", getProcessModel().getVehicleMaxVelocity());
//                LOG.info("vehicle battery : {}", agvInfo.getBattery());

//                getProcessModel().setVehiclePosition(currentPoint);

                if (currentStatus == 0) {
                    if (wait_point.equals("")) {

                        getProcessModel().setVehicleState(Vehicle.State.IDLE);
                    } else {
//                        assert agvInfo_callback != null;
                        if (String.valueOf(operate_point).equals(wait_point) && currentPoint.equals(wait_point)) {
                            operate_point = 0;
                            wait_point = "";
                            getProcessModel().setVehicleState(Vehicle.State.IDLE);
                        } else if (currentPoint.equals(wait_point) && action.equals("CHARGE")) {
                            if (currentCharge == 0) {
                                Thread.sleep(200);
                                agv.charge();
                                Thread.sleep(200);
                                agv.charge();
                                chargeTime = System.currentTimeMillis();
                                getProcessModel().publishUserNotification(new UserNotification(MessageFormatter.format("charge in point : {}", currentPoint).getMessage(), UserNotification.Level.INFORMATIONAL));
                            } else if (currentCharge == 1 && (System.currentTimeMillis() - chargeTime) > 180000) {
                                wait_point = "";
                                action = "";
                                getProcessModel().setVehicleState(Vehicle.State.IDLE);
                                getProcessModel().publishUserNotification(new UserNotification(MessageFormatter.format("charge time : {} minutes", 3).getMessage(), UserNotification.Level.INFORMATIONAL));
                            }
                        } else if (operate_point > 0) {
                            operate_point = 0;
                        }
                    }
                } else if (currentStatus == 1) {
                    getProcessModel().setVehicleState(Vehicle.State.EXECUTING);
                }

//                if (sendDriveOrder != null  && getSentQueue().size() == 0) {
//                    LOG.info("abort path :{}", currentDriveOrder.getRoute());
//                    getProcessModel().publishUserNotification(new UserNotification(MessageFormatter.format("abort path to vehicle: {}", currentDriveOrder.getRoute()).getMessage(), UserNotification.Level.INFORMATIONAL));
//                    agv.abortPath();
//                    sendDriveOrder = null;
//                    curCommand = null;
//                    currentCommand = null;
//                    sendDriveOrder = null;
//                }
                if(currentCommand == null && curCommand == null && getSentQueue().size() > 0) {

                    synchronized (ExampleCommAdapter.this) {
                        curCommand = getSentQueue().peek();
                    }
                    currentCommand = curCommand;
                }
                simAdvanceTime = (int) (ADVANCE_TIME * 1.0);
                if (curCommand == null) {
                    Uninterruptibles.sleepUninterruptibly(ADVANCE_TIME, TimeUnit.MILLISECONDS);
                    getProcessModel().getVelocityController().advanceTime(simAdvanceTime);
                } else {
                    // If we were told to move somewhere, simulate the journey.
//                    LOG.info("Processing MovementCommand...");
//                    final Route.Step curStep = curCommand.getStep();
                    if (sendDriveOrder != getcurrentDriveOrder() && getProcessModel().getVehicleState() == Vehicle.State.IDLE) {

                        configRoute.setRoute(getcurrentDriveOrder());
                        configRoute.setAngle(currentAngle);
                        byte[] path = configRoute.getPath();
                        String debugPath = configRoute.getDebugPath();
                        Thread.sleep(500);
                        if (!agv.sendPath(path)) {
                            return;
                        }
                        sendDriveOrder = getcurrentDriveOrder();
                        LOG.info("send path to vehicle : {}", debugPath);
//                        MessageFormatter.format(format, arg).getMessage()
//                        UserNotification notif = new UserNotification(MessageFormatter.format("send path to vehicle: {}", path).getMessage(), UserNotification.Level.INFORMATIONAL);
                        getProcessModel().publishUserNotification(new UserNotification(MessageFormatter.format("send path to vehicle: {}", debugPath).getMessage(), UserNotification.Level.INFORMATIONAL));
                        pathStartPosition = sendDriveOrder.getRoute().getSteps().get(0).getSourcePoint();
                        pathStartID = sendDriveOrder.getRoute().getSteps().get(0).getSourcePoint().getName();
                        getProcessModel().setVehicleState(Vehicle.State.EXECUTING);
                        if(getcurrentDriveOrder().getDestination().getOperation().equals("LOAD") || getcurrentDriveOrder().getDestination().getOperation().equals("UNLOAD") || getcurrentDriveOrder().getDestination().getOperation().equals("CHARGE")) {
                            wait_point = getcurrentDriveOrder().getRoute().getFinalDestinationPoint().getName();
                            action = "CHARGE";
                            LOG.warn("XXX wait_point: {} action: {}", wait_point, getcurrentDriveOrder().getDestination().getOperation());
                        }
                        ppp = getcurrentDriveOrder().getRoute().getSteps().get(0).getSourcePoint().getName();

                    }
                    if (currentPoint.equals(currentCommand.getStep().getDestinationPoint().getName()) && !currentPoint.equals(ppp)) {
                        ppp = currentPoint;
//                        currentPoint = p;
//                        if( currentPoint == p)
//                        getProcessModel().setVehiclePosition(p.getName());
                        if (getSentQueue().isEmpty()) {
//                            if (getProcessModel().getVehicleState() == Vehicle.State.IDLE) {
                                MovementCommand sentCmd = getSentQueue().poll();
                                getProcessModel().commandExecuted(curCommand);
                                getProcessModel().publishUserNotification(new UserNotification(MessageFormatter.format("reach to end point: {}", currentPoint).getMessage(), UserNotification.Level.INFORMATIONAL));

                                currentCommand = null;
                                curCommand = null;
//                                getProcessModel().setVehicleState(Vehicle.State.IDLE);
                                ppp = null;
//                            }
                            Thread.sleep(500);
                        } else {
//                            currentPoint ==
                            MovementCommand sentCmd = getSentQueue().poll();
                            getProcessModel().commandExecuted(curCommand);
                            getProcessModel().publishUserNotification(new UserNotification(MessageFormatter.format("reach to point: {}", currentPoint).getMessage(), UserNotification.Level.INFORMATIONAL));

                            currentCommand = null;
                            curCommand = null;

                        }
                    }

                }
                Thread.sleep(200);
            } catch (Exception ex) {
                LOG.error(ex.getMessage());
//                LOG.error(ex.printStackTrace());
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
