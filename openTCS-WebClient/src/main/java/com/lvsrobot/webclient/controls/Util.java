/*******************************************************************************
 * Copyright (c) 2002, 2012 Innoopract Informationssysteme GmbH.
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

import java.io.IOException;
import java.io.InputStream;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;


public final class Util {

  private Util() {
  }

  public static void textSizeAdjustment( final Label label, final Control control ) {
    final Composite parent = control.getParent();
    parent.addControlListener( new ControlAdapter() {
      @Override
      public void controlResized( final ControlEvent e ) {
        int height = label.computeSize( SWT.DEFAULT, SWT.DEFAULT ).y;
        int width = height * 3;
        if( parent.getLayout() instanceof RowLayout ) {
          control.setLayoutData( new RowData( width, height ) );
        } else if( parent.getLayout() instanceof GridLayout ) {
          control.setLayoutData( new GridData( width, height ) );
        }
      }
    } );
  }

  public static Image loadImage( Display display, String name ) {
    Image result = null;
    InputStream stream = Util.class.getClassLoader().getResourceAsStream( name );
    if( stream != null ) {
      try {
        result = new Image( display, stream );
      } finally {
        try {
          stream.close();
        } catch( IOException unexpected ) {
          throw new RuntimeException( "Failed to close image input stream", unexpected );
        }
      }
    }
    return result;
  }
}
