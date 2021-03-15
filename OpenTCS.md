juice 如何使用注入
在opentcs中使用注入非常的简单，只需要在类的构造方法中指定参数即可。例如需要用到TCSObjectService这个服务，在参数中指定，然后接收此参数，需要检测非空。
```@Inject
  public OrderHandler(TransportOrderService orderService,
                      VehicleService vehicleService,
                      DispatcherService dispatcherService,
                      @KernelExecutor ExecutorService kernelExecutor,
//                      @ServiceCallWrapper CallWrapper callWrapper,
                      @Nonnull TCSObjectService objectService) {
    this.orderService = requireNonNull(orderService, "orderService");
    this.vehicleService = requireNonNull(vehicleService, "vehicleService");
    this.dispatcherService = requireNonNull(dispatcherService, "dispatcherService");
    this.kernelExecutor = requireNonNull(kernelExecutor, "kernelExecutor");
//    this.callWrapper = requireNonNull(callWrapper, "callWrapper");
    this.objectService = requireNonNull(objectService, "objectService");
  }
```
扩展HTTP接口

通过HTTP接口发送指令给指定车辆

发送指令给适配器需要获取车辆名称的引用，再调用VehicleService的sendCommAdapter方法即可。在适配器中重写BasicVehicleCommAdapter中的
excute方法，接收VehicleCommAdapterEvent事件。

```
public String sendCommand(String name, Command command) {
    try {
      TCSObjectReference<Vehicle> vehicleReference = vehicleService.fetchObject(Vehicle.class, name).getReference();
      VehicleCommAdapterEvent event = new VehicleCommAdapterEvent(name, command.getCommand());
      try {
//      callWrapper.call(() -> vehicleService.sendCommAdapterCommand(vehicleReference, new PublishEventCommand(event)));
        vehicleService.sendCommAdapterCommand(vehicleReference, new PublishEventCommand(event));

      } catch (Exception e) {
        LOG.warn("Can't send command to vehicle");
        e.getMessage();
        throw new ObjectUnknownException(("Can't send command to vehicle"));
      }

    } catch (Exception e) {
      e.getMessage();
      LOG.warn("Can't found vechile name: {}", name);
      throw new ObjectUnknownException("Unknow Vehicle name: " + name);
    }
    return String.format("Send command: %s to Vehicle: %s success.", command.getCommand(),name);
  }
```

```
@Override
public void execute(AdapterCommand command) {
        PublishEventCommand publishCommand = (PublishEventCommand) command;
```

适配器任务

基础适配器有两个列表分别是移动指令列表和以发送指令列表，当判断适配器可以发送命令则从移动指令列表取出添加到已发送列表

```
BasicVehicleCommAdapter
/**
     * This adapter's command queue.
     */
    private final Queue<MovementCommand> commandQueue = new LinkedBlockingQueue<>();
    /**
     * Contains the orders which have been sent to the vehicle but which haven't
     * been executed by it, yet.
     */
    private final Queue<MovementCommand> sentQueue = new LinkedBlockingQueue<>();
    
    if (getSentQueue().size() < sentQueueCapacity) && !getCommandQueue().isEmpty()
    curCmd = getCommandQueue().poll();
                    if (curCmd != null) {
                        try {
                            sendCommand(curCmd);
                            //send driver order，adapter implement sendCommand，receive curCmd
                            getSentQueue().add(curCmd);
                            //add driving order to the queue of sent orders
                            getProcessModel().commandSent(curCmd);
                            //Notify the kernel that the drive order has been sent to the vehicle
```

跟车辆通行的适配器只操作已发送指令列表，使用peek获取要发送的命令，车辆到达预期地点则使用poll删除已发送指令头部，再使用ProcessModel通知内核，代表车辆执行此命令成功。然后获取下一个指令安装上面的步骤重复执行，知道将所有指令执行情况都上报给内核，此时才会判断路径执行成功。

```
ExampleCommAdapter
curCommand = getSentQueue().peek();
MovementCommand sentcmd = getSentQueue().poll();
getProcessModel().commandExecuted(curCommand);
```

车辆执行完的移动命令如何通知到内核

```
/**
   * Notifies observers that the given command has been executed by the comm adapter/vehicle.
   *
   * @param executedCommand The command that has been executed.
   */
  public void commandExecuted(@Nonnull MovementCommand executedCommand) {
    getPropertyChangeSupport().firePropertyChange(Attribute.COMMAND_EXECUTED.name(),
                                                  null,
                                                  executedCommand);
  }

  /**
   * Notifies observers that the given command could not be executed by the comm adapter/vehicle.
   *
   * @param failedCommand The command that could not be executed.
   */
  public void commandFailed(@Nonnull MovementCommand failedCommand) {
    getPropertyChangeSupport().firePropertyChange(Attribute.COMMAND_FAILED.name(),
                                                  null,
                                                  failedCommand);
  }
```

DefaultVehicleController 重写PropertyChangeListener的方法实现监听适配器发送过来的事件，如果执行命令失败会取消此车辆的订单
```
DefaultVehicleController
//属性变更回调函数，使用getProcessModel发送车辆消息监听到车辆属性变更时调用
  @Override
  public void propertyChange(PropertyChangeEvent evt) {
    if (evt.getSource() != commAdapter.getProcessModel()) {
      return;
    }

    handleProcessModelEvent(evt);
  }

//处理驱动器消息类型，调用不同的处理函数，如指令发送成功或位置变更
  private void handleProcessModelEvent(PropertyChangeEvent evt) {
    eventBus.onEvent(new ProcessModelEvent(evt.getPropertyName(),
            commAdapter.createTransferableProcessModel()));

    if (Objects.equals(evt.getPropertyName(), VehicleProcessModel.Attribute.POSITION.name())) {
      updateVehiclePosition((String) evt.getNewValue());
    }
    else if (Objects.equals(evt.getPropertyName(),
            VehicleProcessModel.Attribute.PRECISE_POSITION.name())) {
      updateVehiclePrecisePosition((Triple) evt.getNewValue());
    }
    else if (Objects.equals(evt.getPropertyName(),
            VehicleProcessModel.Attribute.ORIENTATION_ANGLE.name())) {
      vehicleService.updateVehicleOrientationAngle(vehicle.getReference(),
              (Double) evt.getNewValue());
    }
    else if (Objects.equals(evt.getPropertyName(),
            VehicleProcessModel.Attribute.ENERGY_LEVEL.name())) {
      vehicleService.updateVehicleEnergyLevel(vehicle.getReference(), (Integer) evt.getNewValue());
    }
    else if (Objects.equals(evt.getPropertyName(),
            VehicleProcessModel.Attribute.LOAD_HANDLING_DEVICES.name())) {
      vehicleService.updateVehicleLoadHandlingDevices(vehicle.getReference(),
              (List<LoadHandlingDevice>) evt.getNewValue());
    }
    else if (Objects.equals(evt.getPropertyName(), VehicleProcessModel.Attribute.STATE.name())) {
      updateVehicleState((Vehicle.State) evt.getNewValue());
    }
    else if (Objects.equals(evt.getPropertyName(),
            VehicleProcessModel.Attribute.COMM_ADAPTER_STATE.name())) {
      updateCommAdapterState((VehicleCommAdapter.State) evt.getNewValue());
    }
    else if (Objects.equals(evt.getPropertyName(),
            VehicleProcessModel.Attribute.COMMAND_EXECUTED.name())) {
      commandExecuted((MovementCommand) evt.getNewValue());
    }
    else if (Objects.equals(evt.getPropertyName(),
            VehicleProcessModel.Attribute.COMMAND_FAILED.name())) {
      dispatcherService.withdrawByVehicle(vehicle.getReference(), true, false);
    }
    else if (Objects.equals(evt.getPropertyName(),
            VehicleProcessModel.Attribute.USER_NOTIFICATION.name())) {
      notificationService.publishUserNotification((UserNotification) evt.getNewValue());
    }
    else if (Objects.equals(evt.getPropertyName(),
            VehicleProcessModel.Attribute.COMM_ADAPTER_EVENT.name())) {
      eventBus.onEvent((VehicleCommAdapterEvent) evt.getNewValue());
    }
    else if (Objects.equals(evt.getPropertyName(),
            VehicleProcessModel.Attribute.VEHICLE_PROPERTY.name())) {
      VehicleProcessModel.VehiclePropertyUpdate propUpdate
              = (VehicleProcessModel.VehiclePropertyUpdate) evt.getNewValue();
      vehicleService.updateObjectProperty(vehicle.getReference(),
              propUpdate.getKey(),
              propUpdate.getValue());
    }
    else if (Objects.equals(evt.getPropertyName(),
            VehicleProcessModel.Attribute.TRANSPORT_ORDER_PROPERTY.name())) {
      VehicleProcessModel.TransportOrderPropertyUpdate propUpdate
              = (VehicleProcessModel.TransportOrderPropertyUpdate) evt.getNewValue();
      if (currentDriveOrder != null) {
        vehicleService.updateObjectProperty(currentDriveOrder.getTransportOrder(),
                propUpdate.getKey(),
                propUpdate.getValue());
      }
    }
  }

```

commandExcuted 判断移动指令是否真的执行成功

```
private void commandExecuted(MovementCommand executedCommand) {
    requireNonNull(executedCommand, "executedCommand");

    synchronized (commAdapter) {
      // Check if the executed command is the one we expect at this point.
      MovementCommand expectedCommand = commandsSent.peek();
      if (!Objects.equals(expectedCommand, executedCommand)) {
        LOG.warn("{}: Communication adapter executed unexpected command: {} != {}",
                vehicle.getName(),
                executedCommand,
                expectedCommand);
        // XXX The communication adapter executed an unexpected command. Do something!
      }
      // Remove the command from the queue, since it has been processed successfully.
      lastCommandExecuted = commandsSent.remove();
      // Free resources allocated for the command before the one now executed.
      Set<TCSResource<?>> oldResources = allocatedResources.poll();
      if (oldResources != null) {
        LOG.debug("{}: Freeing resources: {}", vehicle.getName(), oldResources);
        scheduler.free(this, oldResources);
      }
      else {
        LOG.debug("{}: Nothing to free.", vehicle.getName());
      }
      // Check if there are more commands to be processed for the current drive order.
      if (pendingCommand == null && futureCommands.isEmpty()) {
        LOG.debug("{}: No more commands in current drive order", vehicle.getName());
        // Check if there are still commands that have been sent to the communication adapter but
        // not yet executed. If not, the whole order has been executed completely - let the kernel
        // know about that so it can give us the next drive order.
        if (commandsSent.isEmpty() && !waitingForAllocation) {
          LOG.debug("{}: Current drive order processed", vehicle.getName());
          currentDriveOrder = null;
          // Let the kernel/dispatcher know that the drive order has been processed completely (by
          // setting its state to AWAITING_ORDER).
          vehicleService.updateVehicleRouteProgressIndex(vehicle.getReference(),
                  Vehicle.ROUTE_INDEX_DEFAULT);
          vehicleService.updateVehicleProcState(vehicle.getReference(),
                  Vehicle.ProcState.AWAITING_ORDER);
        }
      }
      // There are more commands to be processed.
      // Check if we can send another command to the comm adapter.
      else if (canSendNextCommand()) {
        allocateForNextCommand();
      }
    }
  }
```

```
/**
   * Sets the point which a vehicle is expected to occupy next.
   *
   * @param vehicleRef A reference to the vehicle to be modified.
   * @param pointRef A reference to the point which the vehicle is expected to
   * occupy next.
   * @throws ObjectUnknownException If the referenced vehicle does not exist.
   * @deprecated Use{@link InternalVehicleService#updateVehicleNextPosition(
   * org.opentcs.data.TCSObjectReference, org.opentcs.data.TCSObjectReference)} instead.
   */
  @Deprecated
  void setVehicleNextPosition(TCSObjectReference<Vehicle> vehicleRef,
                              TCSObjectReference<Point> pointRef)
      throws ObjectUnknownException;

/**
   * Sets a vehicle's index of the last route step travelled for the current
   * drive order of its current transport order.
   *
   * @param vehicleRef A reference to the vehicle to be modified.
   * @param index The new index.
   * @throws ObjectUnknownException If the referenced vehicle does not exist.
   * @deprecated Use{@link InternalVehicleService#updateVehicleRouteProgressIndex(
   * org.opentcs.data.TCSObjectReference, int)} instead.
   */
  @Deprecated
  void setVehicleRouteProgressIndex(TCSObjectReference<Vehicle> vehicleRef,
                                    int index)
      throws ObjectUnknownException;
      
 /**
   * Sets a transport order's state.
   * Note that transport order states are intended to be manipulated by the
   * dispatcher only. Calling this method from any other parts of the kernel may
   * result in undefined behaviour.
   *
   * @param ref A reference to the transport order to be modified.
   * @param newState The transport order's new state.
   * @throws ObjectUnknownException If the referenced transport order does not
   * exist.
   * @deprecated Use {@link InternalTransportOrderService#updateTransportOrderState(
   * org.opentcs.data.TCSObjectReference, org.opentcs.data.order.TransportOrder.State)} instead.
   */
  @Deprecated
  void setTransportOrderState(TCSObjectReference<TransportOrder> ref,
                              TransportOrder.State newState)
      throws ObjectUnknownException;     
```