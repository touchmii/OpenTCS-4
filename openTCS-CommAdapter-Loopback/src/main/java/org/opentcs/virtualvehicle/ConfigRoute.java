package org.opentcs.virtualvehicle;

import org.opentcs.data.model.Point;
import org.opentcs.data.order.DriveOrder;
import org.opentcs.data.order.Route;

import java.util.ArrayList;
import java.util.List;

public class ConfigRoute {

    private DriveOrder driveorder;

    private Route route;

    private List<Route.Step> steps;

    public int init = 0;

//    public ConfigRoute() {
//
//    }
    public void setRoute(DriveOrder dv) {
        driveorder = dv;
        route = driveorder.getRoute();
        steps = route.getSteps();
        init = 1;
    }
    public int[] getPath() {
        int[] path = new int[steps.size()*4+3];
        switch (driveorder.getDestination().getOperation()) {
            case "MOVE":
                path[1] = 0;
                break;
            case "LOAD":
                path[1] = 1;
                break;
            case "UNLOAD":
                path[1] = 2;
                break;
            case "CHARGE":
                path[1] = 3;
                break;
            default:
                break;
        }
        path[2] = steps.size();
        for(int i = 0; i < steps.size(); i++) {
            Route.Step point = steps.get(i);
            path[i*4+3] = Integer.parseInt(new String(point.getDestinationPoint().getName()));
            path[i*4+4] = (int)point.getDestinationPoint().getPosition().getX();
            path[i*4+5] = (int)point.getDestinationPoint().getPosition().getY();
            path[i*4+6] = 0;
        }

        return path;
    }
//    public
}
