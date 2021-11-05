package com.lvsrobot.vehicletcp;

import org.opentcs.data.model.Point;
import org.opentcs.data.order.DriveOrder;
import org.opentcs.data.order.Route;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class DoorController {
    private static final Logger LOG = LoggerFactory.getLogger(DoorController.class);

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
        String doorName;
        for (Route.Step doorStep : doorList) {
            int index = driveOrder.getRoute().getSteps().indexOf(doorStep);
            doorName = doorStep.getDestinationPoint().getProperty("door");
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
                openDoorList.add(doorStep.getSourcePoint().withProperty("door", doorName).withProperty("action", "open"));
            } else {
                openDoorList.add(driveOrder.getRoute().getSteps().get(index - 1).getSourcePoint().withProperty("door", doorName).withProperty("action", "open"));
            }
        }
        return openDoorList;
    }
    public static List<Point> getCloseDoor(DriveOrder driveOrder) {
        List<Route.Step> doorList = checkPassDoor(driveOrder);
        List<Point> closeDoorList = new ArrayList<>();
        String doorName;
        for (Route.Step doorStep : doorList) {
            int index = driveOrder.getRoute().getSteps().indexOf(doorStep);
            doorName = doorStep.getDestinationPoint().getProperty("door");
            if ((driveOrder.getRoute().getSteps().size() - 1) > 0) {

                if (driveOrder.getRoute().getSteps().get(index + 1).getDestinationPoint().getProperty("door") != null) {
                    closeDoorList.add(driveOrder.getRoute().getSteps().get(index + 2).getDestinationPoint().withProperty("door", doorName).withProperty("action", "close"));
                } else {
                    closeDoorList.add(driveOrder.getRoute().getSteps().get(index + 1).getDestinationPoint().withProperty("door", doorName).withProperty("action", "close"));
                }
            }
        }
        return closeDoorList;
    }

    public static List<DriveOrder> splitDriverOrder(DriveOrder driveOrder) {
        List<Route.Step> doorStepList = checkPassDoor(driveOrder);
        List<Route.Step> stepList = driveOrder.getRoute().getSteps();
        List<DriveOrder> driveOrderList = new ArrayList<>();
        if ( doorStepList.size() > 0 && stepList.indexOf(doorStepList.get(0)) == 0) {
            //开门点在第一步需要去除，否则一要影响路径分割
            doorStepList.remove(0);
        }
        if (doorStepList.size() > 0 ) {
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
            if (driveOrder.getRoute().getSteps().size() > 49) {
                driveOrderList = halfOrder(driveOrder);
            } else {
                driveOrderList.add(driveOrder);
            }
        }
        //把举升充电分解成单独的路径,只包含动作的订单不处理
//        if (driveOrder.getRoute().getSteps().size() > 1 && !driveOrder.getDestination().getOperation().equals("MOVE")) {
//            Point point = driveOrder.getRoute().getFinalDestinationPoint();
//            List<Route.Step> steps = new ArrayList<>();
//            steps.add(new Route.Step(new Path("none", point.getReference(), point.getReference()), point, Vehicle.Orientation.FORWARD, 0));
//            Route route = new Route(steps, 1);
//            driveOrderList.add( new DriveOrder(new DriveOrder.Destination(point.getReference()).withOperation(driveOrder.getDestination().getOperation())).withRoute(route));
//
//        }
        return driveOrderList;
    }

    public static List<DriveOrder> halfOrder(DriveOrder driveOrder) {
        List<DriveOrder> driveOrderList = new ArrayList<>();
        if (driveOrder.getRoute().getSteps().size() > 49) {
            List<Route.Step> stepListx = driveOrder.getRoute().getSteps();
            Route.Step splitStep = stepListx.get(49);
            List<Route.Step> steps1 = new ArrayList<>();
            List<Route.Step> steps2 = new ArrayList<>();
            boolean flag = false;
            for (Route.Step step : stepListx) {
                if (!step.equals(splitStep) && !flag) {
                    steps1.add(step);
                } else {
                    flag = true;
                    steps2.add(step);
                }
            }
            Route route1 =  new Route(steps1, steps1.size());
            Route route2 =  new Route(steps2, steps2.size());
            driveOrderList.add(new DriveOrder(new DriveOrder.Destination(steps1.get(steps1.size()-1).getDestinationPoint().getReference())).withRoute(route1));
            driveOrderList.add(new DriveOrder(new DriveOrder.Destination(steps2.get(steps2.size()-1).getDestinationPoint().getReference())).withRoute(route2));

        }
        return driveOrderList;
    }
}
