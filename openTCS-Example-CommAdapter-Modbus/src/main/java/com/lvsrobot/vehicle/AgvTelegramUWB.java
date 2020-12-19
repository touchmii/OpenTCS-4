package com.lvsrobot.vehicle;

//import de.re.easymodbus.modbusclient.ModbusClient;

import com.intelligt.modbus.jlibmodbus.Modbus;
import com.intelligt.modbus.jlibmodbus.exception.ModbusIOException;
import com.intelligt.modbus.jlibmodbus.master.ModbusMaster;
import com.intelligt.modbus.jlibmodbus.master.ModbusMasterFactory;
import com.intelligt.modbus.jlibmodbus.serial.SerialParameters;
import com.intelligt.modbus.jlibmodbus.serial.SerialPort;
import com.intelligt.modbus.jlibmodbus.tcp.TcpParameters;
import org.opentcs.data.model.Triple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;

public class AgvTelegramUWB {
    private ModbusMaster m;
//    private ModbusClient modbusClient;
    private static final Logger LOG = LoggerFactory.getLogger(AgvTelegramUWB.class);
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
    public AgvTelegramUWB(String ip, int port) {
        try {
//            TcpParameters tcpParameters = new TcpParameters();
            SerialParameters serialParameters = new SerialParameters();
//            InetAddress address = InetAddress.getByName(ip);
//            tcpParameters.setHost(address);
//            tcpParameters.setPort(port);
//            tcpParameters.setKeepAlive(true);
            serialParameters.setBaudRate(SerialPort.BaudRate.BAUD_RATE_115200);
            serialParameters.setDevice("COM3");
//            m = ModbusMasterFactory.createModbusMasterTCP(tcpParameters);
            m = ModbusMasterFactory.createModbusMasterRTU(serialParameters);
            Modbus.setAutoIncrementTransactionId(true);
            //给0x28寄存器发2 代表连续测量模式 等等同于writeMutiRegister（1，40，int[] {2})
            m.writeSingleRegister(1, 40, 2);
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
        return  this.m.isConnected();
    }
    public void Connecte() {
        if(!this.isConnected()) {
            try {

                this.m.connect();
            } catch (ModbusIOException e) {
                LOG.error("ModbusIOException: {}", e.toString());

//                disConnecte();
            }
        }
    }
    public void disConnecte() {
        if(this.isConnected()) {
            try {

                this.m.disconnect();
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
        try {
//            modbusClient.Connect();
//            if(!this.isConnected()) {
            this.Connecte();
//            }
            //读取0x2A开始 6个寄存器（16位int X 6）
            retReadInputRegisters = m.readHoldingRegisters(1,42 , 6);
            LOG.debug("rec : {}", retReadInputRegisters);

//            modbusClient.Disconnect();
        }   catch (ModbusIOException e) {
            LOG.error("MosbusException: {}", e.toString());
//            modbusClient.Disconnect();
            this.disConnecte();
            return null;

        }
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
//        agvInfo.setStatus(retReadInputRegisters[7]);
        int[] precisePosition = {10*(int)(short)retReadInputRegisters[3], 10*(int)(short)retReadInputRegisters[4]};
        agvInfo.setPrecisePosition(precisePosition);
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
//            if(!modbusClient.isConnected()) {
//                modbusClient.Connect();
//            }
//            modbusClient.WriteSingleRegister(100,path);
            m.writeMultipleRegisters(1,100, path);
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
            m.writeSingleRegister(1, 56, 1);
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
            m.writeSingleRegister(1, 57, 0);
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
            m.writeSingleRegister(1, 55, 1);
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
            m.writeSingleRegister(1, 55, 0);
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
            m.writeMultipleRegisters(1, 100, send_path);
        } catch (Exception e) {
            LOG.error("forkAction failt: {}", e.toString());
            this.disConnecte();
            return false;
        }
        return true;
    }

}
