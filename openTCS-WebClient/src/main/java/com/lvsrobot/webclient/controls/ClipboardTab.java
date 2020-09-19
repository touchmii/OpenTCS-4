/*******************************************************************************
 * Copyright (c) 2020 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package com.lvsrobot.webclient.controls;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.rap.rwt.RWT;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTError;
//import org.eclipse.swt.dnd.Clipboard;
//import org.eclipse.swt.dnd.;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;


public class ClipboardTab extends ExampleTab {
  
//  private Clipboard clipboard;
  private Text copyText;
  private Text pasteText;
  
  public ClipboardTab() {
    super( "Clipboard" );
    setHorizontalSashFormWeights( new int[] { 100, 0 } );
  }

  @Override
  protected void createStyleControls( Composite parent ) {
  }

  @Override
  protected void createExampleControls( Composite parent ) {
//    clipboard = new Clipboard( parent.getDisplay() );
    
    configureDisplay( parent.getDisplay() );

    parent.setLayout( new GridLayout( 2, false )  );

    copyText = new Text( parent, SWT.SINGLE | SWT.BORDER );
    copyText.setLayoutData( new GridData( SWT.FILL, SWT.CENTER, true, false ) );
    Button copyButton = new Button( parent, SWT.PUSH );
    copyButton.setText( "Copy" );
    copyButton.addListener( SWT.Selection, new Listener() {
      @Override
      public void handleEvent( Event event ) {
        String text = copyText.getText();
        if( !text.isEmpty() ) {
          setClipboardData( text );
        }
      }
    } );

    pasteText = new Text( parent, SWT.SINGLE | SWT.BORDER );
    pasteText.setLayoutData( new GridData( SWT.FILL, SWT.CENTER, true, false ) );
    Button pasteButton = new Button( parent, SWT.PUSH );
    pasteButton.setText( "Paste" );
    pasteButton.addListener( SWT.Selection, new Listener() {
      @Override
      public void handleEvent( Event event ) {
        String text = getClipboardData();
        pasteText.setText( text );
      }
    } );

  }
  
  private void configureDisplay( Display display ) {
    display.setData( RWT.ACTIVE_KEYS, new String[] { "CTRL+C", "CTRL+V" } );
    display.setData( RWT.CANCEL_KEYS, new String[] { "CTRL+C", "CTRL+V" } );
    display.addFilter( SWT.KeyDown, new Listener() {
      @Override
      public void handleEvent( Event event ) {
        if( event.character == 'c' ) {
          String text = copyText.getText();
          if( !text.isEmpty() ) {
            setClipboardData( text );
          }
        } else if( event.character == 'v' ) {
          String text = getClipboardData();
          pasteText.setText( text );
        }
      }
    } );
  }
  
  private void setClipboardData( String textData ) {
    try {
      Transfer[] transfers = new Transfer[]{ TextTransfer.getInstance() };
      Object[] data = new Object[]{ textData };
//      clipboard.setContents( data, transfers );
    } catch( SWTError error ) {
      String message = "Unable to set client clipboard data!";
      MessageDialog.openError( getShell(), "Clipboard Error", message );
    }
  }
  
  private String getClipboardData() {
//    String textData = ( String )clipboard.getContents( TextTransfer.getInstance() );
    String textData = null;
    if( textData == null ) {
      String message = "Unable to get client clipboard data!";
      MessageDialog.openError( getShell(), "Clipboard Error", message );
      return "";
    }
    return textData;
  }
  
}
