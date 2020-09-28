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
package com.lvsrobot.webclient.demo.presentation;

import org.eclipse.jface.dialogs.PopupDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.widgets.*;
import org.eclipse.ui.presentations.IPresentablePart;

final class StackPopup extends PopupDialog {
  private final Control contentProxy;
  private final Object[] parts;
  private final SelectionListener selectionListener;

  StackPopup( final Shell parent,
              final Control contentProxy,
              final Object[] parts,
              final SelectionListener selectionListener )
  {
    super( parent,
           SWT.RESIZE | SWT.ON_TOP,
           false,
           false,
           false,
           false,
           null,
           null );
    this.contentProxy = contentProxy;
    this.parts = parts;
    this.selectionListener = selectionListener;
  }

  @Override
  protected void adjustBounds() {
    Display display = contentProxy.getDisplay();
    Point position = display.map( contentProxy.getParent(),
                                  null,
                                  contentProxy.getLocation() );
    getShell().setBounds( position.x - 1,
                          position.y + 1,
                          contentProxy.getSize().x + 4,
                          contentProxy.getSize().y + 2 );
  }

  @Override
  public int open() {
    int result = super.open();
    Listener closeListener = new Listener() {
      public void handleEvent( Event event ) {
        close();
      }
    };
    getShell().addListener( SWT.Deactivate, closeListener );
    getShell().addListener( SWT.Close, closeListener );
    contentProxy.addListener( SWT.Dispose, closeListener );
    Shell controlShell = contentProxy.getShell();
    controlShell.addListener( SWT.Move, closeListener );

    getShell().setAlpha( 230 );
    getShell().setBackgroundImage( Images.IMG_MIDDLE_CENTER );
    getShell().setActive();
    return result;
  }

  @Override
  protected Control createDialogArea( final Composite parent ) {
    final Table result = new Table( parent, SWT.NONE );
    FontData fontData = result.getFont().getFontData()[ 0 ];
    result.setFont( new Font( parent.getDisplay(),
                              fontData.getName(),
                              fontData.getHeight() + 4,
                              fontData.getStyle() ) );
    result.setBackgroundImage( Images.IMG_MIDDLE_CENTER );
    result.addControlListener( new ControlAdapter() {
      @Override
      public void controlResized( final ControlEvent evt ) {
        result.removeControlListener( this );
        Point size = result.getSize();
        result.setSize( size.x + 25, size.y + 25 );
        result.addControlListener( this );
      }
    } );
    result.setRedraw( false );
    result.setItemCount( parts.length );
    TableItem[] items = result.getItems();
    for( int i = 0; i < items.length; i++ ) {
      TableItem item = items[ i ];
      IPresentablePart part = ( IPresentablePart )parts[ i ];
      item.setText( part.getTitle() );
      item.setImage( part.getTitleImage() );
      item.setData( part );
    }
    result.setRedraw( true );
    result.addSelectionListener( selectionListener );
    return result;

  }
}