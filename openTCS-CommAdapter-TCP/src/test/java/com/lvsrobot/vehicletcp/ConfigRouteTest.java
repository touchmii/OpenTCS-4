package com.lvsrobot.vehicletcp;

import  org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.runners.MethodSorters;
import org.opentcs.data.model.Path;
import org.opentcs.data.model.Point;
import org.opentcs.data.model.Triple;
import org.opentcs.data.model.Vehicle;
import org.opentcs.data.order.DriveOrder;
import org.opentcs.data.order.Route;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class ConfigRouteTest {
    private static ConfigRoute configRoute = new ConfigRoute();
    private static DriveOrder driveOrder;
    private static DriveOrder driveOrder2;
    @BeforeAll
    public static void init() {

        Point dest = new Point("550").withPosition(new Triple(13000, -5000, 0));
        Point start = new Point("237").withPosition(new Triple(16000, -5500, 0));
        Point mid = new Point("543").withPosition(new Triple(14000, -5500, 0));

        Route.Step step1 = new Route.Step(new Path("1", start.getReference(), mid.getReference()), start, mid, Vehicle.Orientation.FORWARD, 0);
        Route.Step step2 = new Route.Step(new Path("2", mid.getReference(), dest.getReference()), mid, dest, Vehicle.Orientation.FORWARD, 1);
        List<Route.Step> steps = new ArrayList<>();
        steps.add(step1);
        steps.add(step2);
        Route route = new Route(steps, 2);
        driveOrder = new DriveOrder(new DriveOrder.Destination(dest.getReference())).withRoute(route);

        Point dest2 = new Point("550").withPosition(new Triple(14000, 6000, 0)).withProperty("back", "1");
        Point start2 = new Point("237").withPosition(new Triple(13000, 5000, 0));
        Point mid2 = new Point("543").withPosition(new Triple(14000, 5000, 0));

        Route.Step step12 = new Route.Step(new Path("1", start2.getReference(), mid2.getReference()), start2, mid2, Vehicle.Orientation.FORWARD, 0);
        Route.Step step22 = new Route.Step(new Path("2", mid2.getReference(), dest2.getReference()), mid2, dest2, Vehicle.Orientation.FORWARD, 1);
        List<Route.Step> steps2 = new ArrayList<>();
        steps2.add(step12);
        steps2.add(step22);
        Route route2 = new Route(steps2, 2);

        driveOrder2 = new DriveOrder(new DriveOrder.Destination(dest2.getReference())).withRoute(route2);
    }
        public DriveOrder newDriverOrder(List<Triple> tripleList) {
        List<Route.Step> stepsList = new ArrayList<>();
        Point destPoint = null;
        for (int i = 0; i < tripleList.size()-1; i++) {
            Point pointa = new Point("Point"+String.valueOf(i)).withPosition(tripleList.get(i));
            Point pointb = new Point("Point"+String.valueOf(i+1)).withPosition(tripleList.get(i+1));
            if (i == 3) {
                pointb.withProperty("door", "JN2");
            }
            stepsList.add(new Route.Step(new Path("Path"+String.valueOf(i), pointa.getReference(), pointb.getReference()), pointa, pointb, Vehicle.Orientation.FORWARD, i));
            if (i+2 == tripleList.size()) {
                destPoint = pointb;
            }
        }
        Route route = new Route(stepsList, stepsList.size());
        DriveOrder driveOrder = new DriveOrder(new DriveOrder.Destination(destPoint.getReference())).withRoute(route);
        return driveOrder;
    }
    public DriveOrder newDriverOrder2(List<Point> pointList) {
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




    @Test
    void getPath() {
        configRoute.setRoute(driveOrder);
        configRoute.setAngle(0);
        byte[] pathlist = {0, 1, 12, 0, 16, 0, 0, 1, 1, 0, 1, 4, 1, 0, 2, 0, 1, 0, 3, 0, 0, -26};
        assertEquals(pathlist.length, configRoute.getPath().length);
        assertEquals("robot path angle 0.0, 1LD->2->3", configRoute.getDebugPath());
    }
    @Test
    void getPath2() {
        configRoute.setRoute(driveOrder2);
        configRoute.setAngle(90);
        byte[] pathlist = {0, 1, 12, 0, 16, 0, 0, 1, 1, 0, 1, 4, 1, 0, 2, 0, 1, 0, 3, 0, 0, -26};
        assertEquals(pathlist.length, configRoute.getPath().length);
        assertEquals("robot path angle 0.0, 237->543BR->550", configRoute.getDebugPath());
    }

    @Test
    void newDriver() {
        List<Triple> tripleList = new ArrayList<>();
        for (int i=0; i < 5; i++) {
            tripleList.add(new Triple(1000*i, 0, 0));
        }
        DriveOrder driveOrder = newDriverOrder(tripleList);
        DoorController.checkPassDoor(driveOrder);

    }
    @Test
    void newDriver2() {
        List<Point> pointList = new ArrayList<>();
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


        DriveOrder driveOrder = newDriverOrder2(pointList);

        List<DriveOrder> driveOrderList = DoorController.splitDriverOrder(driveOrder);
        DoorController.checkPassDoor(driveOrder);

        List<Point> openDoor = DoorController.getOpenDoor(driveOrder);
        List<Point> closeDoor = DoorController.getCloseDoor(driveOrder);

        int a = 0;


    }

    @Test
    void getDebugPath() {

    }
}