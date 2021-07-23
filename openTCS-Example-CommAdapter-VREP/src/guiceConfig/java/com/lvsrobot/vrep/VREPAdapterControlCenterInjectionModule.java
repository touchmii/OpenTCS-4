/**
 * Copyright (c) Fraunhofer IML
 */
package com.lvsrobot.vrep;

import com.google.inject.assistedinject.FactoryModuleBuilder;
import com.lvsrobot.vrep.exchange.AdapterPanelComponentsFactory;
import com.lvsrobot.vrep.exchange.ExampleCommAdapterPanelFactory;
import org.opentcs.customizations.controlcenter.ControlCenterInjectionModule;

/**
 * A custom Guice module for project-specific configuration.
 *
 * @author Martin Grzenia (Fraunhofer IML)
 */
public class VREPAdapterControlCenterInjectionModule
    extends ControlCenterInjectionModule {

  @Override
  protected void configure() {
    install(new FactoryModuleBuilder().build(AdapterPanelComponentsFactory.class));

    commAdapterPanelFactoryBinder().addBinding().to(ExampleCommAdapterPanelFactory.class);
  }
}
