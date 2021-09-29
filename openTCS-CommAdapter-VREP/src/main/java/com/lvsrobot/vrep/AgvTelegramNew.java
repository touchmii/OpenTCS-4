package com.lvsrobot.vrep;



import coppelia.FloatWA;
import coppelia.IntW;
import coppelia.FloatW;
import coppelia.remoteApi;

import java.net.InetAddress;

import org.opentcs.data.model.Triple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AgvTelegramNew {

    private remoteApi sim;
    private int clientID;
    private IntW vehicle_handel = new IntW(-1);
    private IntW floor_handel = new IntW(-1);
    private boolean connected = false;
//    private ModbusClient modbusClient;
    private static final Logger LOG = LoggerFactory.getLogger(AgvTelegramNew.class);
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
    public AgvTelegramNew(String ip, int port) {
        try {
            sim = new remoteApi();
            sim.simxFinish(-1);
            clientID = sim.simxStart("127.0.0.1", 19999, true, true, 5000, 5);
            if (clientID != -1) {
                connected = true;
                int ret = sim.simxGetObjectHandle(this.clientID, "youBot", this.vehicle_handel, sim.simx_opmode_blocking);
                int ret2 = sim.simxGetObjectHandle(this.clientID, "ResizableFloor_5_25", this.floor_handel, sim.simx_opmode_blocking);
            }
        }
//        catch (ModbusIOException e) {
//            LOG.info("ConnectionError: {}", e.getMessage());
//        }
        catch (Exception e) {
//            e.printStackTrace();
            LOG.info("Exception: {}", e.getMessage());
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
        return  connected;
    }
    public void Connecte() {
        if(!this.isConnected()) {
            try {

                clientID = sim.simxStart("127.0.0.1", 19999, true, true, 5000, 5);
                if (clientID != -1) {
                    connected = true;
                }
            } catch (Exception e) {
                LOG.error("ModbusIOException: {}", e.toString());

//                disConnecte();
            }
        }
    }
    public void disConnecte() {
        if(this.isConnected()) {
            try {

                sim.simxFinish(-1);
            } catch (Exception e) {
                LOG.error("Exception :{}", e.toString());
            }
        }
    }

    /**
     * 获取车辆信息
     * @return AgvInfo
     * 所有车辆信息
     */
    public synchronized AgvInfo getAgvInfo() {

//        byte[] retBytes = socket.send(sendBytes);
        int[] retReadInputRegisters = new int[60];
        FloatWA positon = new FloatWA(3);
        FloatWA angle = new FloatWA(3);
        try {
//            modbusClient.Connect();
//            if(!this.isConnected()) {
            this.Connecte();
//            }
            int ret = sim.simxGetObjectPosition(clientID, vehicle_handel.getValue(), floor_handel.getValue(), positon, remoteApi.simx_opmode_blocking);
            int ret2 = sim.simxGetObjectOrientation(clientID, vehicle_handel.getValue(), floor_handel.getValue(), angle, remoteApi.simx_opmode_blocking);
            LOG.debug("rec : {}", retReadInputRegisters);
//            modbusClient.Disconnect();
        }
//        catch (Exception e) {
//            LOG.error("MosbusException: {}", e.toString());
////            modbusClient.Disconnect();
//            this.disConnecte();
//            return null;
//
//        }
//        catch (ModbusIOException e) {
//            LOG.info("ConnectException: {}", e.getMessage());
//        }
        catch (Exception e) {
            LOG.error("Exception: {}", e.toString());
        }
//        if (retBytes == null)
//            return null;
//        if (retBytes.length != 8) {
//            return null;
//        }
        AgvInfo agvInfo = new AgvInfo();
//        agvInfo.setCurrentPosition({byteToUnsignedInt(retBytes[0]) << 8 | byteToUnsignedInt(retBytes[1]), byteToUnsignedInt(retBytes[2]) << 8 | byteToUnsignedInt(retBytes[3])});
//        agvInfo.setSpeed(byteToUnsignedInt(retBytes[3]));
//        agvInfo.setElectric(byteToUnsignedInt(retBytes[4]));
//        agvInfo.setException(byteToUnsignedInt(retBytes[5]));
//        agvInfo.setStatus(byteToUnsignedInt(retBytes[6]));
//        agvInfo.setPosition(retReadInputRegisters[0]);
//        agvInfo.setSpeed(retReadInputRegisters[]);
//        agvInfo.setElectric(retReadInputRegisters[2]);
//        agvInfo.setException(retReadInputRegisters[3]);
        agvInfo.setStatus(retReadInputRegisters[7]);
        int[] precisePosition = {(int)(1000*positon.getArray()[0]), (int)(1000*positon.getArray()[1])};
        agvInfo.setPrecisePosition(precisePosition);
        int[] currentPosition = {10*(int)(short)retReadInputRegisters[12], 10*(int)(short)retReadInputRegisters[13]};
        agvInfo.setCurrentPosition(currentPosition);
        int[] previousPosition = {10*(int)(short)retReadInputRegisters[14], 10*(int)(short)retReadInputRegisters[15]};
        agvInfo.setPreviousPositon(previousPosition);
        double orientation = angle.getArray()[1]/Math.PI*180; //[1]代表Z轴角度
        agvInfo.setVehicleOrientation(orientation);
        agvInfo.setBattery(90);
        agvInfo.setLoadStatus(retReadInputRegisters[32]);
        agvInfo.setCharge_status(retReadInputRegisters[33]);
        agvInfo.setSpeed(retReadInputRegisters[52]);
        //是否触发避障，0未触发 1触发 地址18
        agvInfo.setVehicleAvoidance(retReadInputRegisters[18]);
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
        try {
              this.Connecte();
//            if(!modbusClient.isConnected()) {
//                modbusClient.Connect();
//            }
//            modbusClient.WriteSingleRegister(100,path);
//            m.writeMultipleRegisters(1,100, path);
        } catch (Exception e) {
            LOG.error("send path error: {}", e.toString());
            this.disConnecte();
            return false;
        }
        return true;
    }

    /**
     * 终止驱动订单
     * @return
     */
    public synchronized boolean abortPath() {
        try{
            this.Connecte();
//            m.writeSingleRegister(1, 56, 1);
        } catch (Exception e) {
            LOG.error("abort failt: {}", e.toString());
            this.disConnecte();
            return false;
        }
        return true;
    }
    /**
     * 清除报警
     * @return
     */
    public synchronized boolean resetAlarm() {
        try{
            this.Connecte();
//            m.writeSingleRegister(1, 57, 0);
        } catch (Exception e) {
            LOG.error("resetAlarm failt: {}", e.toString());
            this.disConnecte();
            return false;
        }
        return true;
    }

    /**
     * 暂停驱动订单
     * @return
     */
    public synchronized boolean pausePath() {
        try{
            this.Connecte();
//            m.writeSingleRegister(1, 55, 1);
        } catch (Exception e) {
            LOG.error("pause failt: {}", e.toString());
            this.disConnecte();
            return false;
        }
        return true;
    }

    /**
     * 取消暂停驱动订单
     * @return
     */
    public synchronized boolean resumePath() {
        try{
            this.Connecte();
//            m.writeSingleRegister(1, 55, 0);
        } catch (Exception e) {
            LOG.error("resume failt: {}", e.toString());
            this.disConnecte();
            return false;
        }
        return true;
    }

    /**
     * 载货动作
     * @return boolean
     * 是否成功
     */
    public synchronized boolean forkAction(Triple current_coord, int forkAction, int pointName) {
        int[] send_path = {1, forkAction, 1, pointName, (int)current_coord.getX()/10, (int)current_coord.getY()/10, 365};
        try{
            this.Connecte();
//            m.writeMultipleRegisters(1, 100, send_path);
        } catch (Exception e) {
            LOG.error("forkAction failt: {}", e.toString());
            this.disConnecte();
            return false;
        }
        return true;
    }

}
