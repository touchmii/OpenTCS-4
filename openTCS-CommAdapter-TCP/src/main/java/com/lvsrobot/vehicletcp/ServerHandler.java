package com.lvsrobot.vehicletcp;


import java.io.UnsupportedEncodingException;


import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import org.opentcs.data.model.Vehicle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 处理某个客户端的请求
 * @author zhb
 */
public class ServerHandler extends SimpleChannelInboundHandler<Object> {

    private TCPCommAdapter commAdapter;
    public ServerHandler(TCPCommAdapter commAdapter) {
        this.commAdapter = commAdapter;
    }

    private static final Logger LOG = LoggerFactory.getLogger(ServerHandler.class);

    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        commAdapter.getProcessModel().setClient_ctx(ctx);
        ctx.fireChannelActive();
    }

    @Override
    public void channelInactive(final ChannelHandlerContext ctx) {
        LOG.error("disconnect: {}", ctx.channel().remoteAddress());
        commAdapter.getProcessModel().setServerConnect(false);
        try {
            super.channelActive(ctx);
        } catch (Exception e) {
            e.printStackTrace();
        }
        commAdapter.getProcessModel().setClient_ctx(null);

    }

    @Override
    public void channelUnregistered(final ChannelHandlerContext ctx) throws Exception {
        LOG.error("XX disconnect: {}", ctx.channel().remoteAddress());
//        commAdapter.setServerConnect(false);
    }

    // 读取数据
    @Override
    public void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
//        ctx.writeAndFlush(Unpooled.copiedBuffer("Ackxxx", CharsetUtil.UTF_8));
//        ctx.writeAndFlush(msg);
//        if (msg != null) {
//            LOG.info("{} recive :{}, msg: {}", commAdapter.getName(), ctx.channel().remoteAddress(), msg);
//        }
        //BiZhang_Flag = 1
        if(msg.toString().contains("Not Find 2DCord") || msg.toString().contains("Robot PianLi LuXian")) {
            LOG.error("{} Device Not Find Code", commAdapter.getName());
            if (commAdapter.getProcessModel().getVehicleState().equals(Vehicle.State.EXECUTING)) {
                commAdapter.getProcessModel().setVehicleProperty("runError", "ERROR");
                commAdapter.getProcessModel().setVehicleState(Vehicle.State.ERROR);
            }
        } else if (msg.toString().contains("BiZhang_Flag = 1")) {
            LOG.error("{} Device BiZhang On", commAdapter.getName());
            commAdapter.getProcessModel().setVehicleProperty("BiZhang", "On");
        } else if (msg.toString().contains("BiZhang_Flag = 0")) {
            LOG.error("{} Device BiZhang Off", commAdapter.getName());
            commAdapter.getProcessModel().setVehicleProperty("BiZhang", "Off");
        } else if (msg.toString().contains("robot zanting 2")) {
            commAdapter.getProcessModel().setAbortPathFlag(true);
            commAdapter.getProcessModel().setVehicleProperty("AbortPath", "On");
        } else if (msg.toString().contains("robot setpathflag 0")
                && commAdapter.getProcessModel().getVehicleState().equals(Vehicle.State.EXECUTING)) {
//            commAdapter.getProcessModel().setVehicleProperty("AbortPath", "On");
            LOG.error("Device setpathflag 0");
            commAdapter.getProcessModel().setVehicleState(Vehicle.State.ERROR);
        }
    }

    // 出现异常的处理
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.err.println("server 读取数据出现异常");
        ctx.close();
    }

}

