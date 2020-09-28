/*******************************************************************************
 * Copyright (c) 2002, 2013 Innoopract Informationssysteme GmbH and others.
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

import org.eclipse.jface.action.ContributionManager;
import org.eclipse.jface.internal.provisional.action.IToolBarManager2;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.*;

@SuppressWarnings( "restriction" )
class DemoToolBarManager extends ContributionManager implements IToolBarManager2 {
  private Control control;

  public void addPropertyChangeListener( final IPropertyChangeListener lsnr ) {
  }

  public ToolBar createControl( final Composite parent ) {
    return null;
  }

  public Control createControl2( final Composite parent ) {
    Label result = new Label( parent, SWT.NONE );
    result.setText( "ToolBar" );
    control = result;
    return result;
  }

  public void dispose() {
    if( control != null ) {
      control.dispose();
    }
  }

  public ToolBar getControl() {
    return null;
  }

  public Control getControl2() {
    return control;
  }

  public int getItemCount() {
    return getItems().length;
  }

  public void removePropertyChangeListener(
    final IPropertyChangeListener lsnr )
  {
  }

  public void update( final boolean force ) {
  }

}