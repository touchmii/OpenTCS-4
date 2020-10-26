package com.lvsrobot.vehiclejbh;

import org.opentcs.data.model.Triple;
import org.opentcs.data.order.DriveOrder;
import org.opentcs.data.order.Route;

import java.util.List;

public class ConfigRoute {

    private DriveOrder driveorder;

    private Route route;

    private List<Route.Step> steps;

    public int init = 0;

    //坐标点比例
    private int point_mutiple = 100;

    public double angle = 0;
    public int convertPoint(String _point_name) {
        if (_point_name.indexOf("Point") == 0 ) {
            return Integer.parseInt(_point_name.replace("Point-", ""));
        } else {
            return Integer.parseInt(_point_name);
        }
    }

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

    public static  byte[] toByte(int[] source){//参数为要被转换的int[]的引用
        StringBuilder sb=new StringBuilder();
        for (int j : source) {
            sb.append(Integer.toHexString(j));
        }
        return sb.toString().getBytes();//返回为转换后的byte[]

    }



    public int[] getPath() {
        int[] path = new int[steps.size()*4+7];
        switch (driveorder.getDestination().getOperation()) {
            case "MOVE":
                path[1] = 0;
                break;
            case "LOAD":
                path[1] = 1;
//                path[path.length-5] = path[path.length-1];
                break;
            case "UNLOAD":
                path[1] = 2;
                break;
            case "CHARGING":
                path[1] = 3;
                break;
            default:
                break;
        }
        path[0] = 0xFFFF;
        path[2] = steps.size()+1;
        path[3] = convertPoint(steps.get(0).getSourcePoint().getName());
        path[4] = (int)steps.get(0).getSourcePoint().getPosition().getX()/point_mutiple;
        path[5] = (int)steps.get(0).getSourcePoint().getPosition().getY()/point_mutiple;
        path[6] = 365;
        for(int i = 1; i < steps.size(); i++) {
            //判断是否需要转弯至少需要三个点
            Triple point0 = steps.get(i - 1).getSourcePoint().getPosition();
            Triple point1 = steps.get(i).getSourcePoint().getPosition();
            Triple point2 = steps.get(i).getDestinationPoint().getPosition();
            int td = 200;
            int td2 = -200;
            int A1 = (int) (point1.getX() - point0.getX());
            int A2 = (int) (point1.getY() - point0.getY());
            int B1 = (int) (point2.getX() - point1.getX());
            int B2 = (int) (point2.getY() - point1.getY());
            path[3 + 4 * i] = convertPoint(steps.get(i).getSourcePoint().getName());
            path[4 + 4 * i] = (int) point1.getX() / point_mutiple;
            path[5 + 4 * i] = (int) point1.getY() / point_mutiple;
            if ((Math.abs(A1) < td && A2 > td && B1 > td && Math.abs(B2) < td) || (Math.abs(A1) < td && A2 < td2 && B1 < td2 && Math.abs(B2) < td)
                    || (A1 > td && Math.abs(A2) < td && Math.abs(B1) < td && B2 < td2) || (A1 > td && Math.abs(A2) < td && Math.abs(B1) < td && B2 > td)) {
                //右转 "3"
                path[6 + 4 * i] = 3;
//                path[5+2*i] = 49;
            } else if ((A1 < td && A1 > td2 && B1 < td && B1 > td2) || (A2 < td && A2 > td2 && B2 < td && B2 > td2)) {
                //直行 "0"
                //path[4+2*(i-1)] = 0
                path[6 + 4 * i] = 0;
//                path[5+2*i] = 49;
            } else {
                //左转 "2"
                path[6 + 4 * i] = 2;
//                path[5+2*i] = 49;
            }

        }

//        int steps_size = steps.size();
//        Triple dest_point = steps.get(steps_size).getDestinationPoint().getPosition();
//        path[steps.size()*4+3] = convertPoint(steps.get(steps_size).getDestinationPoint().getName());
//        path[steps.size()*4+4] = (int)dest_point.getX()/point_mutiple;
//        path[steps.size()*4+5] = (int)dest_point.getY()/point_mutiple;
//        path[steps.size()*4+6] = 0;

        return path;
    }







//        适配器控制的 小车左右向前移动，完全依靠opentcs进行小车的通信。

        //暂时车辆第一步需要往前走
//        path[4] = 48;
//        path[5] = 49;
//
//        for(int i = 1; i < steps.size(); i++) {
//            //判断是否需要转弯至少需要三个点
//            Triple point0 = steps.get(i-1).getSourcePoint().getPosition();
//            Triple point1 = steps.get(i).getSourcePoint().getPosition();
//            Triple point2 = steps.get(i).getDestinationPoint().getPosition();
//            int td = 200;
//            int td2 = -200;
//            int A1 = (int)(point1.getX()-point0.getX());
//            int A2 = (int)(point1.getY()-point0.getY());
//            int B1 = (int)(point2.getX()-point1.getX());
//            int B2 = (int)(point2.getY()-point1.getY());
//            int turn_time = 1;
//            if((Math.abs(A1) < td && A2 > td && B1 > td && Math.abs(B2) < td ) || (Math.abs(A1) < td && A2 < td2 && B1 < td2 && Math.abs(B2) < td)
//            || (A1 > td && Math.abs(A2) < td && Math.abs(B1) < td && B2 < td2) || (A1 > td && Math.abs(A2) < td && Math.abs(B1) < td && B2 > td) ) {
//                //右转 "3"
//                turn_time++;
//                path[4+2*i] = 51;
//                path[5+2*i] = 49;
//            } else if((A1 < td && A1 > td2 && B1 < td && B1 > td2) || (A2 < td && A2 > td2 && B2 < td && B2 > td2)) {
//                //直行 "0"
//                //path[4+2*(i-1)] = 0
//                path[4+2*i] = 48;
//                path[5+2*i] = 49;
//            } else {
//                //左转 "2"
//                turn_time++;
//                path[4+2*i] = 50;
//                path[5+2*i] = 49;
//            }
//
//        }
//        return path;
//    }
//从上面开始，到这里结束
}
