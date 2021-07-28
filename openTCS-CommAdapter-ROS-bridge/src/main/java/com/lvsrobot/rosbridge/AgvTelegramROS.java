package com.lvsrobot.rosbridge;

import java.io.IOException;
import java.net.InetAddress;

import edu.wpi.rail.jrosbridge.Ros;
import edu.wpi.rail.jrosbridge.Topic;
import edu.wpi.rail.jrosbridge.callback.TopicCallback;
import edu.wpi.rail.jrosbridge.handler.RosHandler;
import edu.wpi.rail.jrosbridge.messages.Message;
import edu.wpi.rail.jrosbridge.messages.geometry.Pose2D;
import org.opentcs.data.model.Triple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.websocket.Session;

public class AgvTelegramROS {
    private Ros ros;
    private Topic topic_echo;
    private Topic topic_info;


    private static final Logger LOG = LoggerFactory.getLogger(AgvTelegramROS.class);

    /**
     * 新建车辆通信连接
     * @param String ip
     * @param int port
     */
    public AgvTelegramROS(String ip, int port) {
        try {
//            ros = new Ros(ip);
            ros = new Ros("192.168.0.104");
            ros.addRosHandler(new RosHandler() {
                @Override
                public void handleConnection(Session session) {
                    System.out.println("ROS is Connected");
                }

                @Override
                public void handleDisconnection(Session session) {
                    System.out.println("ROS is disconnect");
                }

                @Override
                public void handleError(Session session, Throwable t) {
                    System.out.println(("ROS is Error"));
                }
            });
            ros.connect();
            topic_echo = new Topic(ros, "/echo", "std_msgs/String");
            topic_info = new Topic(ros, "/pose", "geometry_msgs/Pose2D");
            topic_info.subscribe(new TopicCallback() {
                @Override
                public void handleMessage(Message message) {
                    System.out.println("From ROS: "+ message.toString());
                }
            });
        }
        catch (Exception e) {
//            e.printStackTrace();
            LOG.info("Exception: {}", e.getMessage());
        }
    }

//    public

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
        return  this.ros.isConnected();
    }
    public void Connecte() {
        if(!this.isConnected()) {
            try {

                this.ros.connect();
            } catch (Exception e) {
                LOG.error("ModbusIOException: {}", e.toString());

//                disConnecte();
            }
        }
    }
    public void disConnecte() {
        if(this.isConnected()) {
            try {

                this.ros.disconnect();
            } catch (Exception e) {
                LOG.error("Exception :{}", e.toString());
            }
        }
    }

    public void echoTopic(String msg) {
        Message toSend = new Message("{\"data\": \"hello, world!\"}");
        topic_echo.publish(toSend);
    }

    /**
     * 获取车辆信息
     * @return AgvInfo
     * 所有车辆信息
     */
    public synchronized AgvInfo getAgvInfo() {

//        byte[] retBytes = socket.send(sendBytes);
        int[] retReadInputRegisters = new int[60];

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
//        //是否触发避障，0未触发 1触发 地址18
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
        try {
              this.Connecte();

            Message toSend = new Message("{\"data\": \"send path!\"}");
            topic_echo.publish(toSend);
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
            echoTopic("abortPath");
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
            echoTopic("resetAlarm");
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
            echoTopic("pausePath");
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
            echoTopic("resumePath");
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
            echoTopic("fork up");
        } catch (Exception e) {
            LOG.error("forkAction failt: {}", e.toString());
            this.disConnecte();
            return false;
        }
        return true;
    }

}
