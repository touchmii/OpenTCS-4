package com.lvsrobot.kernel.extensions.sqlservice.model;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.lvsrobot.kernel.extensions.sqlservice.status.binding.Destination;
import com.lvsrobot.kernel.extensions.sqlservice.status.binding.TransportOrderState;
import org.javalite.activejdbc.Model;
import org.opentcs.data.order.TransportOrder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TransportModel extends Model{

    /**
     * Maps between objects and their JSON representations.
     */
    private static final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule()).disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    public static void insertModel(TransportOrderState transport) {
        TransportModel t = new TransportModel();
        t.set("name", transport.getName(), "type", transport.getType(), "state", transport.getState(),
                "intendedVehicle", transport.getIntendedVehicle(), "processingVehicle", transport.getProcessingVehicle(),
                "destinations", toJson(transport.getDestinations()), "category", transport.getCategory()).saveIt();
    }
    public static void updateModel(TransportOrderState transport, String name) {
        TransportModel t = TransportModel.findFirst("name = ?", name);
        if (t != null) {
            t.set("name", transport.getName(), "type", transport.getType(), "state", transport.getState(),
                    "intendedVehicle", transport.getIntendedVehicle(), "processingVehicle", transport.getProcessingVehicle(),
                    "destinations", toJson(transport.getDestinations()), "category", transport.getCategory()).saveIt();
        }
    }
    public static boolean existModel(String Name) {
        TransportModel t = TransportModel.findFirst("name = ?", Name);
        return t != null;
    }

    public static List<TransportOrderState> getUnFinishModel() {
        List<TransportModel> t = TransportModel.where("state = 'DISPATCHABLE'");
        List<TransportOrderState> orderStates = new ArrayList<>();
        t.forEach(tt -> orderStates.add(toTransport(tt)));
        return orderStates;
    }

    private static String toJson(Object object) throws IllegalStateException {
        try {
            return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(object);
        } catch (JsonProcessingException exc) {
            throw new IllegalStateException("Could not produce JSON output", exc);
        }
    }

    private static TransportOrderState toTransport(TransportModel t) {
        TransportOrderState transport = new TransportOrderState();
        transport.setName((String) t.get("name"));
        transport.setState(TransportOrder.State.valueOf(t.getString("state")));
        transport.setType(t.getString("type"));
        transport.setCategory(t.getString("category"));
        Destination[] destinations;
        try {
            destinations = objectMapper.readValue(t.get("destinations").toString(), Destination[].class);
            transport.setDestinations(Arrays.asList(destinations));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return transport;
    }

}
