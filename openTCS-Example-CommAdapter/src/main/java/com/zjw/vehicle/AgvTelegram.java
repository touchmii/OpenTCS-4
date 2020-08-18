package com.zjw.vehicle;

//package com.lvsrobot.vehicletcp;

public class AgvTelegram {
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
        agvInfo.setElectric(byteToUnsignedInt(retBytes[11]));
//        agvInfo.setException(byteToUnsignedInt(retBytes[5]));
        agvInfo.setStatus(byteToUnsignedInt(retBytes[12]));
        return agvInfo;
    }

    public boolean sendWork(String finalOperation) {
        return true;
    }

    public boolean sendPath(int dest) {
        byte[] sendBytes = new byte[8];
        sendBytes[0] = 'b';
        sendBytes[1] = (byte) (dest >> 8);
        sendBytes[2] = (byte) (dest & 0xFF);
        sendBytes[3] = 0;
        sendBytes[4] = 0;
        sendBytes[5] = 0;
        sendBytes[6] = 0;
        sendBytes[7] = 0;
        byte[] retBytes = socket.send(sendBytes);
        if (retBytes == null)
            return false;
        if (retBytes.length != 8) {
            return false;
        }
        return true;
    }
}
