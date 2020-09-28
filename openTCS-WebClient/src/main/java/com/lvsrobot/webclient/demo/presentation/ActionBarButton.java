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


import org.eclipse.jface.action.Action;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.widgets.*;

class ActionBarButton
  extends MouseAdapter
  implements IPropertyChangeListener
{
  private final Action action;
  private final Label label;
  private final Display display;

  ActionBarButton( Action action, Composite actionBar ) {
    this.action = action;
    this.label = new Label( actionBar, SWT.NONE );
    this.display = label.getDisplay();
    label.setText( action.getText() );
    FontData fontData = label.getFont().getFontData()[ 0 ];
    int height = fontData.getHeight() + 2;
    label.setFont( new Font( display, fontData.getName(), height, fontData.getStyle() ) );
    label.pack();
    action.addPropertyChangeListener( this );
    adjustEnablement( action.isEnabled() );
  }

  @Override
  public void mouseUp( MouseEvent event ) {
    run();
  }

  public void run() {
    action.run();
  }

  public void propertyChange( PropertyChangeEvent event ) {
    if( "enabled".equals( event.getProperty() ) ) {
      adjustEnablement( ( ( Boolean )event.getNewValue() ).booleanValue() );
    }
  }

  private void adjustEnablement( boolean booleanValue ) {
    if( booleanValue ) {
      label.addMouseListener( this );
      label.setForeground( display.getSystemColor( SWT.COLOR_WHITE ) );
    } else {
      label.removeMouseListener( this );
      label.setForeground( new Color( display, 192, 192, 192 ) );
    }
  }
}