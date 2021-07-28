/**
 * Copyright (c) The openTCS Authors.
 *
 * This program is free software and subject to the MIT license. (For details,
 * see the licensing information (LICENSE.txt) you should have received with
 * this copy of the software.)
 */
package org.opentcs.kernel.extensions.sockethost.status;

import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Keep reconnecting to the server while printing out the current uptime and
 * connection attempt getStatus.
 */
@Sharable
public class MESClientHandler extends SimpleChannelInboundHandler<Object> {

    /**
     * This class's Logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger(MESClientHandler.class);

    public long startTime = -1;
    private ChannelHandlerContext lctx = null;
    private static Integer byteToUnsignedInt(byte data) {
        return data & 0xff;
    }
    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        if (startTime < 0) {
            startTime = System.currentTimeMillis();
        }
        LOG.info("Conncted to MES : {}", ctx.channel().remoteAddress());
//        ctx.writeAndFlush(Unpooled.copiedBuffer("Hello", CharsetUtil.UTF_8));
        lctx = ctx;
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        // Discard received data
//        ByteBuf byteBuf =  (ByteBuf) msg;
//        byte[] m = SerializationU
//        System.out.println("Recive client: " + ctx.channel().remoteAddress() + " message: " + byteBuf.toString(CharsetUtil.UTF_8));
        Date d = new Date();
//        System.out.println("Recive client: " + d.toGMTString() + ctx.channel().remoteAddress() + " message: " + javax.xml.bind.DatatypeConverter.printHexBinary((byte[]) msg));
//        ctx.channel().writeAndFlush(msg);
    }
//    @Override
//    public void channelRead(ChannelHandlerContext ctx, Obj)

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) {
        if (!(evt instanceof IdleStateEvent)) {
            return;
        }

        IdleStateEvent e = (IdleStateEvent) evt;
        if (e.state() == IdleState.ALL_IDLE) {
//        if (e.state() == IdleState.READER_IDLE) {
            // The connection was OK but there was no traffic for last period.
            LOG.info("Disconnecting due to no inbound traffic");
            ctx.close();
        }
    }

    @Override
    public void channelInactive(final ChannelHandlerContext ctx) {
        LOG.info("Disconnected MES from: {}", ctx.channel().remoteAddress());
    }

    @Override
    public void channelUnregistered(final ChannelHandlerContext ctx) throws Exception {
        LOG.debug("Sleeping for: {} s Connect to MES", StatusMessageDispatcher.RECONNECT_DELAY);

        ctx.channel().eventLoop().schedule(new Runnable() {
            @Override
            public void run() {
                StatusMessageDispatcher.connect();
            }
        }, StatusMessageDispatcher.RECONNECT_DELAY, TimeUnit.SECONDS);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }


    public void disConnect() {
        this.lctx.close();
    }

}
