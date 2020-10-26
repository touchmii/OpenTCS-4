package com.lvsrobot.vehiclejbh;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AgvTelegram {
    private SocketUtils socket;
    AgvInfo agvInfo = new AgvInfo();
    public AgvTelegram(String ip, int port) {
        socket = new SocketUtils(ip, port);
    }

    private static final Logger LOG = LoggerFactory.getLogger(AgvTelegram.class);

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
//        byte[] sendBytes = {0, 1, 2, 1, (byte)253};
//        sendBytes[0] = 'a';
//        sendBytes[1] = 0;
//        sendBytes[2] = 0;
//        sendBytes[3] = 0;
//        sendBytes[4] = 0;
//        sendBytes[5] = 0;
//        sendBytes[6] = 0;
//        sendBytes[7] = 0;
        int[] query_command = {0xFFDD, 0x01};
        byte[] retBytes = socket.send(ttoByteArray(query_command));
        if (retBytes == null)
            return null;
        if (retBytes.length < 18) {
            return null;
        }
//        AgvInfo agvInfo = new AgvInfo();
//        agvInfo.setPosition(byteToUnsignedInt(retBytes[6]) << 8 | byteToUnsignedInt(retBytes[7]));
//        agvInfo.setSpeed(byteToUnsignedInt(retBytes[8]));
//        agvInfo.setAngle(byteToUnsignedInt(retBytes[9]));
//        agvInfo.setElectric(byteToUnsignedInt(retBytes[11]));
//        agvInfo.setException(byteToUnsignedInt(retBytes[5]));
//        agvInfo.setStatus(byteToUnsignedInt(retBytes[12]));




//        agvInfo.setCurrentPosition({byteToUnsignedInt(retBytes[0]) << 8 | byteToUnsignedInt(retBytes[1]), byteToUnsignedInt(retBytes[2]) << 8 | byteToUnsignedInt(retBytes[3])});
//        agvInfo.setSpeed(byteToUnsignedInt(retBytes[3]));
//        agvInfo.setElectric(byteToUnsignedInt(retBytes[4]));
//        agvInfo.setException(byteToUnsignedInt(retBytes[5]));
//        agvInfo.setStatus(byteToUnsignedInt(retBytes[6]));
        int[] retInts = BeToIntArray(retBytes);
        agvInfo.setStatus(retInts[7]);
        int[] precisePosition = {retInts[2], retInts[3]};
        agvInfo.setPrecisePosition(precisePosition);
//        int[] currentPosition = {byteToUnsignedInt(retBytes[12]), byteToUnsignedInt(retBytes[13])};
//        agvInfo.setCurrentPosition(currentPosition);
//        int[] previousPosition = {byteToUnsignedInt(retBytes[14]), byteToUnsignedInt(retBytes[15])};
//        agvInfo.setPreviousPositon(previousPosition);
        double orientation = (double)retInts[5];
        agvInfo.setVehicleOrientation(orientation);
        agvInfo.setBattery(retInts[6]);
//        agvInfo.setLoadStatus(byteToUnsignedInt(retBytes[32]));
//        agvInfo.setCharge_status(byteToUnsignedInt(retBytes[33]));
//        agvInfo.setSpeed(byteToUnsignedInt(retBytes[52]));
        //是否触发避障，0未触发 1触发 地址18
//        agvInfo.setVehicleAvoidance(byteToUnsignedInt(retBytes[18]));

        return agvInfo;
    }

    public boolean sendWork(String finalOperation) {
        return true;
    }

    public boolean sendPath(int[] _path) {
//        byte[] sendBytes = new byte[9];
//        byte[] senBytes = new byte[leng(_path)];

//        byte [] sendBytes = {0, 1, 12, 0, 12, 0, 0, 1, 1};
//        byte[] sendBytes2 = new byte[8];
//        sendBytes2[0] = (byte) (sour >> 8);
//        sendBytes[1] = (byte) (sour & 0xFF);
//        sendBytes[2] = (byte)director;
//        sendBytes[3] = 1;
//        sendBytes[4] = (byte) (dest >> 8);
//        sendBytes[6] = (byte) (dest & 0xFF);
//        sendBytes[7] = 0;
//        sendBytes[8] = 0;
//        byte[18] sendBytes3 =
        LOG.info("Int to Bytes", ttoByteArray(_path));

        byte[] retBytes = socket.send(ttoByteArray(_path));
//        if (retBytes == null)
//            return false;
//        if (retBytes.length != 8) {
//            return false;
//        }
        return true;
    }

    /**
     *
     * @param byte1
     * @param byte2
     * @return
     */
    public static byte[] unitByteArray(byte[] byte1,byte[] byte2){
        byte[] unitByte = new byte[byte1.length + byte2.length];
        System.arraycopy(byte1, 0, unitByte, 0, byte1.length);
        System.arraycopy(byte2, 0, unitByte, byte1.length, byte2.length);
        return unitByte;
    }

    /**
     * Byte数组转Int数组
     * @param bytes
     * @return
     */
    static public int[] BeToIntArray(byte[] bytes) {
        int[] dst = new int[bytes.length / 2];
        for (int i = 0, j = 0; i < dst.length; i++, j += 2)
            dst[i] = ((bytes[j] & 0xff) << 8) | (bytes[j + 1] & 0xff);
        return dst;
    }

    /**
     * Int数组转Byte数组
     * @param i16
     * @return
     */
    static public byte[] ttoByteArray(int[] i16) {
        byte[] dst = new byte[i16.length * 2];
        for (int i = 0, j = 0; i < i16.length; i++, j += 2) {
            dst[j] = (byte) ((i16[i] >> 8) & 0xff);
            dst[j + 1] = (byte) (i16[i] & 0xff);
        }
        return dst;
    }
}
