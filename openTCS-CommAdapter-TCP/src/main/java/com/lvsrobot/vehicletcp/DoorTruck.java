package com.lvsrobot.vehicletcp;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lvsrobot.vehicletcp.binding.DoorStatus;
import org.opentcs.data.model.Point;
import org.opentcs.util.CyclicTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.netty.http.client.HttpClient;

import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;

public class DoorTruck extends CyclicTask {
    private static final Logger LOG = LoggerFactory.getLogger(DoorTruck.class);
    private TCPProcessModel processModel;
    private Queue<Point> pointQueue = new LinkedBlockingQueue<>();
    private Map<String, DoorStatus> doorStatusMap = new HashMap<>();
    private String curPoint;
    private DoorController doorController;
    public static HttpClient httpClient = HttpClient.create();
    private String name;

    private Timer timer;
    //开门检查时钟
    private Timer open_timer;
    private long open_check_time = 10*1000;
    private long delay_check_time = 60*1000;
    private long cycle_check_time = 3*60*1000;

    public static String BY = "192.168.10.150";
    public static String ZJPH = "192.168.10.151";
    public static String ZJYP = "192.168.10.152";
    public static String YL = "192.168.10.153";
    public static String PLDF = "192.168.10.154";
    public static String RQCF = "192.168.10.155";
    public static String RQQX = "192.168.10.156";
    public static String LS = "192.168.10.157";
    public static String PH = "192.168.10.158";
    public static String GFZL = "192.168.10.159";
    public static String YP1 = "192.168.10.160";
    public static String YP2 = "192.168.10.161";
    public static String JN2 = "192.168.10.162";
    public static String SFZL2 = "192.168.10.163";
    public static String SFZL1 = "192.168.10.164";
    public static String JN1 = "192.168.10.165";
    public static Map< String , String > doorMap =
            new HashMap<String, String>(){{
                put("BY"   , "192.168.10.150");
                put("ZJPH" , "192.168.10.151");
                put("ZJYP" , "192.168.10.152");
                put("YL"   , "192.168.10.153");
                put("PLDF" , "192.168.10.154");
                put("RQCF" , "192.168.10.155");
                put("RQQX" , "192.168.10.156");
                put("LS"   , "192.168.10.157");
                put("PH"   , "192.168.10.158");
                put("GFZL" , "192.168.10.159");
                put("YP1"  , "192.168.10.160");
                put("YP2"  , "192.168.10.161");
                put("JN2"  , "192.168.10.162");
                put("SFZL1" , "192.168.10.164");
                put("SFZL2" , "192.168.10.163");
                put("JN1" , "192.168.10.165");
            }};

    public boolean doorAction (String doorName, String action) {
        String host_ = doorMap.get(doorName);
        String action_ = action;

        httpClient             // Prepares an HTTP client ready for configuration
                .headers(h -> h.set("Authorization", "Basic bG9naW46UGFzc3cwcmQ="))
                .port(80)  // Obtains the server's port and provides it as a port to which this
                .host(host_)   // client should connect
                .get()               // Specifies that POST method will be used
                .uri("/door.lc?action="+action_)   // Specifies the path
                //        .send(ByteBufFlux.fromString(Flux.just("Hello")))  // Sends the request body
//                .
                .responseContent()   // Receives the response body
                .aggregate()
                .asString()
//                .doOnSuccess()
                .doOnError(e -> {
                    setDoorStatus(doorName, "e.toString()");
//                    LOG.error("door: {} action error: {}", doorName, e.toString());
                })
                .subscribe(s -> {
                    setDoorStatus(doorName, s);
//                    LOG.info("{} recice door state {}", "xx", s);
                });
        return true;
//            .log("http-client")
//                .block();
//
//        return doorStatus;
    }

    public DoorTruck(TCPProcessModel processModel) {
        super(200);
        this.processModel = processModel;
        timer = new Timer();
        open_timer = new Timer();
        timer.schedule(new CheckDoor(), delay_check_time, cycle_check_time);
    }
    public void cleanDoor() {
        pointQueue.clear();
    }
    public void addOpenDoor(Point point, String name) {
        pointQueue.add(point.withProperty("door", name).withProperty("action", "open"));
    }
    public void addCloseDoor(Point point, String name) {
        pointQueue.add(point.withProperty("door", name).withProperty("action", "close"));
    }
    public void addDoorList(List<Point> pointList, List<Point> pointList2) {
        for(int i = 0; i< pointList.size(); i++) {
            pointQueue.add(pointList.get(i));
            pointQueue.add(pointList2.get(i));
        }
        LOG.info("door point: {}", pointQueue.toString());
    }

    public DoorStatus getDoorStatus(String name) {
        return doorStatusMap.get(name);
    }

    public void setDoorStatus(String name, String status) {
        DoorStatus doorStatus;
        ObjectMapper mapper = new ObjectMapper();
        try {
            doorStatus = mapper.readValue(status, DoorStatus.class);//readValue到一个实体类中.
            if (doorStatus.getAction().equals("open")) {
                open_timer.schedule(new CheckOpen(name), open_check_time);
            }
        } catch (Exception e) {
//            System.out.println(e.getMessage());
            doorStatus = new DoorStatus();
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
            doorAction(doorName, "status");
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

                if(pointQueue.peek() != null && pointQueue.peek().getName().equals(curPoint)) {
                    Point point = pointQueue.poll();
                    doorAction(point.getProperty("door"), point.getProperty("action"));
                    LOG.info("door: {} action: {} in point: {}", point.getProperty("door"), point.getProperty("action"), curPoint);

                }
            } catch (Exception e) {
                LOG.error("doorTruck error: {}", e.getStackTrace());
            }
    }
}
