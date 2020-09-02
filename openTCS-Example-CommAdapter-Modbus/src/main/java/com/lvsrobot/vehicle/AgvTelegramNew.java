package com.lvsrobot.vehicle;

//import de.re.easymodbus.modbusclient.ModbusClient;
import com.intelligt.modbus.jlibmodbus.Modbus;
import com.intelligt.modbus.jlibmodbus.exception.ModbusIOException;
import com.intelligt.modbus.jlibmodbus.exception.ModbusNumberException;
import com.intelligt.modbus.jlibmodbus.exception.ModbusProtocolException;
import com.intelligt.modbus.jlibmodbus.master.ModbusMaster;
import com.intelligt.modbus.jlibmodbus.master.ModbusMasterFactory;
import com.intelligt.modbus.jlibmodbus.msg.request.ReadHoldingRegistersRequest;
import com.intelligt.modbus.jlibmodbus.msg.response.ReadHoldingRegistersResponse;
import com.intelligt.modbus.jlibmodbus.tcp.TcpParameters;

import java.net.ConnectException;
import java.net.InetAddress;
import java.rmi.ConnectIOException;

import de.re.easymodbus.exceptions.ModbusException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AgvTelegramNew {
    private ModbusMaster m;
//    private ModbusClient modbusClient;
    private static final Logger LOG = LoggerFactory.getLogger(AgvTelegramNew.class);
//    private SocketUtils socket;

//    public AgvTelegram(String ip, int port) {
//        socket = new SocketUtils(ip, port);
//    }
//    AgvInfo agvInfo = new AgvInfo();
    public AgvTelegramNew(String ip, int port) {
        try {
            TcpParameters tcpParameters = new TcpParameters();
            InetAddress address = InetAddress.getByName(ip);
            tcpParameters.setHost(address);
            tcpParameters.setPort(port);
            tcpParameters.setKeepAlive(true);
            m = ModbusMasterFactory.createModbusMasterTCP(tcpParameters);
            Modbus.setAutoIncrementTransactionId(true);
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

    public AgvInfo getAgvInfo() {
        byte[] sendBytes = new byte[8];
        sendBytes[0] = 'a';
        sendBytes[1] = 0;
        sendBytes[2] = 0;
        sendBytes[3] = 0;
        sendBytes[4] = 0;
        sendBytes[5] = 0;
        sendBytes[6] = 0;
        sendBytes[7] = 0;
//        byte[] retBytes = socket.send(sendBytes);
        int[] retReadInputRegisters = new int[40];
        try {
//            modbusClient.Connect();
//            if(!this.isConnected()) {
            this.Connecte();
//            }
            retReadInputRegisters = m.readHoldingRegisters(1, 0, 60);
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
        agvInfo.setStatus(retReadInputRegisters[7]);
        int precisePosition[] = {10*(int)(short)retReadInputRegisters[0], 10*(int)(short)retReadInputRegisters[1]};
        agvInfo.setPrecisePosition(precisePosition);
        int currentPosition[] = {10*(int)(short)retReadInputRegisters[12], 10*(int)(short)retReadInputRegisters[13]};
        agvInfo.setCurrentPosition(currentPosition);
        int previousPosition[] = {10*(int)(short)retReadInputRegisters[14], 10*(int)(short)retReadInputRegisters[15]};
        agvInfo.setPreviousPositon(previousPosition);
        double orientation = (double)retReadInputRegisters[7];
        agvInfo.setVehicleOrientation(orientation);
        agvInfo.setBattery(retReadInputRegisters[10]);
        agvInfo.setLoadStatus(retReadInputRegisters[32]);
        agvInfo.setSpeed(retReadInputRegisters[52]);
//        LOG.info("rec 51 {}",retReadInputRegisters[51]);
//        LOG.info("rec 52 {}",retReadInputRegisters[52]);
//        LOG.info("rec 53 {}",retReadInputRegisters[53]);

//        agvInfo.setCurrent_position_id(retReadInputRegisters[13]);
//        agvInfo.setBattery(retReadInputRegisters[14]);
//        agvInfo.

        return agvInfo;
    }

    public boolean sendWork(String finalOperation) {
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
    public boolean sendPath(int[] path) {
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
    public boolean abortPath() {
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
}
