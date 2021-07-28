/**
 * Copyright (c) Fraunhofer IML
 */
package com.lvsrobot.rosbridge;

import com.google.inject.assistedinject.FactoryModuleBuilder;
import org.opentcs.customizations.kernel.KernelInjectionModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ROSBridgeKernelInjectionModule extends KernelInjectionModule {

    private static final Logger LOG = LoggerFactory.getLogger(ROSBridgeKernelInjectionModule.class);

    @Override
    protected void configure() {
        install(new FactoryModuleBuilder().build(ROSBridgeAdapterComponentsFactory.class));
        vehicleCommAdaptersBinder().addBinding().to(ROSBridgeCommAdapterFactory.class);
    }
}
