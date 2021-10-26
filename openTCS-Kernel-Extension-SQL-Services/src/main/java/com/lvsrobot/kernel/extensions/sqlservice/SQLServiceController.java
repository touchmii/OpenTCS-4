package com.lvsrobot.kernel.extensions.sqlservice;

import com.lvsrobot.kernel.extensions.sqlservice.model.TransportModel;
import com.lvsrobot.kernel.extensions.sqlservice.status.RequestStatusHandler;
import com.lvsrobot.kernel.extensions.sqlservice.status.binding.TransportOrderState;
import org.javalite.activejdbc.Base;
import org.javalite.activejdbc.DB;
import org.opentcs.components.kernel.services.TCSObjectService;
import org.opentcs.customizations.ApplicationEventBus;
import org.opentcs.customizations.ApplicationHome;
import org.opentcs.data.ObjectUnknownException;
import org.opentcs.data.TCSObjectEvent;
import org.opentcs.data.order.TransportOrder;
import org.opentcs.util.CyclicTask;
import org.opentcs.util.event.EventBus;
import org.opentcs.util.event.EventHandler;
import org.opentcs.util.event.EventSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.io.File;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

import static java.util.Objects.requireNonNull;

public class SQLServiceController extends CyclicTask {

    private final File dataDirectory;
    private final File modelFile;
    private RequestStatusHandler statusHandler;
    private EventBus eventBus;
    private EventSource eventSource;
    TCSObjectService orderService;
    private SQLServiceConfiguration serviceConfiguration;
    private Queue<TransportOrderState> transportOrderStateQueue = new LinkedBlockingQueue<>();
    private DB db = new DB("main");
    
    private static final Logger LOG = LoggerFactory.getLogger(SQLServiceController.class);

    @Inject
    public SQLServiceController(@ApplicationHome File directory,
                                RequestStatusHandler statusHandler,
                                TCSObjectService orderService,
                                SQLServiceConfiguration serviceIotConfiguration,
                                @ApplicationEventBus EventBus eventBus,
                                @ApplicationEventBus EventSource eventSource) {
        super(1*1000);
        this.statusHandler = statusHandler;
        this.eventBus = eventBus;
        this.eventSource = eventSource;
        this.orderService = orderService;
        this.serviceConfiguration = serviceIotConfiguration;
        eventSource.subscribe(new EvendHandle());
        this.dataDirectory = new File(requireNonNull(directory, "directory"), "data");
        this.modelFile = new File(dataDirectory, serviceIotConfiguration.sqlName());
//        Base.open(serviceConfiguration.sqlDriver(), "jdbc:sqlite:"+modelFile.getAbsolutePath(), "", "");
//        try {
//            db.open(serviceIotConfiguration.sqlDriver(), "jdbc:sqlite:"+modelFile.getAbsolutePath(), "", "");
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

    public class EvendHandle implements EventHandler {
        @Override
        public void onEvent(Object event) {
            if (event instanceof TCSObjectEvent) {
                handleObjectEvent((TCSObjectEvent) event);
//            LOG.info("Iot rec Event: {}", event.toString());
            }
        }
        private void handleObjectEvent(TCSObjectEvent evt) {
            if (evt.getCurrentOrPreviousObjectState() instanceof TransportOrder) {
                TransportOrder order = null;
                switch (evt.getType()) {
                    case OBJECT_CREATED:
                        order = (TransportOrder) evt.getCurrentOrPreviousObjectState();
                        break;
                    case OBJECT_MODIFIED:
                        order = (TransportOrder) evt.getCurrentOrPreviousObjectState();
                        break;
                    case OBJECT_REMOVED:
                        order = (TransportOrder) evt.getCurrentOrPreviousObjectState();
                        break;
                    default:
                        LOG.warn("Unhandled event type: {}", evt.getType());
                }
                TransportOrderState orderState = getTransportOrderByName(order.getName());
                transportOrderStateQueue.add(orderState);
            }
        }
    }

    /**
     * Finds the transport order with the given name.
     *
     * @param name The name of the requested transport order.
     * @return A single transport order with the given name.
     * @throws ObjectUnknownException If a transport order with the given name does not exist.
     */
    public TransportOrderState getTransportOrderByName(String name) throws ObjectUnknownException {
        requireNonNull(name, "name");

        return orderService.fetchObjects(TransportOrder.class,
                t -> t.getName().equals(name))
                .stream()
                .map(t -> TransportOrderState.fromTransportOrder(t))
                .findAny()
                .orElseThrow(() -> new ObjectUnknownException("Unknown transport order: " + name));
    }

/*    @Override
    public void terminate() {
        try {
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.terminate();
    }*/



    @Override
    protected void runActualTask() {

        if (transportOrderStateQueue.size()>0) {
            if (!Base.hasConnection()) {
                Base.open(serviceConfiguration.sqlDriver(), "jdbc:sqlite:"+modelFile.getAbsolutePath(), "", "");
            }
            TransportModel.insertModel(transportOrderStateQueue.poll());
        }
    }


}
