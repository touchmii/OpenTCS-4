/**
 * Copyright (c) The openTCS Authors.
 *
 * This program is free software and subject to the MIT license. (For details,
 * see the licensing information (LICENSE.txt) you should have received with
 * this copy of the software.)
 */
package org.opentcs.kernel.extensions.sqlservice.status;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.HashSet;
import java.util.Iterator;
import static java.util.Objects.requireNonNull;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.inject.Inject;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.timeout.IdleStateHandler;
import org.opentcs.components.kernel.KernelExtension;
import org.opentcs.customizations.ApplicationEventBus;
import org.opentcs.kernel.extensions.sqlservice.SQLServiceConfiguration;
import org.opentcs.util.event.EventSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An instance of this class accepts TCP connections from clients that wish to
 * be informed of generic status changes. XML messages are sent to all clients
 * connected whenever something interesting happens. Interesting events can be:
 * <ul>
 * <li>Changes of a transport order's state.</li>
 * <li>Changes of a vehicle's state.</li>
 * </ul>
 *
 * @author Stefan Walter (Fraunhofer IML)
 */
public class StatusMessageDispatcher
    implements KernelExtension {

  /**
   * This class's Logger.
   */
  private static final Logger LOG = LoggerFactory.getLogger(StatusMessageDispatcher.class);
  /**
   * This class's configuration.
   */
  private final SQLServiceConfiguration configuration;
  /**
   * Where we register for application events.
   */
  private final EventSource eventSource;
  /**
   * This dispatcher's listener task.
   */
  private ConnectionListener connectionListener;
  /**
   * This dispatcher's <em>enabled</em> flag.
   */
  private volatile boolean enabled;

  static final int RECONNECT_DELAY = 5;

  private static MESClientHandler handler = new MESClientHandler();

//  private MESConnectionHandler mesConnectionHandler;

  private static final Bootstrap bs = new Bootstrap();

  private static ChannelFuture f;

  /**
   * Creates a new StatusMessageDispatcher.
   *
   * @param eventSource Where this instance registers for application events.
   * @param configuration This class's configuration.
   */
  @Inject
  public StatusMessageDispatcher(@ApplicationEventBus EventSource eventSource,
                                 SQLServiceConfiguration configuration) {
    this.eventSource = requireNonNull(eventSource, "eventSource");
    this.configuration = requireNonNull(configuration, "configuration");
  }

  @Override
  public boolean isInitialized() {
    return enabled;
  }

  @Override
  public void initialize() {
    if (enabled) {
      return;
    }

    EventLoopGroup group = new NioEventLoopGroup();
    bs.group(group)
            .channel(NioSocketChannel.class)
            .remoteAddress(configuration.MESServerHost(), configuration.MESServerPort())
            .handler(new ChannelInitializer<SocketChannel>() {
              @Override
              protected void initChannel(SocketChannel ch) throws Exception {
                ch.pipeline().addLast(new StringEncoder());
//                ch.pipeline().addLast(new ByteToMsgDecoder());
//                ch.pipeline().addLast(new ByteArrayDecoder());
                ch.pipeline().addLast(new IdleStateHandler(10000, 0, 0), handler);
              }
            });
    f = bs.connect();

    MESConnectionHandler mesConnectionHandler = new MESConnectionHandler(f, eventSource, configuration.statusMessageSeparator());
    eventSource.subscribe(mesConnectionHandler);
    Thread mesConnectionHandlerThread = new Thread(mesConnectionHandler, "mesConnectionHandler");
    mesConnectionHandlerThread.start();
    connectionListener = new ConnectionListener();
    Thread connectionListenerThread = new Thread(connectionListener, "xmlStatusConnListener");
    connectionListenerThread.start();
    enabled = true;
    LOG.debug("StatusMessageDispatcher enabled");
  }

  static void connect() {
    bs.connect().addListener(new ChannelFutureListener() {
      @Override
      public void operationComplete(ChannelFuture future) throws Exception {
        if (future.cause() != null) {
          handler.startTime = -1;
          handler.println("Failed to connect: " + future.cause());
        } else {
          f = future;
        }
      }
    });
  }

  @Override
  public void terminate() {
    if (!enabled) {
      return;
    }
    handler.disConnect();
    f.channel().closeFuture();
    f = null;
    bs.group().shutdownGracefully();
    connectionListener.terminate();
    connectionListener = null;
  }

  /**
   * The task listening for new client connections.
   */
  private class ConnectionListener
      implements Runnable {

    /**
     * This listener's server socket.
     */
    private volatile ServerSocket serverSocket;
    /**
     * This task's termination flag.
     */
    private volatile boolean terminated;
    /**
     * A set of connection handlers that have been created.
     */
    private final Set<ConnectionHandler> runningHandlers = new HashSet<>();


    /**
     * Creates a new ConnectionListener.
     */
    private ConnectionListener() {
    }

    /**
     * Flags this task for termination.
     */
    private void terminate() {
      terminated = true;
      if (serverSocket != null && !serverSocket.isClosed()) {
        try {
          serverSocket.close();
        }
        catch (IOException exc) {
          LOG.warn("Exception closing server socket", exc);
        }
      }
    }

    @Override
    public void run() {
      ExecutorService clientExecutor = Executors.newCachedThreadPool();
      // Set up a server socket and wait for connections to handle.
      try {
        serverSocket = new ServerSocket(configuration.statusServerPort());
        terminated = false;
        while (!terminated) {
          Socket clientSocket = serverSocket.accept();
          LOG.debug("Connection from {}:{}",
                    clientSocket.getInetAddress().getHostAddress(),
                    clientSocket.getPort());
          ConnectionHandler newHandler = new ConnectionHandler(clientSocket,
                                                               eventSource,
                                                               configuration.statusMessageSeparator());
          eventSource.subscribe(newHandler);
          clientExecutor.execute(newHandler);
          runningHandlers.add(newHandler);
          // Forget any handlers that have terminated since the last run.
          Iterator<ConnectionHandler> iter = runningHandlers.iterator();
          while (iter.hasNext()) {
            ConnectionHandler curHandler = iter.next();
            if (curHandler.isTerminated()) {
              iter.remove();
            }
          }
        }
      }
      catch (SocketException exc) {
        // Check if we're supposed to terminate.
        if (terminated) {
          LOG.debug("Received termination signal.");
        }
        else {
          LOG.warn("SocketException without termination flag set", exc);
        }
      }
      catch (IOException exc) {
        LOG.warn("IOException handling server socket", exc);
      }
      finally {
        clientExecutor.shutdown();
        if (serverSocket != null && !serverSocket.isClosed()) {
          try {
            serverSocket.close();
          }
          catch (IOException exc) {
            LOG.error("Couldn't close server socket", exc);
          }
        }
        // Terminate all handlers that may still be running.
        for (ConnectionHandler handler : runningHandlers) {
          eventSource.unsubscribe(handler);
          handler.terminate();
        }
      }
      LOG.debug("Terminated connection listener.");
    }
  }

}
