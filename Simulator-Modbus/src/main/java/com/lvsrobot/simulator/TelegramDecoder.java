/**
 * Copyright (c) Fraunhofer IML
 */
package com.lvsrobot.simulator;

import com.google.common.primitives.Ints;
import de.fraunhofer.iml.opentcs.example.commadapter.vehicle.telegrams.OrderRequest;
import de.fraunhofer.iml.opentcs.example.commadapter.vehicle.telegrams.StateRequest;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.apache.commons.codec.binary.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Checks if the incoming data was sent by the {@link APTVCommAdapter}.
 *
 * @author Martin Grzenia (Fraunhofer IML)
 */
public class TelegramDecoder
    extends ByteToMessageDecoder {

  /**
   * This class's Logger.
   */
  private static final Logger LOG = LoggerFactory.getLogger(TelegramDecoder.class);
  /**
   * The minimum bytes required to even try decoding (size of the smallest telegram).
   */
  private final long minimumBytesRequired;

  public TelegramDecoder() {
    minimumBytesRequired = Ints.min(StateRequest.TELEGRAM_LENGTH,
                                    OrderRequest.TELEGRAM_LENGTH);
  }

  @Override
  protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {
    // Don't do anything if we don't have enough bytes.
    if (in.readableBytes() < minimumBytesRequired) {
      return;
    }

    LOG.debug("Trying to decode incoming bytes: {}", in);
    byte[] telegramData;
    in.markReaderIndex();

    if (in.readableBytes() >= StateRequest.TELEGRAM_LENGTH) {
      LOG.debug("Checking if it's an state request");
      telegramData = new byte[StateRequest.TELEGRAM_LENGTH];
      in.readBytes(telegramData);
      LOG.debug("TelegramData: " + Hex.encodeHexString(telegramData));
      if (telegramData[2] == StateRequest.TYPE) {
        out.add(telegramData);
        return;
      }
      else {
        in.resetReaderIndex();
      }
    }

    if (in.readableBytes() >= OrderRequest.TELEGRAM_LENGTH) {
      LOG.debug("Checking if it's a order request");
      telegramData = new byte[OrderRequest.TELEGRAM_LENGTH];
      in.readBytes(telegramData);
      LOG.debug("TelegramData: " + Hex.encodeHexString(telegramData));
      if (telegramData[2] == OrderRequest.TYPE) {
        out.add(telegramData);
      }
      else {
        in.resetReaderIndex();
      }
    }
  }
}
