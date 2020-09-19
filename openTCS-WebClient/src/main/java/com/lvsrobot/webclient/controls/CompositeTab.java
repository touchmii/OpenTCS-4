/*******************************************************************************
 * Copyright (c) 2007, 2015 Innoopract Informationssysteme GmbH and others.
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
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;


public final class CompositeTab extends ExampleTab {

  private Composite composite;
  private boolean addMouseListener;
  private int backgroundMode;

  public CompositeTab() {
    super( "Composite" );
  }

  @Override
  protected void createStyleControls( final Composite parent ) {
    createStyleButton( "BORDER", SWT.BORDER );
    createOrientationButton();
    cteateRoundedBorderGroup();
    createVisibilityButton();
    createEnablementButton();
    createBgColorButton();
    createBgGradientButton();
    createBgImageButton();
    createBackgroundModeControls( parent );
    createCursorCombo();
    Button cbAddMouseListener = new Button( parent, SWT.CHECK );
    cbAddMouseListener.setText( "Attach MouseListener" );
    cbAddMouseListener.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected( SelectionEvent e ) {
        addMouseListener = !addMouseListener;
        createNew();
      }
    } );
    cbAddMouseListener.setSelection( addMouseListener );
  }

  @Override
  protected void createExampleControls( final Composite parent ) {
    parent.setLayout( new FillLayout() );
    parent.setBackgroundMode( backgroundMode );
    composite = new Composite( parent, getStyle() );
    if( addMouseListener ) {
      MouseListener listener = new MouseListener(  ) {
        @Override
        public void mouseDoubleClick( MouseEvent e ) {
          log( "mouseDoubleClick: " + e );
        }
        @Override
        public void mouseDown( MouseEvent e ) {
          log( "mouseDown: " + e );
        }
        @Override
        public void mouseUp( MouseEvent e ) {
          log( "mouseUp: " + e );
        }
      };
      composite.addMouseListener( listener );
    }
    composite.setLayout( new RowLayout( SWT.HORIZONTAL ) );
    registerControl( composite );
    Label label = new Label( composite, SWT.NONE );
    label.setText( "Label" );
    Button pushButton = new Button( composite, SWT.PUSH );
    pushButton.setText( "Push Button" );
    Button radioButton = new Button( composite, SWT.RADIO );
    radioButton.setText( "Radio Button" );
    Button checkButton = new Button( composite, SWT.CHECK );
    checkButton.setText( "Check Box" );
    Text text = new Text( composite, SWT.SINGLE | SWT.BORDER );
    text.setText( "text" );
    Text multiText = new Text( composite, SWT.MULTI | SWT.BORDER );
    multiText.setText( "Multiline Text" );
    multiText.setLayoutData( new RowData( 80, 60 ) );
    Combo combo = new Combo( composite, SWT.NONE );
    combo.add( "Item 1" );
    combo.add( "Item 2" );
    combo.add( "Item 3" );
    List list = new List( composite, SWT.BORDER );
    list.add( "Item 1" );
    list.add( "Item 2" );
    list.add( "Item 3" );
    Composite composite2 = new Composite( composite, SWT.NONE );
    composite2.setBackground( bgColors[ BG_COLOR_GREEN ] );
    Group group = new Group( composite, SWT.NONE );
    group.setText( "Group" );
    new DateTime( composite, SWT.BORDER | SWT.TIME );
    new Spinner( composite, SWT.BORDER | SWT.NONE );
  }

  protected void createBackgroundModeControls( final Composite parent ) {
    Group group = new Group( parent, SWT.NONE );
    group.setText( "Background Mode" );
    group.setLayout( new GridLayout() );
    final Button noneButton = new Button( group, SWT.RADIO );
    noneButton.setText( "SWT.INHERIT_NONE" );
    final Button defaultButton = new Button( group, SWT.RADIO );
    defaultButton.setText( "SWT.INHERIT_DEFAULT" );
    final Button forceButton = new Button( group, SWT.RADIO );
    forceButton.setText( "SWT.INHERIT_FORCE" );
    SelectionListener selectionAdapter = new SelectionAdapter() {
      @Override
      public void widgetSelected( SelectionEvent e ) {
        if( defaultButton.getSelection() ) {
          backgroundMode = SWT.INHERIT_DEFAULT;
        } else if( forceButton.getSelection() ) {
          backgroundMode = SWT.INHERIT_FORCE;
        } else {
          backgroundMode = SWT.INHERIT_NONE;
        }
        composite.setBackgroundMode( backgroundMode );
      }
    };
    noneButton.addSelectionListener( selectionAdapter );
    defaultButton.addSelectionListener( selectionAdapter );
    forceButton.addSelectionListener( selectionAdapter );
    noneButton.setSelection( true );
  }

}
