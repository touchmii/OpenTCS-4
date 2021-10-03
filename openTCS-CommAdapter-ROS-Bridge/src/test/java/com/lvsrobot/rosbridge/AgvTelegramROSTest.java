package com.lvsrobot.rosbridge;

import com.lvsrobot.rosbridge.AgvTelegramROS;
import com.lvsrobot.rosbridge.msg.WaypointList;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.opentcs.data.model.Path;
import org.opentcs.data.model.Point;
import org.opentcs.data.model.Triple;
import org.opentcs.data.model.Vehicle;
import org.opentcs.data.order.DriveOrder;
import org.opentcs.data.order.Route;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class AgvTelegramROSTest {

    public static List<Point> pointList;
    public AgvTelegramROS agvTelegramROS;

    public static DriveOrder newDriverOrder(List<Point> pointList) {
        List<Route.Step> stepsList = new ArrayList<>();
        Point destPoint = pointList.get(pointList.size()-1);
        for (int i = 0; i < pointList.size()-1; i++) {
//            Point pointa = new Point("Point"+String.valueOf(i)).withPosition(tripleList.get(i));
//            Point pointb = new Point("Point"+String.valueOf(i+1)).withPosition(tripleList.get(i+1));
            stepsList.add(new Route.Step(new Path("Step"+String.valueOf(i), pointList.get(i).getReference(), pointList.get(i+1).getReference()), pointList.get(i), pointList.get(i+1), Vehicle.Orientation.FORWARD, i));
        }
        Route route = new Route(stepsList, stepsList.size());
        DriveOrder driveOrder = new DriveOrder(new DriveOrder.Destination(destPoint.getReference())).withRoute(route);
        return driveOrder;
    }

    @BeforeAll
    public static void newDriverOrder() {
        pointList = new ArrayList<>();
//        for (int i=0; i < 5; i++) {
//            pointList.add(new Point("Point"+String.valueOf(i)).withPosition(new Triple(1000*i, 0, 0)));
//        }
        pointList.add(new Point("Point"+String.valueOf(3)).withPosition(new Triple(1000*5, 0, 0))
//                .withProperty("dis", "0")
        );
        pointList.add(new Point("Point"+String.valueOf(4)).withPosition(new Triple(1000*5, 0, 0))
                .withProperty("dis", "0")
        );
        pointList.add(new Point("Point"+String.valueOf(5)).withPosition(new Triple(1000*5, 0, 0)).withProperty("door", "JN2"));
        pointList.add(new Point("Point"+String.valueOf(6)).withPosition(new Triple(1000*5, 0, 0)).withProperty("door", "JN2"));
        pointList.add(new Point("Point"+String.valueOf(7)).withPosition(new Triple(1000*5, 0, 0)));
        pointList.add(new Point("Point"+String.valueOf(8)).withPosition(new Triple(1000*5, 0, 0)).withProperty("dis", "0"));
//        for (int i=7; i < 9; i++) {
//            pointList.add(new Point("Point"+String.valueOf(i)).withPosition(new Triple(1000*i, 0, 0)));
//        }
        pointList.add(new Point("Point"+String.valueOf(9)).withPosition(new Triple(1000*5, 0, 0)).withProperty("door", "JN1"));
        pointList.add(new Point("Point"+String.valueOf(10)).withPosition(new Triple(1000*5, 0, 0)).withProperty("door", "JN1"));
        pointList.add(new Point("Point"+String.valueOf(11)).withPosition(new Triple(1000*5, 0, 0)));
//        for (int i=11; i < 15; i++) {
//            pointList.add(new Point("Point"+String.valueOf(i)).withPosition(new Triple(1000*i, 0, 0)));
//        }


//        DriveOrder driveOrder = newDriverOrder(pointList);
    }

    @Test
    public void sendWaypoint() {
        agvTelegramROS = new AgvTelegramROS("10.211.55.7", 9090, mock(ROSBridgeProcessModel.class));
//        ConfigRoute configRoute = new ConfigRoute();
        DriveOrder driveOrder = newDriverOrder(pointList);
//        configRoute.setRoute(driveOrder);
        WaypointList waypointList = agvTelegramROS.getWaypointList(driveOrder);
        agvTelegramROS.sendWaypointList(waypointList);
        for (int i =0; i< 2000; i++) {
            try {
                TimeUnit.MILLISECONDS.sleep(500);
            } catch (Exception e) {}
            agvTelegramROS.sendWaypointList(waypointList);
        }
        try {
            TimeUnit.SECONDS.sleep(300);
        } catch (Exception e) {}
        assertEquals(1,1);
    }


}