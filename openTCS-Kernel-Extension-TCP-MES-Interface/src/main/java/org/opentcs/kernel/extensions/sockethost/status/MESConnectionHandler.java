/**
 * Copyright (c) The openTCS Authors.
 *
 * This program is free software and subject to the MIT license. (For details,
 * see the licensing information (LICENSE.txt) you should have received with
 * this copy of the software.)
 */
package org.opentcs.kernel.extensions.sockethost.status;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.netty.channel.ChannelFuture;
import org.opentcs.data.TCSObject;
import org.opentcs.data.TCSObjectEvent;
import org.opentcs.data.model.Vehicle;
import org.opentcs.data.order.TransportOrder;
import org.opentcs.kernel.extensions.sockethost.status.binding.OrderStatusMessage;
import org.opentcs.kernel.extensions.sockethost.status.binding.StatusMessage;
import org.opentcs.kernel.extensions.sockethost.status.binding.VehicleStatusMessage;
import org.opentcs.util.event.EventHandler;
import org.opentcs.util.event.EventSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;

import static java.util.Objects.requireNonNull;

/**
 * The task handling client connections.
 */
class MESConnectionHandler
    implements Runnable,
               EventHandler {

  /**
   * This class's Logger.
   */
  private static final Logger LOG = LoggerFactory.getLogger(MESConnectionHandler.class);
  /**
   * The connection to the client.
   */
  private ChannelFuture channelFuture;
  /**
   * The source of status events.
   */
  private final EventSource eventSource;
  /**
   * A string indicating the end of a status message/separating status messages
   * in the stream.
   */
  private final String messageSeparator;
  /**
   * Commands to be processed.
   */
  private final BlockingQueue<ConnectionCommand> commands = new PriorityBlockingQueue<>();
  /**
   * This connection handler's <em>terminated</em> flag.
   */
  private volatile boolean terminated;

  /**
   * Maps between objects and their JSON representations.
   */
  private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule()).disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);


  /**
   * Creates a new ConnectionHandler.
   *
   * @param clientSocket The socket for communication with the client.
   * @param evtSource The source of the status events with which the handler
   * is supposed to register.
   */
  MESConnectionHandler(ChannelFuture channelFuture,
                       EventSource evtSource,
                       String messageSeparator) {
    this.channelFuture = requireNonNull(channelFuture, "chanelFuture");
    this.eventSource = requireNonNull(evtSource, "evtSource");
    this.messageSeparator = requireNonNull(messageSeparator, "messageSeparator");
//    checkArgument(channelFuture.isSuccess(), "clientSocket is not connected");
  }

  /**
   * Adds an event to this handler's queue.
   *
   * @param event The event to be processed.
   */
  @Override
  public void onEvent(Object event) {
    requireNonNull(event, "event");
    if (event instanceof TCSObjectEvent) {
      commands.offer(new ConnectionCommand.ProcessObjectEvent((TCSObjectEvent) event));
    }
  }

  @Override
  public void run() {
    try
//            (Writer writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(),
//                                                                   Charset.forName("UTF-8"))))
    {
      while (channelFuture.channel().isWritable()) {
        consume(commands.take(), channelFuture);
      }
      LOG.debug("Terminating connection handler.");
    }
    catch (IOException | InterruptedException exc) {
      LOG.warn("Exception terminates connection handler.", exc);
      terminated = true;
    }
    finally {
      LOG.debug("Unregistering from event source");
//      eventSource.unsubscribe(this);
    }
  }

  /**
   * Terminates this listener.
   */
  public void terminate() {
    commands.offer(new ConnectionCommand.PoisonPill());
  }

  /**
   * Returns whether this listener has terminated.
   *
   * @return True if yes, false if not.
   */
  public boolean isTerminated() {
    return terminated;
  }

  private void consume(ConnectionCommand command, ChannelFuture writer)
      throws IOException {
    if (command instanceof ConnectionCommand.PoisonPill) {
      terminated = true;
    }
    else if (command instanceof ConnectionCommand.ProcessObjectEvent) {
      processObjectEvent(((ConnectionCommand.ProcessObjectEvent) command).getEvent(), writer);
    }
  }

  private void processObjectEvent(TCSObjectEvent event, ChannelFuture writer)
      throws IOException {
    TCSObject<?> eventObject = event.getCurrentOrPreviousObjectState();
    //TODO 分析事件类型、将订单状态发送给MES系统
    if (eventObject instanceof TransportOrder) {
//      sendMessage(OrderStatusMessage.fromTransportOrder((TransportOrder) eventObject), writer);
      sendJson(OrderStatusMessage.fromTransportOrder((TransportOrder) eventObject), writer);
    }
    else if (eventObject instanceof Vehicle) {
//      sendMessage(VehicleStatusMessage.fromVehicle((Vehicle) eventObject), writer);
      sendJson(VehicleStatusMessage.fromVehicle((Vehicle) eventObject), writer);
    }
  }

  private void sendJson(StatusMessage message, ChannelFuture writer) throws IOException {
    String status = toJson(message);
    writer.channel().writeAndFlush(status);
  }
  private String toJson(Object object) throws IllegalStateException {
    try {
      return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(object);
    } catch (JsonProcessingException exc) {
      throw new IllegalStateException("Could not produce JSON output", exc);
    }
  }
}
