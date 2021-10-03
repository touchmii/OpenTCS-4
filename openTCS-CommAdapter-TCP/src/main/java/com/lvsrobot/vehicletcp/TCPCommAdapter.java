package com.lvsrobot.vehicletcp;

import com.google.common.util.concurrent.Uninterruptibles;
import com.google.inject.assistedinject.Assisted;
import com.lvsrobot.vehicletcp.binding.DoorStatus;
import org.opentcs.components.kernel.services.TCSObjectService;
import org.opentcs.components.kernel.services.TransportOrderService;
import org.opentcs.customizations.kernel.KernelExecutor;
import org.opentcs.data.model.Point;
import org.opentcs.data.model.Triple;
import org.opentcs.data.model.Vehicle;
import org.opentcs.data.model.Vehicle.Orientation;
import org.opentcs.data.notification.UserNotification;
import org.opentcs.data.order.DriveOrder;
import org.opentcs.data.order.Route;
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
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static java.util.Objects.requireNonNull;

//import org.opentcs.virtualvehicle.ConfigRoute;

/**
 * An example implementation for a communication adapter.
 *
 * @author Mats Wilhelm (Fraunhofer IML)
 */
public class TCPCommAdapter extends BasicVehicleCommAdapter {
    /**
     * The name of the load handling device set by this adapter.
     */
    public static final String LHD_NAME = "default";
    /**
     * This class's Logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger(TCPCommAdapter.class);
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
    private final TCPAdapterComponentsFactory componentsFactory;
    /**
     * The kernel's executor.
     */
    private final ExecutorService kernelExecutor;
    /**
     * The task simulating the virtual vehicle's behaviour.
     */
    private CyclicTask vehicleTask;
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

    private Timer pathTimer = new Timer(getName()+"pathTimer");

    private ConfigRoute configRoute = new ConfigRoute();

    private Point currentPoint;
    private String currentID;

    private Point previousPoint;
    private String previousID;

    private AgvInfo agvInfo_callback = null;

    private MovementCommand currentCommand;

    private MovementCommand previousCommand;

    private final TransportOrderService orderService;

    private final TCSObjectService objectService;

    private int operate_point = 0;

    private boolean abortPath = false;

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
    public TCPCommAdapter(@Assisted Vehicle vehicle, TCPAdapterComponentsFactory componentsFactory, @KernelExecutor ExecutorService kernelExecutor, TransportOrderService orderService, @Nonnull TCSObjectService objectService) {
        //父类BasicVehicleCommAdapter实例需要的参数
        super(new TCPProcessModel(vehicle), 30, 30, "CHARGE");
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
        agv = new AgvTelegramNew(getProcessModel().getIp(), getProcessModel().getPort(), this);
//        agv.Connect();
        agv.getAgvInfo();



        getProcessModel().getVelocityController().addVelocityListener(getProcessModel());

//        getProcessModel().setVehiclePosition(getInitialPosition());

        // Create task for vehicle simulation.
        vehicleTask = new VehicleTask();
        Thread Thread = new Thread(vehicleTask, getName() + "-TCP-Task");
        Thread.start();
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
        pathTimer.cancel();
        currentDriveOrder = null;
        sendDriveOrder = null;
        currentPoint = null;
        currentID = null;
        previousPoint = null;
        previousID = null;
        currentCommand = null;
        previousCommand = null;
//        this.setcurrentDriveOrder(null);
        vehicleTask.terminate();
        vehicleTask = null;
        operate_point = 0;
        getProcessModel().getVelocityController().removeVelocityListener(getProcessModel());
        super.disable();
    }

//    @Override
//    public DriveOrder getcurrentDriverOrder() {
//        orderService.fetchObject();
//    }

    @Override
    @Deprecated
    protected List<org.opentcs.drivers.vehicle.VehicleCommAdapterPanel> createAdapterPanels() {
        return Arrays.asList(componentsFactory.createPanel(this));
    }

    @Override
    public final TCPProcessModel getProcessModel() {
        return (TCPProcessModel) super.getProcessModel();
    }


    @Override
    public synchronized void sendCommand(MovementCommand cmd) {
        requireNonNull(cmd, "cmd");

        // Reset the execution flag for single-step mode.
        singleStepExecutionAllowed = false;
        // Don't do anything else - the command will be put into the sentQueue
        // automatically, where it will be picked up by the ulation task.
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
            LOG.info("{} Operate action in Point: {}", getName(), operate_point);
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

        LOG.info("{} controlcenter action: '{}'", getName(), publishCommand.getEventAppendix());
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
        return new TCPProcessModelTO()
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
       LOG.info("{} abort path", getName());
       //agv.abortPath();
       getSentQueue().clear();
       abortPath = true;

   }

   public void publishNotify(String msg, UserNotification.Level level) {
       getProcessModel().publishUserNotification(new UserNotification(
       MessageFormatter.format("{}: {}", vehicle.getName(),msg).getMessage(), level));
   }

   public void callback(AgvInfo agvInfo) {
        agvInfo_callback = agvInfo;
   }

   private class pathTimer extends TimerTask {
        private byte[] path;
        private String debugPath;
        private boolean exex_success = false;
        public pathTimer(byte[] path_, String debugPath_) {
            path = path_;
            debugPath = debugPath_;
        }
        @Override
        public void run() {
            if (getProcessModel().getVehicleState().equals(Vehicle.State.IDLE) && !exex_success) {
                LOG.info("{} resend path {}",getName(), debugPath);
                try {
                    Thread.sleep(1000);
                } catch (Exception e) {}
                agv.sendPath(path);
            } else if (getProcessModel().getVehicleState().equals(Vehicle.State.EXECUTING)) {
                exex_success = true;
            }
        }
    }

   private class VehicleTask extends CyclicTask {
        private int AdvanceTime;

        private VehicleTask() {
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

        private List<DriveOrder> driveOrderList = null;
        private List<Route.Step> stepList = null;
        private int driver_index = 0;

        private List<Point> openDoorList = null;
        private List<Point> closeDoorList = null;
        private String waitDoorID = null;
        private String openDoorID = null;
        private String closeDoorID = null;
        private int openDoorIndex = 0;
        private int closeDoorIndex = 0;
        private String doorName = null;
        private Route.Step doorStep = null;
        private boolean singleAction = false;
        private boolean updateDoorFlag = false;

        @Override
        protected void runActualTask() {
            try {
                //取消路径
                if (abortPath) {
                    action = "";

                    ppp = null;

                     wait_point = "";
                     wait_oprate = "";

                     driveOrderList = null;
                     stepList = null;
                     driver_index = 0;

                     openDoorList = null;
                     closeDoorList = null;
                     waitDoorID = null;
                     openDoorID = null;
                     closeDoorID = null;
                     openDoorIndex = 0;
                     closeDoorIndex = 0;
                     doorName = null;
                     doorStep = null;
                     singleAction = false;
                     updateDoorFlag = false;
                }
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
                    /*if(currentPoint_int == 522 && currentAngle == 0 && agvInfo_callback.getBizhang() > 10) {
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
                    } else if((currentPoint_int == 777 || currentPoint_int == 776 || currentPoint_int == 187 || currentPoint_int == 760 || currentPoint_int == 606 || currentPoint_int == 756) && currentAngle == 270 && agvInfo_callback.getBizhang() > 10) {
                        agv.radarDis(10, 35, 15);
                        agv.radarDis(10, 35, 15);
                        LOG.warn("关避障 出X号工位");
                    } else if((currentPoint_int == 190 || currentPoint_int == 314 || currentPoint_int == 180 || currentPoint_int == 574 || currentPoint_int == 298 || currentPoint_int == 717) && currentAngle == 270 && agvInfo_callback.getBizhang() < 20) {
                        agv.radarDis(70, 35, 15);
                        agv.radarDis(70, 35, 15);
                        LOG.warn("开避障 出X号工位");
                    }
                    else if (currentPoint_int != 777 && currentPoint_int != 776 && currentPoint_int != 187 && currentPoint_int != 760 && currentPoint_int != 606 && currentPoint_int != 756 && currentPoint_int != 190 && currentPoint_int != 314 && currentPoint_int != 180 && currentPoint_int != 574 && currentPoint_int != 298 && currentPoint_int != 717 && currentPoint_int != 207 && agvInfo_callback.getBizhang() < 20) {
                        agv.radarDis(70, 35, 15);
                        agv.radarDis(70, 35, 15);
                        LOG.warn("开避障 所有点位");
                    }
*/
                    if (!currentPoint.equals("0")) {
                        getProcessModel().setVehiclePosition(currentPoint);
                    }
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
                            } else if (currentCharge == 1 && (System.currentTimeMillis() - chargeTime) > 600000) {
                                wait_point = "";
                                action = "";
                                getProcessModel().setVehicleState(Vehicle.State.IDLE);
                                getProcessModel().publishUserNotification(new UserNotification(MessageFormatter.format("charge time : {} minutes", 10).getMessage(), UserNotification.Level.INFORMATIONAL));
                            }
                        } else if (currentPoint.equals(wait_point) && (action.equals("LOAD") || action.equals("UNLOAD")))  {
                            if (action.equals("LOAD")) {
                                try {
                                    Thread.sleep(1000);
                                } catch (Exception e) {}
                                agv.liftAction(currentPoint, AgvTelegramNew.LIFTACTION.UP);
                                try {
                                    Thread.sleep(1000);
                                } catch (Exception e) {}
                                agv.liftAction(currentPoint, AgvTelegramNew.LIFTACTION.UP);
                            }
                            if (action.equals("UNLOAD")) {
                                try {
                                    Thread.sleep(1000);
                                } catch (Exception e) {}
                                agv.liftAction(currentPoint, AgvTelegramNew.LIFTACTION.DOWN);
                                try {
                                    Thread.sleep(1000);
                                } catch (Exception e) {}
                                agv.liftAction(currentPoint, AgvTelegramNew.LIFTACTION.DOWN);
                            }
    //                            getProcessModel().setVehicleState(Vehicle.State.IDLE);
                            LOG.info("{} action: {} in pint: {} succeed", getName(), action, wait_point);
                            publishNotify(String.format("%s action: %s, in point %s succeed",getName(), action, wait_point), UserNotification.Level.INFORMATIONAL);
                            wait_point = "";
                            action = "";
                            if(singleAction) {
                                //提前处理单独的升降订单
                                MovementCommand sentCmd = getSentQueue().poll();
                                getProcessModel().commandExecuted(currentCommand);
                                currentCommand = null;
                                curCommand = null;
                                singleAction = false;
                            }
                        } else if (operate_point > 0) {
                            operate_point = 0;
                        }
                    }
                } else if (currentStatus == 1 || currentStatus == 2 || currentStatus == 4) {
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

                    synchronized (TCPCommAdapter.this) {
                        curCommand = getSentQueue().peek();
                    }
                    currentCommand = curCommand;
                    //检测是否为单步动作，源点为空
                    try {
                        currentCommand.getRoute().getSteps().get(0).getSourcePoint();
                    } catch (NullPointerException e) {
                        if(currentCommand.getFinalDestination().getName().equals(currentPoint)) {
                            singleAction = true;
                        }
                    }
                    LOG.debug("{} command: {}, driver order: {}", getName(), curCommand.toString(), getcurrentDriveOrder().toString());

                }
                AdvanceTime = (int) (ADVANCE_TIME * 1.0);
                if (curCommand == null) {
                    Uninterruptibles.sleepUninterruptibly(ADVANCE_TIME, TimeUnit.MILLISECONDS);
                    getProcessModel().getVelocityController().advanceTime(AdvanceTime);
                //起点跟终点相同的订单且动作不为空
                } else if ( singleAction
                        && !currentCommand.getOperation().equals("NOP")) {
                    wait_point = currentCommand.getFinalDestination().getName();
                    action = currentCommand.getOperation();
                    getProcessModel().setVehicleState(Vehicle.State.EXECUTING);
                    publishNotify(String.format("exec action: %s in Location: %s", action, currentCommand.getOpLocation()), UserNotification.Level.INFORMATIONAL);
                } else {
                    // If we were told to move somewhere, simulate the journey.
    //                    LOG.info("Processing MovementCommand...");
    //                    final Route.Step curStep = curCommand.getStep();
                    if (driveOrderList == null) {
                        configRoute.setRoute(getcurrentDriveOrder());
                        configRoute.setAngle(currentAngle);
                        byte[] path = configRoute.getPath();
                        String debugPath_ = configRoute.getDebugPath();
                        LOG.info("{} split befor path: {}", getName(), debugPath_);
                        driveOrderList = DoorController.splitDriverOrder(getcurrentDriveOrder());
                        stepList = DoorController.checkPassDoor(getcurrentDriveOrder());
                        driver_index = 0;
                        if (DoorController.checkPassDoor(getcurrentDriveOrder()).size() > 0) {
                            openDoorIndex = 0;
                            closeDoorIndex = 0;
                            openDoorList = DoorController.getOpenDoor(getcurrentDriveOrder());
                            closeDoorList = DoorController.getCloseDoor(getcurrentDriveOrder());
                            openDoorID = openDoorList.get(0).getName();
                            closeDoorID = closeDoorList.get(0).getName();
                            doorStep = stepList.get(0);
                            stepList.get(0).getSourcePoint().getName();
                            waitDoorID = stepList.get(0).getSourcePoint().getName();
                            try {
                                doorName = stepList.get(0).getDestinationPoint().getProperty("door");
                            } catch (Exception e) {
                                LOG.error("{} get first door Name error", getName());
                            }
                            LOG.info("{} init open door point: {}, wait door point: {}, close door point: {}, door name: {}", getName(), openDoorID, waitDoorID, openDoorID, doorName);


                        }
                    }
                    if (sendDriveOrder != driveOrderList.get(driver_index) && getProcessModel().getVehicleState() == Vehicle.State.IDLE) {

                        if (currentCharge == 1) {
                            Thread.sleep(200);
                            agv.discharge();
                            Thread.sleep(200);
                            agv.discharge();
                            Thread.sleep(1000);
                            getProcessModel().publishUserNotification(new UserNotification(MessageFormatter.format("DisCharge in point : {}", currentPoint).getMessage(), UserNotification.Level.INFORMATIONAL));
                        }


                        String debugPath = null;
                        Boolean sendPathFlag = false;
                        if (currentPoint.equals(waitDoorID)) {
                            LOG.info("{} wait door open at waitdoor point: {}", getName(), currentPoint);
                            DoorStatus doorStatus = DoorController.doorAction(doorName, DoorController.DOORACTION.OPEN);
                            if (doorStatus.getError() < 1 && doorStatus.getStatus().equals("open")) {
                                LOG.info("{} open {} door succeed",getName(), doorName);
                                sendPathFlag = true;
    //                                debugPath
                            }
                        }  else {
                            sendPathFlag = true;
                        }

                        if (sendPathFlag) {
    //                            configRoute.setRoute(getcurrentDriveOrder());
                            configRoute.setRoute(driveOrderList.get(driver_index));
                            configRoute.setAngle(currentAngle);
                            byte[] path = configRoute.getPath();
                            debugPath = configRoute.getDebugPath();
                            Thread.sleep(500);
                            agv.sendPath(path);
                            Thread.sleep(1000);
                            if (!agv.sendPath(path)) {
                                LOG.info("{}, send path to vehicle: {}", getName(), debugPath);
                                return;
                            }
                            if (currentCharge == 1) {
                                Thread.sleep(200);
                                agv.sendPath(path);
                                LOG.info("{} send path again", getName());
                            }
                            sendPathFlag = false;
                            try {
                                pathTimer.cancel();
                                pathTimer.schedule(new pathTimer(path, debugPath), 5000, 5000);
                            } catch (Exception e) {
                                LOG.error("{} set path timer fial: {}", e.getMessage());
                            }
    //                        }

    //                        if (getProcessModel().getVehicleState() == Vehicle.State.EXECUTING) {
    //                        if (!sendDriveOrder.getRoute().getSteps().get(0).getSourcePoint().getName().equals(currentPoint)) {

                            sendDriveOrder = driveOrderList.get(driver_index);
                            LOG.info("{} send path to vehicle : {}", getName(), debugPath);
    //                        MessageFormatter.format(format, arg).getMessage()
    //                        UserNotification notif = new UserNotification(MessageFormatter.format("send path to vehicle: {}", path).getMessage(), UserNotification.Level.INFORMATIONAL);
                            publishNotify(String.format("send path to vehicle: %s", debugPath), UserNotification.Level.INFORMATIONAL);
    //                        pathStartPosition = sendDriveOrder.getRoute().getSteps().get(0).getSourcePoint();
                            pathStartPosition = driveOrderList.get(driver_index).getRoute().getSteps().get(0).getSourcePoint();
    //                        pathStartID = sendDriveOrder.getRoute().getSteps().get(0).getSourcePoint().getName();
                            pathStartID = driveOrderList.get(driver_index).getRoute().getSteps().get(0).getSourcePoint().getName();
                            getProcessModel().setVehicleState(Vehicle.State.EXECUTING);
                            if ( driver_index == driveOrderList.size()-1) {
                                if (getcurrentDriveOrder().getDestination().getOperation().equals("LOAD") || getcurrentDriveOrder().getDestination().getOperation().equals("UNLOAD") || getcurrentDriveOrder().getDestination().getOperation().equals("CHARGE")) {
                                    wait_point = getcurrentDriveOrder().getRoute().getFinalDestinationPoint().getName();
                                    action = getcurrentDriveOrder().getDestination().getOperation();
                                    LOG.warn("{} XXX wait_point: {} action: {}", getName(), wait_point, getcurrentDriveOrder().getDestination().getOperation());
                                }
                            }
    //                        ppp = getcurrentDriveOrder().getRoute().getSteps().get(0).getSourcePoint().getName();
                            ppp = driveOrderList.get(driver_index).getRoute().getSteps().get(0).getSourcePoint().getName();
                        }

                    }
                    if (!currentPoint.equals(ppp)) {

                        if (currentPoint.equals(openDoorID)) {
                            LOG.info("{} open {} door at opendoor point: {}", getName(), doorName, currentPoint);
                            publishNotify(String.format("open %s door at point: %s", doorName, currentPoint), UserNotification.Level.INFORMATIONAL);
                            DoorStatus doorStatus = DoorController.doorAction(doorName, DoorController.DOORACTION.OPEN);
                        } else if (currentPoint.equals(closeDoorID) && !updateDoorFlag) {
                            LOG.info("{} close {} door at point: {}", getName(), doorName, currentPoint);
                            publishNotify(String.format("close %s door at point: %s", doorName, currentPoint), UserNotification.Level.INFORMATIONAL);
                            DoorStatus doorStatus = DoorController.doorAction(doorName, DoorController.DOORACTION.CLOSE);
                            if (doorStatus.getError() == 0) {
                                LOG.info("{} close {} door at point: {} succeed!", getName(), doorName, currentPoint);
                                updateDoorFlag = true;
    //                            }
    //                            if (doorStatus.getError() == -4) {
    //                                for (Route.Step step : stepList) {
    //                                    if (doorStep.equals(step)) {
    //                                        doorStep = stepList.get(stepList.indexOf(step)+1);
    //                                    }
    //                                }
                            }

                            if (updateDoorFlag) {

                                openDoorIndex++;
                                closeDoorIndex++;
                                try {
                                    updateDoorFlag = false;
                                    doorName = stepList.get(openDoorIndex).getDestinationPoint().getProperty("door");
                                    openDoorID = openDoorList.get(openDoorIndex).getName();
                                    closeDoorID = closeDoorList.get(closeDoorIndex).getName();
                                    waitDoorID = stepList.get(openDoorIndex).getSourcePoint().getName();
                                    LOG.info("{} update open door point: {}, wait door point: {}, close door point: {}, door name: {}", getName(), openDoorID, closeDoorID, waitDoorID, doorName);
                                } catch ( Exception e) {
                                    LOG.error("{} get next door name error: {}", getName(), e.getMessage());
                                }
                            }
                        }

                        ppp = currentPoint;
    //                        currentPoint = p;
    //                        if( currentPoint == p)
    //                        getProcessModel().setVehiclePosition(p.getName());
                        if (currentPoint.equals(currentCommand.getStep().getDestinationPoint().getName())) {
                            LOG.info("{} current dist location: {}, no. dist point: {}, current point: {}", getName(), getcurrentDriveOrder().getDestination().getDestination().getName(), driveOrderList.get(driver_index).getDestination().getDestination().getName(), currentPoint);

    //                            if (currentPoint.equals(currentDriveOrder.getDestination().getDestination().getName())) {
                            if (currentPoint.equals(driveOrderList.get(driver_index).getDestination().getDestination().getName()) ||
                                currentPoint.equals(driveOrderList.get(driver_index).getRoute().getSteps().get(driveOrderList.get(driver_index).getRoute().getSteps().size()-1).getDestinationPoint().getName())) {
                                if (driver_index == driveOrderList.size()-1) {
                                    getProcessModel().publishUserNotification(new UserNotification(MessageFormatter.format("reach to end point: {}", currentPoint).getMessage(), UserNotification.Level.INFORMATIONAL));
                                    driveOrderList = null;
                                    stepList = null;
                                    driver_index = 0;
                                } else {
                                    getProcessModel().publishUserNotification(new UserNotification(MessageFormatter.format("reach to NO. {} path end point: {}", driver_index, currentPoint).getMessage(), UserNotification.Level.INFORMATIONAL));
                                    driver_index++;
                                }
                            } else {

                                getProcessModel().publishUserNotification(new UserNotification(MessageFormatter.format("reach to point: {}", currentPoint).getMessage(), UserNotification.Level.INFORMATIONAL));

                            }

                            MovementCommand sentCmd = getSentQueue().poll();
                            getProcessModel().commandExecuted(curCommand);
                            currentCommand = null;
                            curCommand = null;

                        } else {
                            List<String> remainCommand = getSentQueue().stream().map(movementCommand -> movementCommand.getStep().getDestinationPoint().getName()).collect(Collectors.toList());
                            if (remainCommand.contains(currentPoint)) {
                                while(!curCommand.getStep().getDestinationPoint().getName().equals(currentPoint)) {
                                    MovementCommand sentCmd = getSentQueue().poll();
                                    getProcessModel().commandExecuted(curCommand);
                                    getProcessModel().publishUserNotification(new UserNotification(MessageFormatter.format("miss to point: {}", curCommand.getStep().getDestinationPoint().getName()).getMessage(), UserNotification.Level.INFORMATIONAL));
                                    curCommand = getSentQueue().peek();
                                    //TODO miss to point后不发送任务
                                }
                                MovementCommand sentCmd = getSentQueue().poll();
                                getProcessModel().commandExecuted(curCommand);
                                currentCommand = null;
                                curCommand = null;

                            }
                        }
                    }

                }
                Thread.sleep(200);
            } catch (Exception ex) {
                // LOG.error(ex.getMessage());
    //                LOG.error(ex.printStackTrace());
            }
        }

    }

    //    class


    private enum LoadState {
        EMPTY,
        FULL;
    }
}
