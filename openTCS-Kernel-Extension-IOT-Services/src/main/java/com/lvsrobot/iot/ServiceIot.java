package com.lvsrobot.iot;

import org.opentcs.components.kernel.KernelExtension;
import org.opentcs.components.kernel.services.TransportOrderService;
import org.opentcs.components.kernel.services.VehicleService;

import javax.inject.Inject;

public class ServiceIot implements KernelExtension {
    private TransportOrderService orderService;
    private VehicleService vehicleService;
    private ServiceController serviceController;
    private boolean initialize = false;

    @Inject
    public void ServiceIot(ServiceController serviceController) {

        this.serviceController = serviceController;
    }
    @Override
    public void initialize() {
//        serviceController = new ServiceController();
//        serviceController.run();
        Thread IotServiceThread = new Thread(serviceController, "IotService");
        IotServiceThread.start();
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
