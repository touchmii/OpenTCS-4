/**
 * Copyright (c) Fraunhofer IML
 */
package com.lvsrobot.vehicletcp;

import com.google.inject.assistedinject.FactoryModuleBuilder;
import org.opentcs.customizations.kernel.KernelInjectionModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TCPAdapterKernelInjectionModule extends KernelInjectionModule {

    private static final Logger LOG = LoggerFactory.getLogger(TCPAdapterKernelInjectionModule.class);

    @Override
    protected void configure() {
        install(new FactoryModuleBuilder().build(ExampleAdapterComponentsFactory.class));
        vehicleCommAdaptersBinder().addBinding().to(ExampleCommAdapterFactory.class);
    }
}
