package com.lvsrobot.vehicletcp;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.util.CharsetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
                    .childHandler(new Chanel(this.commAdapter) //配置客户端句柄
                    );

            // 绑定端口，开始接受链接
            ChannelFuture cf = sbs.bind(port).sync();

            // 等待服务端口的关闭；在这个例子中不会发生，但你可以优雅实现；关闭你的服务
//            cf.channel().closeFuture().sync();
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
                LOG.error("cant write: {}", e.getMessage());
            }
            return true;
        } else {
            return false;
        }
    }


}

