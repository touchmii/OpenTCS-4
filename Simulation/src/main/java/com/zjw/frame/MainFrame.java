package com.zjw.frame;

import com.zjw.entity.AgvInfo;
import com.zjw.utils.ByteUtils;

import javax.swing.*;
import java.awt.*;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class MainFrame extends JFrame {
    private int width = 900;
    private int height = 550;
    private JLabel lblCurrent;
    private JTextArea firstTextArea;
    private JLabel lblOther;
    private JTextArea secondTextArea;
    private JScrollPane firstTextAreaScrollPane;
    private JScrollPane secondTextAreaScrollPane;
    private JLabel lblPort;
    private JTextField txtPort;
    private JButton btnStartServer;
    private JButton btnGetCurrent;
    private JButton btnSet;
    public boolean isStarted = false;
    public Map<String, AgvInfo> agvInfos;//存储AGV信息

    public MainFrame() {
        agvInfos = new LinkedHashMap<>();
        //获得屏幕分辨率
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Dimension dims = toolkit.getScreenSize();
        int sWidth = (int) dims.getWidth();
        int sHeight = (int) dims.getHeight();
        this.setSize(width, height);
        this.setLayout(null);
        this.setTitle("AGV模拟器");
        this.setResizable(false);//不能放大缩小
        this.setLocation((sWidth - width) / 2, (sHeight - height) / 2);
        initWidget();
        setWidgetBounds();
        addWidget();
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setVisible(true);
        setListener();
        new Thread(() -> {
            while (true) {
                try {
                    SwingUtilities.invokeLater(() -> {
                        String str = "";
                        //循环显示agv信息
                        for (Map.Entry<String, AgvInfo> pair : agvInfos.entrySet()) {
                            str += "AGV" + pair.getKey() + "  当前位置：" + pair.getValue().getCurrentPosition() + "   当前状态：" + pair.getValue().getStatus() + "   当前电量：" + pair.getValue().getElectric() + "   瞬时速度：" + pair.getValue().getSpeed() + "   异常：" + pair.getValue().getException() + "\r\n";
                        }
                        firstTextArea.setText(str);
                    });
                    Thread.sleep(500);
                } catch (Exception ex) {
                }


            }
        }).start();
    }

    private void setListener() {
        btnGetCurrent.addActionListener(e -> {
            String str = "";
            for (Map.Entry<String, AgvInfo> entry : agvInfos.entrySet()) {
                str = str + entry.getValue().getCurrentPosition() + ";" + entry.getValue().getStatus() + ";" + entry.getValue().getElectric() + ";" + entry.getValue().getSpeed() + ";" + entry.getValue().getException() + "\r\n";
            }
            secondTextArea.setText(str);
        });

        btnSet.addActionListener(e -> {
            try {
                List<String> list = getLines(secondTextArea);
                int startPort = Integer.parseInt(txtPort.getText().trim());
                for (int i = 0; i < list.size(); i++) {
                    int port = startPort + i;
                    String[] split = list.get(i).split(";");
                    agvInfos.get(port + "").setCurrentPosition(Integer.parseInt(split[0]));
                    agvInfos.get(port + "").setStatus(Integer.parseInt(split[1]));
                    agvInfos.get(port + "").setElectric(Integer.parseInt(split[2]));
                    agvInfos.get(port + "").setSpeed(Integer.parseInt(split[3]));
                    agvInfos.get(port + "").setException(Integer.parseInt(split[4]));
                }
            } catch (Exception ex) {

            }
        });

        btnStartServer.addActionListener(e -> {
            if (isStarted) {
                return;
            }
            isStarted = true;
            new Thread(() ->
            {
                try {
                    List<String> lines = getLines(secondTextArea);
                    int port = Integer.parseInt(txtPort.getText().trim());
                    //循环开启服务
                    for (int i = 0; i < lines.size(); i++) {
                        int myPort = port + i;
                        String[] split = lines.get(i).split(";");
                        AgvInfo agvInfo = new AgvInfo();
                        agvInfo.setCurrentPosition(Integer.parseInt(split[0]));
                        agvInfo.setStatus(Integer.parseInt(split[1]));
                        agvInfo.setElectric(Integer.parseInt(split[2]));
                        agvInfo.setSpeed(Integer.parseInt(split[3]));
                        agvInfo.setException(Integer.parseInt(split[4]));
                        agvInfos.put(myPort + "", agvInfo);
                        new StartServerThread(myPort).start();
                    }
                } catch (Exception ex) {
                    isStarted = false;
                }
            }).start();
        });
    }

    class StartServerThread extends Thread {
        private int port;
        ServerSocket serverSocket;

        public StartServerThread(int port) {
            this.port = port;
        }

        @Override
        public void run() {
            try {
                serverSocket = new ServerSocket(port);
                while (true) {
                    //建立连接，获取socket对象
                    Socket socket = serverSocket.accept();
                    new ClientHandler(port, socket).start();
                }
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
        }
    }

    class ClientHandler extends Thread {
        private int port;
        private Socket socket;

        public ClientHandler(int port, Socket socket) {
            this.port = port;
            this.socket = socket;
        }

        @Override
        public void run() {
            try {
                socket.setSoTimeout(1000);
                InputStream in = socket.getInputStream();
                byte[] retBytes = new byte[4096];
                int recCount = in.read(retBytes);
                OutputStream out = socket.getOutputStream();
                if (recCount != 8) {
                    out.write(getErrorBytes());
                } else if (ByteUtils.byteToUnsignedInt(retBytes[0]) == 'a') {
                    out.write(getStatusBytes(port));
                } else if (ByteUtils.byteToUnsignedInt(retBytes[0]) == 'b') {
                    int destPosition = ByteUtils.byteToUnsignedInt(retBytes[1]) << 8 | ByteUtils.byteToUnsignedInt(retBytes[2]);
                    doExecute(port, destPosition);
                    out.write(getSuccessByte());
                } else {
                    out.write(getErrorBytes());
                }
                out.close();
                in.close();
            } catch (Exception e) {
            }
        }
    }

    private void doExecute(int port, int position) {
        new ExecuteThread(port, position).start();
    }

    class ExecuteThread extends Thread {
        private int port;
        private int position;

        public ExecuteThread(int port, int position) {
            this.port = port;
            this.position = position;
        }

        @Override
        public void run() {
            try {
                if (agvInfos.get(port + "").getStatus() == 1) {
                    return;
                }
                agvInfos.get(port + "").setStatus(1);
                Thread.sleep(5000);
                agvInfos.get(port + "").setCurrentPosition(position);
                agvInfos.get(port + "").setStatus(0);
            } catch (Exception ex) {

            }
        }

    }

    private byte[] getSuccessByte() {
        byte[] bytes = new byte[8];
        bytes[0] = (byte) 'b';
        bytes[1] = (byte) 'o';
        bytes[2] = 0;
        bytes[3] = 0;
        bytes[4] = 0;
        bytes[5] = 0;
        bytes[6] = 0;
        bytes[7] = 0;
        return bytes;
    }

    private byte[] getStatusBytes(int port) {
        AgvInfo agvInfo = agvInfos.get(port + "");
        byte[] bytes = new byte[8];
        bytes[0] = (byte) 'a';
        bytes[1] = (byte) (agvInfo.getCurrentPosition() >> 8);
        bytes[2] = (byte) agvInfo.getCurrentPosition();
        bytes[3] = (byte) agvInfo.getSpeed();
        bytes[4] = (byte) agvInfo.getElectric();
        bytes[5] = (byte) agvInfo.getException();
        bytes[6] = (byte) agvInfo.getStatus();
        bytes[7] = 0;
        return bytes;
    }

    private byte[] getErrorBytes() {
        byte[] bytes = new byte[8];
        bytes[0] = (byte) 'c';
        bytes[1] = 0;
        bytes[2] = 0;
        bytes[3] = 0;
        bytes[4] = 0;
        bytes[5] = 0;
        bytes[6] = 0;
        bytes[7] = 0;
        return bytes;
    }


    private List<String> getLines(JTextArea area) {
        String str = area.getText().replace("\r", "");
        String[] strArray = str.split("\n");
        List<String> list = new ArrayList<>();
        for (String str1 : strArray) {
            if (str1.trim().length() != 0) {
                list.add(str1.trim());
            }
        }
        return list;
    }

    private void initWidget() {
        lblCurrent = new JLabel("当前信息");
        firstTextArea = new JTextArea();
        firstTextAreaScrollPane = new JScrollPane(firstTextArea);
        lblOther = new JLabel("其他信息");

        secondTextArea = new JTextArea("0;0;80;3;0");
        secondTextAreaScrollPane = new JScrollPane(secondTextArea);

        lblPort = new JLabel("起始端口号：");
        txtPort = new JTextField("4001");
        btnStartServer = new JButton("开启服务");
        btnGetCurrent = new JButton("获取当前信息");
        btnSet = new JButton("设置");
    }

    private void setWidgetBounds() {
        lblCurrent.setBounds(10, 5, 200, 30);
        firstTextAreaScrollPane.setBounds(10, 35, 880, 200);
        lblOther.setBounds(10, 236, 200, 30);
        secondTextAreaScrollPane.setBounds(10, 267, 880, 200);
        lblPort.setBounds(10, 470, 160, 30);
        txtPort.setBounds(100, 470, 100, 30);
        btnStartServer.setBounds(210, 470, 100, 30);
        btnGetCurrent.setBounds(320, 470, 160, 30);
        btnSet.setBounds(490, 470, 100, 30);
    }

    private void addWidget() {
        this.add(lblCurrent);
        this.add(firstTextAreaScrollPane);
        this.add(lblOther);
        this.add(secondTextAreaScrollPane);
        this.add(lblPort);
        this.add(txtPort);
        this.add(btnStartServer);
        this.add(btnGetCurrent);
        this.add(btnSet);
    }
}
