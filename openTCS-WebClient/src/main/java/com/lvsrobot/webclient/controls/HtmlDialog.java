/*******************************************************************************
 * Copyright (c) 2007, 2012 Innoopract Informationssysteme GmbH and others.
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
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.swt.widgets.Shell;


public class HtmlDialog {

  private final Shell shell;
  private final String text;

  public HtmlDialog( final Shell parent, final String title, final String text )
  {
    int style = SWT.APPLICATION_MODAL | SWT.DIALOG_TRIM | SWT.RESIZE;
    shell = new Shell( parent, style );
    this.text = text;
    shell.setText( title  );
    shell.setBounds( 200, 50, 400, 600 );
    shell.setLayout( new FillLayout() );
    Composite composite = new Composite( shell, SWT.NONE );
    createContents( composite );
  }

  public void open() {
    shell.layout();
    shell.open();
  }

  public void close() {
    shell.close();
  }

  public void setSize( final int width, final int height ) {
    shell.setSize( width, height );
  }

  public void setLocation( final int x, final int y ) {
    shell.setLocation( x, y );
  }

  private void createContents( final Composite parent ) {
    Layout layout = new GridLayout();
    parent.setLayout( layout );
    // browser
    Browser browser = new Browser( parent, SWT.BORDER );
    browser.setText( text );
    GridData browserData = new GridData( GridData.FILL_BOTH );
    browser.setLayoutData( browserData );
    // button bar
    Button closeButton = new Button( parent, SWT.PUSH );
    closeButton.setText( "Close" );
    shell.setDefaultButton( closeButton );
    closeButton.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected( final SelectionEvent event ) {
        close();
      }
    } );
    GridData buttonData = new GridData( GridData.HORIZONTAL_ALIGN_END );
    closeButton.setLayoutData( buttonData );
  }
}
