/**
 * Copyright (c) Fraunhofer IML
 */
package com.lvsrobot.http;

import com.google.inject.assistedinject.FactoryModuleBuilder;
import com.lvsrobot.http.exchange.AdapterPanelComponentsFactory;
import com.lvsrobot.http.exchange.ExampleCommAdapterPanelFactory;
import org.opentcs.customizations.controlcenter.ControlCenterInjectionModule;

/**
 * A custom Guice module for project-specific configuration.
 *
 * @author Martin Grzenia (Fraunhofer IML)
 */
public class HTTPAdapterControlCenterInjectionModule
    extends ControlCenterInjectionModule {

  @Override
  protected void configure() {
    install(new FactoryModuleBuilder().build(AdapterPanelComponentsFactory.class));

    commAdapterPanelFactoryBinder().addBinding().to(ExampleCommAdapterPanelFactory.class);
  }
}
