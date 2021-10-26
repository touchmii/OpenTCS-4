package com.lvsrobot.iot;

import org.opentcs.customizations.kernel.KernelInjectionModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Singleton;

public class ServiceIotModule extends KernelInjectionModule {
    private static final Logger LOG = LoggerFactory.getLogger(ServiceController.class);

    @Override
    protected void configure() {
        ServiceIotConfiguration configuration
                = getConfigBindingProvider().get(ServiceIotConfiguration.PREFIX,
                ServiceIotConfiguration.class);

        if (!configuration.enable()) {
            LOG.info("Service Iot disabled by configuration.");
            return;
        }

        bind(ServiceIotConfiguration.class)
                .toInstance(configuration);

        extensionsBinderOperating().addBinding().to(ServiceIot.class).in(Singleton.class);
    }

}
