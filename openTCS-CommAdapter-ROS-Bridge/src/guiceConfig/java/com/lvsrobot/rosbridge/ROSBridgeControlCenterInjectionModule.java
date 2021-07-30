/**
 * Copyright (c) Fraunhofer IML
 */
package com.lvsrobot.rosbridge;

import com.google.inject.assistedinject.FactoryModuleBuilder;
import com.lvsrobot.rosbridge.exchange.AdapterPanelComponentsFactory;
import com.lvsrobot.rosbridge.exchange.ROSBridgeCommAdapterPanelFactory;
import org.opentcs.customizations.controlcenter.ControlCenterInjectionModule;

/**
 * A custom Guice module for project-specific configuration.
 *
 * @author Martin Grzenia (Fraunhofer IML)
 */
public class ROSBridgeControlCenterInjectionModule
    extends ControlCenterInjectionModule {

  @Override
  protected void configure() {
    install(new FactoryModuleBuilder().build(AdapterPanelComponentsFactory.class));

    commAdapterPanelFactoryBinder().addBinding().to(ROSBridgeCommAdapterPanelFactory.class);
  }
}
