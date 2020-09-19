/*******************************************************************************
 * Copyright (c) 2002, 2015 Innoopract Informationssysteme GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
package com.lvsrobot.webclient.controls;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Text;


public class SashFormTab extends ExampleTab {

  public SashFormTab() {
    super( "SashForm" );
  }

  @Override
  protected void createStyleControls( final Composite parent ) {
    createStyleButton( "BORDER", SWT.BORDER );
    createStyleButton( "VERTICAL", SWT.VERTICAL );
    createStyleButton( "HORIZONTAL", SWT.HORIZONTAL );
    createOrientationButton();
    createVisibilityButton();
    createEnablementButton();
  }

  @Override
  protected void createExampleControls( final Composite top ) {
    top.setLayout( new FillLayout() );
    int style = getStyle();
    SashForm sashForm = new SashForm( top, style );
    Text text = new Text( sashForm, SWT.MULTI | SWT.WRAP );
    text.setText(    "Lorem ipsum dolor sit amet, consectetur adipisicing "
                   + "elit, sed do eiusmod tempor incididunt ut labore et "
                   + "dolore magna aliqua." );
    List list = new List( sashForm, SWT.MULTI );
    String[] items = new String[ 8 ];
    for( int i = 0; i < items.length; i++ ) {
      items[ i ] = "Item " + ( i + 1 );
    }
    list.setItems( items );
    registerControl( sashForm );
  }

}
