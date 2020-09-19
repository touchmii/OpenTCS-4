/*******************************************************************************
 * Copyright (c) 2002, 2013 Innoopract Informationssysteme GmbH.
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
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

class NestedLayoutsTab extends ExampleTab {

  private boolean propPrefSize = false;

  public NestedLayoutsTab() {
    super( "Nested Layouts" );
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
  }

  @Override
  protected void createExampleControls( Composite parent ) {
    parent.setLayout( new GridLayout() );
    Composite comp = new Composite( parent, SWT.NONE );
    comp.setBackground( bgColors[ BG_COLOR_BROWN ] );
    comp.setLayout( new GridLayout() );
    if( propPrefSize ) {
      comp.setLayoutData( new GridData() );
    } else {
      comp.setLayoutData( new GridData( GridData.FILL_BOTH ) );
    }

    // Image label
    Label imageLabel = new Label( comp, SWT.NONE );
    imageLabel.setImage( Util.loadImage( parent.getDisplay(), "icons/lockkey.gif" ) );
    imageLabel.setLayoutData( new GridData( 32, 32 ) );

    // Login data
    Group loginGroup = new Group( comp, SWT.NONE );
    loginGroup.setText( "Login Data" );
    loginGroup.setLayout( new GridLayout( 2, false ) );
    new Label( loginGroup, SWT.NONE ).setText( "Username:" );
    Text userText = new Text( loginGroup, SWT.SINGLE | SWT.BORDER );
    userText.setText( "john" );
    userText.setLayoutData( new GridData( GridData.FILL_HORIZONTAL ) );
    new Label( loginGroup, SWT.NONE ).setText( "Password:" );
    Text passText = new Text( loginGroup, SWT.PASSWORD | SWT.BORDER );
    passText.setLayoutData( new GridData( GridData.FILL_HORIZONTAL ) );

    // Button bar
    Composite buttons = new Composite( comp, SWT.NONE );
    RowLayout buttonLayout = new RowLayout( SWT.HORIZONTAL);
    buttonLayout.marginRight = 0;
    buttons.setLayout( buttonLayout );
    buttons.setLayoutData( new GridData( GridData.HORIZONTAL_ALIGN_END ) );
    Button loginButton = new Button( buttons, SWT.PUSH );
    loginButton.setText( "Login" );
    Button cancelButton = new Button( buttons, SWT.PUSH );
    cancelButton.setText( "Cancel" );

    comp.layout();
    registerControl( comp );
  }
}
