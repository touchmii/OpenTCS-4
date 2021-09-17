/**
 * Copyright (c) The openTCS Authors.
 *
 * This program is free software and subject to the MIT license. (For details,
 * see the licensing information (LICENSE.txt) you should have received with
 * this copy of the software.)
 */
package org.opentcs.kernel.extensions.sqlservice;

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
      orderKey = "0_general")
  boolean enable();

  @ConfigurationEntry(
      type = "Boolean",
      description = "SQL driver",
      orderKey = "1_orders_0"
  )
  String sqlDriver();

  @ConfigurationEntry(
      type = "String",
      description = "The TCP address SQL.",
      orderKey = "1_orders_1")
  int SQLServerIP();

  @ConfigurationEntry(
      type = "Integer",
      description = "The Port SQL.",
      orderKey = "1_orders_2")
  int SQLServerPort();

  @ConfigurationEntry(
      type = "Integer",
      description = "The maximum number of bytes read from sockets before closing the connection.",
      orderKey = "1_orders_3")
  int ordersInputLimit();

  @ConfigurationEntry(
      type = "Integer",
      description = "The TCP port on which to listen for incoming status channel connections.",
      orderKey = "2_status_0")
  int statusServerPort();

  @ConfigurationEntry(
      type = "String",
      description = "The MES address.",
      orderKey = "3_host_1"
  )
  String MESServerHost();

  @ConfigurationEntry(
      type = "Integer",
      description = "The MES port.",
      orderKey = "3_host_2"
  )
  int MESServerPort();
  @ConfigurationEntry(
        type = "Integer",
        description = "The MES timeout.",
        orderKey = "3_host_3"
    )
  int MESServerTimeout();

  @ConfigurationEntry(
      type = "String",
      description = "A string to be used for separating subsequent status messages in the stream.",
      orderKey = "2_status_1")
  String statusMessageSeparator();
}
