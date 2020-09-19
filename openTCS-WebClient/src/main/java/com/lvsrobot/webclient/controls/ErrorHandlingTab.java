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

import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.client.service.JavaScriptExecutor;
import org.eclipse.rap.rwt.service.ServerPushSession;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTError;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;


public class ErrorHandlingTab extends ExampleTab {

  public ErrorHandlingTab() {
    super( "Error Handling" );
    setHorizontalSashFormWeights( new int[] { 100, 0 } );
  }

  @Override
  protected void createStyleControls( final Composite parent ) {
  }

  @Override
  protected void createExampleControls( final Composite parent ) {
    parent.setLayout( new GridLayout( 1, false ) );
    Label lblInfo = new Label( parent, SWT.WRAP );
    lblInfo.setLayoutData( new GridData( SWT.FILL, SWT.DEFAULT, true, false ) );
    String info
      = "Simulate a server-side session timeout.\n"
      + "Click the 'Invalidate Session' button that will invalidate the "
      + "session after a short delay.\n"
      + "Thereafter, try to proceed using the application. With the next "
      + "request, a new session is created. You are informed about that and "
      + "can start working with the new session";
    lblInfo.setText( info );
    Button btnInvalidateSession = new Button( parent, SWT.PUSH );
    String msg = "Invalidate Session";
    btnInvalidateSession.setText( msg );
    final Label lblFeedback = new Label( parent, SWT.NONE );
    btnInvalidateSession.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected( final SelectionEvent event ) {
        lblFeedback.setText( "The session will be invalidated shortly." );
        lblFeedback.getParent().layout();
        RWT.getUISession().getHttpSession().setMaxInactiveInterval( 2 );
      }
    } );
    Button btnErrorResponse = new Button( parent, SWT.PUSH );
    btnErrorResponse.setText( "Deliver response with JavaScript error" );
    btnErrorResponse.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected( final SelectionEvent event ) {
        JavaScriptExecutor executor = RWT.getClient().getService( JavaScriptExecutor.class );
        if( executor != null ) {
          executor.execute( "this is no valid JavaScript!" );
        }
      }
    } );
    Button btnClientError = new Button( parent, SWT.PUSH );
    btnClientError.setText( "Throw uncaught client-side JavaScript error" );
    btnClientError.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected( final SelectionEvent event ) {
        StringBuffer script = new StringBuffer();
        script.append( "window.setTimeout( '" );
        script.append( "rwt.runtime.ErrorHandler.processJavaScriptError( " );
        script.append( "new Error( \"I am client-side error\" ) )" );
        script.append( "', 1000 );" );
        JavaScriptExecutor executor = RWT.getClient().getService( JavaScriptExecutor.class );
        if( executor != null ) {
          executor.execute( script.toString() );
        }
      }
    } );
    Button btnServerException = new Button( parent, SWT.PUSH );
    btnServerException.setText( "Throw uncaught server-side exeption" );
    btnServerException.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected( final SelectionEvent event ) {
        throw new RuntimeException( "Some exeption occured" );
      }
    } );
    Button btnServerError = new Button( parent, SWT.PUSH );
    btnServerError.setText( "Throw uncaught server-side error" );
    btnServerError.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected( final SelectionEvent event ) {
        throw new SWTError( "Some error occured" );
      }
    } );
    final Button btnStartUICallback = new Button( parent, SWT.CHECK );
    btnStartUICallback.setText( "Enabled UICallback" );
    final ServerPushSession serverPush = new ServerPushSession();
    btnStartUICallback.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected( final SelectionEvent event ) {
        if( btnStartUICallback.getSelection() ) {
          serverPush.start();
        } else {
          serverPush.stop();
        }
      }
    } );
  }
}
