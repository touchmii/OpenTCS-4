/*******************************************************************************
 * Copyright (c) 2002, 2013 Innoopract Informationssysteme GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package com.lvsrobot.webclient.controls;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;


public class LoginDialog extends Dialog {

  private static final int LOGIN_ID = IDialogConstants.CLIENT_ID + 1;

  private Text userText;
  private Text passText;
  private Label mesgLabel;
  private final String title;
  private final String message;
  private String username;
  private String password;

  public LoginDialog( Shell parent, String title, String message ) {
    super( parent );
    this.title = title;
    this.message = message;
  }

  public String getPassword() {
    return password;
  }

  public void setUsername( String username ) {
    this.username = username;
  }

  public String getUsername() {
    return username;
  }

  @Override
  protected void configureShell( Shell shell ) {
    super.configureShell( shell );
    if( title != null ) {
      shell.setText( title );
    }
  }

  @Override
  protected Control createDialogArea( Composite parent ) {
    Composite composite = ( Composite )super.createDialogArea( parent );
    composite.setLayout( new GridLayout( 2, false ) );
    mesgLabel = new Label( composite, SWT.NONE );
    GridData messageData = new GridData( SWT.FILL, SWT.CENTER, true, false );
    messageData.horizontalSpan = 2;
    mesgLabel.setLayoutData( messageData );
    Label userLabel = new Label( composite, SWT.NONE );
    userLabel.setText( "Username:" );
    userText = new Text( composite, SWT.BORDER );
    userText.setLayoutData( new GridData( SWT.FILL, SWT.CENTER, true, false ) );
    Label passLabel = new Label( composite, SWT.NONE );
    passLabel.setText( "Password:" );
    passText = new Text( composite, SWT.BORDER | SWT.PASSWORD );
    passText.setLayoutData( new GridData( SWT.FILL, SWT.CENTER, true, false ) );
    initilizeDialogArea();
    return composite;
  }

  @Override
  protected void createButtonsForButtonBar( Composite parent ) {
    createButton( parent, IDialogConstants.CANCEL_ID, "Cancel", false );
    createButton( parent, LOGIN_ID, "Login", true );
  }

  @Override
  protected void buttonPressed( int buttonId ) {
    if( buttonId == LOGIN_ID ) {
      username = userText.getText();
      password = passText.getText();
      setReturnCode( OK );
      close();
    } else {
      password = null;
    }
    super.buttonPressed( buttonId );
  }

  private void initilizeDialogArea() {
    if( message != null ) {
      mesgLabel.setText( message );
    }
    if( username != null ) {
      userText.setText( username );
    }
    userText.setFocus();
  }

}
