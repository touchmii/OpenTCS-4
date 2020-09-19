/*******************************************************************************
 * Copyright (c) 2002, 2012 Innoopract Informationssysteme GmbH and others.
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

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class RequestTab extends ExampleTab {

  public RequestTab() {
    super( "Long running request" );
  }

  @Override
  protected void createStyleControls( final Composite parent ) {
  }

  @Override
  protected void createExampleControls( final Composite parent ) {
    parent.setLayout( new GridLayout( 4, false ) );
    Label lblInfo = new Label( parent, SWT.WRAP );
    String msg
      = "Simulate a long running server-side task. You should see the mouse "
      + "cursor change after a short delay.";
    lblInfo.setText( msg );
    GridData gridData = new GridData();
    gridData.horizontalSpan = 4;
    lblInfo.setLayoutData( gridData );
    Label lblProcessingTime = new Label( parent, SWT.NONE );
    lblProcessingTime.setText( "Processing time" );
    final Text txtProcessingTime = new Text( parent, SWT.BORDER );
    txtProcessingTime.setText( "5000" );
    Label lblMS = new Label( parent, SWT.NONE );
    lblMS.setText( "ms" );
    Button btnRun = new Button( parent, SWT.PUSH );
    btnRun.setText( "Run" );
    btnRun.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected( SelectionEvent event ) {
        String timeText = txtProcessingTime.getText().trim();
        int time = -1;
        try {
          time = Integer.parseInt( timeText );
        } catch( NumberFormatException e ) {
          // ignore as time is initialized with an illegal value
        }
        if( time >= 0 ) {
          boolean interrupted = false;
          try {
            Thread.sleep( time );
          } catch( InterruptedException e ) {
            interrupted = true;
          }
          Shell shell = parent.getShell();
          String msg = interrupted ? "Interrupted" : "Done";
          MessageDialog.openInformation( shell, "Information", msg );
        } else {
          Shell shell = parent.getShell();
          String msg = "\'" + timeText + "\' is not a valid processing time.";
          MessageDialog.openError( shell, "Error", msg );
        }
      }
    } );
  }
}
