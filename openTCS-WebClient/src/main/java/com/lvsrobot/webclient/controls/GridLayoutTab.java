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
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;


public class GridLayoutTab extends ExampleTab {

  private boolean propPrefSize;
  private boolean propEqualWidth;

  public GridLayoutTab() {
    super( "GridLayout" );
  }

  @Override
  protected void createStyleControls( Composite parent ) {
    final Button prefSizeButton = createPropertyButton( "Preferred Size" );
    prefSizeButton.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected( final SelectionEvent event ) {
        propPrefSize = prefSizeButton.getSelection();
        createNew();
      }
    } );
    final Button equalButton = createPropertyButton( "Make Columns Equal Width" );
    equalButton.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected( SelectionEvent event ) {
        propEqualWidth = equalButton.getSelection();
        createNew();
      }
    } );
  }

  @Override
  protected void createExampleControls( Composite parent ) {
    GridLayout parentLayout = new GridLayout();
    parentLayout.marginWidth = 5;
    parent.setLayout( parentLayout );
    Composite comp = new Composite( parent, SWT.NONE );
    comp.setBackground( new Color( parent.getDisplay(), 0xcc, 0xb7, 0x91 ) );
    GridLayout gridLayout = new GridLayout();
    gridLayout.numColumns = 3;
    gridLayout.marginWidth = 3;
    gridLayout.marginHeight = 3;
    gridLayout.makeColumnsEqualWidth = propEqualWidth;
    comp.setLayout( gridLayout );

    Button button1 = new Button( comp, SWT.PUSH );
    button1.setText( "B1" );
    GridData gridData = new GridData();
    gridData.verticalAlignment = GridData.FILL;
    button1.setLayoutData( gridData );

    new Button( comp, SWT.PUSH ).setText( "Wide Button 2" );

    Button button3 = new Button( comp, SWT.PUSH );
    button3.setText( "Button 3" );
    gridData = new GridData();
    gridData.verticalAlignment = GridData.FILL;
    gridData.verticalSpan = 2;
    gridData.grabExcessVerticalSpace = true;
    gridData.horizontalAlignment = GridData.FILL;
    gridData.grabExcessHorizontalSpace = true;
    button3.setLayoutData( gridData );

    Button button4 = new Button( comp, SWT.PUSH );
    button4.setText( "B4" );
    gridData = new GridData();
    gridData.verticalAlignment = GridData.FILL;
    button4.setLayoutData( gridData );

    new Button( comp, SWT.PUSH).setText( "Button 5" );

    if( propPrefSize ) {
      comp.setLayoutData( new GridData() );
    } else {
      comp.setLayoutData( new GridData( GridData.FILL_BOTH ) );
    }
    comp.layout();
    registerControl( comp );
  }
}
