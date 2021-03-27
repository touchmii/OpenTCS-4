/**
 * Copyright (c) The openTCS Authors.
 *
 * This program is free software and subject to the MIT license. (For details,
 * see the licensing information (LICENSE.txt) you should have received with
 * this copy of the software.)
 */
package org.opentcs.kernel.extensions.sockethost.orders.binding;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

/**
 * An order to be processed by the kernel.
 *
 * @author Stefan Walter (Fraunhofer IML)
 */
@XmlType(propOrder={"id"})
public class TCSOrder {
  
  /**
   * The id of this order.
   */
  private String id = "";
  
  /**
   * Creates a new instance.
   */
  public TCSOrder() {
    // Do nada.
  }
  
  /**
   * Returns the id of this order.
   * 
   * @return The id.
   */
  @XmlAttribute(name="id", required=true)
  public String getId() {
    return id;
  }

  /**
   * Sets the id of this order.
   * 
   * @param id The new id.
   */
  public void setId(String id) {
    this.id = id;
  }
  
}
