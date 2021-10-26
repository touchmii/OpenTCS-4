/**
 * Copyright (c) The openTCS Authors.
 *
 * This program is free software and subject to the MIT license. (For details,
 * see the licensing information (LICENSE.txt) you should have received with
 * this copy of the software.)
 */
package com.lvsrobot.kernel.extensions.sqlservice.status;

import com.lvsrobot.kernel.extensions.sqlservice.SQLServiceConfiguration;
import com.lvsrobot.kernel.extensions.sqlservice.status.binding.OrderStatusMessage;
import com.lvsrobot.kernel.extensions.sqlservice.status.binding.StatusMessage;
import com.lvsrobot.kernel.extensions.sqlservice.status.binding.StatusMessageList;
import com.lvsrobot.kernel.extensions.sqlservice.status.binding.VehicleStatusMessage;
import org.opentcs.components.Lifecycle;
import org.opentcs.customizations.ApplicationEventBus;
import org.opentcs.data.TCSObject;
import org.opentcs.data.TCSObjectEvent;
import org.opentcs.data.model.Vehicle;
import org.opentcs.data.order.TransportOrder;
import org.opentcs.util.event.EventHandler;
import org.opentcs.util.event.EventSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.Collection;
import java.util.SortedMap;
import java.util.TreeMap;

import static java.util.Objects.requireNonNull;
import static org.opentcs.util.Assertions.checkInRange;

/**
 * Provides descriptions of recent events.
 *
 * @author Stefan Walter (Fraunhofer IML)
 */
public class StatusEventDispatcher
    implements Lifecycle,
               EventHandler {

  /**
   * This class's logger.
   */
  private static final Logger LOG = LoggerFactory.getLogger(StatusEventDispatcher.class);
  /**
   * The interface configuration.
   */
  private final SQLServiceConfiguration configuration;
  /**
   * Where we register for application events.
   */
  private final EventSource eventSource;
  /**
   * The events collected.
   */
  private final SortedMap<Long, StatusMessage> events = new TreeMap<>();
  /**
   * The number of events collected so far.
   */
  private long eventCount;
  /**
   * Whether this instance is initialized.
   */
  private boolean initialized;

  @Inject
  public StatusEventDispatcher(SQLServiceConfiguration configuration,
                               @ApplicationEventBus EventSource eventSource) {
    this.configuration = requireNonNull(configuration, "configuration");
    this.eventSource = requireNonNull(eventSource, "eventSource");
  }

  @Override
  public void initialize() {
    if (isInitialized()) {
      return;
    }

    synchronized (events) {
      eventCount = 0;
      events.clear();
    }

    eventSource.subscribe(this);

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

    eventSource.unsubscribe(this);

    initialized = false;
  }

  @Override
  public void onEvent(Object event) {
    if (!(event instanceof TCSObjectEvent)) {
      return;
    }
    TCSObject<?> object = ((TCSObjectEvent) event).getCurrentOrPreviousObjectState();
    if (object instanceof TransportOrder) {
      synchronized (events) {
        addOrderStatusMessage((TransportOrder) object, eventCount);
        eventCount++;
        cleanUpEvents();
        events.notifyAll();
      }
    }
    else if (object instanceof Vehicle) {
      synchronized (events) {
        addVehicleStatusMessage((Vehicle) object, eventCount);
        eventCount++;
        cleanUpEvents();
        events.notifyAll();
      }
    }
  }

  /**
   * Provides a list of events within the given range, waiting at most <code>timeout</code>
   * milliseconds for new events if there currently aren't any.
   *
   * @param minSequenceNo The minimum sequence number for accepted events.
   * @param maxSequenceNo The maximum sequence number for accepted events.
   * @param timeout The maximum time to wait for events (in ms) if there currently aren't any.
   * @return A list of events within the given range.
   */
  public StatusMessageList fetchEvents(long minSequenceNo, long maxSequenceNo, long timeout)
      throws IllegalArgumentException {
    checkInRange(minSequenceNo, 0, Long.MAX_VALUE, "minSequenceNo");
    checkInRange(maxSequenceNo, minSequenceNo, Long.MAX_VALUE, "maxSequenceNo");
    checkInRange(timeout, 0, Long.MAX_VALUE, "timeout");

    StatusMessageList result = new StatusMessageList();
    synchronized (events) {
      Collection<StatusMessage> messages = events.subMap(minSequenceNo, maxSequenceNo).values();
      if (messages.isEmpty()) {
        try {
          events.wait(timeout);
        }
        catch (InterruptedException exc) {
          LOG.warn("Unexpectedly interrupted", exc);
        }
      }
      messages = events.subMap(minSequenceNo, maxSequenceNo).values();
      result.getStatusMessages().addAll(messages);
    }
    return result;
  }

  private void addOrderStatusMessage(TransportOrder order, long sequenceNumber) {
    events.put(sequenceNumber, OrderStatusMessage.fromTransportOrder(order, sequenceNumber));
  }

  private void addVehicleStatusMessage(Vehicle vehicle, long sequenceNumber) {
    events.put(sequenceNumber, VehicleStatusMessage.fromVehicle(vehicle, sequenceNumber));
  }

  private void cleanUpEvents() {
    // XXX Sanitize maxEventCount
    int maxEventCount = 100;
    while (events.size() > maxEventCount) {
      events.remove(events.firstKey());
    }
  }
}
