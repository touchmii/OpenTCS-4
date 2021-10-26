package com.lvsrobot.kernel.extensions.sqlservice;

import org.opentcs.customizations.kernel.KernelInjectionModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Singleton;

public class ServiceSQLModule extends KernelInjectionModule {
    private static final Logger LOG = LoggerFactory.getLogger(SQLServiceController.class);

    @Override
    protected void configure() {
        SQLServiceConfiguration configuration
                = getConfigBindingProvider().get(SQLServiceConfiguration.PREFIX,
                SQLServiceConfiguration.class);

        if (!configuration.enable()) {
            LOG.info("Service SQL disabled by configuration.");
            return;
        }

        bind(SQLServiceConfiguration.class)
                .toInstance(configuration);

        extensionsBinderOperating().addBinding().to(ServiceSQL.class).in(Singleton.class);
    }

}
