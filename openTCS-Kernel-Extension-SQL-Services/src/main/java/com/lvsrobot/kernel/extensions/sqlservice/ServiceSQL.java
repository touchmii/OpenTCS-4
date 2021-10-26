package com.lvsrobot.kernel.extensions.sqlservice;

import org.opentcs.components.kernel.KernelExtension;
import org.opentcs.components.kernel.services.TransportOrderService;
import org.opentcs.components.kernel.services.VehicleService;

import javax.inject.Inject;

public class ServiceSQL implements KernelExtension {
    private TransportOrderService orderService;
    private VehicleService vehicleService;
    private SQLServiceController serviceController;
    private boolean initialize = false;

    @Inject
    public void ServiceIot(SQLServiceController serviceController) {

        this.serviceController = serviceController;
    }
    @Override
    public void initialize() {
//        serviceController = new ServiceController();
//        serviceController.run();
        Thread SQLServiceThread = new Thread(serviceController, "SQLService");
        SQLServiceThread.start();
        initialize = true;
    }

    @Override
    public boolean isInitialized() {
        return initialize;
    }

    @Override
    public void terminate() {
        initialize = false;
        serviceController.terminate();

    }
}
