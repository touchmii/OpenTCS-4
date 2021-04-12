/**
 * Copyright (c) Fraunhofer IML
 */
package com.lvsrobot.serial;

import com.google.inject.assistedinject.FactoryModuleBuilder;
import org.opentcs.customizations.kernel.KernelInjectionModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SerialAdapterKernelInjectionModule extends KernelInjectionModule {

    private static final Logger LOG = LoggerFactory.getLogger(SerialAdapterKernelInjectionModule.class);

    @Override
    protected void configure() {
        install(new FactoryModuleBuilder().build(ExampleAdapterComponentsFactory.class));
        vehicleCommAdaptersBinder().addBinding().to(ExampleCommAdapterFactory.class);
    }
}
