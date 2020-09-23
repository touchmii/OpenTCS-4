import org.opentcs.access.KernelServicePortal;
import org.opentcs.access.rmi.KernelServicePortalBuilder;
import org.opentcs.components.kernel.services.PlantModelService;

import java.util.List;

public class rmitest {
    public static void main(String[] args) {
        KernelServicePortal servicePortal = new KernelServicePortalBuilder().build();

        servicePortal.login("127.0.0.1", 1099);
        PlantModelService plantModelService = servicePortal.getPlantModelService();
        String modelName = plantModelService.getModelName();

        List<Object> events = servicePortal.fetchEvents(1000);

        System.out.println(modelName);
        System.out.println("rmitest");
    }
}
