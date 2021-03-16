package com.lvsrobot.vehicletcp;

import org.opentcs.data.model.Triple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

public class AgvTelegram {

    private static final Logger LOG = LoggerFactory.getLogger(AgvTelegram.class);
    private SocketUtils socket;
    AgvInfo agvInfo = new AgvInfo();
    public AgvTelegram(String ip, int port) {
        socket = new SocketUtils(ip, port);
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

    public AgvInfo getAgvInfo() {
        byte[] sendBytes = {0, 1, 2, 1, (byte)253};
//        sendBytes[0] = 'a';
//        sendBytes[1] = 0;
//        sendBytes[2] = 0;
//        sendBytes[3] = 0;
//        sendBytes[4] = 0;
//        sendBytes[5] = 0;
//        sendBytes[6] = 0;
//        sendBytes[7] = 0;
        byte[] retBytes = socket.send(sendBytes);
        if (retBytes == null)
            return null;
        if (retBytes.length < 18) {
            return null;
        }
//        AgvInfo agvInfo = new AgvInfo();
        agvInfo.setPosition(byteToUnsignedInt(retBytes[6]) << 8 | byteToUnsignedInt(retBytes[7]));
        agvInfo.setSpeed(byteToUnsignedInt(retBytes[8]));
        agvInfo.setAngle(byteToUnsignedInt(retBytes[9]));
        agvInfo.setBattery(byteToUnsignedInt(retBytes[11]));
//        agvInfo.setException(byteToUnsignedInt(retBytes[5]));
        agvInfo.setStatus(byteToUnsignedInt(retBytes[12]));
        return agvInfo;
    }

    public boolean sendWork(String finalOperation) {
        return true;
    }
    public boolean sendPath(byte[] path) {
        byte[] retBytes = socket.send(path);
        LOG.info(Arrays.toString(retBytes));
//        if (retBytes == null || retBytes.length != 18) {
//            return false;
//        }
        return true;
    }
    public boolean sendPath(int sour, int dest, int director) {
//        byte[] sendBytes = new byte[9];
        byte [] sendBytes = {0, 1, 12, 0, 12, 0, 0, 1, 1};
        byte[] sendBytes2 = new byte[8];
        sendBytes2[0] = (byte) (sour >> 8);
        sendBytes[1] = (byte) (sour & 0xFF);
        sendBytes[2] = (byte)director;
        sendBytes[3] = 1;
        sendBytes[4] = (byte) (dest >> 8);
        sendBytes[6] = (byte) (dest & 0xFF);
        sendBytes[7] = 0;
        sendBytes[8] = 0;
//        byte[18] sendBytes3 =

        byte[] retBytes = socket.send(sendBytes);
        if (retBytes == null)
            return false;
        if (retBytes.length != 18) {
            return false;
        }
        return true;
    }
    public static byte[] unitByteArray(byte[] byte1,byte[] byte2){
        byte[] unitByte = new byte[byte1.length + byte2.length];
        System.arraycopy(byte1, 0, unitByte, 0, byte1.length);
        System.arraycopy(byte2, 0, unitByte, byte1.length, byte2.length);
        return unitByte;
    }

    public void abortPath() {}

    public void pausePath() {
    }

    public void resumePath() {
    }

    public void resetAlarm() {
    }

    public void forkAction(Triple vehiclePrecisePosition, int i, int parseInt) {
    }
}
