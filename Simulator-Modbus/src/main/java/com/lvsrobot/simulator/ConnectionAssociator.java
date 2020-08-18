/**
 * Copyright (c) Fraunhofer IML
 */
package com.lvsrobot.simulator;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.opentcs.contrib.tcp.netty.ClientEntry;
import org.opentcs.contrib.tcp.netty.ConnectionAssociatedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

import static java.util.Objects.requireNonNull;

/**
 * Associates incoming messages with clients interested in these.
 * Here it is always only one client.
 *
 * @author Martin Grzenia (Fraunhofer IML)
 */
public class ConnectionAssociator
    extends ChannelInboundHandlerAdapter {

  /**
   * This class's Logger.
   */
  private static final Logger LOG = LoggerFactory.getLogger(ConnectionAssociator.class);
  /**
   * A pool of clients that may connect to a TcpServerChannelManager.
   */
  private final Map<Object, ClientEntry<byte[]>> clientEntries;
  /**
   * The associated client.
   */
  private ClientEntry<byte[]> client;

  public ConnectionAssociator(Map<Object, ClientEntry<byte[]>> clientEntries) {
    this.clientEntries = requireNonNull(clientEntries, "clientEntries");
  }

  @Override
  public void channelRead(ChannelHandlerContext ctx, Object msg) {
    handleMessage(ctx, msg);
  }

  private void handleMessage(ChannelHandlerContext ctx, Object msg) {
    LOG.debug("Handling incoming message: {}", msg);
    if (!(msg instanceof byte[])) {
      LOG.debug("Not an instance of byte[] - ignoring.");
      return;
    }

    if (client == null) {
      LOG.debug("Received the first data from the client.");
      client = clientEntries.get(VehicleSimulator.CLIENT_OBJECT);

      // If no one is interested ignore the message and close the connection.
      if (client == null) {
        LOG.debug("Ignoring message for unknown key '{}'. Registered entries: {}",
                  VehicleSimulator.CLIENT_OBJECT,
                  clientEntries.keySet());
        ctx.close();
        return;
      }

      client.setChannel(ctx.channel());
      // Notify any listeners that the channel has been associated to this nickname, implicitly
      // notifying the comm adapter that a connection has been established.
      ctx.fireUserEventTriggered(new ConnectionAssociatedEvent(VehicleSimulator.CLIENT_OBJECT));
    }

    byte[] incomingData = (byte[]) msg;
    client.getConnectionEventListener().onIncomingTelegram(incomingData);
  }
}
