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
package com.lvsrobot.webclient.controls;

import java.util.Iterator;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FontDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;


public class TextSizeTab extends ExampleTab {

  private static String[] testStrings;
  private boolean propFixedSize;
  private int nextIndex = 0;
  private String labelText = "";
  private Font font;

  public TextSizeTab() {
    super( "TextSize" );
    testStrings = createTestStrings();
    switchText();
  }

  @Override
  protected void createStyleControls( Composite parent ) {
    createStyleButton( "BORDER", SWT.BORDER );
    createStyleButton( "WRAP", SWT.WRAP );
    createFontChooser();
    final Button fixedSizeButton = createPropertyButton( "Fixed Size" );
    fixedSizeButton.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected( SelectionEvent event ) {
        propFixedSize = fixedSizeButton.getSelection();
        createNew();
      }
    } );
    Button nextButton = createPropertyButton( "Next Text", SWT.PUSH );
    nextButton.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected( SelectionEvent e ) {
        switchText();
        createNew();
      }
    } );
  }

  @Override
  protected void createExampleControls( Composite parent ) {
    int style = getStyle();
    Color background = new Color( parent.getDisplay(), 0xcc, 0xb7, 0x91 );

    Label label1 = new Label( parent, style );
    label1.setBackground( background );
    label1.setText( labelText );
    label1.setLocation( 10, 10 );
    registerControl( label1 );

    Text text1 = new Text( parent, style );
    text1.setBackground( background );
    text1.setText( labelText );
    text1.setLocation( 10, 100 );
    registerControl( text1 );

    updateControls();
  }

  private void updateControls() {
    Iterator<Control> iter = controls.iterator();
    while( iter.hasNext() ) {
      Control control = iter.next();
      control.setFont( font );
      if( propFixedSize ) {
        control.setSize( control.computeSize( 200, SWT.DEFAULT ) );
      } else {
        control.pack();
      }
    }
  }

  private void switchText() {
    labelText = testStrings[ nextIndex ];
    nextIndex = (nextIndex + 1) % testStrings.length;
  }

  private static String[] createTestStrings() {
    String[] result = new String[] {
      "Lorem ipsum",
      "Lorem ipsum dolor sit amet",
      "Lorem ipsum dolor sit amet, consectetur adipisici "
      + "elit, sed do eiusmod tempor incididunt ut labore et "
      + "dolore magna aliqua.",

      "Lorem ipsum dolor sit amet, consectetur adipisici "
      + "elit, sed do eiusmod tempor incididunt ut labore et "
      + "dolore magna aliqua.\n"
      + "Ut enim ad minim veniam, quis nostrud exercitation "
      + "ullamco laboris nisi ut aliquip ex ea commodo "
      + "consequat.\n",

      "Lorem ipsum dolor sit amet, consectetur adipisici "
      + "elit, sed do eiusmod tempor incididunt ut labore et "
      + "dolore magna aliqua. "
      + "Ut enim ad minim veniam, quis nostrud exercitation "
      + "ullamco laboris nisi ut aliquip ex ea commodo "
      + "consequat. ",

      ""
    };
    return result;
  }

  @Override
  protected Button createFontChooser() {
    final Button button = new Button( styleComp, SWT.PUSH );
    button.setText( "Font" );
    button.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected( SelectionEvent event ) {
        FontDialog fontChooser = new FontDialog( getShell(), SWT.NONE );
        Control control = controls.get( 0 );
        fontChooser.setFontList( control.getFont().getFontData() );
        if( fontChooser.open() != null ) {
          font = new Font( control.getDisplay(), fontChooser.getFontList() );
        }
        updateControls();
      }
    } );
    return button;
  }
}
