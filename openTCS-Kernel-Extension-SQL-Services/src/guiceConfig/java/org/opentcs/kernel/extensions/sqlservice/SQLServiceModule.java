/**
 * Copyright (c) The openTCS Authors.
 *
 * This program is free software and subject to the MIT license. (For details,
 * see the licensing information (LICENSE.txt) you should have received with
 * this copy of the software.)
 */
package org.opentcs.kernel.extensions.sqlservice;

import org.opentcs.customizations.kernel.KernelInjectionModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Configures the TCP host interface extension.
 *
 * @author Stefan Walter (Fraunhofer IML)
 */
public class SQLServiceModule
    extends KernelInjectionModule {

  /**
   * This class's logger.
   */
  private static final Logger LOG = LoggerFactory.getLogger(SQLServiceModule.class);

  @Override
  protected void configure() {
    SQLServiceConfiguration configuration
        = getConfigBindingProvider().get(SQLServiceConfiguration.PREFIX,
                                         SQLServiceConfiguration.class);

    if (!configuration.enable()) {
      LOG.info("TCP host interface disabled by configuration.");
      return;
    }

    bind(SQLServiceConfiguration.class)
        .toInstance(configuration);
    // The status channel is available in all modes.
//    extensionsBinderAllModes().addBinding()
//        .to(StatusMessageDispatcher.class)
//        .in(Singleton.class);

    // The order interface is available only in operating mode.
//    extensionsBinderOperating().addBinding()
//        .to(XMLTelegramOrderReceiver.class);
  }
}
