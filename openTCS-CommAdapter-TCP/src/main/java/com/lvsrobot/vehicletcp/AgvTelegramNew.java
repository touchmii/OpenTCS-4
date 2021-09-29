package com.lvsrobot.vehicletcp;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.bytes.ByteArrayDecoder;
import io.netty.handler.codec.bytes.ByteArrayEncoder;
import io.netty.handler.timeout.IdleStateHandler;
import org.opentcs.data.model.Triple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.border.MatteBorder;


public class AgvTelegramNew {

    private static final Logger LOG = LoggerFactory.getLogger(AgvTelegramNew.class);
    AgvInfo agvInfo = new AgvInfo();

    static final int RECONNECT_DELAY = 5;
    static final int READ_TIMEOUT = 0;
    static final int WRITE_TIMEOUT = 0;
    static final int IDLE_TIMEOUT = 50;
    static String remote_ip = null;
    static int remote_port = 0;
    static ChannelFuture f = null;
    EventLoopGroup group = null;
    private final UptimeClientHandler handler;
    private Bootstrap bs = null;
    private String name;
    public AgvTelegramNew(String ip, int port, TCPCommAdapter tcpCommAdapter) {
        handler = new UptimeClientHandler(tcpCommAdapter, this);
        name = tcpCommAdapter.getName();
        remote_ip = ip;
        remote_port = port;
        group = new NioEventLoopGroup();
        bs = new Bootstrap();
        bs.group(group)
                .channel(NioSocketChannel.class)
                .remoteAddress(ip, port)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new ByteArrayEncoder());
                        ch.pipeline().addLast(new ByteToMsgDecoder());
                        ch.pipeline().addLast(new ByteArrayDecoder());
                        ch.pipeline().addLast(new IdleStateHandler(READ_TIMEOUT, WRITE_TIMEOUT, IDLE_TIMEOUT), handler);
                    }
                });
        f = bs.connect();

    }

    public boolean isConnected() {
        if (f == null) {
            return false;
        } else {
            return f.channel().isActive();
        }
    }
    public void Connect() {
//        if(!this.isConnected()) {
//            f = bs.connect();
//        }
    }
    public void disConnect() {
        if(this.isConnected()) {
            handler.disConnect();
        }
    }

    public void Terminal() {
        try {

            f.channel().closeFuture();
            f = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
        group.shutdownGracefully();
    }

    public void connect() {
        bs.connect().addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                if (future.cause() != null) {
                    handler.startTime = -1;
//                    handler.println("Failed to connect");
                    LOG.error("{} Failed to connect: {} by: {}", remote_ip,future.cause());
                } else {
                    f = future;
                }
            }
        });
    }

    public synchronized AgvInfo getAgvInfo() {
        this.Connect();
        byte[] query = {0, 1, 2, 1, (byte)253};
        f.channel().writeAndFlush(query);
        return agvInfo;
    }
    public synchronized boolean sendPath(byte[] path) {
        this.Connect();
        f.channel().writeAndFlush(path);
        LOG.debug("{} send path: {}", this.name, ByteBufUtil.hexDump(path));
        return true;
    }

    public synchronized boolean radarDis(int distance, int width, int length) {
        this.Connect();
        byte[] radarCommand = {0, 1, 4, 0, 3, (byte)distance, (byte)width, (byte)length, 0};
        byte check = 0;
        for(int i=0; i<8;i++) {
            check = (byte) (check ^ radarCommand[i]);
        }
        radarCommand[8] = (byte) ~ check;
        try {

            Thread.sleep(200);//毫秒
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        f.channel().writeAndFlush(radarCommand);
        LOG.debug("send radar command: {}", ByteBufUtil.hexDump(radarCommand));
        try {

            Thread.sleep(200);//毫秒
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return true;
    }

    public synchronized boolean abortPath() {
        this.Connect();
        byte[] abort_path = {0, 1, 6, 0, 2, 0, 0};
        f.channel().writeAndFlush(abort_path);
        return true;
    }

    public void charge() {
        this.Connect();
        byte[] charge = {0, 1, 3, 0, 2, 2, 0, (byte)253};
        f.channel().writeAndFlush(charge);
    }
    public void discharge() {
        this.Connect();
        byte[] discharge = {0, 1, 3, 0, 2, 3, 0, (byte)252};
        f.channel().writeAndFlush(discharge);
    }

    public void pausePath() {
    }

    public void resumePath() {
    }

    public void resetAlarm() {
    }

    public void forkAction(Triple vehiclePrecisePosition, int i, int parseInt) {
    }
    public void liftAction(String point, LIFTACTION action) {
        int point_num = Integer.parseInt(point);
        byte action_;
        if (action == LIFTACTION.UP) {
            action_ = 2;
        } else {
            action_ = 0x12;
        }
        byte[] path_data = {0, 1, 0xc, 0, 8, 0, 0, 1, 1, (byte)(point_num/256), (byte)(point_num%256), action_, 0, 0};
        byte check = 0;
        for(int i = 0; i < 13; i++) {
            check = (byte) (check ^ path_data[i]);
        }
        path_data[13] = (byte) ~ check;
        LOG.debug("{} send lift action command: {}", this.name, ByteBufUtil.hexDump(path_data));
        f.channel().writeAndFlush(path_data);
    }

    public void sendWork(String operation) {
    }

    public enum LIFTACTION {
        UP,
        DOWN;
    }
}
