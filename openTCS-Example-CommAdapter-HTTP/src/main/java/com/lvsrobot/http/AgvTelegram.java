package com.lvsrobot.http;

import de.re.easymodbus.exceptions.ConnectionException;
import de.re.easymodbus.exceptions.ModbusException;
import de.re.easymodbus.modbusclient.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class AgvTelegram {
    private ModbusClient modbusClient;
    private static final Logger LOG = LoggerFactory.getLogger(AgvTelegram.class);
//    private SocketUtils socket;

//    public AgvTelegram(String ip, int port) {
//        socket = new SocketUtils(ip, port);
//    }
//    AgvInfo agvInfo = new AgvInfo();
    public AgvTelegram(String ip, int port) {
        if (modbusClient == null) {

            modbusClient = new ModbusClient(ip, port);
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
        return  this.modbusClient.isConnected();
    }
    public void Connecte() {
        if(!this.isConnected()) {
            try {

                this.modbusClient.Connect();
            } catch (IOException e) {
                LOG.info("ModbusIOException: {}", e.toString());
//                disConnecte();
            }
        }
    }
    public void disConnecte() {
        if(this.isConnected()) {
            try {

                this.modbusClient.Disconnect();
            } catch (IOException e) {
                LOG.info("IO Exception :{}", e.toString());
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
            retReadInputRegisters = modbusClient.ReadHoldingRegisters(0,35);
//            modbusClient.Disconnect();
        }   catch (ConnectionException e) {
//            LOG.info("ConnectionException Message :{}", e.getMessage());
            LOG.info("ConnectionException String:{}", e.getMessage());
//            LOG.info("ConnectionException Cause:{}", e.getCause());
//            modbusClient.Disconnect();
            this.disConnecte();
            return null;

        }  catch (ModbusException e) {
            LOG.info("ModbusExcepiton: {}", e.getMessage());
        }   catch (Exception e) {
            LOG.info("UnknownHostExcetion: {}", e.getMessage());
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
        int precisePosition[] = {retReadInputRegisters[0]*10, retReadInputRegisters[1]*10};
        agvInfo.setPrecisePosition(precisePosition);
        int currentPosition[] = {retReadInputRegisters[12]*10, retReadInputRegisters[13]*10};
        agvInfo.setCurrentPosition(currentPosition);
        int previousPosition[] = {retReadInputRegisters[14]*10, retReadInputRegisters[15]*10};
        agvInfo.setPreviousPositon(previousPosition);
        double orientation = (double)retReadInputRegisters[7];
        agvInfo.setVehicleOrientation(orientation);
        agvInfo.setBattery(retReadInputRegisters[10]);
        agvInfo.setLoadStatus(retReadInputRegisters[32]);

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
            modbusClient.WriteMultipleRegisters(100, path);
        } catch (ConnectionException e) {
            LOG.info("send path error: {}", e.getMessage());
            this.disConnecte();
            return false;
        } catch (Exception e) {
            LOG.info("Exception: {}", e.getMessage());
        }
        return true;
    }

    public boolean abortPath() {
        try {
            this.Connecte();
            modbusClient.WriteSingleRegister(56, 1);
        } catch ( ConnectionException e ) {
            LOG.info("abort path error: {}", e.toString());
            this.disConnecte();
            return false;
        } catch (Exception e) {
            LOG.info("Exception: {}", e.toString());
        }
        return true;
    }

    public boolean sendData(int address, int reg, String error_msg) {
        try {
            this.Connecte();
            modbusClient.WriteSingleRegister(address, reg);
        } catch (ConnectionException e) {
            LOG.info("connection error: {}, send msg: {}", e.toString(), error_msg);
            this.disConnecte();
            return false;
        } catch (Exception e) {
            LOG.info("Exception: {}", e.toString());
        }
        return true;

    }

    public boolean pauseVehicle() {
        return sendData(55, 1, "pauseVehicle");
    }

    public boolean resumeVehicle() {
        return sendData(55,1, "resumeVehicle");
    }
}
