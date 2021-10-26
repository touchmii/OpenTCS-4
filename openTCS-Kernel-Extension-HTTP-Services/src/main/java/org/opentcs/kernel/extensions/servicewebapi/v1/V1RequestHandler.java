/**
 * Copyright (c) The openTCS Authors.
 *
 * This program is free software and subject to the MIT license. (For details,
 * see the licensing information (LICENSE.txt) you should have received with
 * this copy of the software.)
 */
package org.opentcs.kernel.extensions.servicewebapi.v1;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.opentcs.data.ObjectExistsException;
import org.opentcs.data.ObjectUnknownException;
import org.opentcs.data.model.Vehicle;
import org.opentcs.kernel.extensions.servicewebapi.HttpConstants;
import org.opentcs.kernel.extensions.servicewebapi.RequestHandler;
import org.opentcs.kernel.extensions.servicewebapi.v1.order.OrderHandler;
import org.opentcs.kernel.extensions.servicewebapi.v1.order.binding.Command;
import org.opentcs.kernel.extensions.servicewebapi.v1.order.binding.Transport;
import org.opentcs.kernel.extensions.servicewebapi.v1.status.RequestStatusHandler;
import org.opentcs.kernel.extensions.servicewebapi.v1.status.StatusEventDispatcher;
import org.opentcs.kernel.extensions.servicewebapi.v1.status.binding.Destination;
import org.opentcs.kernel.extensions.servicewebapi.v1.status.binding.TransportOrderState;
import org.opentcs.kernel.extensions.servicewebapi.v1.status.binding.VehicleState;
import spark.QueryParamsMap;
import spark.Request;
import spark.Response;
import spark.Service;

import javax.inject.Inject;
import java.io.IOException;
import java.util.List;

import static java.util.Objects.requireNonNull;

/**
 * Handles requests and produces responses for version 1 of the web API.
 *
 * @author Stefan Walter (Fraunhofer IML)
 */
public class V1RequestHandler
        implements RequestHandler {

    /**
     * Maps between objects and their JSON representations.
     */
    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule()).disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    /**
     * Collects interesting events and provides them for client requests.
     */
    private final StatusEventDispatcher statusEventDispatcher;
    /**
     * Creates transport orders.
     */
    private final OrderHandler orderHandler;

    private final RequestStatusHandler statusInformationProvider;
    /**
     * Whether this instance is initialized.
     */
    private boolean initialized;

    @Inject
    public V1RequestHandler(StatusEventDispatcher statusEventDispatcher, OrderHandler orderHandler, RequestStatusHandler requestHandler) {
        this.statusEventDispatcher = requireNonNull(statusEventDispatcher, "statusEventDispatcher");
        this.orderHandler = requireNonNull(orderHandler, "orderHandler");
        this.statusInformationProvider = requireNonNull(requestHandler, "requestHandler");
    }

    @Override
    public void initialize() {
        if (isInitialized()) {
            return;
        }

        statusEventDispatcher.initialize();

        initialized = true;
    }

    @Override
    public boolean isInitialized() {
        return initialized;
    }

    @Override
    public void terminate() {
        if (!isInitialized()) {
            return;
        }

        statusEventDispatcher.terminate();

        initialized = false;
    }

    @Override
    public void addRoutes(Service service) {
        requireNonNull(service, "service");

        service.get("/events", this::handleGetEvents);
        service.put("/vehicles/:NAME/integrationLevel", this::handlePutVehicleIntegrationLevel);
        service.post("/vehicles/:NAME/withdrawal", this::handlePostWithdrawalByVehicle);
        service.get("/vehicles/:NAME", this::handleGetVehicleByName);
        service.get("/vehiclesbrief/:NAME", this::handleGetVehicleBriefByName);
        service.get("/vehicles", this::handleGetVehicles);
        service.post("/vehicle/sndpath:START/:END", this::handleSendPath);
        service.post("/transportOrders/:NAME/withdrawal", this::handlePostWithdrawalByOrder);
        service.post("/transportOrders/:NAME", this::handlePostTransportOrder);
        service.get("/transportOrders/:NAME", this::handleGetTransportOrderByName);
        service.get("/transportOrders", this::handleGetTransportOrders);
        service.get("/transportOrdersbrief", this::handleGetTransportOrdersBrief);
        service.get("/points", this::handleGetPoints);
        service.get("/locations", this::handleGetLocations);
        service.get("/paths", this::handleGetPaths);
        service.get("/driverorders", this::handleGetDriverOrder);
        service.get("/driverorder/:NAME", this::handleGetDriverOrderByName);
        service.get("/vehicledetails", this::handleGetVehicleDetails);
        service.get("/vehicledetailsbrief", this::handleGetVehicleDetailsBrief);
        service.post("/command/:NAME", this::handlePostCommandByVehicle);
        service.get("/mapbrief.html", this::handleGetMapBrief);
        service.post("/Point", this::handleReplacePoint);
    }

    private Object handleReplacePoint(Request request, Response response) {
        response.type(HttpConstants.CONTENT_TYPE_TEXT_PLAIN_UTF8);
        return orderHandler.replacePoint(request.queryParams("name"), request.queryParams("X"), request.queryParams("Y"));
    }

    private Object handlePostCommandByVehicle(Request request, Response response) throws IllegalArgumentException, IllegalStateException {
        response.type(HttpConstants.CONTENT_TYPE_TEXT_PLAIN_UTF8);
        return orderHandler.sendCommand(request.params(":NAME"), fromJson(request.body(), Command.class));
    }

    private Object handleSendPath(Request request, Response response) throws IllegalArgumentException, IllegalStateException {
        orderHandler.createOrder(request.params(":END"), fromJson(request.body(), Transport.class));
        response.type(HttpConstants.CONTENT_TYPE_TEXT_PLAIN_UTF8);
        return "";
    }
    private Object handleGetDriverOrder(Request request, Response response) throws IllegalArgumentException, IllegalStateException {
        response.type(HttpConstants.CONTENT_TYPE_TEXT_PLAIN_UTF8);
        return toJson(statusInformationProvider.getDriverOrder());
    }
    private Object handleGetDriverOrderByName(Request request, Response response) throws IllegalArgumentException, IllegalStateException {
        response.type(HttpConstants.CONTENT_TYPE_TEXT_PLAIN_UTF8);
        return toJson(statusInformationProvider.getDriverOrderByName(request.params(":NAME")));
    }
    private Object handleGetVehicleDetails(Request request, Response response) throws IllegalArgumentException, IllegalStateException {
        response.type(HttpConstants.CONTENT_TYPE_APPLICATION_JSON_UTF8);
        return toJson(statusInformationProvider.getVehicles());
    }
    private Object handleGetVehicleDetailsBrief(Request request, Response response) throws IllegalArgumentException, IllegalStateException {
        response.type(HttpConstants.CONTENT_TYPE_TEXT_PLAIN_UTF8);
        List<Vehicle> vehicles = statusInformationProvider.getVehicles();
        StringBuilder vehiclesBrief = new StringBuilder();
        for (Vehicle vehicle : vehicles) {
            vehiclesBrief.append(String.format("车辆: %s, 位置: %s 电量: %s 状态: %s", vehicle.getName(), vehicle.getCurrentPosition().getName(), vehicle.getEnergyLevel(), vehicle.getState()));
        }
        return vehiclesBrief.toString();
    }
    private Object handleGetMapBrief(Request request, Response response) throws IllegalArgumentException, IllegalStateException {
//        response.type(HttpConstants.CONTENT_TYPE_TEXT_PLAIN_UTF8);
        response.type("text/html");
        return statusInformationProvider.getSVG();
    }
    private Object handleGetPoints(Request request, Response response) throws IllegalArgumentException, IllegalStateException {
        response.type(HttpConstants.CONTENT_TYPE_APPLICATION_JSON_UTF8);
        return toJson(statusInformationProvider.getPoints());
    }

    private Object handleGetLocations(Request request, Response response) throws IllegalArgumentException, IllegalStateException {
        response.type(HttpConstants.CONTENT_TYPE_APPLICATION_JSON_UTF8);
        return toJson(statusInformationProvider.getLocations());
    }

    private Object handleGetPaths(Request request, Response response) throws IllegalArgumentException, IllegalStateException {
        response.type(HttpConstants.CONTENT_TYPE_APPLICATION_JSON_UTF8);
        return toJson(statusInformationProvider.getPaths());
    }

    private Object handleGetEvents(Request request, Response response) throws IllegalArgumentException, IllegalStateException {
        response.type(HttpConstants.CONTENT_TYPE_APPLICATION_JSON_UTF8);
        return toJson(statusEventDispatcher.fetchEvents(minSequenceNo(request), maxSequenceNo(request), timeout(request)));
    }

    private Object handlePostTransportOrder(Request request, Response response) throws ObjectUnknownException, ObjectExistsException, IllegalArgumentException, IllegalStateException {
        orderHandler.createOrder(request.params(":NAME"), fromJson(request.body(), Transport.class));
        response.type(HttpConstants.CONTENT_TYPE_TEXT_PLAIN_UTF8);
        return "";
    }

    private Object handlePostWithdrawalByOrder(Request request, Response response) throws ObjectUnknownException {
        orderHandler.withdrawByTransportOrder(request.params(":NAME"), immediate(request), disableVehicle(request));
        response.type(HttpConstants.CONTENT_TYPE_TEXT_PLAIN_UTF8);
        return "";
    }

    private Object handlePostWithdrawalByVehicle(Request request, Response response) throws ObjectUnknownException {
        orderHandler.withdrawByVehicle(request.params(":NAME"), immediate(request), disableVehicle(request));
        response.type(HttpConstants.CONTENT_TYPE_TEXT_PLAIN_UTF8);
        return "";
    }

    private Object handleGetTransportOrders(Request request, Response response) {
        response.type(HttpConstants.CONTENT_TYPE_APPLICATION_JSON_UTF8);
        return toJson(statusInformationProvider.getTransportOrdersState(valueIfKeyPresent(request.queryMap(), "intendedVehicle")));
    }
    private Object handleGetTransportOrdersBrief(Request request, Response response) {
        response.type(HttpConstants.CONTENT_TYPE_TEXT_PLAIN_UTF8);
        List<TransportOrderState> transportOrders = statusInformationProvider.getTransportOrdersState(valueIfKeyPresent(request.queryMap(), "intendedVehicle"));
        StringBuilder transportOrdersBrief = new StringBuilder();
        for (TransportOrderState transport : transportOrders) {
            StringBuilder ds = new StringBuilder();
            for (Destination d : transport.getDestinations()) {
                ds.append(" ").append(d.getLocationName());
            }
//            transport.getDestinations().stream().forEach(point -> point.getLocationName())
            transportOrdersBrief.append(String.format("订单: %s, 途经点: %s, 状态: %s, 处理车辆: %s.\n", transport.getName(), ds.toString(), transport.getState(), transport.getProcessingVehicle()));
        }
        return transportOrdersBrief.toString();
    }

    private Object handleGetTransportOrderByName(Request request, Response response) {
        response.type(HttpConstants.CONTENT_TYPE_APPLICATION_JSON_UTF8);
        return toJson(statusInformationProvider.getTransportOrderByName(request.params(":NAME")));
    }

    private Object handleGetVehicles(Request request, Response response) throws IllegalArgumentException {
        response.type(HttpConstants.CONTENT_TYPE_APPLICATION_JSON_UTF8);
        return toJson(statusInformationProvider.getVehiclesState(valueIfKeyPresent(request.queryMap(), "procState"))
        );
    }

    private Object handleGetVehicleByName(Request request, Response response) throws ObjectUnknownException {
        response.type(HttpConstants.CONTENT_TYPE_TEXT_PLAIN_UTF8);
        return toJson(statusInformationProvider.getVehicleStateByName(request.params(":NAME")));
    }
    private Object handleGetVehicleBriefByName(Request request, Response response) throws ObjectUnknownException {
        response.type(HttpConstants.CONTENT_TYPE_TEXT_PLAIN_UTF8);
        VehicleState vehicle = statusInformationProvider.getVehicleStateByName(request.params(":NAME"));
        return String.format("车辆名称: %s, 位置: %s, 电量: %s, 状态: %s, 任务: %s.", vehicle.getName(), vehicle.getCurrentPosition(), vehicle.getEnergyLevel(),vehicle.getState(), vehicle.getTransportOrder());
    }

    private Object handlePutVehicleIntegrationLevel(Request request, Response response) throws ObjectUnknownException, IllegalArgumentException {
        statusInformationProvider.putVehicleIntegrationLevel(request.params(":NAME"), valueIfKeyPresent(request.queryMap(), "newValue"));
        response.type(HttpConstants.CONTENT_TYPE_TEXT_PLAIN_UTF8);
        return "";
    }

    private String valueIfKeyPresent(QueryParamsMap queryParams, String key) {
        if (queryParams.hasKey(key)) {
            return queryParams.value(key);
        } else {
            return null;
        }
    }

    private <T> T fromJson(String jsonString, Class<T> clazz) throws IllegalArgumentException {
        try {
            return objectMapper.readValue(jsonString, clazz);
        } catch (IOException exc) {
            throw new IllegalArgumentException("Could not parse JSON input", exc);
        }
    }

    private String toJson(Object object) throws IllegalStateException {
        try {
            return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(object);
        } catch (JsonProcessingException exc) {
            throw new IllegalStateException("Could not produce JSON output", exc);
        }
    }

    private long minSequenceNo(Request request) throws IllegalArgumentException {
        String param = request.queryParamOrDefault("minSequenceNo", "0");
        try {
            return Long.parseLong(param);
        } catch (NumberFormatException exc) {
            throw new IllegalArgumentException("Malformed minSequenceNo: " + param);
        }
    }

    private long maxSequenceNo(Request request) throws IllegalArgumentException {
        String param = request.queryParamOrDefault("maxSequenceNo", String.valueOf(Long.MAX_VALUE));
        try {
            return Long.parseLong(param);
        } catch (NumberFormatException exc) {
            throw new IllegalArgumentException("Malformed minSequenceNo: " + param);
        }
    }

    private long timeout(Request request) throws IllegalArgumentException {
        String param = request.queryParamOrDefault("timeout", "1000");
        try {
            // Allow a maximum timeout of 10 seconds so server threads are only bound for a limited time.
            return Math.min(10000, Long.parseLong(param));
        } catch (NumberFormatException exc) {
            throw new IllegalArgumentException("Malformed timeout: " + param);
        }
    }

    private boolean immediate(Request request) {
        return Boolean.parseBoolean(request.queryParamOrDefault("immediate", "false"));
    }

    private boolean disableVehicle(Request request) {
        return Boolean.parseBoolean(request.queryParamOrDefault("disableVehicle", "false"));
    }
}
