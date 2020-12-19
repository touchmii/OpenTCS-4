### OpenTCS Dispatch 理解

```java
@Override
public void initialize() {
    if (isInitialized()) {
        return;
    }

    LOG.debug("Initializing...");

    transportOrderUtil.initialize();
    orderReservationPool.clear();

    fullDispatchTask.initialize();

    implicitDispatchTrigger = new ImplicitDispatchTrigger(this);
    eventSource.subscribe(implicitDispatchTrigger);

    LOG.debug("Scheduling periodic dispatch task with interval of {} ms...", configuration.idleVehicleRedispatchingInterval());
    periodicDispatchTaskFuture = kernelExecutor.scheduleAtFixedRate(periodicDispatchTaskProvider.get(), configuration.idleVehicleRedispatchingInterval(), configuration.idleVehicleRedispatchingInterval(), TimeUnit.MILLISECONDS);

    initialized = true;
}
```

opentcs 的调度逻辑写在fullDispatchTask里面，可以通过http接口和上位机使用此调度服务，此外opentcs有个循环的线程会定期执行调度
任务，即periodicDispatchTaskFuture，它使用scheduleAtFixedRate方法设置循环时间来定期执行，此时间通过配置文件指定。此线程检测
到有车辆处于完全集成状态就会调用调度方法，小车处于其它状态则不会。如果此时没有通过http或上位机发送任务则永远不会执行调度，还有一个
隐式的调度线程implicitDispatchTrigger，此线程监听Event事件，判断事件为订单类型则执行调度，猜测为上位机远程调用所使用，即再上位机
发送任务。http服务直接使用dispatchservice来执行调度。

路由是服务于调度的，同时也被订单工具使用再检查是否可路由时，后续再通过调度服务做详细的检查。我们的重点放在执行调度时所做的事情,调度函数
是一个由多个子函数组成的非循环无返回值函数，也就是说可以被多次执行。只要小车是完全集成状态则会定期执行，如果此时有下单则会使用线程锁
防止产生冲突。

此函数具体做了什么事情呢，简单点概括 1检查此订单 2完成撤销订单 3关联新的驱动订单给小车 4按车辆的顺序(如果有的话)将车辆分配给下一个运输订单


```java
@Override
public final void run() {
    LOG.debug("Starting full dispatch run...");
    //检查仍然处于原始状态的运输订单，并尝试为它们准备*分配
    checkNewOrdersPhase.run();
    // Check what vehicles involved in a process should do.
    //在车辆停止后，完成运输订单的撤销。
    finishWithdrawalsPhase.run();
    //分配下一个驱动器订单给等待它的每辆车，或者如果车辆已经完成了它的最后一个驱动器订单，则完成相应的传输订单
    assignNextDriveOrdersPhase.run();
    //按车辆的顺序(如果有的话)将车辆分配给下一个运输订单
    assignSequenceSuccessorsPhase.run();
    // Check what vehicles not already in a process should do.
    assignOrders();
    rechargeVehicles();
    parkVehicles();

    LOG.debug("Finished full dispatch run.");
}

/**
 * Assignment of orders to vehicles.
 * <p>
 * Default: Assigns reserved and then free orders to vehicles.
 * </p>
 */
protected void assignOrders() {
    //分配预留的运输订单(如果有的话)给刚完成他们的已撤回订单的车辆。
    assignReservedOrdersPhase.run();
    //将运输订单分配给当前未处理且未绑定到任何订单序列的车辆
    assignFreeOrdersPhase.run();
}

/**
 * Recharging of vehicles.
 * <p>
 * Default: Sends idle vehicles with a degraded energy level to recharge locations.
 * </p>
 */
protected void rechargeVehicles() {
    rechargeIdleVehiclesPhase.run();
}

/**
 * Parking of vehicles.
 * <p>
 * Default: Sends idle vehicles to parking positions.
 * </p>
 */
protected void parkVehicles() {
    prioritizedReparkPhase.run();
    prioritizedParkingPhase.run();
    parkIdleVehiclesPhase.run();
}
```

可以看到这些任务没有强制的前后关联，也没有返回值和参数。opentcs已经把订单小车和地图放置在一个资源池里面了，这些函数只要把这些资源取出来
经过处理再放置回去即可，就是水流过一个过滤器再留回去。不得不说，这极大的方便了我们做一些修改。

要实现双向路径的调度，只需要再管制区的基础上将线路检查和分配策略改变即可。具体来说，把预分配的路径跟资源池（已经分配给小车）里的路径做比对
看有没有交集，估算是否会在交点发出碰撞，如果会的画将此路径拆分成两段路径，先让小车去相交点的前一个点。每当小车执行完一个被拆分的路径就触发
一次调度，再判断小车是否需要停车等待，设置一个相交点的等待车辆列表，如果有其它车辆需要经过此处则增加路由成本。如果等待时间过长则重新路由分配
新的路线，这个时间可以再开始等待的时候记录，每次触发调度时做检查，超时重新路由（取消等待车辆的驱动订单，分配新的路径）。通过给地图设置一些
障碍点，可再路由时避开此处。