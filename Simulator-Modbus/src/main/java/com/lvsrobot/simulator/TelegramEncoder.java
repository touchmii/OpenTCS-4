/**
 * Copyright (c) Fraunhofer IML
 */
package com.lvsrobot.simulator;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Encodes outgoing data.
 *
 * @author Martin Grzenia (Fraunhofer IML)
 */
public class TelegramEncoder
    extends MessageToByteEncoder<byte[]> {

  /**
   * This class's Logger.
   */
  private static final Logger LOG = LoggerFactory.getLogger(TelegramEncoder.class);

  @Override
  protected void encode(ChannelHandlerContext ctx, byte[] msg, ByteBuf out)
      throws Exception {
    LOG.debug("Encoding bytes: {}", msg);
    out.writeBytes(msg);
  }
}
