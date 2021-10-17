package com.lvsrobot.vehicletcp;


import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.opentcs.data.model.Vehicle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 处理某个客户端的请求
 *
 * @author zhb
 */
public class ServerHandler extends SimpleChannelInboundHandler<Object> {

    private TCPCommAdapter commAdapter;

    public ServerHandler(TCPCommAdapter commAdapter) {
        this.commAdapter = commAdapter;
    }

    private static final Logger LOG = LoggerFactory.getLogger(ServerHandler.class);

    private long pre_obstacle_time;

    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        commAdapter.getProcessModel().setClient_ctx(ctx);
        ctx.fireChannelActive();
    }

    @Override
    public void channelInactive(final ChannelHandlerContext ctx) {
        LOG.error("disconnect: {}", ctx.channel().remoteAddress());
        commAdapter.getProcessModel().setServerConnect(false);
        commAdapter.getProcessModel().setClient_ctx(null);
        ctx.fireChannelInactive();

    }

    @Override
    public void channelUnregistered(final ChannelHandlerContext ctx) throws Exception {
        LOG.error("XX disconnect: {}", ctx.channel().remoteAddress());
//        commAdapter.setServerConnect(false);
    }

    // 读取数据
    @Override
    public void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        //BiZhang_Flag = 1
        if (msg.toString().contains("Not Find 2DCord") || msg.toString().contains("Robot PianLi LuXian")) {
            LOG.error("{} Device Not Find Code", commAdapter.getName());
            if (commAdapter.getProcessModel().getVehicleState().equals(Vehicle.State.EXECUTING)) {
                commAdapter.getProcessModel().setVehicleProperty("runError", "ERROR");
                commAdapter.getProcessModel().setVehicleState(Vehicle.State.ERROR);
            }
        } else if (msg.toString().contains("BiZhang_Flag = 1")) {
            LOG.debug("{} Device BiZhang On", commAdapter.getName());
            pre_obstacle_time = System.currentTimeMillis();
            commAdapter.getProcessModel().setVehicleProperty("BiZhang", "On");
            commAdapter.getProcessModel().setObstacleFlag(true);
        } else if (msg.toString().contains("BiZhang_Flag = 0")) {
            LOG.debug("{} Device BiZhang Off", commAdapter.getName());
            commAdapter.getProcessModel().setVehicleProperty("BiZhang", "Off");
            commAdapter.getProcessModel().setObstacleFlag(false);
            if ( System.currentTimeMillis() - pre_obstacle_time < 3000) {
                commAdapter.addObstaclePath();
                pre_obstacle_time = System.currentTimeMillis();
//                LOG.error("{} 雷达误报警);
            }
        } else if (msg.toString().contains("robot zanting 2")) {
            commAdapter.getProcessModel().setAbortPathFlag(true);
            commAdapter.getProcessModel().setVehicleProperty("AbortPath", "On");
            LOG.error("{} Device zangting 2", commAdapter.getName());
        } else if (msg.toString().contains("robot setpathflag 0")
                && commAdapter.getProcessModel().getVehicleState().equals(Vehicle.State.EXECUTING)) {
            commAdapter.getProcessModel().setVehicleProperty("AbortPath", "");
            commAdapter.getProcessModel().setVehicleProperty("runError", "");
            LOG.error("{} Device setpathflag 0", commAdapter.getName());
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

