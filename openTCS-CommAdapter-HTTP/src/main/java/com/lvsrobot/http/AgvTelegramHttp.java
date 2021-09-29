package com.lvsrobot.http;

//import de.re.easymodbus.modbusclient.ModbusClient;

import org.json.JSONArray;
import org.json.JSONObject;
import org.opentcs.data.model.Triple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

public class AgvTelegramHttp {
//    private ModbusMaster m;
//    private ModbusClient modbusClient;
    private String vehicleHost = null;
    private Integer vehiclePort = null;
    private Boolean vehicleConnect = null;
    private static final Logger LOG = LoggerFactory.getLogger(AgvTelegramHttp.class);
//    private SocketUtils socket;

//    public AgvTelegram(String ip, int port) {
//        socket = new SocketUtils(ip, port);
//    }
//    AgvInfo agvInfo = new AgvInfo();

    /**
     * 新建车辆通信连接
     * @param String ip
     * @param int port
     */
    public AgvTelegramHttp(String ip, int port) {
        vehicleHost = ip;
        vehiclePort = port;
        String stateResponseJson = HttpUtil.doGet("http://" + vehicleHost + ":" + vehiclePort.toString() + "/v1/vehicle/status");
        if (!Objects.isNull(stateResponseJson)) {
            LOG.info("{}: 连接成功", vehicleHost);
            vehicleConnect = true;
//            getProcessModel().setCommAdapterConnected(true);
            // Check for resending last request
//            requestResponseMatcher.checkForSendingNextRequest();
        }
    }

    /**
     * byte转无符号int
     *
     * @param data
     * @return
     */
    private static Integer byteToUnsignedInt(byte data) {
        return data & 0xff;
    }

    public boolean isConnected() {
        return  vehicleConnect;
    }


    /**
     * 获取车辆信息
     * @return AgvInfo
     * 所有车辆信息
     */
    public synchronized AgvInfo getAgvInfo() {

//        byte[] retBytes = socket.send(sendBytes);
        int[] retReadInputRegisters = new int[60];

        String stateResponseJson = HttpUtil.doGet("http://" + vehicleHost + ":" + vehiclePort.toString() + "/v1/vehicle/status");
        AgvInfo agvInfo = new AgvInfo();
        if (!Objects.isNull(stateResponseJson)) {
            LOG.info("从主机: {} 接收到: {}", vehicleHost, stateResponseJson);
            JSONObject jo = new JSONObject(stateResponseJson);
            int[] precisePosition = {jo.getInt("x"), jo.getInt("y")};
            agvInfo.setPrecisePosition(precisePosition);
            agvInfo.setBattery(jo.getInt("battery"));
            Integer vehicleStatus = null;
            switch (jo.getString("status")) {
                case "idle":
                    vehicleStatus = 1;
                    break;
                case "executing":
                    vehicleStatus = 2;
                    break;
                case "error":
                    vehicleStatus = 3;
                    break;
                default:
                    vehicleStatus = 4;
            }
            agvInfo.setStatus(vehicleStatus);
            Integer vehicleLoad = null;
            switch (jo.getString("fork")) {
                case "load":
                    vehicleLoad = 1;
                    break;
                case "unload":
                    vehicleLoad = 0;
                    break;
                default:
                    vehicleLoad = 2;
            }
            agvInfo.setLoadStatus(vehicleLoad);
        }
//        agvInfo.setCurrentPosition({byteToUnsignedInt(retBytes[0]) << 8 | byteToUnsignedInt(retBytes[1]), byteToUnsignedInt(retBytes[2]) << 8 | byteToUnsignedInt(retBytes[3])});
//        agvInfo.setSpeed(byteToUnsignedInt(retBytes[3]));
//        agvInfo.setElectric(byteToUnsignedInt(retBytes[4]));
//        agvInfo.setException(byteToUnsignedInt(retBytes[5]));
//        agvInfo.setStatus(byteToUnsignedInt(retBytes[6]));
//        agvInfo.setPosition(retReadInputRegisters[0]);
//        agvInfo.setSpeed(retReadInputRegisters[]);
//        agvInfo.setElectric(retReadInputRegisters[2]);
//        agvInfo.setException(retReadInputRegisters[3]);
//        agvInfo.setStatus(retReadInputRegisters[7]);
//        int[] precisePosition = {10*(int)(short)retReadInputRegisters[0], 10*(int)(short)retReadInputRegisters[1]};
//        agvInfo.setPrecisePosition(precisePosition);
//        int[] currentPosition = {10*(int)(short)retReadInputRegisters[12], 10*(int)(short)retReadInputRegisters[13]};
//        agvInfo.setCurrentPosition(currentPosition);
//        int[] previousPosition = {10*(int)(short)retReadInputRegisters[14], 10*(int)(short)retReadInputRegisters[15]};
//        agvInfo.setPreviousPositon(previousPosition);
//        double orientation = (double)retReadInputRegisters[2];
//        agvInfo.setVehicleOrientation(orientation);
//        agvInfo.setBattery(retReadInputRegisters[10]);
//        agvInfo.setLoadStatus(retReadInputRegisters[32]);
//        agvInfo.setCharge_status(retReadInputRegisters[33]);
//        agvInfo.setSpeed(retReadInputRegisters[52]);
//        是否触发避障，0未触发 1触发 地址18
//        agvInfo.setVehicleAvoidance(retReadInputRegisters[18]);
//        LOG.info("rec 51 {}",retReadInputRegisters[51]);
//        LOG.info("rec 52 {}",retReadInputRegisters[52]);
//        LOG.info("rec 53 {}",retReadInputRegisters[53]);

//        agvInfo.setCurrent_position_id(retReadInputRegisters[13]);
//        agvInfo.setBattery(retReadInputRegisters[14]);
//        agvInfo.

        return agvInfo;
    }

    public synchronized boolean sendWork(String finalOperation) {
        return true;
    }

//    public boolean sendPath(int dest) {
//        byte[] sendBytes = new byte[8];
//        sendBytes[0] = 'b';
//        sendBytes[1] = (byte) (dest >> 8);
//        sendBytes[2] = (byte) (dest & 0xFF);
//        sendBytes[3] = 0;
//        sendBytes[4] = 0;
//        sendBytes[5] = 0;
//        sendBytes[6] = 0;
//        sendBytes[7] = 0;
//        byte[] retBytes = socket.send(sendBytes);
//        if (retBytes == null)
//            return false;
//        if (retBytes.length != 8) {
//            return false;
//        }
//        return true;
//    }

    /**
     * 发送驱动订单
     * @param int[] path
     * @return boolean
     * 是否成功
     */
    public synchronized boolean sendPath(int[] path) {
        JSONObject orderRequestJson = new JSONObject();
        orderRequestJson.put("action", "load");
        JSONArray ja = new JSONArray();
        for(int i = 0; i < (path.length-3)/4; i++) {
//            jo.put();
//            String a = Integer(path[i*4+3]).toString();
            ja.put(String.valueOf(path[i*4+3]));
            ja.put(path[i*4+4]);
            ja.put(path[i*4+5]);
            switch (path[i*4+6]) {
                case 90:
                    ja.put("left");
                    break;
                case -90:
                    ja.put("right");
                    break;
                default:
                    ja.put("direct");
            }
        }
        orderRequestJson.put("path", ja);
        try {

            String orderResponseJson = HttpUtil.doPost("http://" + vehicleHost + ":" + vehiclePort.toString() + "/v1/vehicle/sendpath", orderRequestJson.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
//        String orderRequestJson = JSONObject.to

        return true;
    }

    /**
     * 终止驱动订单
     * @return
     */
    public synchronized boolean abortPath() {

        return true;
    }
    /**
     * 清除报警
     * @return
     */
    public synchronized boolean resetAlarm() {

        return true;
    }

    /**
     * 暂停驱动订单
     * @return
     */
    public synchronized boolean pausePath() {

        return true;
    }

    /**
     * 取消暂停驱动订单
     * @return
     */
    public synchronized boolean resumePath() {

        return true;
    }

    /**
     * 载货动作
     * @return boolean
     * 是否成功
     */
    public synchronized boolean forkAction(Triple current_coord, int forkAction, int pointName) {

        return true;
    }

}
