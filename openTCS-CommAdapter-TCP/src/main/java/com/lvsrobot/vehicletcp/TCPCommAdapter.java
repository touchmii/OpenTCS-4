package com.lvsrobot.vehicletcp;

import com.google.inject.assistedinject.Assisted;
import com.lvsrobot.vehicletcp.controller.PathTruck;
import org.opentcs.components.kernel.services.TCSObjectService;
import org.opentcs.components.kernel.services.TransportOrderService;
import org.opentcs.customizations.kernel.KernelExecutor;
import org.opentcs.data.model.Point;
import org.opentcs.data.model.Vehicle;
import org.opentcs.data.notification.UserNotification;
import org.opentcs.data.order.DriveOrder;
import org.opentcs.data.order.TransportOrder;
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
import java.util.concurrent.LinkedBlockingQueue;
import java.util.stream.Collectors;

import static java.util.Objects.requireNonNull;


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

    private PathTruck pathTruck;

    private final TransportOrderService orderService;

    private final TCSObjectService objectService;
    private String curPoint;
    private String prePoint;

    private Queue<String> updatePointQueue = new LinkedBlockingQueue<>();

    NettyServer nettyServer;

    /**
     * Creates a new instance.
     *
     * @param vehicle           The attached vehicle.
     * @param componentsFactory The components factory.
     *                          父类BasicVehicleCommAdapter引用了ExampleProcessModel的父类
     *                          ProcessModel，此处将自己实现的类关联在以前，可以让内核调用到。
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
        pathTruck = new PathTruck(getProcessModel(), this);
        Thread threadPathTruck = new Thread(pathTruck, getName() + "pathTruck");
        threadPathTruck.start();

        nettyServer = new NettyServer(getProcessModel().getPortlog(), this);
        nettyServer.start();

        getProcessModel().getVelocityController().addVelocityListener(getProcessModel());

        getProcessModel().setVehicleProperty("error", "xx");

        // Create task for vehicle.
        vehicleTask = new VehicleTask();
        Thread Thread = new Thread(vehicleTask, getName() + "-TCP-Task");
        Thread.start();
        super.enable();
    }

    @Override
    public synchronized void disable() {
        if (!isEnabled()) {
            return;
        }
        currentDriveOrder = null;
        sendDriveOrder = null;

        this.setcurrentDriveOrder(null);
        vehicleTask.terminate();
        vehicleTask = null;

        pathTruck.terminate();

        nettyServer.interrupt();


        getProcessModel().getVelocityController().removeVelocityListener(getProcessModel());
        super.disable();
    }

    //    @Override
    public DriveOrder getxcurrentDriverOrder() {
        //orderService.fetchObject(TransportOrder.class, t -> t.getName().equals(name)).stream().map(order -> order.getCurrentDriveOrder());
        List<DriveOrder> driveOrderList = orderService.fetchObjects(TransportOrder.class, t -> t.getProcessingVehicle().getName().equals(getName())).stream().map(order -> order.getCurrentDriveOrder()).collect(Collectors.toList());
        return driveOrderList.get(0);
    }

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

        switch (command_str) {
            case "pausePath":
//                agv.pausePath();
                getProcessModel().sendMsg("robot zanting 3\n");
                publishNotify("send pausePath Command");
                break;
            case "resumePath":
//                agv.resumePath();
                getProcessModel().sendMsg("robot zanting 0\n");
                publishNotify("send resumePath Command");
                break;
            case "xPath":
                try {
                    pathTruck.resumePath(false);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                publishNotify("send resume path command to vehicle");
                break;
            case "abortPath":
//                agv.abortPath();
                publishNotify("send abortPath Command");
                break;
            case "resetAlarm":
//                agv.resetAlarm();
                publishNotify("send resetAlarm Command");
                break;
            case "forkLoad":
                getProcessModel().sendMsg("robot lifterat 50\n");
                publishNotify("send lift up Command");
                break;
            case "forkUnload":
                getProcessModel().sendMsg("robot lifterat 0\n");
                publishNotify("send lift down Command");
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
//        自动连接
    }

    @Override
    protected synchronized void disconnectVehicle() {

    }

    @Override
    protected synchronized boolean isVehicleConnected() {
        return true;
//        return this.agv.isConnected();
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


    @Override
    public void abortDriveOrder() {
        LOG.info("{} abort path", getName());
        //agv.abortPath();
        pathTruck.abortPath();
        setcurrentDriveOrder(null);
        getSentQueue().clear();
//       sendMsg("robot zanting 2\n");
    }

    public void publishNotify(String msg, UserNotification.Level level) {
        getProcessModel().publishUserNotification(new UserNotification(
                MessageFormatter.format("{}: {}", vehicle.getName(), msg).getMessage(), level));
    }

    public void publishNotify(String msg) {
        getProcessModel().publishUserNotification(new UserNotification(
                MessageFormatter.format("{}: {}", vehicle.getName(), msg).getMessage(), UserNotification.Level.INFORMATIONAL));
    }

    public void callback(AgvInfo agvInfo) {
        if (agvInfo != null) {
            int p = agvInfo.getPosition();
            if (p > 1 && p < 1000) {
                curPoint = String.valueOf(p);
                pathTruck.updatePoint(curPoint);
                if (!curPoint.equals(prePoint)) {
//                    checkMovement(curPoint);
                    updatePointQueue.add(curPoint);
                    prePoint = curPoint;
                }
            }
            getProcessModel().setVehiclePathState(agvInfo.getStatus());
            getProcessModel().setCurChargeState(agvInfo.getCharge());

            getProcessModel().setVehiclePosition(curPoint);
            getProcessModel().setObstacle(agvInfo.getObstacle());
            getProcessModel().setVehicleOrientationAngle(agvInfo.getAngle());
            getProcessModel().setVehicleEnergyLevel(agvInfo.getBattery());

        }
    }

    public void checkObstacle() {
        if (objectService.fetchObject(Point.class, curPoint).getProperty("obstacle") == null) {
//            LOG.info("{} 开避障位置");
            if (getProcessModel().getObstacle() < 59) {
                pathTruck.addPath(radarDis(60, 35, 10));
                pathTruck.addPath(radarDis(60, 35, 10));
            }
        }

    }

    public byte[] radarDis(int distance, int width, int length) {
        byte[] radarCommand = {0, 1, 4, 0, 3, (byte)distance, (byte)width, (byte)length, 0};
        byte check = 0;
        for(int i=0; i<8;i++) {
            check = (byte) (check ^ radarCommand[i]);
        }
        radarCommand[8] = (byte) ~ check;
        return radarCommand;
    }

    public void missMovement(MovementCommand command, String point) {
//       String point = getProcessModel().getVehiclePosition();
        List<String> remainCommand = getSentQueue().stream().map(movementCommand -> movementCommand.getStep().getDestinationPoint().getName()).collect(Collectors.toList());
        if (remainCommand.contains(point)) {
            while (!command.getStep().getDestinationPoint().getName().equals(point)) {
                getSentQueue().poll();
                getProcessModel().commandExecuted(command);
                LOG.info("{} miss point: {}, current  point: {}", getName(), command.getStep().getDestinationPoint().getName(), point);
                getProcessModel().publishUserNotification(new UserNotification(MessageFormatter.format("miss to point: {}", command.getStep().getDestinationPoint().getName()).getMessage(), UserNotification.Level.INFORMATIONAL));
                command = getSentQueue().peek();
                //TODO miss to point后不发送任务
            }
            getSentQueue().poll();
            getProcessModel().commandExecuted(command);

        } else {
        }
    }

    //小车点位发生变化时
    public void checkMovement(String curPoint) {
        String point = curPoint;
        MovementCommand command = getSentQueue().peek();

        if (point.equals(command.getStep().getDestinationPoint().getName())) {
            // LOG.debug("{} current dist location: {}, no. dist point: {}, current point: {}", getName(), getcurrentDriveOrder().getDestination().getDestination().getName(), driveOrderList.get(driver_index).getDestination().getDestination().getName(), curPoint);

            getSentQueue().poll();
            getProcessModel().commandExecuted(command);

        } else {
            missMovement(command, point);
        }
    }

    private class VehicleTask extends CyclicTask {

        private VehicleTask() {
            super(500);
        }

        @Override
        protected void runActualTask() {
            try {
                if (updatePointQueue.peek() != null && getSentQueue().peek() != null) {
                    checkMovement(updatePointQueue.poll());
                    checkObstacle();
                }

                //获取状态  位置  速度  方向等
                /*if(curCommand == null && getSentQueue().size() > 0) {

                    synchronized (TCPCommAdapter.this) {
                        curCommand = getSentQueue().peek();
                    }
                    curCommand = curCommand;
                    //检测是否为单步动作，源点为空
                    try {
                        curCommand.getRoute().getSteps().get(0).getSourcePoint();
                    } catch (NullPointerException e) {
                        if(curCommand.getFinalDestination().getName().equals(curPoint)) {
                            singleAction = true;
                        }
                    }
                    LOG.debug("{} command: {}, driver order: {}", getName(), curCommand.toString(), getcurrentDriveOrder().toString());

                }
                if (curCommand == null) {
                //起点跟终点相同的订单且动作不为空
                } else if ( singleAction
                        && !curCommand.getOperation().equals("NOP")) {
                    wait_point = curCommand.getFinalDestination().getName();
                    action = curCommand.getOperation();
                    getProcessModel().setVehicleState(Vehicle.State.EXECUTING);
                    publishNotify(String.format("exec action: %s in Location: %s", action, curCommand.getOpLocation()), UserNotification.Level.INFORMATIONAL);
                } else {
                    // If we were told to move somewhere, simulate the journey.
    //                    LOG.info("Processing MovementCommand...");
    //                    final Route.Step curStep = curCommand.getStep();

                }*/
            } catch (Exception ex) {
                // LOG.error(ex.getMessage());
            }
        }
    }


    private enum LoadState {
        EMPTY,
        FULL;
    }
}
