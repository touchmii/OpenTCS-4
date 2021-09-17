/**
 * Copyright (c) Fraunhofer IML
 */
package com.lvsrobot.vehicletcp;

import java.util.ResourceBundle;

import org.opentcs.drivers.vehicle.VehicleCommAdapterDescription;

/**
 * The comm adapter's {@link VehicleCommAdapterDescription}.
 *
 * @author Martin Grzenia (Fraunhofer IML)
 */
public class TCPCommAdapterDescription extends VehicleCommAdapterDescription {

    @Override
    public String getDescription() {
        return ResourceBundle.getBundle("com/lvsrobot/vehicletcp/Bundle").getString("AdapterFactoryDescription");
    }
}
