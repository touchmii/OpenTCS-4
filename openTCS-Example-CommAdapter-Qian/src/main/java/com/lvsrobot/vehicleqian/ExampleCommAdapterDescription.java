/**
 * Copyright (c) Fraunhofer IML
 */
package com.lvsrobot.vehicleqian;

import java.util.ResourceBundle;

import org.opentcs.drivers.vehicle.VehicleCommAdapterDescription;

/**
 * The comm adapter's {@link VehicleCommAdapterDescription}.
 *
 * @author Martin Grzenia (Fraunhofer IML)
 */
public class ExampleCommAdapterDescription extends VehicleCommAdapterDescription {

    @Override
    public String getDescription() {
        return ResourceBundle.getBundle("com/lvsrobot/vehicleqian/Bundle").getString("AdapterFactoryDescription");
    }
}
