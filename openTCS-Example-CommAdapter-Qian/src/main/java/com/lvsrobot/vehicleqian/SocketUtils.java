package com.lvsrobot.vehicleqian;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import org.opentcs.contrib.tcp.netty.*;

public class SocketUtils {
    private String ip;
    private int port;

    public SocketUtils(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }

    public byte[] send(byte[] bytes) {
        Socket socket = null;
        try {
            socket = new Socket(ip, port);
            socket.setSoTimeout(500);
            OutputStream out = socket.getOutputStream();
            out.write(bytes);
            InputStream in = socket.getInputStream();
            byte[] buf = new byte[18];
            int len = in.read(buf);
            out.close();
            in.close();
            socket.close();
            if (len == 0)
                return null;
            return buf;
        } catch (Exception ex) {
            try {
                socket.close();
            } catch (Exception e) {
            }
            return null;
        } finally {
            try {
                socket.close();
            } catch (Exception e) {
            }
        }
    }
}
