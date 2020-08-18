/**
 * Copyright (c) Fraunhofer IML
 */
package com.lvsrobot.simulator;

import com.google.common.base.Strings;
import com.google.common.primitives.Ints;
import de.fraunhofer.iml.opentcs.example.commadapter.vehicle.LaurusTcsCommAdapter;
import de.fraunhofer.iml.opentcs.example.commadapter.vehicle.telegrams.OrderRequest;
import de.fraunhofer.iml.opentcs.example.commadapter.vehicle.telegrams.StateRequest;
import de.re.easymodbus.server.ModbusServer;
import io.netty.channel.ChannelHandler;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import org.opentcs.contrib.tcp.netty.ClientEntry;
import org.opentcs.contrib.tcp.netty.ConnectionEventListener;
import org.opentcs.contrib.tcp.netty.TcpServerChannelManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;



/**
 * A standalone application to simulate communication between the {@link LaurusTcsCommAdapter} and a
 * vehicle.
 *
 * @author Martin Grzenia (Fraunhofer IML)
 */
public class VehicleSimulator
        implements ConnectionEventListener<byte[]> {

    /**
     * This class's logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger(VehicleSimulator.class);
    /**
     * An idendifier for the client that connects to this vehicle.
     */
    public static final Object CLIENT_OBJECT = new Object();
    /**
     * The pool of clients to connect to this vehicle.
     * Here it's only one client.
     */
    private final Map<Object, ClientEntry<byte[]>> client = new HashMap<>();
    /**
     * Manages the connection to the {@link LaurusTcsCommAdapter}.
     */
    private final TcpServerChannelManager<byte[], byte[]> vehicleServer;
    /**
     * The executor for the simluation.
     */
    private final ScheduledExecutorService simultationExecutor = Executors
            .newSingleThreadScheduledExecutor(runnable -> new Thread(runnable, "simulationExecutor"));
    /**
     * The executor for specific tasks.
     */
    private final ScheduledExecutorService taskExecutor = Executors
            .newSingleThreadScheduledExecutor(runnable -> new Thread(runnable, "taskExecutor"));

    /**
     * The internal state of the simulated vehicle.
     */
    private final VehicleState vehicleState = new VehicleState();

    /**
     * Creates a new instance.
     */
    public VehicleSimulator() {
        vehicleServer = new TcpServerChannelManager<>(4002,
                client,
                this::getChannelHandlers,
                5000,
                true);
        vehicleServer = new ModbusServer()
    }

    private void initialize() {
        if (vehicleServer.isInitialized()) {
            return;
        }
        vehicleServer.initialize();
        vehicleServer.register(CLIENT_OBJECT, this, true);
    }

    private void terminate() {
        if (!vehicleServer.isInitialized()) {
            return;
        }
        vehicleServer.terminate();
    }

    @Override
    public void onIncomingTelegram(byte[] request) {
        if (request[2] == StateRequest.TYPE) {
            //            LOG.info("Incoming StateRequest : {}", request);
            int telegramCounter = Ints.fromBytes((byte) 0, (byte) 0, request[3], request[4]);
            //            LOG.info("小车收到TCS发来【状态报文】,报文ID:telegramCounter={}", telegramCounter);
            vehicleState.setTelegramCounter(telegramCounter);
            byte[] response = createStateResponse();
            //            LOG.info("Sending StateRequest response: {}", response);
            LOG.info("小车返回当前状态：id={},pos.id={},opstate={},loadstate={},lastReceivedOrderId={},currentOrderId={},lastFinishedOrderId={}",
                    Ints.fromBytes((byte) 0, (byte) 0, response[3], response[4]),
                    Ints.fromBytes((byte) 0, (byte) 0, response[5], response[6]),
                    (char) response[7],
                    (char) response[8],
                    Ints.fromBytes((byte) 0, (byte) 0, response[9], response[10]),
                    Ints.fromBytes((byte) 0, (byte) 0, response[11], response[12]),
                    Ints.fromBytes((byte) 0, (byte) 0, response[13], response[14]),
                    response);
            vehicleServer.send(CLIENT_OBJECT, response);
        } else if (request[2] == OrderRequest.TYPE) {
            LOG.info("Incoming OrderRequest : {}", request);
            int telegramCounter = Ints.fromBytes((byte) 0, (byte) 0, request[3], request[4]);
            LOG.info("小车收到TCS发来【订单报文】报文,id={},orderId={},destId={},destAction={}",
                    Ints.fromBytes((byte) 0, (byte) 0, request[3], request[4]),
                    Ints.fromBytes((byte) 0, (byte) 0, request[5], request[6]),
                    Ints.fromBytes((byte) 0, (byte) 0, request[7], request[8]),
                    (char) request[9]
            );

            vehicleState.setTelegramCounter(telegramCounter);
            int orderID = Ints.fromBytes((byte) 0, (byte) 0, request[5], request[6]);
            vehicleState.setLastReceivedOrderId(orderID);

            simulateCompleteTask(request);

            byte[] response = createOrderResponse();
            LOG.info("小车响应订单接受情况：id={},orderId={}",
                    Ints.fromBytes((byte) 0, (byte) 0, response[3], response[4]),
                    Ints.fromBytes((byte) 0, (byte) 0, response[5], response[6])
            );
            LOG.info("Sending OrderRequest response: {}", response);
            vehicleServer.send(CLIENT_OBJECT, response);
        }
    }

    /**
     * 模拟小车执行tcs下发的订单任务
     *
     * @author Rico
     * @date 2020/2/5 2:25 下午
     */
    private void simulateCompleteTask(byte[] request) {
        int requestId = Ints.fromBytes((byte) 0, (byte) 0, request[3], request[4]);
        int orderID = Ints.fromBytes((byte) 0, (byte) 0, request[5], request[6]);
        int destId = Ints.fromBytes((byte) 0, (byte) 0, request[7], request[8]);
        char destAction = (char) request[9];
        if (vehicleState.getOperationMode() == 'I') {
            vehicleState.setCurrOrderId(orderID);
            //执行移动: 更改小车运行状态为移动中 --> 更新小车当前点位 --> 更改小车运行状态为空闲(完成移动)
            if (vehicleState.getCurrOrderId() != destId) {
                taskExecutor.schedule(setOperationState('M'), 0, TimeUnit.SECONDS);
                taskExecutor.schedule(setPositionId(destId), 1, TimeUnit.SECONDS);
            }
            if ('N' == destAction) {
                taskExecutor.schedule(setOperationState('I'), 1, TimeUnit.SECONDS);
            } else if ('L' == destAction) {
                taskExecutor.schedule(setOperationState('A'), 1, TimeUnit.SECONDS);
                taskExecutor.schedule(setLoadState('F'), 2, TimeUnit.SECONDS);
                taskExecutor.schedule(setOperationState('I'), 2, TimeUnit.SECONDS);
            } else if ('U' == destAction) {
                taskExecutor.schedule(setOperationState('A'), 1, TimeUnit.SECONDS);
                taskExecutor.schedule(setLoadState('E'), 2, TimeUnit.SECONDS);
                taskExecutor.schedule(setOperationState('I'), 2, TimeUnit.SECONDS);
            }
            taskExecutor.schedule(setLastFinished(orderID), 2, TimeUnit.SECONDS);

        }


    }


    @Override
    public void onConnect() {
        LOG.info("Communication adapter connected to vehicle.");
        configureVehicleBehaviour();
    }

    @Override
    public void onFailedConnectionAttempt() {
    }

    @Override
    public void onDisconnect() {
        LOG.info("Communication adapter disconnected from vehicle.");
        terminate();
        initialize();
    }

    @Override
    public void onIdle() {
        LOG.info("Communucation adapter is idle.");
    }

    private void startSimulationThread() {
        Runnable simulationTask = () -> {
            LOG.info("Starting simulation... (press the return key to stop the simulation)");
            Scanner scanner = new Scanner(System.in);
            boolean input = false;
            while (!input) {
                input = Strings.isNullOrEmpty(scanner.nextLine());
            }
            LOG.info("Stopping simulation...");
            terminate();
            System.exit(0);
        };
        simultationExecutor.schedule(simulationTask, 0, TimeUnit.SECONDS);
    }

    /**
     * Initializes the vehicle state.
     */
    private void configureVehicleBehaviour() {
        //    taskExecutor.schedule(setOperationMode('A'), 0, TimeUnit.SECONDS);
        //    taskExecutor.schedule(setOperationState('M'), 0, TimeUnit.SECONDS);
        //    taskExecutor.schedule(setPositionId(7089), 0, TimeUnit.SECONDS);
        taskExecutor.schedule(setPositionId(1), 0, TimeUnit.SECONDS);
        //
        //    taskExecutor.schedule(setOperationState('A'), 10, TimeUnit.SECONDS);
        taskExecutor.schedule(setOperationState('I'), 20, TimeUnit.SECONDS);
        //    taskExecutor.schedule(setOperationState('C'), 22, TimeUnit.SECONDS);
    }

    /**
     * Creates a state response from the simulated vehicle state.
     *
     * @return The byte representation of a state response
     */
    private byte[] createStateResponse() {
        return vehicleState.toStateResponse().getRawContent();
    }

    /**
     * Creates an order response from the simulated vehicle state.
     *
     * @return The byte representation of a state response
     */
    private byte[] createOrderResponse() {
        return vehicleState.toOrderResponse().getRawContent();
    }

    /**
     * Returns the channel handlers for the nio pipeline.
     *
     * @return The channel handlers for the nio pipeline
     */
    private List<ChannelHandler> getChannelHandlers() {
        return Arrays.asList(new LengthFieldBasedFrameDecoder(getMaxTelegramLength(), 1, 1, 2, 0),
                new TelegramDecoder(),
                new TelegramEncoder(),
                new ConnectionAssociator(client));
    }

    /**
     * Returns the maximum telegram length.
     *
     * @return The maximum telegram length
     */
    private int getMaxTelegramLength() {
        return Ints.max(StateRequest.TELEGRAM_LENGTH,
                OrderRequest.TELEGRAM_LENGTH);
    }

    /**
     * Starts the simulation of the vehicle.
     *
     * @param args
     */
    public static void main(String[] args) {
        VehicleSimulator simulator = new VehicleSimulator();
        simulator.initialize();
        simulator.startSimulationThread();
    }

    private Runnable setPositionId(int positionId) {
        return () -> vehicleState.setPositionId(positionId);
    }

    private Runnable setOperationMode(char operationMode) {
        return () -> vehicleState.setOperationMode(operationMode);
    }

    private Runnable setOperationState(char operationState) {
        return () -> vehicleState.setOperationMode(operationState);
    }

    private Runnable setLoadState(char loadState) {
        return () -> vehicleState.setLoadState(loadState);

    }

    private Runnable setLastFinished(int orderID) {
        return () -> vehicleState.setLastFinishedOrderId(orderID);

    }

}
