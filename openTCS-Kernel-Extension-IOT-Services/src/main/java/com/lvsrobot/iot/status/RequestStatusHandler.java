/**
 * Copyright (c) The openTCS Authors.
 *
 * This program is free software and subject to the MIT license. (For details,
 * see the licensing information (LICENSE.txt) you should have received with
 * this copy of the software.)
 */
package com.lvsrobot.iot.status;

import org.opentcs.components.kernel.services.TCSObjectService;
import org.opentcs.components.kernel.services.TransportOrderService;
import org.opentcs.components.kernel.services.VehicleService;
import org.opentcs.customizations.kernel.KernelExecutor;
import org.opentcs.data.ObjectUnknownException;
import org.opentcs.data.model.Location;
import org.opentcs.data.model.Path;
import org.opentcs.data.model.Point;
import org.opentcs.data.model.Vehicle;
import org.opentcs.data.order.DriveOrder;
import org.opentcs.data.order.TransportOrder;
import com.lvsrobot.iot.status.binding.TransportOrderState;
import com.lvsrobot.iot.status.binding.VehicleState;
import com.lvsrobot.iot.status.filter.TransportOrderFilter;
import com.lvsrobot.iot.status.filter.VehicleFilter;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;

import static java.util.Objects.requireNonNull;

/**
 * Handles requests for getting the current state of model elements.
 *
 * @author Mustafa Yalciner (Fraunhofer IML)
 */
public class RequestStatusHandler {

    /**
     * The service we use to fetch objects.
     */
    private final TransportOrderService orderService;
    /**
     * Used to update vehicle instances.
     */
    private final VehicleService vehicleService;
    /**
     * The kernel's executor service.
     */
    private final ExecutorService kernelExecutor;

    private final TCSObjectService objectService;

    /**
     * Creates a new instance.
     *
     * @param orderService   The service we use to get the transport orders.
     * @param vehicleService Used to update vehicle instances.
     * @param kernelExecutor The kernel's executor service.
     */
    @Inject
    public RequestStatusHandler(TransportOrderService orderService, VehicleService vehicleService, @KernelExecutor ExecutorService kernelExecutor, @Nonnull TCSObjectService objectService) {
        this.orderService = requireNonNull(orderService, "orderService");
        this.vehicleService = requireNonNull(vehicleService, "vehicleService");
        this.kernelExecutor = requireNonNull(kernelExecutor, "kernelExecutor");
        this.objectService = requireNonNull(objectService, "objectService");
    }

    public List<Point> getPoints() {
        return objectService.fetchObjects(Point.class).stream().collect(Collectors.toList());
    }

    public List<Location> getLocations() {
        return objectService.fetchObjects(Location.class).stream().collect(Collectors.toList());
    }

    public List<Path> getPaths() {
        return objectService.fetchObjects(Path.class).stream().collect(Collectors.toList());
    }

    public List<DriveOrder> getDriverOrder() {
//        Vehicle vehicle = orderService.fetchObjects(Vehicle.class, )
        return orderService.fetchObjects(TransportOrder.class).stream().map(order -> order.getCurrentDriveOrder()).collect(Collectors.toList());
    }
    public DriveOrder getDriverOrderByName(String name) throws ObjectUnknownException {
//        Vehicle vehicle = orderService.fetchObjects(Vehicle.class, )
        return orderService.fetchObjects(TransportOrder.class, t -> t.getName().equals(name)).stream().map(order -> order.getCurrentDriveOrder())
                .findAny()
                .orElseThrow(() -> new ObjectUnknownException("Unknown transport order: " + name));
    }

    public List<Vehicle> getVehicles() {
        List<Vehicle> vehicles = orderService.fetchObjects(Vehicle.class,
                new VehicleFilter("PROCESSING_ORDER"))
                .stream().collect(Collectors.toList());
        return vehicles;
    }

    /**
     * Find all transport orders and filters depending on the given parameters.
     *
     * @param intendedVehicle The filter parameter for the name of the
     *                        intended vehicle for the transport order. The filtering is disabled for this parameter if the
     *                        value is null.
     * @return A list of transport orders that match the filter.
     */
    public List<TransportOrderState> getTransportOrdersState(@Nullable String intendedVehicle) {
        if (intendedVehicle != null) {
            Vehicle vehicle = orderService.fetchObject(Vehicle.class, intendedVehicle);
            if (vehicle == null) {
                throw new ObjectUnknownException("Unknown vehicle: " + intendedVehicle);
            }
        }

        return orderService.fetchObjects(TransportOrder.class, new TransportOrderFilter(intendedVehicle))
                .stream()
                .map(order -> TransportOrderState.fromTransportOrder(order))
                .collect(Collectors.toList());
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

    /**
     * Find all vehicles orders and filters depending on the given parameters.
     *
     * @param procState The filter parameter for the processing state of the vehicle.
     *                  The filtering is disabled for this parameter if the value is null.
     * @return A list of vehicles, that match the filter.
     */
    public List<VehicleState> getVehiclesState(@Nullable String procState) {
        List<VehicleState> vehicles = orderService.fetchObjects(Vehicle.class,
                new VehicleFilter(procState))
                .stream()
                .map(vehicle -> VehicleState.fromVehicle(vehicle))
//                .map((v) -> { VehicleState vehicleState = VehicleState.fromVehicle(v);
//                    vehicleState.setDriveOrder(getDriverOrderByName(vehicleState.getTransportOrder()));
//                    return vehicleState;})
                .collect(Collectors.toList());
        return vehicles;
    }

    /**
     * Finds the vehicle with the given name.
     *
     * @param name The name of the requested vehicle.
     * @return A single vehicle that has the given name.
     * @throws ObjectUnknownException If a vehicle with the given name does not exist.
     */
    public VehicleState getVehicleStateByName(String name) throws ObjectUnknownException {
        requireNonNull(name, "name");

        return orderService.fetchObjects(Vehicle.class, v -> v.getName().equals(name))
                .stream()
                .map(vehicle -> VehicleState.fromVehicle(vehicle))
//                .map((v) -> { VehicleState vehicleState = VehicleState.fromVehicle(v);
//                vehicleState.setDriveOrder(getDriverOrderByName(vehicleState.getTransportOrder()));
//                return vehicleState;})
//                .map(v -> v.setDriveOrder(getDriverOrderByName(v.getTransportOrder())))
                .findAny()
                .orElseThrow(() -> new ObjectUnknownException("Unknown vehicle: " + name));
    }

    public void putVehicleIntegrationLevel(String name, String value) throws ObjectUnknownException, IllegalArgumentException {
        requireNonNull(name, "name");
        requireNonNull(value, "value");

        Vehicle vehicle = orderService.fetchObject(Vehicle.class, name);
        if (vehicle == null) {
            throw new ObjectUnknownException("Unknown vehicle: " + name);
        }

        Vehicle.IntegrationLevel level = Vehicle.IntegrationLevel.valueOf(value);

        kernelExecutor.submit(() -> vehicleService.updateVehicleIntegrationLevel(vehicle.getReference(), level)
        );
    }

}
