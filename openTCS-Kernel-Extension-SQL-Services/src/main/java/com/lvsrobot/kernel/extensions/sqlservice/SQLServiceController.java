package com.lvsrobot.kernel.extensions.sqlservice;

import com.lvsrobot.kernel.extensions.sqlservice.model.TransportModel;
import com.lvsrobot.kernel.extensions.sqlservice.status.RequestStatusHandler;
import com.lvsrobot.kernel.extensions.sqlservice.status.binding.Destination;
import com.lvsrobot.kernel.extensions.sqlservice.status.binding.Property;
import com.lvsrobot.kernel.extensions.sqlservice.status.binding.TransportOrderState;
import org.javalite.activejdbc.Base;
import org.javalite.activejdbc.DB;
import org.opentcs.access.KernelRuntimeException;
import org.opentcs.access.to.order.DestinationCreationTO;
import org.opentcs.access.to.order.TransportOrderCreationTO;
import org.opentcs.components.kernel.services.TCSObjectService;
import org.opentcs.components.kernel.services.TransportOrderService;
import org.opentcs.customizations.ApplicationEventBus;
import org.opentcs.customizations.ApplicationHome;
import org.opentcs.customizations.kernel.KernelExecutor;
import org.opentcs.data.ObjectExistsException;
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
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;

import static java.util.Objects.requireNonNull;

public class SQLServiceController extends CyclicTask {

    /**
     * Executes tasks modifying kernel data.
     */
    private final ExecutorService kernelExecutor;

    /**
     * The service we use to create transport orders.
     */
    private final TransportOrderService orderService;

    private final File dataDirectory;
    private final File modelFile;
    private RequestStatusHandler statusHandler;
    private EventBus eventBus;
    private EventSource eventSource;
    private final TCSObjectService objectService;
    private SQLServiceConfiguration serviceConfiguration;
    private Queue<TransportOrderState> transportOrderStateQueue = new LinkedBlockingQueue<>();
    private DB db = new DB("main");
    
    private static final Logger LOG = LoggerFactory.getLogger(SQLServiceController.class);

    @Inject
    public SQLServiceController(@ApplicationHome File directory,
                                TransportOrderService orderService,
                                @KernelExecutor ExecutorService kernelExecutor,
                                RequestStatusHandler statusHandler,
                                TCSObjectService objectService,
                                SQLServiceConfiguration serviceSQLConfiguration,
                                @ApplicationEventBus EventBus eventBus,
                                @ApplicationEventBus EventSource eventSource) {
        super(1*1000);
        this.statusHandler = statusHandler;
        this.eventBus = eventBus;
        this.eventSource = eventSource;
        this.objectService = objectService;
        this.kernelExecutor = requireNonNull(kernelExecutor, "kernelExecutor");
        this.orderService = requireNonNull(orderService, "orderService");
        this.serviceConfiguration = serviceSQLConfiguration;
        eventSource.subscribe(new EvendHandle());
        this.dataDirectory = new File(requireNonNull(directory, "directory"), "data");
        this.modelFile = new File(dataDirectory, serviceSQLConfiguration.sqlName());
        Base.open(serviceConfiguration.sqlDriver(), "jdbc:sqlite:"+modelFile.getAbsolutePath(), "", "");
        List<TransportOrderState> orderStateList = TransportModel.getUnFinishModel();
        orderStateList.forEach(t -> createOrder(t));
        LOG.info("resume order succeed");
//        try {
//            db.open(serviceIotConfiguration.sqlDriver(), "jdbc:sqlite:"+modelFile.getAbsolutePath(), "", "");
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

    /***
     * Create Order
     * @param order order object
     * @throws ObjectUnknownException
     * @throws ObjectExistsException
     * @throws KernelRuntimeException
     * @throws IllegalStateException
     */
    public void createOrder(TransportOrderState order)
            throws ObjectUnknownException,
            ObjectExistsException,
            KernelRuntimeException,
            IllegalStateException {
        requireNonNull(order, "order");
        TransportOrderCreationTO to;
            to
                    = new TransportOrderCreationTO(order.getName(), destinations(order))
                    .withIntendedVehicleName(order.getIntendedVehicle())
//                    .withDependencyNames(new HashSet<>(order.getDependencies()))
                    .withDeadline(deadline(order));
//                    .withProperties(properties(order.getProperties()));


        try {
            kernelExecutor.submit(() -> {
                orderService.createTransportOrder(to);
//                dispatcherService.dispatch();
            }).get();
        }
        catch (InterruptedException exc) {
            throw new IllegalStateException("Unexpectedly interrupted");
        }
        catch (ExecutionException exc) {
            if (exc.getCause() instanceof RuntimeException) {
                throw (RuntimeException) exc.getCause();
            }
            throw new KernelRuntimeException(exc.getCause());
        }
    }

    private List<DestinationCreationTO> destinations(TransportOrderState order) {
        List<DestinationCreationTO> result = new ArrayList<>(order.getDestinations().size());

        for (Destination dest : order.getDestinations()) {
            DestinationCreationTO to = new DestinationCreationTO(dest.getLocationName(),
                    dest.getOperation());

            for (Property prop : dest.getProperties()) {
                to = to.withProperty(prop.getKey(), prop.getValue());
            }

            result.add(to);
        }

        return result;
    }

    private Map<String, String> properties(List<Property> properties) {
        Map<String, String> result = new HashMap<>();
        for (Property prop : properties) {
            result.put(prop.getKey(), prop.getValue());
        }
        return result;
    }

    private Instant deadline(TransportOrderState order) {
//    return order.getDeadline() == null ? Instant.MAX : order.getDeadline();
        return Instant.now().minus(60, ChronoUnit.MINUTES);
        /*if (order.getDeadline() == null) {
//      return Instant.MAX;
            return Instant.now().minus(60, ChronoUnit.MINUTES);
        } else {
            return order.getDeadline() == Instant.MIN ? Instant.now().minus(10, ChronoUnit.MINUTES) : order.getDeadline();
        }*/
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

        return objectService.fetchObjects(TransportOrder.class,
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
            TransportOrderState t = transportOrderStateQueue.peek();
            if (TransportModel.existModel(t.getName())) {
                TransportModel.updateModel(t, t.getName());
            } else {
                TransportModel.insertModel(t);
            }
            transportOrderStateQueue.poll();
        }
    }


}
