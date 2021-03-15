/**
 * Copyright (c) The openTCS Authors.
 *
 * This program is free software and subject to the MIT license. (For details,
 * see the licensing information (LICENSE.txt) you should have received with
 * this copy of the software.)
 */
package org.opentcs.kernel.extensions.servicewebapi.v1.order;

import java.time.Instant;
import java.util.*;

import static java.util.Objects.requireNonNull;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import javax.annotation.Nonnull;
import javax.inject.Inject;
import org.opentcs.access.KernelRuntimeException;
import org.opentcs.access.to.order.DestinationCreationTO;
import org.opentcs.access.to.order.TransportOrderCreationTO;
import org.opentcs.components.kernel.services.DispatcherService;
import org.opentcs.components.kernel.services.TCSObjectService;
import org.opentcs.components.kernel.services.TransportOrderService;
import org.opentcs.components.kernel.services.VehicleService;
import org.opentcs.customizations.ServiceCallWrapper;
import org.opentcs.customizations.kernel.KernelExecutor;
import org.opentcs.data.ObjectExistsException;
import org.opentcs.data.ObjectUnknownException;
import org.opentcs.data.TCSObjectReference;
import org.opentcs.data.model.Vehicle;
import org.opentcs.data.order.TransportOrder;
import org.opentcs.drivers.vehicle.AdapterCommand;
import org.opentcs.drivers.vehicle.VehicleCommAdapter;
import org.opentcs.drivers.vehicle.VehicleCommAdapterEvent;
import org.opentcs.drivers.vehicle.commands.PublishEventCommand;
import org.opentcs.kernel.extensions.servicewebapi.v1.order.binding.Command;
import org.opentcs.kernel.extensions.servicewebapi.v1.order.binding.Destination;
import org.opentcs.kernel.extensions.servicewebapi.v1.order.binding.Property;
import org.opentcs.kernel.extensions.servicewebapi.v1.order.binding.Transport;
import org.opentcs.util.CallWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Handles requests for creating or withdrawing transport orders.
 *
 * @author Stefan Walter (Fraunhofer IML)
 */
public class OrderHandler {

  /**
   * The service we use to create transport orders.
   */
  private final TransportOrderService orderService;
  /**
   * The service we use to update vehicle states.
   */
  private final VehicleService vehicleService;
  /**
   * The service we use to withdraw transport orders.
   */
  private final DispatcherService dispatcherService;
  /**
   * Executes tasks modifying kernel data.
   */
  private final ExecutorService kernelExecutor;

  private static final Logger LOG = LoggerFactory.getLogger(OrderHandler.class);

//  private final CallWrapper callWrapper;

  private final TCSObjectService objectService;

  /**
   * Creates a new instance.
   *
   * @param orderService Used to create transport orders.
   * @param vehicleService Used to update vehicle state.
   * @param dispatcherService Used to withdraw transport orders.
   * @param kernelExecutor Executes tasks modifying kernel data.
   */
  @Inject
  public OrderHandler(TransportOrderService orderService,
                      VehicleService vehicleService,
                      DispatcherService dispatcherService,
                      @KernelExecutor ExecutorService kernelExecutor,
//                      @ServiceCallWrapper CallWrapper callWrapper,
                      @Nonnull TCSObjectService objectService) {
    this.orderService = requireNonNull(orderService, "orderService");
    this.vehicleService = requireNonNull(vehicleService, "vehicleService");
    this.dispatcherService = requireNonNull(dispatcherService, "dispatcherService");
    this.kernelExecutor = requireNonNull(kernelExecutor, "kernelExecutor");
//    this.callWrapper = requireNonNull(callWrapper, "callWrapper");
    this.objectService = requireNonNull(objectService, "objectService");
  }

  /***
   * Send Command to Vehicle Adapter
   * @param name Vehicle name
   * @param command Command name
   * @return process result
   */
  public String sendCommand(String name, Command command) {
    try {
      TCSObjectReference<Vehicle> vehicleReference = vehicleService.fetchObject(Vehicle.class, name).getReference();
      VehicleCommAdapterEvent event = new VehicleCommAdapterEvent(name, command.getCommand());
      try {
//      callWrapper.call(() -> vehicleService.sendCommAdapterCommand(vehicleReference, new PublishEventCommand(event)));
        vehicleService.sendCommAdapterCommand(vehicleReference, new PublishEventCommand(event));

      } catch (Exception e) {
        LOG.warn("Can't send command to vehicle");
        e.getMessage();
        throw new ObjectUnknownException(("Can't send command to vehicle"));
      }

    } catch (Exception e) {
      e.getMessage();
      LOG.warn("Can't found vechile name: {}", name);
      throw new ObjectUnknownException("Unknow Vehicle name: " + name);
    }
    return String.format("Send command: %s to Vehicle: %s success.", command.getCommand(),name);
  }

  /***
   * Create Order
   * @param name order name
   * @param order order object
   * @throws ObjectUnknownException
   * @throws ObjectExistsException
   * @throws KernelRuntimeException
   * @throws IllegalStateException
   */
  public void createOrder(String name, Transport order)
      throws ObjectUnknownException,
             ObjectExistsException,
             KernelRuntimeException,
             IllegalStateException {
    requireNonNull(name, "name");
    requireNonNull(order, "order");
    TransportOrderCreationTO to;
    if (name.equals("Shortcuts")) {

      to
          = new TransportOrderCreationTO(String.format("TOrder-%s", UUID.randomUUID()), destinations(order))
              .withIntendedVehicleName(order.getIntendedVehicle())
              .withDependencyNames(new HashSet<>(order.getDependencies()))
              .withDeadline(deadline(order))
              .withProperties(properties(order.getProperties()));
    } else {

      to
                = new TransportOrderCreationTO(name, destinations(order))
                    .withIntendedVehicleName(order.getIntendedVehicle())
                    .withDependencyNames(new HashSet<>(order.getDependencies()))
                    .withDeadline(deadline(order))
                    .withProperties(properties(order.getProperties()));
    }

    try {
      kernelExecutor.submit(() -> {
        orderService.createTransportOrder(to);
        dispatcherService.dispatch();
      }).get();
    }
    catch (InterruptedException exc) {
      throw new IllegalStateException("Unexpectedly interrupted");
    }
    catch (ExecutionException exc) {
      if (exc.getCause() instanceof RuntimeException) {
        throw (RuntimeException) exc.getCause();
      }
      throw new KernelRuntimeException(exc.getCause());
    }
  }

  public void withdrawByTransportOrder(String name, boolean immediate, boolean disableVehicle)
      throws ObjectUnknownException {
    requireNonNull(name, "name");

    if (orderService.fetchObject(TransportOrder.class, name) == null) {
      throw new ObjectUnknownException("Unknown transport order: " + name);
    }

    kernelExecutor.submit(() -> {
      TransportOrder order = orderService.fetchObject(TransportOrder.class, name);
      if (disableVehicle && order.getProcessingVehicle() != null) {
        vehicleService.updateVehicleIntegrationLevel(order.getProcessingVehicle(),
                                                     Vehicle.IntegrationLevel.TO_BE_RESPECTED);
      }

      dispatcherService.withdrawByTransportOrder(order.getReference(), immediate);
    });
  }

  public void withdrawByVehicle(String name, boolean immediate, boolean disableVehicle)
      throws ObjectUnknownException {
    requireNonNull(name, "name");

    Vehicle vehicle = orderService.fetchObject(Vehicle.class, name);
    if (vehicle == null) {
      throw new ObjectUnknownException("Unknown vehicle: " + name);
    }

    kernelExecutor.submit(() -> {
      if (disableVehicle) {
        vehicleService.updateVehicleIntegrationLevel(vehicle.getReference(),
                                                     Vehicle.IntegrationLevel.TO_BE_RESPECTED);
      }

      dispatcherService.withdrawByVehicle(vehicle.getReference(), immediate);
    });
  }

  private List<DestinationCreationTO> destinations(Transport order) {
    List<DestinationCreationTO> result = new ArrayList<>(order.getDestinations().size());

    for (Destination dest : order.getDestinations()) {
      DestinationCreationTO to = new DestinationCreationTO(dest.getLocationName(),
                                                           dest.getOperation());

      for (Property prop : dest.getProperties()) {
        to = to.withProperty(prop.getKey(), prop.getValue());
      }

      result.add(to);
    }

    return result;
  }

  /**
   * Get Vehicle Reference
   *
   * @param void
   * @return VehicleReference
   * 获取车辆的引用
   *
   **/
//  private TCSObjectReference<Vehicle> getVehicleReference(String vehicleName) throws Exception {
//    return callWrapper.call(() -> vehicleService.fetchObject(Vehicle.class, vehicleName)).getReference();
//    return vehicleService.fetchObject(Vehicle.class, vehicleName).getReference();
//  }

  /**
   * Send Command to Adapter
   * AdapterComomand 适配器命令
   * @param command
   */
//  private void sendCommAdapterCommand(AdapterCommand command) {
//    try {
//      TCSObjectReference<Vehicle> vehicleRef = getVehicleReference();
//      callWrapper.call(() -> vehicleService.sendCommAdapterCommand(vehicleRef, command));
//    } catch (Exception ex) {
//      LOG.warn("Error sending comm adapter command '{}'", command, ex);
//    }
//  }


  private Instant deadline(Transport order) {
//    return order.getDeadline() == null ? Instant.MAX : order.getDeadline();
    if (order.getDeadline() == null) {
//      return Instant.MAX;
      return Instant.ofEpochSecond(21556889864403199L);
    } else {
      return order.getDeadline() == Instant.MIN ? Instant.now() : order.getDeadline();
    }
  }

  private Map<String, String> properties(List<Property> properties) {
    Map<String, String> result = new HashMap<>();
    for (Property prop : properties) {
      result.put(prop.getKey(), prop.getValue());
    }
    return result;
  }

}
