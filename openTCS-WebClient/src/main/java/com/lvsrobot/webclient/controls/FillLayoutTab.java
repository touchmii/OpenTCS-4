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
package com.lvsrobot.webclient.controls;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;


public class FillLayoutTab extends ExampleTab {

  private boolean propPrefSize;

  public FillLayoutTab() {
    super( "FillLayout" );
  }

  @Override
  protected void createStyleControls( Composite parent ) {
    createStyleButton( "HORIZONTAL", SWT.HORIZONTAL );
    createStyleButton( "VERTICAL", SWT.VERTICAL );
    final Button prefSizeButton = createPropertyButton( "Preferred Size" );
    prefSizeButton.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected( SelectionEvent event ) {
        propPrefSize = prefSizeButton.getSelection();
        createNew();
      }
    } );
  }

  @Override
  protected void createExampleControls( Composite parent ) {
    int style = getStyle();
    GridLayout parentLayout = new GridLayout();
    parentLayout.marginWidth = 5;
    parent.setLayout( parentLayout );

    Composite comp = new Composite( parent, SWT.NONE );
    comp.setBackground( new Color( parent.getDisplay(), 0xcc, 0xb7, 0x91 ) );

    FillLayout fillLayout = new FillLayout( style );
    fillLayout.marginWidth = 3;
    fillLayout.marginHeight = 3;
    fillLayout.spacing = 3;
    comp.setLayout( fillLayout );
    Button b1 = new Button( comp, SWT.PUSH );
    b1.setText( "Button 1" );
    Button b2 = new Button( comp, SWT.PUSH );
    b2.setText( "Button 2" );
    Button b3 = new Button( comp, SWT.PUSH );
    b3.setText( "Button 3" );

    if( propPrefSize ) {
      comp.setLayoutData( new GridData() );
    } else {
      comp.setLayoutData( new GridData( GridData.FILL_BOTH ) );
    }
    comp.layout();
    registerControl( comp );
  }
}
