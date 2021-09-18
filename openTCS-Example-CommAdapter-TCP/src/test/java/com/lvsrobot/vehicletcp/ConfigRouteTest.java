package com.lvsrobot.vehicletcp;

import org.junit.Before;
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
    void getDebugPath() {

    }
}