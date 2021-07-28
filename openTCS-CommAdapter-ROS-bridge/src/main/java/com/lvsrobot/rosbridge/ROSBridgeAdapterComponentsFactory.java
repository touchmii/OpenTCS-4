/**
 * Copyright (c) Fraunhofer IML
 */
package com.lvsrobot.rosbridge;

import org.opentcs.data.model.Vehicle;

/**
 * A factory for various instances specific to the comm adapter.
 *
 * @author Martin Grzenia (Fraunhofer IML)
 */
public interface ROSBridgeAdapterComponentsFactory {

  /**
   * Creates a new ExampleCommAdapter for the given vehicle.
   *
   * @param vehicle The vehicle
   * @return A new ExampleCommAdapter for the given vehicle
   * 返回车辆的驱动适配器
   */
  ROSBridgeCommAdapter createExampleCommAdapter(Vehicle vehicle);
}
