package com.lvsrobot.vehicletcp;


import java.io.UnsupportedEncodingException;


import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.util.ReferenceCountUtil;
import org.opentcs.customizations.kernel.KernelExecutor;
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

    @Override
    public void channelInactive(final ChannelHandlerContext ctx) {
        LOG.error("disconnect: {}", ctx.channel().remoteAddress());
        commAdapter.setServerConnect(false);
    }

    @Override
    public void channelUnregistered(final ChannelHandlerContext ctx) throws Exception {
        LOG.error("XX disconnect: {}", ctx.channel().remoteAddress());
//        commAdapter.setServerConnect(false);
    }

    // 读取数据
    @Override
    public void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
//        if (msg != null) {
//            LOG.info("{} recive :{}, msg: {}", commAdapter.getName(), ctx.channel().remoteAddress(), msg);
//        }
        //BiZhang_Flag = 1
        if(msg.toString().contains("Not Find 2DCord") || msg.toString().contains("Robot PianLi LuXian")) {
            LOG.error("{} Device Not Find Code", commAdapter.getName());
            commAdapter.setFindcode(false);
            commAdapter.getProcessModel().setVehicleState(Vehicle.State.ERROR);
        } else if (msg.toString().contains("BiZhang_Flag = 1")) {
            LOG.error("{} Device BiZhang On", commAdapter.getName());
            commAdapter.setPauseFlag(true);
        } else if (msg.toString().contains("BiZhang_Flag = 0")) {
            LOG.error("{} Device BiZhang Off", commAdapter.getName());
            commAdapter.setPauseFlag(false);
        }
        // 普通的处理 及过滤器不多
//        simpleRead(ctx, msg);

        // 有分隔符处理信息
//      Delimiterread(ctx, msg);
    }


    /**
     * 最简单的处理
     * @param ctx
     * @param msg
     * @throws UnsupportedEncodingException
     */
    public void simpleRead(ChannelHandlerContext ctx, Object msg) throws UnsupportedEncodingException{

        ByteBuf bb = (ByteBuf)msg;
        // 创建一个和buf同等长度的字节数组
        byte[] reqByte = new byte[bb.readableBytes()];
        // 将buf中的数据读取到数组中
        bb.readBytes(reqByte);
        String reqStr = new String(reqByte, 8);
        System.err.println("server 接收到客户端的请求： " + reqStr);
        String respStr = new StringBuilder("来自服务器的响应").append(reqStr).append("$_").toString();

        // 返回给客户端响应                                                                                                                                                       和客户端链接中断即短连接，当信息返回给客户端后中断
        ctx.writeAndFlush(Unpooled.copiedBuffer(respStr.getBytes()));//.addListener(ChannelFutureListener.CLOSE);
        // 有了写操作（writeAndFlush）下面就不用释放msg
//      ReferenceCountUtil.release(msg);
    }

    /**
     * 有分隔符的请求信息分包情况处理，包含了转码
     * @param ctx
     * @param msg
     */
    private void Delimiterread(ChannelHandlerContext ctx, Object msg) {
        // 如果把msg直接转成字符串，必须在服务中心添加 socketChannel.pipeline().addLast(new StringDecoder());
        String reqStr = (String)msg;
        System.err.println("server 接收到请求信息是："+reqStr);
        String respStr = new StringBuilder("来自服务器的响应").append(reqStr).append("$_").toString();

        // 返回给客户端响应                                                                                                                                                       和客户端链接中断即短连接，当信息返回给客户端后中断
        ctx.writeAndFlush(Unpooled.copiedBuffer(respStr.getBytes())).addListener(ChannelFutureListener.CLOSE);
    }


    // 数据读取完毕的处理
//    @Override
//    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
//        System.err.println("服务端读取数据完毕");
//    }

    // 出现异常的处理
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.err.println("server 读取数据出现异常");
        ctx.close();
    }

    /**
     * 将请求信息直接转成对象
     * @param ctx
     * @param msg
     */
//    private void handlerObject(ChannelHandlerContext ctx, Object msg) {
//        // 需要序列化 直接把msg转成对象信息，一般不会用，可以用json字符串在不同语言中传递信息
//        Student student = (Student)msg;
//        System.err.println("server 获取信息："+student.getId()+student.getName());
//        student.setName("李四");
//        ctx.write(student);
//    }


}

