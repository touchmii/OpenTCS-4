package com.lvsrobot.rosbridge.commands;

import com.lvsrobot.rosbridge.ROSBridgeCommAdapter;
import org.opentcs.drivers.vehicle.AdapterCommand;
import org.opentcs.drivers.vehicle.VehicleCommAdapter;

public class SetPauseVehicle implements AdapterCommand {

    private final boolean paused;

    public SetPauseVehicle(boolean paused) {this.paused = paused;}

    @Override
    public void execute(VehicleCommAdapter adapter) {
        if (!(adapter instanceof ROSBridgeCommAdapter)) {
            return;
        }

        ROSBridgeCommAdapter ROSBridgeCommAdapter = (ROSBridgeCommAdapter) adapter;
        ROSBridgeCommAdapter.getProcessModel().setVehiclePaused(paused);
    }

}
