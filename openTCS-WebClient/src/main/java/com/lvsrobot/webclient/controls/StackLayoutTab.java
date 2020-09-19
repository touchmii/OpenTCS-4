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
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

public class StackLayoutTab extends ExampleTab {

  private static final int COUNT = 5;
  private Composite comp;
  private StackLayout stackLayout;
  private Control[] bArray;
  private int index;
  private boolean propPrefSize;

  public StackLayoutTab() {
    super( "StackLayout" );
    index = 0;
  }

  @Override
  protected void createStyleControls( Composite parent ) {
    final Button prefSizeButton = createPropertyButton( "Preferred Size" );
    prefSizeButton.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected( SelectionEvent event ) {
        propPrefSize = prefSizeButton.getSelection();
        createNew();
      }
    } );
    Button switchButton = createPropertyButton( "Next", SWT.PUSH );
    switchButton.setLocation( 5, 220 );
    switchButton.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected( SelectionEvent event ) {
        showNext();
      }
    } );
  }

  @Override
  protected void createExampleControls( Composite parent ) {
    GridLayout parentLayout = new GridLayout();
    parentLayout.marginWidth = 5;
    parent.setLayout( parentLayout );

    comp = new Composite( parent, SWT.NONE );
    comp.setBackground( new Color( parent.getDisplay(), 0xcc, 0xb7, 0x91 ) );
    stackLayout = new StackLayout();
    stackLayout.marginWidth = 3;
    stackLayout.marginHeight = 3;
    comp.setLayout( stackLayout );
    bArray = new Button[ COUNT ];
    for( int i = 0; i < COUNT; i++ ) {
      Button button = new Button( comp, SWT.PUSH );
      button.setText( "Control " + ( i+1 ) );
      button.setFont( new Font( parent.getDisplay(), "Serif", 24, SWT.BOLD ) );
      bArray[ i ] = button;
    }
    stackLayout.topControl = bArray[ index ];

    if( propPrefSize ) {
      comp.setLayoutData( new GridData() );
    } else {
      comp.setLayoutData( new GridData( GridData.FILL_BOTH ) );
    }
    comp.layout();
    registerControl( comp );
  }

  private void showNext() {
    index = ( index + 1 ) % COUNT;
    stackLayout.topControl = bArray[ index ];
    comp.layout();
  }
}
