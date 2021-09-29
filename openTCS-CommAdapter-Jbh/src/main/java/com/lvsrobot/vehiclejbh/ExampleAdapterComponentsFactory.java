/**
 * Copyright (c) Fraunhofer IML
 */
package com.lvsrobot.vehiclejbh;

import org.opentcs.data.model.Vehicle;

/**
 * A factory for various instances specific to the comm adapter.
 *
 * @author Martin Grzenia (Fraunhofer IML)
 */
public interface ExampleAdapterComponentsFactory {

  /**
   * Creates a new ExampleCommAdapter for the given vehicle.
   *
   * @param vehicle The vehicle
   * @return A new ExampleCommAdapter for the given vehicle
   */
  ExampleCommAdapter createExampleCommAdapter(Vehicle vehicle);
}
