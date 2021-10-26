package com.lvsrobot.iot;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.lvsrobot.iot.status.RequestStatusHandler;
import com.lvsrobot.iot.status.binding.TransportOrderState;
import com.lvsrobot.iot.status.binding.VehicleState;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.opentcs.components.kernel.services.TCSObjectService;
import org.opentcs.customizations.ApplicationEventBus;
import org.opentcs.data.ObjectUnknownException;
import org.opentcs.data.TCSObjectEvent;
import org.opentcs.data.order.TransportOrder;
import org.opentcs.util.CyclicTask;
import org.opentcs.util.event.EventBus;
import org.opentcs.util.event.EventHandler;
import org.opentcs.util.event.EventSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.*;

import static java.util.Objects.requireNonNull;

public class ServiceController extends CyclicTask {

    private RequestStatusHandler statusHandler;
    private EventBus eventBus;
    private EventSource eventSource;
    TCSObjectService orderService;
    private ServiceIotConfiguration serviceIotConfiguration;

    /**
     * Maps between objects and their JSON representations.
     */
    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule()).disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    //    private ThingsboardClient client;
    private SampleMqttClient client;
    private static final Logger LOG = LoggerFactory.getLogger(ServiceController.class);
    private Timer timer;
    private Timer timerSub;
    private boolean subscribeFlag = false;

    @Inject
    public ServiceController (RequestStatusHandler statusHandler,
                              TCSObjectService orderService,
                              ServiceIotConfiguration serviceIotConfiguration,
                              @ApplicationEventBus EventBus eventBus,
                              @ApplicationEventBus EventSource eventSource) {
        super(1*1000);
        this.statusHandler = statusHandler;
        this.eventBus = eventBus;
        this.eventSource = eventSource;
        this.orderService = orderService;
        this.serviceIotConfiguration = serviceIotConfiguration;
        eventSource.subscribe(new EvendHandle());
        timer = new Timer();
        timerSub = new Timer();
        timer.schedule(new SetVehicle(), 5*1000);
        timerSub.schedule(new Sub(), 5*1000, 3*1000);

        try {
            client = new SampleMqttClient(serviceIotConfiguration.bindAddress(), "AodonG2", serviceIotConfiguration.accessKey());
            client.connect();
            client.subscribe("v1/devices/me/rpc/request/+" ,new MqttCall());
            subscribeFlag = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public class EvendHandle implements EventHandler {
        @Override
        public void onEvent(Object event) {
            if (event instanceof TCSObjectEvent) {
                handleObjectEvent((TCSObjectEvent) event);
//            LOG.info("Iot rec Event: {}", event.toString());
            }
        }
        private void handleObjectEvent(TCSObjectEvent evt) {
            if (evt.getCurrentOrPreviousObjectState() instanceof TransportOrder) {
                TransportOrder order = null;
                switch (evt.getType()) {
                    case OBJECT_CREATED:
                        order = (TransportOrder) evt.getCurrentOrPreviousObjectState();
                        break;
                    case OBJECT_MODIFIED:
                        order = (TransportOrder) evt.getCurrentOrPreviousObjectState();
                        break;
                    case OBJECT_REMOVED:
                        order = (TransportOrder) evt.getCurrentOrPreviousObjectState();
                        break;
                    default:
                        LOG.warn("Unhandled event type: {}", evt.getType());
                }
                TransportOrderState orderState = getTransportOrderByName(order.getName());
                if (orderState.getProcessingVehicle() != null) {
                    String point = statusHandler.getVehicleStateByName(orderState.getProcessingVehicle()).getCurrentPosition();
                    orderState.setVehicleLocation(point);
                }

//                LOG.info("Event Order: {}", toJson(orderState));
            }
        }
    }

    /**
     * Finds the transport order with the given name.
     *
     * @param name The name of the requested transport order.
     * @return A single transport order with the given name.
     * @throws ObjectUnknownException If a transport order with the given name does not exist.
     */
    public TransportOrderState getTransportOrderByName(String name) throws ObjectUnknownException {
        requireNonNull(name, "name");

        return orderService.fetchObjects(TransportOrder.class,
                t -> t.getName().equals(name))
                .stream()
                .map(t -> TransportOrderState.fromTransportOrder(t))
                .findAny()
                .orElseThrow(() -> new ObjectUnknownException("Unknown transport order: " + name));
    }

    @Override
    public void terminate() {
        try {
            client.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.terminate();
    }

    public class SetVehicle extends TimerTask {

        @Override
        public void run() {
            statusHandler.putVehicleIntegrationLevel("Vehicle-01", "TO_BE_UTILIZED");
        }
    }
    public class Sub extends TimerTask {

        @Override
        public void run() {
            if (!subscribeFlag) {
                subscribeFlag = true;
                client.subscribe("v1/devices/me/rpc/request/+" ,new MqttCall());
            }
        }
    }

    public JsonNode getVehicleState() {
        ObjectMapper mapper = new ObjectMapper();
        List<VehicleState> vehicleStateList = statusHandler.getVehiclesState(null);
        Map<String, Object> vehicleStateMap = new HashMap<>();
        for(VehicleState vehicleState: vehicleStateList) {
            Map<String, String> vehicle = mapper.convertValue(vehicleState, Map.class);
            String fix = vehicleState.getName()+"-";

            for (String key: vehicle.keySet()) {
                vehicleStateMap.put(fix+key, vehicle.get(key));
            }
        }

        JsonNode node = mapper.convertValue(vehicleStateMap, JsonNode.class);
        return node;
    }


    class MqttCall implements MqttCallback {

        @Override
        public void connectionLost(Throwable cause) {
            subscribeFlag = false;
            LOG.error("subscribe disconnect");
        }

        @Override
        public void messageArrived(String topic, MqttMessage message) throws Exception {
            LOG.info("recive topic: {}, msg: {}", topic, message);
            String[] list = topic.split("/");
            String rec = new String(message.getPayload());
            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, String> map = objectMapper.readValue(rec, Map.class);
            if (map.get("method").equals("setValue")) {
                if (rec.contains("true")) {
                    statusHandler.putVehicleIntegrationLevel("Vehicle-01", "TO_BE_RESPECTED");
                    statusHandler.putVehicleIntegrationLevel("Vehicle-02", "TO_BE_UTILIZED");
                } else {
                    statusHandler.putVehicleIntegrationLevel("Vehicle-02", "TO_BE_RESPECTED");
                    statusHandler.putVehicleIntegrationLevel("Vehicle-01", "TO_BE_UTILIZED");

                }
            }
            client.publishResponse(message, list[list.length-1]);
        }

        @Override
        public void deliveryComplete(IMqttDeliveryToken token) {

        }
    }

    @Override
    protected void runActualTask() {

        JsonNode vehicleNode = getVehicleState();

        try {
            client.publishAttributes(vehicleNode);
//            client.publishTelemetry(orderNode);
        } catch (Exception e) {
            try {
                client.connect();
            } catch (Exception exception) {
                exception.printStackTrace();
            }
            LOG.error("runActualTssk Error", e.getMessage());
        }
    }

    private String toJson(Object object) throws IllegalStateException {
        try {
            return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(object);
        } catch (JsonProcessingException exc) {
            throw new IllegalStateException("Could not produce JSON output", exc);
        }
    }
}
