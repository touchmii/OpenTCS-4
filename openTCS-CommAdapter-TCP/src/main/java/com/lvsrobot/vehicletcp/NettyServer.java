package com.lvsrobot.vehicletcp;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.LineSeparator;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.util.CharsetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.SocketAddress;

// Constant里面用到的几个参数自己可以直接写死
//import com.test.thread.utils.Constant;

/**
 * tcp/ip 服务端用netty实现
 * @author zhb
 *
 */
public class NettyServer extends Thread {

    private static final Logger LOG = LoggerFactory.getLogger(NettyServer.class);

    private int port;
    private TCPCommAdapter commAdapter;
    private SocketChannel clientHandle;
    private ChannelHandlerContext ctx;
    public NettyServer(int port, TCPCommAdapter commAdapter){
        this.port = port;
        this.commAdapter = commAdapter;
    }

    class Chanel extends ChannelInitializer<SocketChannel>

    {
        private TCPCommAdapter commAdapter;
        public Chanel(TCPCommAdapter commAdapter) {
            this.commAdapter = commAdapter;
        }

        @Override
        protected void initChannel (SocketChannel socketChannel) throws Exception {
            LOG.info("client connect: {}", socketChannel.remoteAddress());
//            commAdapter.setServerConnect(true);

        // marshalling 序列化对象的解码
//                      socketChannel.pipeline().addLast(MarshallingCodefactory.buildDecoder());
        // marshalling 序列化对象的编码
//                      socketChannel.pipeline().addLast(MarshallingCodefactory.buildEncoder());
        // 网络超时时间
//                      socketChannel.pipeline().addLast(new ReadTimeoutHandler(5));
        // 处理接收到的请求
//                            socketChannel.pipeline().addLast(new DelimiterBasedFrameDecoder(50, Unpooled.copiedBuffer("\n", CharsetUtil.UTF_8)));
            socketChannel.pipeline().addLast(new StringDecoder());
            socketChannel.pipeline().addLast(new ServerHandler(this.commAdapter)); // 这里相当于过滤器，可以配置多个
            //TODO 判断连接的客户端为小车，有可能是其它客户端连接。
//            commAdapter.setTcpServerHandle(socketChannel);
//            clientHandle = socketChannel;
//            ctx = socketChannel.pipeline().
        }
    }

    // netty 服务端启动
    @Override
    public void run() {

        // 用来接收进来的连接
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        // 用来处理已经被接收的连接，一旦bossGroup接收到连接，就会把连接信息注册到workerGroup上
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            // nio服务的启动类
            ServerBootstrap sbs = new ServerBootstrap();
            // 配置nio服务参数
            sbs.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class) // 说明一个新的Channel如何接收进来的连接
                    .option(ChannelOption.SO_BACKLOG, 128) // tcp最大缓存链接个数,配置服务器参数
                    .childOption(ChannelOption.SO_KEEPALIVE, true) //保持连接,配置客户端参数
//                    .handler(new LoggingHandler(LogLevel.INFO)) // 打印日志级别
//                    .handler(new ChannelOutboundHandlerAdapter() {
//                        @Override
//                        public void connect(ChannelHandlerContext ctx, SocketAddress remoteAddress,
//                                SocketAddress localAddress, ChannelPromise promise) throws Exception {
//                            ctx.connect(remoteAddress, localAddress, promise);
//                            ctx.write("helloxx", ctx.voidPromise());
//                        }
//
//                    })
                    .childHandler(new Chanel(this.commAdapter) //配置客户端句柄
                    );

            // 绑定端口，开始接受链接
            ChannelFuture cf = sbs.bind(port).sync();

            // 开多个端口
//          ChannelFuture cf2 = sbs.bind(3333).sync();
//          cf2.channel().closeFuture().sync();

            // 等待服务端口的关闭；在这个例子中不会发生，但你可以优雅实现；关闭你的服务
            cf.channel().closeFuture().sync();
            LOG.info("{} Close server", commAdapter.getName());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally{
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    public boolean sendMsg(String str) {
        if (clientHandle != null && clientHandle.isActive()) {
            try {
                clientHandle.writeAndFlush(Unpooled.copiedBuffer(str, CharsetUtil.UTF_8));
            } catch (Exception e) {
                LOG.error("cant write: {}", e.getStackTrace());
            }
            return true;
        } else {
            return false;
        }
    }


    // 开启netty服务线程

    /**
     *
     * 解决数据传输中中的拆包和粘包问题方案：
     *
     * 一 . 用特定字符当做分隔符，例如：$_
     *  （1） 将下列代码添加到 initChannel方法内
     //将双方约定好的分隔符转成buf
     ByteBuf bb = Unpooled.copiedBuffer("$_".getBytes(Constant.charset));
     socketChannel.pipeline().addLast(new DelimiterBasedFrameDecoder(1024, bb));
     //将接收到信息进行解码，可以直接把msg转成字符串
     socketChannel.pipeline().addLast(new StringDecoder());

     （2） 在 ServerHandler中的 channelRead方法中应该替换内容为
     // 如果把msg直接转成字符串，必须在服务中心添加 socketChannel.pipeline().addLast(new StringDecoder());
     String reqStr = (String)msg;
     System.err.println("server 接收到请求信息是："+reqStr);
     String respStr = new StringBuilder("来自服务器的响应").append(reqStr).append("$_").toString();
     // 返回给客户端响应
     ctx.writeAndFlush(Unpooled.copiedBuffer(respStr.getBytes()));

     (3) 因为分隔符是双方约定好的，在ClientNetty和channelRead中也应该有响应的操作


     二. 双方约定好是定长报文
     // 双方约定好定长报文为6，长度不足时服务端会一直等待直到6个字符，所以客户端不足6个字符时用空格补充；其余操作，参考分隔符的情况
     socketChannel.pipeline().addLast(new FixedLengthFrameDecoder(6));


     三. 请求分为请求头和请求体，请求头放的是请求体的长度；一般生产上常用的

     （1）通信双方约定好报文头的长度，先截取改长度，
     （2）根据报文头的长度读取报文体的内容
     *
     *
     */
}

