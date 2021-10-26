package com.lvsrobot.kernel.extensions.sqlservice.model;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.lvsrobot.kernel.extensions.sqlservice.status.binding.TransportOrderState;
import org.javalite.activejdbc.Model;

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
    public static void updateModel(TransportOrderState transport, int i) {
        TransportModel t = TransportModel.findFirst("id = ?", i);
        if (t != null) {
            t.set("name", transport.getName(), "type", transport.getType(), "state", transport.getState(),
                    "intendedVehicle", transport.getIntendedVehicle(), "processingVehicle", transport.getProcessingVehicle(),
                    "destinations", toJson(transport.getDestinations()), "category", transport.getCategory()).saveIt();
        }
    }

    private static String toJson(Object object) throws IllegalStateException {
        try {
            return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(object);
        } catch (JsonProcessingException exc) {
            throw new IllegalStateException("Could not produce JSON output", exc);
        }
    }

}
