/**
 * Copyright (c) The openTCS Authors.
 *
 * This program is free software and subject to the MIT license. (For details,
 * see the licensing information (LICENSE.txt) you should have received with
 * this copy of the software.)
 */
package org.opentcs.kernel.extensions.sqlservice.orders;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StringReader;
import java.io.Writer;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.nio.charset.Charset;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

import static java.util.Objects.requireNonNull;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import javax.inject.Inject;
import org.opentcs.access.CredentialsException;
import org.opentcs.access.to.order.DestinationCreationTO;
import org.opentcs.access.to.order.TransportOrderCreationTO;
import org.opentcs.components.kernel.KernelExtension;
import org.opentcs.components.kernel.services.DispatcherService;
import org.opentcs.components.kernel.services.TransportOrderService;
import org.opentcs.data.ObjectUnknownException;
import org.opentcs.data.order.TransportOrder;
import org.opentcs.kernel.extensions.sqlservice.SQLServiceConfiguration;
import org.opentcs.kernel.extensions.sqlservice.orders.binding.Destination;
import org.opentcs.kernel.extensions.sqlservice.orders.binding.ScriptResponse;
import org.opentcs.kernel.extensions.sqlservice.orders.binding.TCSOrder;
import org.opentcs.kernel.extensions.sqlservice.orders.binding.TCSOrderSet;
import org.opentcs.kernel.extensions.sqlservice.orders.binding.TCSResponse;
import org.opentcs.kernel.extensions.sqlservice.orders.binding.TCSResponseSet;
import org.opentcs.kernel.extensions.sqlservice.orders.binding.TCSScriptFile;
import org.opentcs.kernel.extensions.sqlservice.orders.binding.Transport;
import org.opentcs.kernel.extensions.sqlservice.orders.binding.TransportResponse;
import org.opentcs.kernel.extensions.sqlservice.orders.binding.TransportScript;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Instances of this class accept general orders for the openTCS system encoded
 * in XML via TCP connections.
 * <p>
 * Setting up the server socket is done in a separate thread that is started
 * when {@link #initialize() initialize()} is called and stopped when
 * {@link #terminate() terminate()} is called. Incoming client connections will be
 * handled concurrently in separate threads.
 * </p>
 *
 * <p>
 * Input data is read from the socket until the client shuts down its output
 * stream. The input is then parsed, actions like the creation of transport
 * orders are performed and a response sent back to the client via the same
 * connection.
 * </p>
 *
 * @author Stefan Walter (Fraunhofer IML)
 */
public class XMLTelegramOrderReceiver
    implements KernelExtension {

  /**
   * This class's Logger.
   */
  private static final Logger LOG = LoggerFactory.getLogger(XMLTelegramOrderReceiver.class);
  /**
   * The sequence of bytes marking the end of an incoming telegram.
   */
  private static final String END_OF_TELEGRAM = "\r\n\r\n";
  /**
   * The transport order service.
   */
  private final TransportOrderService transportOrderService;
  /**
   * The dispatcher service.
   */
  private final DispatcherService dispatcherService;
  /**
   * This class's configuration.
   */
  private final SQLServiceConfiguration configuration;
  /**
   * A script file manager to help us with script files.
   */
  private final ScriptFileManager scriptFileManager;
  /**
   * This receiver's <em>enabled</em> flag.
   */
  private boolean enabled;
  /**
   * The listener task.
   */
  private ConnectionListener connectionListener;
  /**
   * The thread in which the listener task is running.
   */
  private Thread connectionListenerThread;

  /**
   * Creates a new instance.
   *
   * @param transportOrderService The transport order service.
   * @param dispatcherService The dispatcher service.
   * @param scriptFileManager The manager for script files to be used.
   * @param configuration This class's configuration.
   */
  @Inject
  public XMLTelegramOrderReceiver(TransportOrderService transportOrderService,
                                  DispatcherService dispatcherService,
                                  ScriptFileManager scriptFileManager,
                                  SQLServiceConfiguration configuration) {
    this.transportOrderService = requireNonNull(transportOrderService, "transportOrderService");
    this.dispatcherService = requireNonNull(dispatcherService, "dispatcherService");
    this.scriptFileManager = requireNonNull(scriptFileManager, "scriptFileManager");
    this.configuration = requireNonNull(configuration, "configuration");
  }

  @Override
  public boolean isInitialized() {
    return enabled;
  }

  @Override
  public void initialize() {
    // Only react if the state really changes.
    if (enabled) {
      return;
    }
    connectionListener = new ConnectionListener();
    connectionListenerThread = new Thread(connectionListener, "xmlOrderConnListener");
    connectionListenerThread.start();
    enabled = true;
    LOG.debug("XMLTelegramOrderReceiver initialized");
  }

  @Override
  public void terminate() {
    // Only react if the state really changes.
    if (!enabled) {
      return;
    }
    LOG.info("Terminating connection listener...");
    connectionListener.terminate();
    try {
      connectionListenerThread.join();
      LOG.info("Connection listener thread has terminated.");
    }
    catch (InterruptedException exc) {
      LOG.warn("Interrupted while waiting for connection listener to die.");
    }
    finally {
      connectionListenerThread = null;
      connectionListener = null;
      enabled = false;
    }
  }

  /**
   * The task listening for new client connections.
   */
  private class ConnectionListener
      implements Runnable {

    /**
     * The socket on which this listener listens for client connections.
     */
    private ServerSocket serverSocket;
    /**
     * This listener's termation flag.
     */
    private volatile boolean terminated;
    /**
     * An executor for managing the ConnectionHandler tasks that process client
     * connections.
     */
    private final Executor clientExecutor = Executors.newCachedThreadPool();

    /**
     * Creates a new ConnectionListener.
     */
    public ConnectionListener() {
      // Do nada.
    }

    /**
     * Signals this listener's working thread it is supposed to terminate.
     */
    public void terminate() {
      terminated = true;
      try {
        if (serverSocket != null && !serverSocket.isClosed()) {
          serverSocket.close();
        }
      }
      catch (IOException exc) {
        LOG.warn("IOException closing server socket", exc);
      }
      finally {
        // Make sure we dispose of the used server socket.
        serverSocket = null;
      }
    }

    @Override
    public void run() {
      // Set up the listening socket.
      try {
        serverSocket = new ServerSocket(configuration.ordersServerPort());
      }
      catch (IOException exc) {
        throw new IllegalStateException(
            "IOException trying to create server socket", exc);
      }
      // Accept new connections until terminated from outside.
      while (!terminated) {
        try {
          Socket clientSocket = serverSocket.accept();
          LOG.info("Connection from {}:{}",
                   clientSocket.getInetAddress().getHostAddress(),
                   clientSocket.getPort());
          clientExecutor.execute(new ConnectionHandler(clientSocket));
        }
        catch (SocketException exc) {
          // Check if we're supposed to terminate.
          if (terminated) {
            LOG.info("Received termination signal.");
          }
          else {
            LOG.warn("SocketException without termination flag set");
            throw new IllegalStateException(
                "SocketException without termination flag set", exc);
          }
        }
        catch (IOException exc) {
          throw new IllegalStateException(
              "IOException listening for connections", exc);
        }
      }
      LOG.info("Terminated connection listener.");
    }
  }

  /**
   * The task handling client connections.
   */
  private class ConnectionHandler
      implements Runnable {

    /**
     * Input buffer size for reading from sockets.
     */
    private static final int IN_BUF_SIZE = 16384;
    /**
     * The connection to the client.
     */
    private final Socket clientSocket;

    /**
     * Creates a new ConnectionHandler.
     *
     * @param clientSocket The socket for communication with the client.
     */
    ConnectionHandler(Socket clientSocket) {
      this.clientSocket = requireNonNull(clientSocket, "clientSocket");
    }

    @Override
    public void run() {
      try (Socket socket = clientSocket) {
        if (!socket.isConnected()) {
          throw new IllegalArgumentException("socket is not connected");
        }
        // Set a timeout for read() operations.
        socket.setSoTimeout(configuration.ordersIdleTimeout());
        TCSOrderSet orderSet = receiveOrder(socket.getInputStream());
        TCSResponseSet responseSet = processOrderSet(orderSet);
        if (orderSet.getOrders().get(0).getId().contains("00")) {
          TransportResponse transportResponse = (TransportResponse) responseSet.getResponses().get(0);
          String response = String.format("XYAGV%s", transportResponse.isExecutionSuccessful());
          socket.getOutputStream().write(response.getBytes());
        } else {
          sendResponse(responseSet, socket.getOutputStream());
        }
      }
      catch (Exception exc) {
        LOG.warn("Unexpected exception, aborting communication", exc);
      }
    }

    private TCSOrderSet receiveOrder(InputStream inputStream)
        throws IOException, IllegalStateException {
      Reader reader = new BufferedReader(new InputStreamReader(inputStream,
                                                               Charset.forName("UTF-8")));
//      LOG.debug()
      String telegram = readTelegram(reader);
      if (telegram.contains("Oorder-")) {
        String[] telegramList = telegram.split("-");
        TCSOrderSet orderSet1 = new TCSOrderSet();
        List<TCSOrder> orders = new LinkedList<>();
        Transport transport = new Transport();
        transport.setDeadline(Date.from(Instant.now().minus(60, ChronoUnit.MINUTES)));
        List<Destination> destinations = new LinkedList<>();
        Destination location_a = new Destination();
        Destination location_b = new Destination();
        location_a.setLocationName(String.format("Location-%s", telegramList[2]));
        location_a.setOperation("LOAD");
        location_b.setLocationName(String.format("Location-%s", telegramList[3]));
        location_b.setOperation("UNLOAD");
        destinations.add(location_a);
        destinations.add(location_b);
        transport.setDestinations(destinations);
        transport.setId(String.valueOf(telegramList[1]));
        orders.add(transport);
        orderSet1.setOrders(orders);
//        LOG.info("")
        return orderSet1;
      }
      TCSOrderSet orderSet = TCSOrderSet.fromXml(new StringReader(telegram));
      LOG.debug("Constructed order set");
      return orderSet;
    }

    private void sendResponse(TCSResponseSet responseSet, OutputStream outputStream)
        throws IOException {
      Writer writer = new BufferedWriter(new OutputStreamWriter(outputStream,
                                                                Charset.forName("UTF-8")));
      LOG.debug("Sending response");
      responseSet.toXml(writer);
      LOG.debug("Sent response, finishing.");
    }

    private String readTelegram(Reader reader)
        throws IllegalStateException, IOException {
      StringBuilder telegram = new StringBuilder();
      char[] buffer = new char[IN_BUF_SIZE];
      int bytesRead = reader.read(buffer);
      if (bytesRead == 21 && buffer[8] == 31) {
        int order_id = buffer[12] | ((buffer[11] & 0xff) << 8);
        int localtion_a = buffer[16] | ((buffer[15] & 0xff) << 8);
        int localtion_b = buffer[20] | ((buffer[19] & 0xff) << 8);
        String xx = String.format("Oorder-%s-%s-%s", order_id, localtion_a, localtion_b);
        LOG.info("Socket Order: {}", xx);
        return xx;
      }
      int totalBytesRead = 0;
      boolean foundEndOfTelegram = false;
      // Read from socket while we haven't encountered the end of telegram
      // marker and we haven't reached the EOF on the stream.
      while (!foundEndOfTelegram && bytesRead != -1) {
        totalBytesRead += bytesRead;
        if (totalBytesRead > configuration.ordersInputLimit()) {
          throw new IllegalStateException("Input limit of exceeded.");
        }
        telegram.append(buffer, 0, bytesRead);
        // If we did NOT receive the end of telegram marker, yet, read on.
        if (!telegram.toString().contains(END_OF_TELEGRAM)) {
          bytesRead = reader.read(buffer);
        }
        // If we did find the end of the telegram, stop reading.
        else {
          foundEndOfTelegram = true;
        }
      }
      LOG.debug("Reached end of telegram, processing input");
      return telegram.toString();
    }

    /**
     * Processes the given order set.
     *
     * @param orderSet The order set to be processed.
     * @return The <code>TCSResponseSet</code>.
     */
    private TCSResponseSet processOrderSet(TCSOrderSet orderSet) {
      assert orderSet != null;
      TCSResponseSet responseSet = new TCSResponseSet();
      for (TCSOrder curOrder : orderSet.getOrders()) {
        if (curOrder instanceof Transport) {
          LOG.debug("Processing 'Transport' element");
          TCSResponse response = processTransport((Transport) curOrder);
          responseSet.getResponses().add(response);
        }
        else if (curOrder instanceof TransportScript) {
          LOG.debug("Processing 'TransportScript' element");
          TransportScript curScript = (TransportScript) curOrder;
          ScriptResponse response = processScriptFile(curScript);
          responseSet.getResponses().add(response);
        }
        else {
          LOG.warn("Unhandled order type: " + curOrder.getClass().getName());
          // Create a negative response for this order.
          TransportResponse response = new TransportResponse();
          response.setId(curOrder.getId());
          response.setOrderName("");
          response.setExecutionSuccessful(false);
          responseSet.getResponses().add(response);
        }
      }
      return responseSet;
    }

    /**
     * Processes a transport.
     *
     * @param transport The transport to be processed.
     * @return The <code>TCSResponse</code>.
     */
    private TCSResponse processTransport(Transport transport) {
      requireNonNull(transport, "transport");

      // Create a response for this order.
      TransportResponse response = new TransportResponse();
      response.setId(transport.getId());
      Map transportproperties = new HashMap<String, String>();
      transportproperties.put("id", transport.getId());

      TransportOrderCreationTO transportOrderCreationTO = new TransportOrderCreationTO("TOrder-",
                                         createDestinations(transport.getDestinations()))
                .withIncompleteName(true)
                .withDeadline(transport.getDeadline() == null
                    ? Instant.MAX
                    : transport.getDeadline().toInstant())
                .withIntendedVehicleName(transport.getIntendedVehicle())
                .withDependencyNames(new HashSet<>(transport.getDependencies()));
      transportOrderCreationTO.setProperty("id", transport.getId());
      try {
        TransportOrder order = transportOrderService.createTransportOrder(transportOrderCreationTO);

        dispatcherService.dispatch();

        response.setOrderName(order.getName());

        // Everything went fine - let the client know.
        response.setExecutionSuccessful(true);
      }
      catch (ObjectUnknownException | CredentialsException exc) {
        LOG.warn("Unexpected exception", exc);
        response.setExecutionSuccessful(false);
      }
      return response;
    }

    /**
     * Processes a script file.
     *
     * @param transportScript The transport script to be processed.
     * @return The <code>ScriptResponse</code>.
     */
    private ScriptResponse processScriptFile(TransportScript transportScript) {
      requireNonNull(transportScript, "transportScript");

      ScriptResponse result = new ScriptResponse();
      result.setId(transportScript.getId());

      // Parse the script file.
      TCSScriptFile scriptFile;
      try {
        scriptFile = scriptFileManager.getScriptFile(transportScript.getFileName());
      }
      catch (IOException exc) {
        LOG.warn("Exception parsing script file", exc);
        result.setParsingSuccessful(false);
        return result;
      }

      // Process all order entries in the script file and create a response entry for each of them.
      String prevOrderName = null;
      for (TCSScriptFile.Order curOrder : scriptFile.getOrders()) {
        TransportResponse response = new TransportResponse();
        response.setId(transportScript.getId());

        try {
          TransportOrderCreationTO orderTO
              = new TransportOrderCreationTO("TOrder-",
                                             createDestinations(curOrder.getDestinations()))
                  .withIncompleteName(true)
                  .withIntendedVehicleName(curOrder.getIntendedVehicle());
          if (scriptFile.getSequentialDependencies() && prevOrderName != null) {
            orderTO.getDependencyNames().add(prevOrderName);
          }

          TransportOrder order = transportOrderService.createTransportOrder(orderTO);

          dispatcherService.dispatch();

          response.setOrderName(order.getName());

          response.setExecutionSuccessful(true);
          prevOrderName = order.getName();
        }
        catch (ObjectUnknownException | CredentialsException exc) {
          LOG.warn("Unexpected exception", exc);
          response.setExecutionSuccessful(false);
          // XXX With sequential dependencies, we should stop here, not add
          // another order without any dependencies!
          prevOrderName = null;
        }
        result.getTransports().add(response);
      }
      return result;
    }

    private List<DestinationCreationTO> createDestinations(List<Destination> destinations) {
      List<DestinationCreationTO> result = new ArrayList<>();
      for (Destination curDest : destinations) {
        result.add(new DestinationCreationTO(curDest.getLocationName(), curDest.getOperation()));
      }
      return result;
    }
  }
}
