/*******************************************************************************
 * Copyright (c) 2009, 2015 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package com.lvsrobot.webclient.controls;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;


public final class ScrolledCompositeTab extends ExampleTab {

  private ScrolledComposite composite;
  private Composite content;
  private Button showFocusedControl;
  private Button alwaysShowScrollBars;

  public ScrolledCompositeTab() {
    super( "ScrolledComposite" );
    setDefaultStyle( SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL );
  }

  @Override
  protected void createStyleControls( final Composite parent ) {
    createStyleButton( "BORDER", SWT.BORDER, true );
    createStyleButton( "H_SCROLL", SWT.H_SCROLL, true );
    createStyleButton( "V_SCROLL", SWT.V_SCROLL, true );
    createOrientationButton();
    createVisibilityButton();
    createEnablementButton();
    createBgColorButton();
    createBgImageButton();
    alwaysShowScrollBars = createAlwaysShowScrollBarsButton();
    showFocusedControl = createShowFocusedControlButton();
    createShowControlButton();
    createFocusControlButton();
    createOriginControl();
  }

  @Override
  protected void createExampleControls( final Composite parent ) {
    parent.setLayout( new GridLayout() );
    composite = new ScrolledComposite( parent, getStyle() );
    composite.setLayoutData( new GridData( GridData.FILL, GridData.FILL, true, true ) );
    content = new Composite( composite, SWT.NONE );
    content.setLayout( new GridLayout( 5, true ) );
    for( int i = 0; i < 100; i++ ) {
      Button b = new Button( content, SWT.PUSH );
      b.setText( "Button " + i );
      GridData data = new GridData();
      data.widthHint = 120;
      b.setLayoutData( data );
    }
    composite.setContent( content );
    composite.setExpandHorizontal( true );
    composite.setExpandVertical( true );
    if( checkControl( alwaysShowScrollBars ) ) {
      composite.setAlwaysShowScrollBars( alwaysShowScrollBars.getSelection() );
    }
    if( checkControl( showFocusedControl ) ) {
      composite.setShowFocusedControl( showFocusedControl.getSelection() );
    }
    composite.setMinSize( content.computeSize( SWT.DEFAULT, SWT.DEFAULT ) );
    composite.addControlListener( new ControlAdapter() {
      @Override
      public void controlResized( ControlEvent e ) {
        composite.setMinSize( content.computeSize( SWT.DEFAULT, SWT.DEFAULT ) );
      }
    } );
    composite.setOrigin( 50, 50 );
    registerControl( composite );
  }

  private void createShowControlButton() {
    Button button = new Button( styleComp, SWT.PUSH );
    button.setText( "Show Button 89" );
    button.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected( SelectionEvent e ) {
        composite.showControl( content.getChildren()[ 89 ] );
      }
    } );
  }

  private void createFocusControlButton() {
    Button button = new Button( styleComp, SWT.PUSH );
    button.setText( "Focus Button 89" );
    button.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected( SelectionEvent e ) {
        content.getChildren()[ 89 ].setFocus();
      }
    } );
  }

  private Button createAlwaysShowScrollBarsButton() {
    final Button button = new Button( styleComp, SWT.CHECK );
    button.setText( "Always Show ScrollBars" );
    button.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected( final SelectionEvent event ) {
        composite.setAlwaysShowScrollBars( button.getSelection() );
      }
    } );
    return button;
  }

  private Button createShowFocusedControlButton() {
    final Button button = new Button( styleComp, SWT.CHECK );
    button.setText( "Show Focused Control" );
    button.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected( final SelectionEvent event ) {
        composite.setShowFocusedControl( button.getSelection() );
      }
    } );
    return button;
  }

  private void createOriginControl() {
    Composite comp = new Composite( styleComp, SWT.NONE );
    RowLayout layout = new RowLayout(  SWT.HORIZONTAL );
    layout.center = true;
    comp.setLayout( layout );
    Label lblX = new Label( comp, SWT.NONE );
    lblX.setText( "Scroll to X:" );
    final Text txtX = new Text( comp, SWT.BORDER );
    txtX.setText( "0" );
    Util.textSizeAdjustment( lblX, txtX );
    Label lblY = new Label( comp, SWT.NONE );
    lblY.setText( "Y:" );
    final Text txtY = new Text( comp, SWT.BORDER );
    txtY.setText( "0" );
    Util.textSizeAdjustment( lblY, txtY );
    Button btnSelect = new Button( comp, SWT.PUSH );
    btnSelect.setText( "OK" );
    btnSelect.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected( final SelectionEvent event ) {
        try {
          int x = Integer.parseInt( txtX.getText() );
          int y = Integer.parseInt( txtY.getText() );
          composite.setOrigin( x, y );
        } catch( Exception e ) {
          // ignore invalid values
        }
      }
    } );
  }

}
