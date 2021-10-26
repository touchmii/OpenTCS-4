package com.lvsrobot.vehicletcp;

import org.junit.jupiter.api.Test;
import org.opentcs.data.model.Path;
import org.opentcs.data.model.Point;
import org.opentcs.data.model.Triple;
import org.opentcs.data.model.Vehicle;
import org.opentcs.data.order.DriveOrder;
import org.opentcs.data.order.Route;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.mock;

class TCPCommAdapterTest {

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
    void resumePathTest() {
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

        TCPCommAdapter commAdapter = mock(TCPCommAdapter.class);
        commAdapter.setcurrentDriveOrder(driveOrder);
//        commAdapter.getProcessModel().setVehiclePosition("Point6");
        try {
//            commAdapter..resumePath(false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}