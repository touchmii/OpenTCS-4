package com.lvsrobot.vehicletcp;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

public class ByteToMsgDecoder extends ByteToMessageDecoder {
    byte[] head = {0, 1, (byte)130};
    ByteBuf header = Unpooled.copiedBuffer(head);
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
//
        if(in.readableBytes() > 17) {
            int index = ByteBufUtil.indexOf(header, in);
//            System.out.println("index: "+ String.valueOf(index));
            if(index > 0) {
//                ByteBuf x = in.readBytes(5);

                in.readerIndex(index);
                if(in.readableBytes() > 17) { out.add(in.readBytes(18));}
//                in.readBytes(index);
            } else if (index == 0) {

                out.add(in.readBytes(18));
            }
        }
    }
}
