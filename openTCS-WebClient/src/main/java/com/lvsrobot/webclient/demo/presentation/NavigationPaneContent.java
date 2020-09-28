/*******************************************************************************
 * Copyright (c) 2008, 2012 Innoopract Informationssysteme GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
package com.lvsrobot.webclient.demo.presentation;

import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;


public abstract class NavigationPaneContent {
  private Control control;
  private Object selector;
  
  public abstract void createControl( Composite parent );
  public abstract String getLabel();

  public ISelectionProvider getSelectionProvider() {
    return null;
  }
  
  public boolean isSelectionProvider() {
    return false;
  }
  
  final void setControl( final Control control ) {
    this.control = control;
  }
  
  final Control getControl() {
    return control;
  }
  
  final Object getSelector() {
    return selector;
  }
  
  final void setSelector( final Object selector ) {
    this.selector = selector;
  }
}
