package com.lvsrobot.vehicletcp;

import org.opentcs.data.model.Triple;
import org.opentcs.data.order.DriveOrder;
import org.opentcs.data.order.Route;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class ConfigRoute {

    private DriveOrder driveorder;

    private Route route;

    private List<Route.Step> steps;

    public int init = 0;

    public double current_angle = 0;

    private static final Logger LOG = LoggerFactory.getLogger(ConfigRoute.class);

    private String debug_path = new String("robot path ");

    //    public ConfigRoute() {
//
//    }
    public void setRoute(DriveOrder dv) {
        driveorder = dv;
        route = driveorder.getRoute();
//        driveorder.getRoute().getSteps().get(0).getPath().getLength()
        steps = route.getSteps();
        init = 1;
        debug_path = "robot path";
    }
    public void setAngle(double _angle) {
        debug_path += String.format("angle %s ,", _angle);
        current_angle = _angle;
    }

    /***
     * 格式{id1,dir,id2,dir,id3,dir,action}
      * @return
     */
    public String getDebugPath() {
        return debug_path;
    }
    public byte[] getPath() {
        byte[] path = new byte[steps.size()*4+14];
//        String debug_path = new String("robot path ");
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
//        path[0] = (int)steps.get(0).getSourcePoint().getPosition().getX()/100;
//        path[1] = (int)steps.get(0).getSourcePoint().getPosition().getY()/100;
//        //终点
//        path[2] = (int)steps.get(steps.size()-1).getSourcePoint().getPosition().getX()/100;
//        path[3] = (int)steps.get(steps.size()-1).getSourcePoint().getPosition().getY()/100;

        //暂时车辆第一步需要往前走
        path[0] = 0x00;
        path[1] = 0x01;
        path[2] = 0x0c;
        //lenght
        path[3] = (byte)((steps.size()*4+8)/256);
        path[4] = (byte)((steps.size()*4+8)%256);
        path[5] = 0x00;
        path[6] = 0x00;
        path[7] = 0x01;
        path[8] = 0x01;
//        if (steps.size() > )
        int td = 800;
        int td2 = -800;
        Triple point0 = new Triple();
        Triple point1 = new Triple();
        Triple point2 = new Triple();
        int point1_id;
        String point1_id_str;
        int point2_id;
        String point2_id_str;
        int A1;
        int A2;
        int B1;
        int B2;
        for(int i = 0; i < steps.size(); i++) {
            //判断是否需要转弯至少需要三个点

            point1_id_str = steps.get(i).getSourcePoint().getName();
            point1_id = Integer.parseInt(point1_id_str);
            point1 = steps.get(i).getSourcePoint().getPosition();
            point2 = steps.get(i).getDestinationPoint().getPosition();
            if (i == 0) {
                if (current_angle == 0) {
                    // 设置一个左侧的点
                    point0 = new Triple(point1.getX()-2*td, point1.getY(), 0);
                } else if (current_angle == 90) {
                    // 设置一个下方的点
                    point0 = new Triple(point1.getX(), point1.getY()-2*td, 0);
                } else if (current_angle == 180) {
                    // 设置一个右侧的点
                    point0 = new Triple(point1.getX()+2*td, point1.getY(), 0);
                } else if (current_angle == 270) {
                    // 设置一个上方的点
                    point0 = new Triple(point1.getX(), point1.getY()+2*td, 0);
                }
            } else {
                point0 = steps.get(i-1).getSourcePoint().getPosition();
            }
            A1 = (int)(point1.getX()-point0.getX());
            A2 = (int)(point1.getY()-point0.getY());
            B1 = (int)(point2.getX()-point1.getX());
            B2 = (int)(point2.getY()-point1.getY());
            int turn_time = 1;
            if (i == 0 &&
                    // 先竖后竖，回到原点
                    ((Math.abs(A1) < td/4 && Math.abs(B1) < td/4 && Math.abs(A2 + B2) < td) ||
                    // 先竖后横，回到原点
                    (Math.abs(A2) < td/4 && Math.abs(B2) < td/4 && Math.abs(A1 + B1) < td))) {
                //需要掉头 0x07
                debug_path += point1_id_str;
                debug_path += "LD->";
                path[9 + 4 * i] = (byte) (point1_id / 256);
                path[10 + 4 * i] = (byte) (point1_id % 256);
                path[11 + 4 * i] = 0x07;
                path[12 + 4 * i] = 0x01;
            } else if (
                    /***
                       ▲   │
                       │   │
                       │   │
                       │   │
                ───────┼───┼─────▶
                       │   │
                ◀──────┼───┼──────
                       │   │
                       │   │
                       │   ▼  ***/

                    //⇅ 先竖后竖，不考虑掉头的情况
                    (Math.abs(A1) < td && Math.abs(B1) < td) ||
                    //⇋ 先横后横
                    (Math.abs(A2) < td && Math.abs(B2) < td)) {
                //直行 0
                //path[4+2*(i-1)] = 0
                debug_path += point1_id_str;
                debug_path += "->";
                path[9 + 4 * i] = (byte) (point1_id / 256);
                path[10 + 4 * i] = (byte) (point1_id % 256);
                path[11 + 4 * i] = 0x00;
                path[12 + 4 * i] = 0x01;
            }
            // ↱, ↲, ↴, ↻    1竖，2横
            /***



                   │   ▲
            ┌────┐ │   │ ┌────┐
            │ 2  │ │   │ │ 4  │
            └────┘ │   │ └────┘
          ◀─────────   │────────

          ─────────│   ────────▶
            ┌────┐ │   │ ┌────┐
            │ 3  │ │   │ │ 1  │
            └────┘ │   │ └────┘
                   │   │
                   ▼   │        ***/

            else if (
                    //↱ 先竖后横，横正竖正
                    (Math.abs(A1) < td && Math.abs(B2) < td && A2 > td && B1 > td) ||
                    //↲ 先竖后横，横负竖负
                    (Math.abs(A1) < td && Math.abs(B2) < td && A2 < td2 && B1 < td2) ||
                    //↴ 先横后竖，横正竖负
                    (Math.abs(A2) < td && Math.abs(B1) < td && A1 > td && B2 < td2) ||
                    //↻ 先横后竖，横负竖正
                    (Math.abs(A2) < td && Math.abs(B1) < td && A1 < td2 && B2 > td)) {
                //右转 0x10
                turn_time++;
                debug_path += point1_id_str;
                debug_path += "RR->";
                path[9 + 4 * i] = (byte) (point1_id / 256);
                path[10 + 4 * i] = (byte) (point1_id % 256);
                path[11 + 4 * i] = 0x10;
                path[12 + 4 * i] = 0x01;
            } else {
                //左转 0x04
                turn_time++;
                debug_path += point1_id_str;
                debug_path += "LL->";
                path[9 + 4 * i] = (byte) (point1_id / 256);
                path[10 + 4 * i] = (byte) (point1_id % 256);
                path[11 + 4 * i] = 0x04;
                path[12 + 4 * i] = 0x01;
            }

        }
        point2_id_str = steps.get(steps.size()-1).getDestinationPoint().getName();
        point2_id = Integer.parseInt(point2_id_str);
        if(steps.get(steps.size()-1).getDestinationPoint().getProperty("charge") != null){
            path[11 + (steps.size()-1) * 4] = 0x20;
            debug_path += "BB";
        }

        path[9+steps.size()*4] = (byte)(point2_id/256);
        path[10+steps.size()*4] = (byte)(point2_id%256);
        path[12+steps.size()*4] = 0;
        byte check = 0;
        for(int i = 0; i < steps.size()*4+13; i++) {
            check = (byte) (check ^ path[i]);
        }
        path[steps.size()*4+13] = (byte) ~ check;

        debug_path += point2_id_str;
        LOG.info(debug_path);
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
