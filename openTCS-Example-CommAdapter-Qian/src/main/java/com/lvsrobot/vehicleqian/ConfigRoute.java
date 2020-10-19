package com.lvsrobot.vehicleqian;

import org.opentcs.data.model.Triple;
import org.opentcs.data.order.DriveOrder;
import org.opentcs.data.order.Route;

import java.util.List;

public class ConfigRoute {

    private DriveOrder driveorder;

    private Route route;

    private List<Route.Step> steps;

    public int init = 0;

    public double angle = 0;

    //    public ConfigRoute() {
//
//    }
    public void setRoute(DriveOrder dv) {
        driveorder = dv;
        route = driveorder.getRoute();
//        driveorder.getRoute().getSteps().get(0).getPath().getLength()
        steps = route.getSteps();
        init = 1;
    }
    public void setAngle(double _angle) {
        angle = _angle;
    }
    public int[] getPath() {
        int[] path = new int[steps.size()*2+4];
//        switch (driveorder.getDestination().getOperation()) {
//            case "MOVE":
//                path[1] = 0;
//                break;
//            case "LOAD":
//                path[1] = 1;
////                path[path.length-5] = path[path.length-1];
//                break;
//            case "UNLOAD":
//                path[1] = 2;
//                break;
//            case "CHARGING":
//                path[1] = 3;
//                break;
//            default:
//                break;
//        }
//        path[2] = steps.size()+1;
//        path[3] = Integer.parseInt(new String(current_point.getName()));
//        path[3] = Integer.parseInt(new String(steps.get(0).getSourcePoint().getName()));
        //起点
        path[0] = (int)steps.get(0).getSourcePoint().getPosition().getX()/100;
        path[1] = (int)steps.get(0).getSourcePoint().getPosition().getY()/100;
        //终点
        path[2] = (int)steps.get(steps.size()-1).getSourcePoint().getPosition().getX()/100;
        path[3] = (int)steps.get(steps.size()-1).getSourcePoint().getPosition().getY()/100;

        //暂时车辆第一步需要往前走
        path[4] = 0;
        path[5] = 1;

        for(int i = 1; i < steps.size(); i++) {
            //判断是否需要转弯至少需要三个点
            Triple point0 = steps.get(i-1).getSourcePoint().getPosition();
            Triple point1 = steps.get(i).getSourcePoint().getPosition();
            Triple point2 = steps.get(i).getDestinationPoint().getPosition();
            int td = 200;
            int td2 = -200;
            int A1 = (int)(point1.getX()-point0.getX());
            int A2 = (int)(point1.getY()-point0.getY());
            int B1 = (int)(point2.getX()-point1.getX());
            int B2 = (int)(point2.getY()-point1.getY());
            int turn_time = 1;
            if((Math.abs(A1) < td && A2 > td && B1 > td && Math.abs(B2) < td ) || (Math.abs(A1) < td && A2 < td2 && B1 < td2 && Math.abs(B2) < td)
            || (A1 > td && Math.abs(A2) < td && Math.abs(B1) < td && B2 < td2) || (A1 > td && Math.abs(A2) < td && Math.abs(B1) < td && B2 > td) ) {
                //右转 "3"
                turn_time++;
                path[4+2*i] = 3;
                path[5+2*i] = 1;
            } else if((A1 < td && A1 > td2 && B1 < td && B1 > td2) || (A2 < td && A2 > td2 && B2 < td && B2 > td2)) {
                //直行 "0"
                //path[4+2*(i-1)] = 0
                path[4+2*i] = 0;
                path[5+2*i] = 1;
            } else {
                //左转 "2"
                turn_time++;
                path[4+2*i] = 2;
                path[5+2*i] = 1;
            }

        }
//        int[] path2;
//        int dir = 0;
//        for(int i=0 , (len(path2)-4)/2, i++) {
//            if(path[4+i*2]!=0){
//                path2[]
//            }
//        }

        return path;
    }
//    public
}
