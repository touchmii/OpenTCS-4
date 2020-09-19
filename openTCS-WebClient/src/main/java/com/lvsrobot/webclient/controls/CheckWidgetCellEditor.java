/*******************************************************************************
 * Copyright (c) 2011, 2012 Rüdiger Herrmann and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Rüdigier Herrmann - initial API and implementation
 *    EclipseSource - ongoing developemnt
 ******************************************************************************/
package com.lvsrobot.webclient.controls;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;


class CheckWidgetCellEditor extends CellEditor {
  private static final char ESCAPE_CHAR = '\u001b';

  private Button checkbox;

  CheckWidgetCellEditor( Composite parent ) {
    super( parent, SWT.CHECK );
  }

  @Override
  protected Control createControl( Composite parent ) {
    checkbox = new Button( parent, getStyle() );
    checkbox.addKeyListener( new KeyAdapter() {
      @Override
      public void keyReleased( KeyEvent event ) {
        if( event.character == ESCAPE_CHAR ) {
          fireCancelEditor();
        }
      }
    } );
    return checkbox;
  }

  @Override
  protected Object doGetValue() {
    return checkbox.getSelection() ? Boolean.TRUE : Boolean.FALSE;
  }

  @Override
  protected void doSetFocus() {
    checkbox.setFocus();
  }

  @Override
  protected void doSetValue( Object value ) {
    checkbox.setSelection( ( ( Boolean )value ).booleanValue() );
  }
}
