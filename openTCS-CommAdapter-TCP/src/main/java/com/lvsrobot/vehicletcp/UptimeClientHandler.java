/*
 * Copyright 2012 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package com.lvsrobot.vehicletcp;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.CharsetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Keep reconnecting to the server while printing out the current uptime and
 * connection attempt getStatus.
 */
@Sharable
public class UptimeClientHandler extends SimpleChannelInboundHandler<Object> {

    private static final Logger LOG = LoggerFactory.getLogger(UptimeClientHandler.class);
    private final TCPCommAdapter tcpCommAdapter;
    private final AgvTelegramNew agvTelegramNew;
    public long startTime = -1;
    private ChannelHandlerContext lctx = null;
    public UptimeClientHandler(TCPCommAdapter tcpCommAdapter_, AgvTelegramNew agvTelegramNew_) {
        tcpCommAdapter = tcpCommAdapter_;
        agvTelegramNew = agvTelegramNew_;
    }
    private static Integer byteToUnsignedInt(byte data) {
        return data & 0xff;
    }
    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        if (startTime < 0) {
            startTime = System.currentTimeMillis();
        }
        LOG.info("Conencted to: {} port: {}", tcpCommAdapter.getName(), ctx.channel().remoteAddress());
//        println("Connected to: " + ctx.channel().remoteAddress());
        ctx.writeAndFlush(Unpooled.copiedBuffer("Hello", CharsetUtil.UTF_8));
        agvTelegramNew.setCtx(ctx);
        tcpCommAdapter.getProcessModel().setClientConnectFlag(true);
        lctx = ctx;
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {

        AgvInfo agvInfo = new AgvInfo();
        byte[] retBytes = (byte[]) msg;
        agvInfo.setPosition(byteToUnsignedInt(retBytes[6]) << 8 | byteToUnsignedInt(retBytes[7]));
        agvInfo.setSpeed(byteToUnsignedInt(retBytes[8]));
        agvInfo.setAngle(byteToUnsignedInt(retBytes[9]));
        agvInfo.setTuopan(byteToUnsignedInt(retBytes[10]));
        agvInfo.setBattery(byteToUnsignedInt(retBytes[11]));
//        agvInfo.setException(byteToUnsignedInt(retBytes[5]));
        agvInfo.setStatus(byteToUnsignedInt(retBytes[12]));
        agvInfo.setBizhang(byteToUnsignedInt(retBytes[13]));
        agvInfo.setCharge(byteToUnsignedInt(retBytes[16]));
        tcpCommAdapter.callback(agvInfo);
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) {
        if (!(evt instanceof IdleStateEvent)) {
            return;
        }

        IdleStateEvent e = (IdleStateEvent) evt;
        if (e.state() == IdleState.ALL_IDLE) {
//        if (e.state() == IdleState.READER_IDLE) {
            // The connection was OK but there was no traffic for last period.
//            println("Disconnecting due to no inbound traffic");
            LOG.error("Disconnect {} {} due to no inbound traffic", tcpCommAdapter.getName(), ctx.channel().remoteAddress());
            ctx.close();
        }
    }

    @Override
    public void channelInactive(final ChannelHandlerContext ctx) {
        LOG.info("{} Disconnected from: {}", tcpCommAdapter.getName(), ctx.channel().remoteAddress());
        agvTelegramNew.setCtx(null);
        tcpCommAdapter.getProcessModel().setClientConnectFlag(false);
        tcpCommAdapter.getProcessModel().setVehiclePathState(-1);
//        println("Disconnected from: " + ctx.channel().remoteAddress());
    }

    @Override
    public void channelUnregistered(final ChannelHandlerContext ctx) throws Exception {
//        println("Sleeping for: " + agvTelegramNew.RECONNECT_DELAY + 's');
        if (agvTelegramNew.reconnectFlag) {
            LOG.info("{} Sleeping for: {} s", tcpCommAdapter.getName(), agvTelegramNew.RECONNECT_DELAY);
            ctx.channel().eventLoop().schedule(new Runnable() {
                @Override
                public void run() {
                    LOG.info("{} Reconnecting to: {}:{}", tcpCommAdapter.getName(), agvTelegramNew.remote_ip, agvTelegramNew.remote_port);
    //                println("Reconnecting to: " + agvTelegramNew.remote_ip + ':' + agvTelegramNew.remote_port);
                    agvTelegramNew.disConnect();
                    agvTelegramNew.connect();
                }
            }, agvTelegramNew.RECONNECT_DELAY, TimeUnit.SECONDS);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
//        LOG.error("{} exception: {}", tcpCommAdapter.getName(), cause.getStackTrace());
        ctx.close();
    }


    public void disConnect() {
        this.lctx.close();
    }

    public void println(String msg) {
        if (startTime < 0) {
            System.err.format("[SERVER IS DOWN] %s%n", msg);
        } else {
            System.err.format("[UPTIME: %5ds] %s%n", (System.currentTimeMillis() - startTime) / 1000, msg);
        }
    }
}
