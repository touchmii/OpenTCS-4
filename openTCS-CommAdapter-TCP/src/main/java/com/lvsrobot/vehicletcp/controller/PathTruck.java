package com.lvsrobot.vehicletcp.controller;

import com.lvsrobot.vehicletcp.*;
import com.lvsrobot.vehicletcp.binding.DoorStatus;
import org.opentcs.data.model.Point;
import org.opentcs.data.model.Vehicle;
import org.opentcs.data.order.DriveOrder;
import org.opentcs.data.order.Route;
import org.opentcs.util.CyclicTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.stream.Collectors;

@SuppressWarnings("unchecked")
public class PathTruck extends CyclicTask {
    private static final Logger LOG = LoggerFactory.getLogger(PathTruck.class);
    private TCPProcessModel processModel;
    private AgvTelegramNew agv;

    public DoorTruck getDoorTruck() {
        return doorTruck;
    }

    private DoorTruck doorTruck;
    private Thread doorTruckThread;
    private Queue<byte[]> pathQueue = new LinkedBlockingQueue<>();
    private Queue<byte[]> pathExecQueue = new LinkedBlockingQueue<>();
    private TCPCommAdapter commAdapter;
    private String name;

    private DriveOrder processDriverOrder = null;

    private Queue<DriveOrder> driveOrderQueue = new LinkedBlockingQueue<>();
    private DriveOrder sendSplitDriveOrder;

    private Queue<String> idleCheckQueue = new LinkedBlockingQueue<>();
    private Queue<String> pathExecCheckQueue = new LinkedBlockingQueue<>();
    private Queue<String> pathFailCheckQueue = new LinkedBlockingQueue<>();
    private Queue<String> pathOffCheckQueue = new LinkedBlockingQueue<>();
    private Queue<String> pathObstacleCheckQueue = new LinkedBlockingQueue<>();

    private boolean active = false;
    private String curPoint;
    private String prePoint;

    private SendCommand sendCommandThread;

    private ConfigRoute configRoute = new ConfigRoute();

    List<Point> waitPoint;

    private Timer delay_timer;
    private Timer recheck_close_door_timer;

    private static long sleep_time = 200;
    private static long idle_check_time = 3000;
    private static long path_exec_check_time = 3000;
    private static long path_fail_check_time = 2 * 60 * 1000;
    private static long path_off_check_time = 60 * 1000;
    private static long path_obstacle_check_time = 500;

    private static int path_state_idle = 0;
    private static int path_state_exec = 1;
    private static int path_state_finish = 2;
    private static int path_state_pause = 3;
    private static int path_state_goal = 4;


    public PathTruck(TCPProcessModel processModel, TCPCommAdapter commAdapter) {
        super(sleep_time);
        this.processModel = processModel;
        this.commAdapter = commAdapter;
        this.name = commAdapter.getName();
        agv = new AgvTelegramNew(processModel.getIp(), processModel.getPort(), commAdapter);
//        agv = new AgvTelegramNew("localhost", processModel.getPort(), commAdapter);
        sendCommandThread = new SendCommand(agv, pathQueue);
        sendCommandThread.start();

        doorTruck = new DoorTruck(processModel, name);
        doorTruck.setDoorMap(commAdapter.getDoorMap());
        doorTruckThread = new Thread(doorTruck, name + "doorTruck");
        doorTruckThread.start();

        delay_timer = new Timer();
        recheck_close_door_timer = new Timer();

    }

    public void addPath(byte[] path) {
        pathQueue.add(path);
    }

    public List<List> checkNewOrder(DriveOrder driveOrder) {
        List<Point> openDoorList = new ArrayList<>();
        List<Point> closeDoorList = new ArrayList<>();
        List<Point> waitDoorList = new ArrayList<>();

//        List<DriveOrder> driveOrderList = DoorController.splitDriverOrder(driveOrder);
        List<Route.Step> doorStepList = DoorController.checkPassDoor(driveOrder);
        if (doorStepList.size() > 0) {
            openDoorList = DoorController.getOpenDoor(driveOrder);
            closeDoorList = DoorController.getCloseDoor(driveOrder);
            for (Route.Step step : doorStepList) {
                //Dist点为门，Src点为门前等待点
                String doorName = step.getDestinationPoint().getProperty("door");
                String roomName = step.getDestinationPoint().getProperty("zh");
                waitDoorList.add(step.getSourcePoint().withProperty("door", doorName).withProperty("zh", roomName));
            }
        }

        List<List> list = new ArrayList<>();
        list.add(openDoorList);
        list.add(closeDoorList);
        list.add(waitDoorList);
        return list;
    }

    public void updatePoint(String curPoint) {
        this.curPoint = curPoint;
    }

    public void abortPath() {
        processModel.sendMsg("robot zanting 2\n");
        processDriverOrder = null;
        sendSplitDriveOrder = null;
        driveOrderQueue.clear();
        pathQueue.clear();
        pathExecQueue.clear();
        doorTruck.cleanDoor();
        waitPoint = null;
    }

    public class SendCommand extends Thread {
        private AgvTelegramNew agv;
        private Queue<byte[]> queue;

        public SendCommand(AgvTelegramNew agv, Queue<byte[]> queue) {
            this.agv = agv;
            this.queue = queue;
        }

        @Override
        public void run() {
            while (this.isAlive()) {
                try {
                    if (queue.peek() != null) {
                        agv.sendPath(queue.poll());
                        Thread.sleep(200);
                    } else {
//                        LOG.info("{} send qurey", commAdapter.getName());
                        agv.getAgvInfo();
                    }

                    Thread.sleep(200);
                } catch (Exception e) {
                }
            }

        }
    }

    public DoorStatus checkDoor(String point) {
        DoorStatus doorStatus = null;
        for (Point p : waitPoint) {
            if (p.getName().equals(point)) {
                doorStatus = doorTruck.getDoorStatus(p.getProperty("door"));
            }
        }
        return doorStatus;
    }

    public String isWaitPoint(String point) {
        String flag = null;
        for (Point p : waitPoint) {
            if (p.getName().equals(point)) {
                flag = p.getProperty("door");
            }
        }
        return flag;
    }

    public Point getWaitPoint(String point) {
        Point pointX = null;
        for (Point p : waitPoint) {
            if (p.getName().equals(point)) {
                pointX = p;
            }
        }
        return pointX;
    }

    public String getRoom(String point) {
        String flag = null;
        for (Point p : waitPoint) {
            if (p.getName().equals(point)) {
                flag = p.getProperty("zh");
            }
        }
        return flag;
    }

    public void resumePath(boolean force) throws Exception {
        String flag = processModel.getVehicle().getProperty("setFlag");
        if (flag != null && flag.equals("1")
        ) {
            throw new Exception(("车辆未发生错误"));
        }

        if (processModel.getVehiclePathState() != path_state_idle) {
            throw new Exception(("车辆未处于待命状态"));
        }
        if (commAdapter.getcurrentDriveOrder() == null) {
            throw new Exception(("车辆所有任务都执行完毕"));
        }

//        driveOrder.getRoute().getSteps().stream().map(step -> step.getSourcePoint().getName().equals())
        //TODO 检测当前位置是否在路径上
        LOG.info("resume Driver order: {}", sendSplitDriveOrder.toString());
        List<Route.Step> stepList = sendSplitDriveOrder.getRoute().getSteps();
        List<Route.Step> newStepList = new ArrayList<>();
        boolean start = false;

        for (Route.Step step : stepList) {
            if (start) {
                newStepList.add(step);
            }
            if (step.getSourcePoint().getName().equals(curPoint)) {
                start = true;
                newStepList.add(step);
            }
        }
        if (start) {

            Route route = new Route(newStepList, newStepList.size());
            DriveOrder newDriverOrder;
            try {
                //创建带动作的订单必须分两步
                String opration = sendSplitDriveOrder.getDestination().getOperation();
                DriveOrder.Destination dest = new DriveOrder.Destination(stepList.get(stepList.size() - 1).getDestinationPoint().getReference());
                dest = dest.withOperation(opration);
                newDriverOrder = new DriveOrder(dest).withRoute(route);
                configRoute.setRoute(newDriverOrder);
                configRoute.setAngle(processModel.getVehicleOrientationAngle());
                byte[] path = configRoute.getPath();

                addPath(path);
                pathExecQueue.add(path);
                LOG.info("{}, send resume path to vehicle: {}", name, configRoute.getDebugPath());
                processModel.setVehicleState(Vehicle.State.EXECUTING);
                processModel.getVehicle().setProperty("setFlag", "0");
            } catch (Exception e) {
                LOG.error("{} 创建恢复路径失败: {}", name, e.getMessage());

//            } finally {
//                newDriverOrder = new DriveOrder(new DriveOrder.Destination(stepList.get(stepList.size() - 1).getDestinationPoint().getReference())).withRoute(route);
//
            }

        } else {
            LOG.error("车辆不在失败的路径上: {}", curPoint);
            throw new Exception(("车辆不在失败的路径上"));
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

    public class DelayLift extends TimerTask {
        @Override
        public void run() {
            sendSplitDriveOrder = null;
        }
    }

    public class ReCheckClose extends TimerTask {

        private Point door;
        private String point;
        private String doorName;
        private List<String> pointList;

        public ReCheckClose(Point p) {
            this.door = p;
            this.doorName = p.getProperty("door");
            this.point = p.getName();
            //获取整条运行中的路径
            pointList = processDriverOrder.getRoute().getSteps().stream().map(s -> s.getDestinationPoint().getName()).collect(Collectors.toList());
        }

        @Override
        public void run() {
//            DoorStatus doorStatus = checkDoor(curPoint);
            while (doorTruck.getDoorStatus(doorName).getStatus().equals("opened")) {
                LOG.debug("自动门状态: {}, waitPoint: {}, curPoint: {}", doorTruck.getDoorStatus(doorName).getStatus(), this.point, curPoint);
                try {
                    LOG.debug("curPoint index: {}, waitPoint index: {}", pointList.indexOf(curPoint), pointList.indexOf(this.point));
                    if (pointList.indexOf(curPoint) - pointList.indexOf(this.point) > 3) {
                        LOG.info("{} 重复检测关门点: {} 门: {}", name, curPoint, doorName);
                        doorTruck.addRecheckCloseDoor(doorName);
                    }
                    Thread.sleep(1000);
                } catch (Exception e) {
                    LOG.error("{} ReCheckDoor Error: {}", name, e.getMessage());
                }
            }
            LOG.debug("自动门关闭定时任务结束");
        }
    }



    @Override
    public void terminate() {
        agv.Terminal();
        doorTruck.terminate();
        super.terminate();
    }

    @Override
    protected void runActualTask() {
        try {
            //设置车辆内核状态，用于接收新的订单, 车辆维持空闲状态3秒钟。通过队列实现
            if (processDriverOrder == null && processModel.getVehiclePathState() == 0) {
                idleCheckQueue.add("idle");
            } else {
                idleCheckQueue.clear();
            }
            if (idleCheckQueue.size() > idle_check_time / sleep_time) {
                idleCheckQueue.clear();
                processModel.setVehicleState(Vehicle.State.IDLE);
            }
            //检测已经发送路径执行状态，三秒后未进入执行状态，重发
            if (pathExecQueue.size() > 0 && processModel.getVehiclePathState() == 0) {
                pathExecCheckQueue.add("idle");
            } else if (pathExecQueue.size() > 0 && processModel.getVehiclePathState() == 1) {
                LOG.debug("{} 路径发送成功", name);
                pathExecQueue.clear();
                pathExecCheckQueue.clear();
            }
            if (pathExecCheckQueue.size() > path_exec_check_time / sleep_time) {
                addPath(pathExecQueue.peek());
                pathExecCheckQueue.clear();
                LOG.info("{} 添加重发路径", name);
            }
            //TODO检查升降状态
//            if (pathExecQueue.peek().)
            //检测车辆在一个位置停止超过2分钟
            if (processModel.getVehiclePathState() == path_state_exec && processModel.getVehicleState() == Vehicle.State.EXECUTING) {
                if (pathFailCheckQueue.peek() !=null && pathFailCheckQueue.peek().equals(curPoint)) {
                    pathFailCheckQueue.add(curPoint);
                } else {
                    pathFailCheckQueue.clear();
                }
            }
            if (pathFailCheckQueue.size() > path_fail_check_time / sleep_time) {
                pathFailCheckQueue.clear();
                processModel.setVehicleState(Vehicle.State.ERROR);
                processModel.setVehicleProperty("UnknowError", "On");
                commAdapter.publishNotify(String.format("未知错误，原地停止超时未行走: %s", curPoint));
                LOG.error("{} 未知错误，原地停止超时未行走", name);
            }

            //检查车辆关机
            if (processModel.isClientConnectFlag() == false && !processModel.getVehicleState().equals(Vehicle.State.UNKNOWN)) {
                pathOffCheckQueue.add("Off");
            } else if (!processModel.getVehicleState().equals(Vehicle.State.UNKNOWN)) {
                pathOffCheckQueue.clear();
            }
            if (pathOffCheckQueue.size() > path_off_check_time / sleep_time) {
                pathOffCheckQueue.clear();
                processModel.setVehicleState(Vehicle.State.UNKNOWN);
                processModel.setVehiclePosition(null);
                processModel.setVehicleEnergyLevel(0);
            }

            //检查避障误报警,暂时点位写死，可初始时一次新判断。检测条件是避障快速切换。
/*            if (processModel.getObstacleFlag()) {
                if (curPoint.equals("238") || curPoint.equals("237") || curPoint.equals("297")) {
                    if (pathObstacleCheckQueue.size() > 0 && !pathObstacleCheckQueue.peek().equals(curPoint)) {
                        pathObstacleCheckQueue.clear();
                    }
                    pathObstacleCheckQueue.add(curPoint);
                }
            } else {
                if (pathObstacleCheckQueue.size() > 0 && pathObstacleCheckQueue.peek().equals(curPoint)) {
                    pathObstacleCheckQueue.add(curPoint);
                }
            }
            if (pathObstacleCheckQueue.size() > 1) {
                pathObstacleCheckQueue.clear();
                LOG.error("{} 雷达误报警检测 点: {}", name, curPoint);
                processModel.setObstacleFlag(false);
                addPath(radarDis(0, 35, 10));
                addPath(radarDis(0, 35, 10));
            }*/

            //检查新订单, 分割订单必须已经发送完成，否则会漏过中途升降动作
            if (sendSplitDriveOrder == null && commAdapter.getcurrentDriveOrder() != null && commAdapter.getcurrentDriveOrder() != processDriverOrder) {
                processDriverOrder = commAdapter.getcurrentDriveOrder();
                List<DriveOrder> driveOrderList = DoorController.splitDriverOrder(processDriverOrder);
                driveOrderQueue.clear();
                driveOrderQueue = new LinkedBlockingQueue<>(driveOrderList);
                LOG.info("{} get new driverOrder: {}", name, processDriverOrder.getDestination());
                if (DoorController.checkPassDoor(processDriverOrder).size() > 0) {
                    List<Point> open;
                    List<Point> close;
                    List<List> list = checkNewOrder(processDriverOrder);
                    //TODO 目标点在自动门处，获取关门点会报错
                    open = list.get(0);
                    close = list.get(1);
                    waitPoint = list.get(2);
                    //设置状态车辆内核状态
                    // 添加开门关门点
                    doorTruck.cleanDoor();
                    doorTruck.addDoorList(open, close);
                    LOG.info("{} new order door point open: {}， wait point: {}, close: {}",name, open, waitPoint, close);
                }
                processModel.setVehicleState(Vehicle.State.EXECUTING);

            }

            //检查要发送的分割路径和车辆状态, 条件只执行一次，不管路径发送失败
            if (sendSplitDriveOrder == null && driveOrderQueue.size() > 0 &&
                    pathExecQueue.size() == 0 && processModel.getVehiclePathState() == 0) {
                //准备第一条分割路径
                sendSplitDriveOrder = driveOrderQueue.peek();
                if (sendSplitDriveOrder.getRoute().getFinalDestinationPoint().getName().equals(curPoint)) {
                    //原地升降
                    LOG.info("原地升降");
                    driveOrderQueue.poll();
                    processModel.commandExecuted(commAdapter.getSentQueue().poll());

                    String opration = processDriverOrder.getDestination().getOperation();
                    byte[] path = configRoute.getliftAction(curPoint, opration);
                    addPath(path);
                    pathExecQueue.add(path);
                    //取出升降指令执行
                    //TODO 多执行一个指令会导致车辆状态无法从执行变为闲置，可能是系统BUG
//                        processModel.commandExecuted(commAdapter.getSentQueue().poll());
//                        if (opration.equals("LOAD")) {
//                            processModel.sendMsg("robot lifterat 50\n");
//                        } else if (opration.equals("UNLOAD")) {
//                            processModel.sendMsg("robot lifterat 0\n");
//                        }
                    // 延时置空分片订单
                    delay_timer.schedule(new DelayLift(), 5 * 1000);
                }
                //检查车辆当前位置是否符合路径起点
                else if (sendSplitDriveOrder.getRoute().getSteps().get(0)
                        .getSourcePoint().getName().equals(curPoint)) {
                    boolean sendFlag = false;
                    //TODO 检查开门状态
                    if (waitPoint != null && isWaitPoint(curPoint) != null) {
                        LOG.debug("此点位需要开门: {}", curPoint);
                        //TODO
                        String doorName = isWaitPoint(curPoint);
                        DoorStatus doorStatus = checkDoor(curPoint);
                        if (doorStatus == null) {
                            sendSplitDriveOrder = null;
                            //无需开门
                        } else if (doorStatus.getError() < -10) {
                            //自动门离线

                            String roomName = getRoom(curPoint);
                            sendSplitDriveOrder = null;
                            processModel.setVehicleProperty("error", String.format("自动门故障: %s", roomName));
                            doorTruck.addRecheckOpenDoor(doorName);
//                            processModel.setVehicleState(Vehicle.State.ERROR);
                        } else if (doorStatus.getStatus().equals("opened")) {
//                            LOG.debug("门已经打开");
                            processModel.setVehicleProperty("error", "");
                            try {
                                //只执行一次，没有循环
                                recheck_close_door_timer.schedule(new ReCheckClose(getWaitPoint(curPoint)), 5000);
                                LOG.debug("添加定时检测关门任务,当前地点: {}, 自动门: {}", curPoint, doorName);
                            } catch (Exception e) {
                                LOG.error("{} 设置关门定时检查任务失败: {}", name, e.getMessage());
                            }
                            sendFlag = true;
                        } else if (doorStatus.getStatus().equals("opening")) {
                            sendSplitDriveOrder = null;
//                            LOG.debug("正在开门");
//                           sendFlag = true;
                        } else if (doorStatus.getStatus().equals("closed")) {
                            sendSplitDriveOrder = null;
                            //删除提前开门点，因为此时门处于关闭状态就表示小车过了提前开门的点位，不删除会影响关闭动作。
                            if (doorTruck.peekDoor().getProperty("door").equals(doorName) && doorTruck.peekDoor().getProperty("action").equals("open")) {
                                LOG.error("delete open door: {} point: {}", doorName, doorTruck.peekDoor());
                                doorTruck.pollDoor();
                            }
                            doorTruck.addRecheckOpenDoor(doorName);
                        } else {
                            sendSplitDriveOrder = null;
//                           processModel.setVehicleProperty("error", "door can't open");
                        }
                        LOG.debug("当前自动门状态: {}", doorStatus.toString());
                    } else {
                        sendFlag = true;
                    }

                    if (sendFlag) {
                        LOG.debug("准备发送路径");
                        byte[] path = configRoute.getPath(sendSplitDriveOrder, processModel.getVehicleOrientationAngle());
                        LOG.debug("获取路径成功");
                        addPath(path);
                        pathExecQueue.add(path);
                        driveOrderQueue.poll();
                        LOG.info("{} send split path: {}", name, configRoute.getDebugPath());
                    }
                } else {
                    LOG.error("{} 车辆不在要发送路径的起点: {} {}", name, sendSplitDriveOrder, curPoint);
                }
            }

            //到达分割路径终点, 升降动作需要单独判断
            if (sendSplitDriveOrder != null &&
                    sendSplitDriveOrder.getRoute().getFinalDestinationPoint().getName().equals(curPoint)
                    && processModel.getVehiclePathState() == 0
            ) {
//                sendSplitDriveOrder = driveOrderQueue.poll();
//                if (sendSplitDriveOrder.getDestination().getOperation().equals("NOP")) {

//                }
                //完成所有路径
                if (processDriverOrder != null && driveOrderQueue.peek() == null) {
                    waitPoint = null;
//                    String opration = processDriverOrder.getDestination().getOperation();
                    /*if (opration.equals("LOAD") || opration.equals("UNLOAD") || opration.equals("CHARGE")) {
                        //无升降动作的话为 MOVE
                        byte[] path = configRoute.getliftAction(curPoint, opration);
                        addPath(path);
                        pathExecQueue.add(path);
                        //取出升降指令执行
                        //TODO 多执行一个指令会导致车辆状态无法从执行变为闲置，可能是系统BUG
//                        processModel.commandExecuted(commAdapter.getSentQueue().poll());
//                        if (opration.equals("LOAD")) {
//                            processModel.sendMsg("robot lifterat 50\n");
//                        } else if (opration.equals("UNLOAD")) {
//                            processModel.sendMsg("robot lifterat 0\n");
//                        }
                        // 延时置空分片订单
                        delay_timer.schedule(new DelayLift(), 5 * 1000);

                    } else {
                        sendSplitDriveOrder = null;
                    }*/
                    sendSplitDriveOrder = null;
                    //TODO 升降指令也作为一条路径;
                    if (commAdapter.getcurrentDriveOrder().equals(processDriverOrder)) {
                        commAdapter.setcurrentDriveOrder(null);
                    }
                    processDriverOrder = null;
                } else {
                    sendSplitDriveOrder = null;
                }
            }


        } catch (Exception e) {
            LOG.error("ERROR: {}", e.getStackTrace());
        }

    }

    public enum pathState {
        IDLE,
        EXEC,
        FINISH,
        PAUSE,
        GOAL;
    }
}
