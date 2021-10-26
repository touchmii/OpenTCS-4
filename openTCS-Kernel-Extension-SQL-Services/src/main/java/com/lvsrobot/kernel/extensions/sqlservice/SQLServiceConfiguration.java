/**
 * Copyright (c) The openTCS Authors.
 *
 * This program is free software and subject to the MIT license. (For details,
 * see the licensing information (LICENSE.txt) you should have received with
 * this copy of the software.)
 */
package com.lvsrobot.kernel.extensions.sqlservice;

import org.opentcs.configuration.ConfigurationEntry;
import org.opentcs.configuration.ConfigurationPrefix;

/**
 * Provides methods to configure the XML-based host interface.
 *
 * @author Martin Grzenia (Fraunhofer IML)
 */
@ConfigurationPrefix(SQLServiceConfiguration.PREFIX)
public interface SQLServiceConfiguration {

  /**
   * This configuration's prefix.
   */
  String PREFIX = "servicesql";

  @ConfigurationEntry(
      type = "Boolean",
      description = "Whether to enable SQL service.",
      orderKey = "0")
  boolean enable();

  @ConfigurationEntry(
      type = "String",
      description = "SQL driver",
      orderKey = "1"
  )
  String sqlDriver();

  @ConfigurationEntry(
      type = "String",
      description = "The URL address SQL.",
      orderKey = "2")
  String sqlURL();

  @ConfigurationEntry(
      type = "String",
      description = "The File Name SQL.",
      orderKey = "3")
  String sqlName();

  @ConfigurationEntry(
      type = "Integer",
      description = "The Port SQL.",
      orderKey = "4")
  int port();

  @ConfigurationEntry(
      type = "Integer",
      description = "The maximum number of bytes read from sockets before closing the connection.",
      orderKey = "5")
  int xx();

}
