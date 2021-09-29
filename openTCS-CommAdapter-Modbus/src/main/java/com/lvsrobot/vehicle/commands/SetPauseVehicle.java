package com.lvsrobot.vehicle.commands;

import com.lvsrobot.vehicle.ExampleCommAdapter;
import org.opentcs.drivers.vehicle.AdapterCommand;
import org.opentcs.drivers.vehicle.VehicleCommAdapter;

public class SetPauseVehicle implements AdapterCommand {

    private final boolean paused;

    public SetPauseVehicle(boolean paused) {this.paused = paused;}

    @Override
    public void execute(VehicleCommAdapter adapter) {
        if (!(adapter instanceof ExampleCommAdapter)) {
            return;
        }

        ExampleCommAdapter exampleCommAdapter = (ExampleCommAdapter) adapter;
        exampleCommAdapter.getProcessModel().setVehiclePaused(paused);
    }

}
