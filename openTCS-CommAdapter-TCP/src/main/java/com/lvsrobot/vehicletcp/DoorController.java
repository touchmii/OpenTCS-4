package com.lvsrobot.vehicletcp;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import com.lvsrobot.vehicletcp.binding.DoorStatus;
import org.opentcs.data.model.Point;
import org.opentcs.data.order.DriveOrder;
import org.opentcs.data.order.Route;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.netty.ByteBufFlux;
import reactor.netty.http.client.HttpClient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.stream.Collectors;

public class DoorController {
    private static final Logger LOG = LoggerFactory.getLogger(DoorController.class);

    /*{1: ['包衣'         ,'192.168.10.150'],
        2: ['中间站(批混)'  ,'192.168.10.151' ],
        3: ['中间站(压片)' ,'192.168.10.152' ],
        4: ['预留(批混)'  ,'192.168.10.153' ],
        5: ['批料代发'    ,'192.168.10.154' ],
        6: ['容器存放'    ,'192.168.10.155' ],
        7: ['容器清洗'    ,'192.168.10.156' ],
        8: ['铝塑包装'    ,'192.168.10.157' ],
        9: ['批混'        ,'192.168.10.158' ],
        10: ['干法制粒'    ,'192.168.10.159' ],
        11: ['压片1','192.168.10.160' ],
        12: ['压片2','192.168.10.161' ],
        13: ['胶囊填充2','192.168.10.162' ],
        14: ['制粒','192.168.10.163' ],
        15: ['制粒缓冲','192.168.10.164' ],
        16: ['胶囊填充1','192.168.10.165' ]*/

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
        put("JN1" , "192.168.10.165");
         }};

    public static DoorStatus doorAction(String doorName, DOORACTION action) {
        String host_ = doorMap.get(doorName);
        String action_;
        switch(action) {
            case STATUS:
                action_ = "status";
                break;
            case OPEN:
                action_ = "open";
                break;
            case CLOSE:
                action_ = "close";
                break;
            default:
                action_ = "status";
        }
        String rec = HttpClient.create()             // Prepares an HTTP client ready for configuration
            .headers(h -> h.set("Authorization", "Basic bG9naW46UGFzc3cwcmQ="))
            .port(80)  // Obtains the server's port and provides it as a port to which this
            .host(host_)   // client should connect
            .get()               // Specifies that POST method will be used
            .uri("/door.lc?action="+action_)   // Specifies the path
        //        .send(ByteBufFlux.fromString(Flux.just("Hello")))  // Sends the request body
            .responseContent()   // Receives the response body
            .aggregate()
            .asString()
//            .log("http-client")
            .block();
//        ObjectMapper objectMapper = new ObjectMapper();
//        JsonNode jsonNode = objectMapper.readTree(str);
        DoorStatus doorStatus;
        ObjectMapper mapper = new ObjectMapper();
        try {
            doorStatus = mapper.readValue(rec, DoorStatus.class);//readValue到一个实体类中.
        } catch (Exception e) {
            System.out.println(e.getMessage());
            doorStatus = new DoorStatus();
        }
        return doorStatus;
    }

    public static List<Route.Step> checkPassDoor(DriveOrder driveOrder) {
        List<Route.Step> doorList = driveOrder.getRoute().getSteps().stream()
                .filter(step -> null != step.getDestinationPoint().getProperty("door"))
        .collect(Collectors.toList());
        String door = null;
        List<Route.Step> newDoorList = new ArrayList<>(doorList);
        int removeStep = 0;
        for (int i =0; i < doorList.size()-1; i++) {
            if (doorList.get(i).getDestinationPoint().getProperty("door").equals(
                    doorList.get(i+1).getDestinationPoint().getProperty("door"))) {
                 newDoorList.remove(i+1-removeStep);
                 removeStep ++;
            }
        }
        return newDoorList;
    }

    public static List<Point> getOpenDoor(DriveOrder driveOrder) {
        List<Route.Step> doorList = checkPassDoor(driveOrder);
        List<Point> openDoorList = new ArrayList<>();
        List<Route.Step> stepList = driveOrder.getRoute().getSteps();
        for (Route.Step doorStep : doorList) {
            int index = driveOrder.getRoute().getSteps().indexOf(doorStep);
//            if (doorList.size() - doorList.indexOf(doorStep) > 0) {
//                int nextdoorIndex = stepList.indexOf(doorList.get(doorList.indexOf(doorStep)+1));
//                if (stepList.indexOf(doorList.get(nextdoorIndex)) - index < 4) {
//
//                }
//            }
            String p = null;
            if (index < 1) {
                index = 1;
            } else {

                p = doorStep.getSourcePoint().getProperty("dis");
            }
            if (p != null && p.equals("0")) {
                openDoorList.add(doorStep.getSourcePoint());
            } else {
                openDoorList.add(driveOrder.getRoute().getSteps().get(index - 1).getSourcePoint());
            }
        }
        return openDoorList;
    }
    public static List<Point> getCloseDoor(DriveOrder driveOrder) {
        List<Route.Step> doorList = checkPassDoor(driveOrder);
        List<Point> closeDoorList = new ArrayList<>();
        for (Route.Step doorStep : doorList) {
            int index = driveOrder.getRoute().getSteps().indexOf(doorStep);
            if ((driveOrder.getRoute().getSteps().size() - 1) > 0) {

                if (driveOrder.getRoute().getSteps().get(index + 1).getDestinationPoint().getProperty("door") != null) {
                    closeDoorList.add(driveOrder.getRoute().getSteps().get(index + 2).getDestinationPoint());
                } else {
                    closeDoorList.add(driveOrder.getRoute().getSteps().get(index + 1).getDestinationPoint());
                }
            }
        }
        return closeDoorList;
    }

    public static List<DriveOrder> splitDriverOrder(DriveOrder driveOrder) {
        List<Route.Step> doorStepList = checkPassDoor(driveOrder);
        List<Route.Step> stepList = driveOrder.getRoute().getSteps();
        List<DriveOrder> driveOrderList = new ArrayList<>();
        if (doorStepList.size() > 0 && stepList.indexOf(doorStepList.get(0)) > 0) {
//            for (int i = 0; i < doorStepList.size(); i ++) {
//                stepList.subList(i, stepList.indexOf(doorStepList.get(i)));
                List<Route.Step> Steps_ = new ArrayList<>();
                int index = 0;
                Route.Step step_ = doorStepList.get(index);
                for (Route.Step step : stepList) {
                    if (! step.equals(step_)) {
                        Steps_.add(step);
                        if (step.equals(stepList.get(stepList.size()-1))) {
                            Route route =  new Route(Steps_, Steps_.size());
                            driveOrderList.add(
                                    new DriveOrder(new DriveOrder.Destination(Steps_.get(Steps_.size()-1).getDestinationPoint().getReference())).withRoute(route)
                            );
                        }
                    } else {
//                        Route.Step removeStep = Steps_.get(Steps_.size()-1);
//                        Steps_.remove(removeStep);
                        if (Steps_.size() > 0) {
//                            String openDoor = null;
//                            try {
//                                openDoor = step.getDestinationPoint().getProperty("door");
//                            } catch (Exception e) {
//                                LOG.error("set path open door error: {}", e.getMessage());
//                            }
                            Route route =  new Route(Steps_, Steps_.size());
                            driveOrderList.add(new DriveOrder(new DriveOrder.Destination(Steps_.get(Steps_.size()-1).getDestinationPoint().getReference())).withRoute(route));
                            Steps_ = new ArrayList<>();
    //                        Steps_.add(removeStep);
                            Steps_.add(step);
                            if (index < doorStepList.size()-1) {
                                index ++;
                                step_ = doorStepList.get(index);
                            }
                        }
                    }
                }
//            }
        } else {
            driveOrderList.add(driveOrder);
        }
        return driveOrderList;
    }

    public enum DOORACTION {
        STATUS,
        OPEN,
        CLOSE,
        TOGGLE,
        CLICK,
        REBOOT;
    }
}
