package com.lvsrobot.vehicletcp;

import org.opentcs.data.model.Triple;
import org.opentcs.data.order.DriveOrder;
import org.opentcs.data.order.Route;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("deprecation")
public class ConfigRoute {

    private DriveOrder driveorder;

    private Route route;

    private List<Route.Step> steps;

    public int init = 0;

    public double current_angle = 0;

    private static final Logger LOG = LoggerFactory.getLogger(ConfigRoute.class);

    private String debug_path = new String("robot path ");

    private Map<String , Integer> actionMap = new HashMap<String, Integer>(){{
        put("LL" , 4);
        put("RR" , 0x10);
        put("LD" , 7);
        put("BK" , 0x17);
        put("BB" , 0x20);
        put("DB" , 0x19);
        put("UB" , 0x18);
        put("BL" , 0x21);
        put("BR" , 0x22);
        put("AA" , 2);
        put("aa" , 3);
    }};

    //    public ConfigRoute() {
//
//    }
    public void setRoute(DriveOrder dv) {
        driveorder = dv;
        route = driveorder.getRoute();
//        driveorder.getRoute().getSteps().get(0).getPath().getLength()
        steps = route.getSteps();
        init = 1;
        debug_path = "robot path ";
    }
    public void setAngle(double _angle) {
        debug_path += String.format("angle %s, ", _angle);
        current_angle = _angle;
    }

    public int mapValue(int v) {
        if (v > 1600) {
            return 1500;
        } else if (v < -1600) {
            return -1500;
        } else {
            return v;
        }
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
                    // 设置一个左侧的虚拟点
                    point0 = new Triple(point1.getX()-2*td, point1.getY(), 0);
                } else if (current_angle == 90) {
                    // 设置一个下方的虚拟点
                    point0 = new Triple(point1.getX(), point1.getY()-2*td, 0);
                } else if (current_angle == 180) {
                    // 设置一个右侧的虚拟点
                    point0 = new Triple(point1.getX()+2*td, point1.getY(), 0);
                } else if (current_angle == 270) {
                    // p设置一个上方的虚拟点
                    point0 = new Triple(point1.getX(), point1.getY()+2*td, 0);
                }
            } else {
                point0 = steps.get(i-1).getSourcePoint().getPosition();
            }
            A1 = mapValue( (int)(point1.getX()-point0.getX()) );
            A2 = mapValue( (int)(point1.getY()-point0.getY()) );
            B1 = mapValue( (int)(point2.getX()-point1.getX()) );
            B2 = mapValue( (int)(point2.getY()-point1.getY()) );
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

//        String distOpration = driveorder.getDestination().getOperation();
        point2_id_str = steps.get(steps.size()-1).getDestinationPoint().getName();
        String pointxx_id_str = steps.get(steps.size()-1).getSourcePoint().getName();
        point2_id = Integer.parseInt(point2_id_str);
        if(steps.get(steps.size()-1).getDestinationPoint().getProperty("charge") != null) {
            path[11 + (steps.size()-1) * 4] = 0x20;
            debug_path += "BB";
        } else if (steps.get(steps.size()-1).getDestinationPoint().getProperty("back") != null
                && steps.get(steps.size()-2).getDestinationPoint().getProperty("back") != null) {
            String pointxx_id_str2 = steps.get(steps.size()-2).getSourcePoint().getName();
            //两个后退点
            if (path[11 + (steps.size()-2) * 4] == 0) {
                path[11 + (steps.size()-2) * 4] = 0x20; //BB nop
                path[11 + (steps.size()-1) * 4] = 0x17; //BK
//                debug_path += "BB";
                debug_path = debug_path.replace(pointxx_id_str2, pointxx_id_str2+"BB");
                debug_path = debug_path.replace(pointxx_id_str, pointxx_id_str+"BK");
            } else if (path[11 + (steps.size()-2) * 4] == 0x10) { // RR
                path[11 + (steps.size()-2) * 4] = 0x21; //BL nop
                path[11 + (steps.size()-1) * 4] = 0x17; //BK
                debug_path = debug_path.replace(pointxx_id_str2+"RR", pointxx_id_str2+"BL");
                debug_path = debug_path.replace(pointxx_id_str, pointxx_id_str+"BK");
            } else if (path[11 + (steps.size()-2) * 4] == 0x4) { //LL
                path[11 + (steps.size()-2) * 4] = 0x22; //BR nop
                path[11 + (steps.size()-1) * 4] = 0x17; //BK
                debug_path = debug_path.replace(pointxx_id_str2+"LL", pointxx_id_str2+"BR");;
                debug_path = debug_path.replace(pointxx_id_str, pointxx_id_str+"BK");;
            }
        } else if (steps.get(steps.size()-1).getDestinationPoint().getProperty("back") != null) {
            if (path[11 + (steps.size()-1) * 4] == 0) {
                path[11 + (steps.size()-1) * 4] = 0x20; //BB nop
                debug_path += "BB";
            } else if (path[11 + (steps.size()-1) * 4] == 0x10) { // RR
                path[11 + (steps.size()-1) * 4] = 0x21; //BL nop
                debug_path = debug_path.replace(pointxx_id_str+"RR", pointxx_id_str+"BL");
            } else if (path[11 + (steps.size()-1) * 4] == 0x4) { //LL
                path[11 + (steps.size()-1) * 4] = 0x22; //BR nop
                debug_path = debug_path.replace(pointxx_id_str+"LL", pointxx_id_str+"BR");;
            }
        }

        path[9+steps.size()*4] = (byte)(point2_id/256);
        path[10+steps.size()*4] = (byte)(point2_id%256);
        switch (driveorder.getDestination().getOperation()) {
            case "MOVE":
//                path[1] = 0;
                break;
            case "LOAD":
//                path[11+steps.size()*4] = 2;;
//                path[path.length-5] = path[path.length-1];
                break;
            case "UNLOAD":
//                path[11+steps.size()*4] = 3;;
                break;
            case "CHARGING":
//                path[1] = 3;
                break;
            default:
                break;
        }
        path[12+steps.size()*4] = 0;
        byte check = 0;
        for(int i = 0; i < steps.size()*4+13; i++) {
            check = (byte) (check ^ path[i]);
        }
        path[steps.size()*4+13] = (byte) ~ check;

        debug_path += point2_id_str;
//        LOG.info(debug_path);
//        int[] path2;
//        int dir = 0;
//        for(int i=0 , (len(path2)-4)/2, i++) {
//            if(path[4+i*2]!=0){
//                path2[]
//            }
//        }

        return path;
    }

    public byte[] getPath(DriveOrder order, double angle) {
        //判断是否有预定义路径
        String dest = order.getRoute().getSteps().get(0).getSourcePoint().getName();
        if (order.getRoute().getFinalDestinationPoint().getProperties().containsKey(dest)) {
            String prePath = order.getRoute().getFinalDestinationPoint().getProperty(dest);
            debug_path = "robot path " + prePath;
            return pairPath(prePath);
        }

        setRoute(order);
        setAngle(angle);
        return getPath();
    }


    public byte[] getliftAction(String point, String action) {
        int point_num = Integer.parseInt(point);
        byte action_ = 0;
        if (action.equals("LOAD")) {
            action_ = 2;
        } else if (action.equals("UNLOAD")) {
            action_ = 0x12;
        } else if (action.equals("CHARGE")) {
            return new byte[]{0, 1, 3, 0, 2, 3, 0, (byte) 252};
        }
        byte[] path_data = {0, 1, 0xc, 0, 8, 0, 0, 1, 1, (byte)(point_num/256), (byte)(point_num%256), action_, 0, 0};
        byte check = 0;
        for(int i = 0; i < 13; i++) {
            check = (byte) (check ^ path_data[i]);
        }
        path_data[13] = (byte) ~ check;
//        LOG.debug("{} send lift action command: {}", this.name, ByteBufUtil.hexDump(path_data));
        return path_data;
    }

    public byte[] pairPath(String pathAscii) {
        String[] points = pathAscii.split("->");
        //暂时车辆第一步需要往前走
        byte[] path = new byte[points.length*4+10];
        path[0] = 0x00;
        path[1] = 0x01;
        path[2] = 0x0c;
        //lenght
        path[3] = (byte)((points.length*4+4)/256);
        path[4] = (byte)((points.length*4+4)%256);
        path[5] = 0x00;
        path[6] = 0x00;
        path[7] = 0x01;
        path[8] = 0x01;
        for (int i = 0; i < points.length; i ++) {
            String[] x = points[i].split("[^A-Z0-9]+|(?<=[A-Z])(?=[0-9])|(?<=[0-9])(?=[A-Z])");
            path[9+i*4] = (byte) (Integer.parseInt(x[0])/256);
            path[10+i*4] = (byte) (Integer.parseInt(x[0])%256);
            if (x.length>1) {
                path[11+i*4] = (byte) actionMap.get(x[1]).intValue();
            } else {path[11+i*4] = 0;}
            if (i < points.length-1) {
                path[12+i*4] = 1;
            } else {path[12+i*4] = 0;}
        }
        byte check = 0;
        for(int i = 0; i < points.length*4+9; i++) {
            check = (byte) (check ^ path[i]);
        }
        path[points.length*4+9] = (byte) ~ check;
        return path;
    }
}
