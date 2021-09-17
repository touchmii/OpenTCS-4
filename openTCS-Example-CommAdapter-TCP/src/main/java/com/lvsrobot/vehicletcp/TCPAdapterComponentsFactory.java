/**
 * Copyright (c) Fraunhofer IML
 */
package com.lvsrobot.vehicletcp;

import org.opentcs.data.model.Vehicle;

/**
 * A factory for various instances specific to the comm adapter.
 *
 * @author Martin Grzenia (Fraunhofer IML)
 */
public interface TCPAdapterComponentsFactory {

  /**
   * Creates a new ExampleCommAdapter for the given vehicle.
   *
   * @param vehicle The vehicle
   * @return A new ExampleCommAdapter for the given vehicle
   */
  TCPCommAdapter createExampleCommAdapter(Vehicle vehicle);

  /**
   * Creates a new panel for the given comm adapter.
   *
   * @param commAdapter The comm adapter to create a panel for.
   * @return A new panel for the given comm adapter.
   */
  @Deprecated
  TCPCommunicationAdapterPanel createPanel(TCPCommAdapter commAdapter);
}
