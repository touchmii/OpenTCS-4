/**
 * Copyright (c) Fraunhofer IML
 */
package com.lvsrobot.serial;

import com.google.inject.assistedinject.FactoryModuleBuilder;
import com.lvsrobot.serial.exchange.AdapterPanelComponentsFactory;
import com.lvsrobot.serial.exchange.ExampleCommAdapterPanelFactory;
import org.opentcs.customizations.controlcenter.ControlCenterInjectionModule;

/**
 * A custom Guice module for project-specific configuration.
 *
 * @author Martin Grzenia (Fraunhofer IML)
 */
public class ExampleControlCenterInjectionModule
    extends ControlCenterInjectionModule {

  @Override
  protected void configure() {
    install(new FactoryModuleBuilder().build(AdapterPanelComponentsFactory.class));

    commAdapterPanelFactoryBinder().addBinding().to(ExampleCommAdapterPanelFactory.class);
  }
}
