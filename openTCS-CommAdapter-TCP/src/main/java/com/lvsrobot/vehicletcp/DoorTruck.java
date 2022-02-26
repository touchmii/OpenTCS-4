package com.lvsrobot.vehicletcp;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lvsrobot.vehicletcp.binding.DoorStatus;
import org.opentcs.data.model.Point;
import org.opentcs.util.CyclicTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;

public class DoorTruck extends CyclicTask {
    private static final Logger LOG = LoggerFactory.getLogger(DoorTruck.class);
    private TCPProcessModel processModel;
    private Queue<Point> pointQueue = new LinkedBlockingQueue<>();
    private Queue<String> recheckOpenQueue = new LinkedBlockingQueue<>();
    private Queue<String> recheckCloseQueue = new LinkedBlockingQueue<>();
    private Map<String, DoorStatus> doorStatusMap = new HashMap<>();
    private String curPoint;
    private DoorController doorController;
    private HttpClient httpClient = HttpClient.create();
    private String name;

    private Timer timer;
    //开门检查时钟
    private Timer open_timer;
    private long open_check_time = 2 * 1000;
    private long delay_check_time = 60 * 1000;
    private long cycle_check_time = 1 * 60 * 1000;

    public void setDoorMap(Map<String, String> doorMap) {
        this.doorMap = doorMap;
    }

    private Map<String, String> doorMap;

    public boolean doorAction(String doorName, String action) {
        String host_ = doorMap.get(doorName);
        String action_ = action;

        httpClient             // Prepares an HTTP client ready for configuration
                .headers(h -> h.set("Authorization", "Basic bG9naW46UGFzc3cwcmQ="))
                .port(80)  // Obtains the server's port and provides it as a port to which this
                .host(host_)   // client should connect
                .get()               // Specifies that POST method will be used
                .uri("/door.lc?action=" + action_)   // Specifies the path
                //        .send(ByteBufFlux.fromString(Flux.just("Hello")))  // Sends the request body
                .responseContent()   // Receives the response body
                .aggregate()
                .asString()
//                .doOnSuccess()
                //doOnError 不影响错误传播
                .doOnError(e -> {
                    setDoorStatus(doorName, "e.toString()", action);
//                    LOG.error("door: {} action error: {}", doorName, e.toString());
                })
                //使用onErrorReturn 或者 OnErrorResume处理错误
                .onErrorResume(e -> Mono.just("xx"))
                //设置重试次数，需要在subscribe前调用
                .retry(2)
                .subscribe(s -> {
                    setDoorStatus(doorName, s, action);
//                    LOG.info("{} receive door state {}", "xx", s);
                });
        return true;
    }

    public DoorTruck(TCPProcessModel processModel, String name) {
        super(1000);
        this.processModel = processModel;
        this.name = name;
        timer = new Timer();
        open_timer = new Timer();
        timer.schedule(new CheckDoor(), delay_check_time, cycle_check_time);
    }

    public Point peekDoor() { return pointQueue.peek(); }

    public void pollDoor() { pointQueue.poll(); }

    public void cleanDoor() {
        pointQueue.clear();
    }

    public void addOpenDoor(Point point, String name) {
        pointQueue.add(point.withProperty("door", name).withProperty("action", "open"));
    }

    public void addCloseDoor(Point point, String name) {
        pointQueue.add(point.withProperty("door", name).withProperty("action", "close"));
    }

    public void addRecheckOpenDoor(String name) {
        if (recheckOpenQueue.size() == 0) {
            recheckOpenQueue.add(name);
        }
    }
    public void addRecheckCloseDoor(String name) {
        if (recheckCloseQueue.size() == 0) {
            recheckCloseQueue.add(name);
        }
    }

    public void addDoorList(List<Point> pointList, List<Point> pointList2) {
        for (int i = 0; i < pointList.size(); i++) {
            pointQueue.add(pointList.get(i));
            pointQueue.add(pointList2.get(i));
        }
        LOG.debug("door point: {}", pointQueue.toString());
    }

    public DoorStatus getDoorStatus(String name) {
        return doorStatusMap.get(name);
    }

    public void setDoorStatus(String name, String status, String action) {
        DoorStatus doorStatus;
        ObjectMapper mapper = new ObjectMapper();
        try {
            doorStatus = mapper.readValue(status, DoorStatus.class);//readValue到一个实体类中.
            if (doorStatus.getAction().equals("open") && !doorStatus.getStatus().equals("opened")) {
                //不能在回调函数里面调用时钟
//                open_timer.schedule(new CheckOpen(name), open_check_time);
                addRecheckOpenDoor(name);
//                recheckOpenQueue.add(name);
            }
        } catch (Exception e) {
            if (!action.equals("status")) {
                LOG.debug("{} door: {} action: {} error", this.name, name, action);
            }
//            System.out.println(e.getMessage());
            /*if (action.equals("open")) {
                recheckOpenQueue.add(name);
            }*/
            doorStatus = new DoorStatus();
            /*if (status.contains("error")) {
                doorStatus.setError(0);
                if (status.contains("Door is already open") || status.contains("\"status\":\"open\"")) {
                    doorStatus.setStatus("opened");
                } else {
                    doorStatus.setStatus("closed");
                }
                if (status.contains("\"action\":\"open\"") && !status.contains("Door is already open")) {
                    addRecheckDoor(name);
                }
            }*/
        }
        doorStatusMap.put(name, doorStatus);
    }

    public class CheckDoor extends TimerTask {

        @Override
        public void run() {
            doorMap.forEach((key, value) -> {
                doorAction(key, "status");
            });
        }
    }

    public class CheckOpen extends TimerTask {
        private String doorName;

        public CheckOpen(String doorName) {
            this.doorName = doorName;
        }

        @Override
        public void run() {
            String door = recheckOpenQueue.poll();
            doorAction(doorName, "open");
            LOG.debug("recheck open door: {} queue: {}" ,doorName, door);
        }
    }

    public class CheckClose extends TimerTask {
        private String doorName;

        public CheckClose(String doorName) {
            this.doorName = doorName;
        }

        @Override
        public void run() {
            String door = recheckCloseQueue.poll();
            doorAction(doorName, "close");
            LOG.debug("recheck close door: {} queue: {}" ,doorName, door);
        }
    }

    @Override
    public void terminate() {
        httpClient = null;
        timer.cancel();
        super.terminate();
    }


    @Override
    protected void runActualTask() {
        curPoint = processModel.getVehiclePosition();
        try {

            if (pointQueue.peek() != null && pointQueue.peek().getName().equals(curPoint)) {
                Point point = pointQueue.poll();
                LOG.info("door: {} action: {} in point: {}", point.getProperty("door"), point.getProperty("action"), curPoint);
                doorAction(point.getProperty("door"), point.getProperty("action"));

            }
            if (recheckOpenQueue.size() > 0) {
                String doorName = recheckOpenQueue.peek();
//                    doorAction(doorName, "open");
//                LOG.info("recheck open door: {}", doorName);
                open_timer.schedule(new CheckOpen(doorName), 500);
            }
            if (recheckCloseQueue.size() > 0) {
                String doorName = recheckCloseQueue.peek();
                open_timer.schedule(new CheckClose(doorName), 1000);
            }

        } catch (Exception e) {
            LOG.error("doorTruck error: {}", e.getMessage());
        }
    }
}
