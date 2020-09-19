/*******************************************************************************
 * Copyright (c) 2002, 2015 Innoopract Informationssysteme GmbH and others.
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
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Slider;
import org.eclipse.swt.widgets.Spinner;

public class SliderTab extends ExampleTab {

  private static final String PROP_CONTEXT_MENU = "contextMenu";
  private static final String PROP_SELECTION_LISTENER = "selectionListener";

  Slider slider;
  Spinner minimumSpinner, maximumSpinner, selectionSpinner, thumbSpinner,
          incrementSpinner, pageIncrementSpinner;

  public SliderTab() {
    super( "Slider" );
    setDefaultStyle( SWT.HORIZONTAL );
  }

  @Override
  protected void createStyleControls( Composite parent ) {
    createStyleButton( parent, "HORIZONTAL", SWT.HORIZONTAL, SWT.RADIO, true );
    createStyleButton( parent, "VERTICAL", SWT.VERTICAL, SWT.RADIO, false );
    createOrientationButton();
    createVisibilityButton();
    createEnablementButton();
    createBgColorButton();
    createBgImageButton();
    minimumSpinner = createSpinnerControl( parent, "Minimum", 0, 100000, 0 );
    minimumSpinner.addModifyListener( new ModifyListener() {
      @Override
      public void modifyText( ModifyEvent event ) {
        int minimum = minimumSpinner.getSelection();
        slider.setMinimum( minimum );
      }
    } );
    maximumSpinner = createSpinnerControl( parent, "Maximum", 0, 100000, 100 );
    maximumSpinner.addModifyListener( new ModifyListener() {
      @Override
      public void modifyText( ModifyEvent event ) {
        int maximum = maximumSpinner.getSelection();
        slider.setMaximum( maximum );
      }
    } );
    selectionSpinner = createSpinnerControl( parent, "Selection", 0, 100000, 0 );
    selectionSpinner.addModifyListener( new ModifyListener() {
      @Override
      public void modifyText( ModifyEvent event ) {
        int selection = selectionSpinner.getSelection();
        slider.setSelection( selection );
      }
    } );
    thumbSpinner = createSpinnerControl( parent, "Thumb", 1, 100000, 10 );
    thumbSpinner.addModifyListener( new ModifyListener() {
      @Override
      public void modifyText( ModifyEvent event ) {
        int thumb = thumbSpinner.getSelection();
        slider.setThumb( thumb );
      }
    } );
    incrementSpinner = createSpinnerControl( parent, "Increment", 0, 100000, 1 );
    incrementSpinner.addModifyListener( new ModifyListener() {
      @Override
      public void modifyText( ModifyEvent event ) {
        int increment = incrementSpinner.getSelection();
        slider.setIncrement( increment );
      }
    } );
    pageIncrementSpinner = createSpinnerControl( parent, "Page Increment", 0, 100000, 10 );
    pageIncrementSpinner.addModifyListener( new ModifyListener() {
      @Override
      public void modifyText( ModifyEvent event ) {
        int pageIncrement = pageIncrementSpinner.getSelection();
        slider.setPageIncrement( pageIncrement );
      }
    } );
    createPropertyCheckbox( "Add Context Menu", PROP_CONTEXT_MENU );
    createPropertyCheckbox( "Add Selection Listener", PROP_SELECTION_LISTENER );
  }

  @Override
  protected void createExampleControls( Composite parent ) {
    parent.setLayout( new RowLayout( SWT.VERTICAL ) );
    int style = getStyle();
    slider = new Slider( parent, style );
    slider.setToolTipText( "foo" );
    if( hasCreateProperty( PROP_CONTEXT_MENU ) ) {
      Menu sliderMenu = new Menu( slider );
      MenuItem sliderMenuItem = new MenuItem( sliderMenu, SWT.PUSH );
      sliderMenuItem.addSelectionListener( new SelectionAdapter() {
        @Override
        public void widgetSelected( final SelectionEvent event ) {
          String message = "You requested a context menu for the Slider";
          MessageDialog.openInformation( slider.getShell(), "Information", message );
        }
      } );
      sliderMenuItem.setText( "Slider context menu item" );
      slider.setMenu( sliderMenu );
    }
    if( hasCreateProperty( PROP_SELECTION_LISTENER ) ) {
      slider.addSelectionListener( new SelectionListener() {
        @Override
        public void widgetSelected( final SelectionEvent event ) {
          String message = "Slider WidgetSelected! Current selection: " + slider.getSelection();
          log( message );
          selectionSpinner.setSelection( slider.getSelection() );
          slider.setToolTipText( String.valueOf( slider.getSelection() ) );
        }

        @Override
        public void widgetDefaultSelected( final SelectionEvent event ) {
          String message = "Slider WidgetDefaultSelected! Current selection: " + slider.getSelection();
          log( message );
          selectionSpinner.setSelection( slider.getSelection() );
          slider.setToolTipText( String.valueOf( slider.getSelection() ) );
        }
      } );
    }
    if( checkControl( minimumSpinner ) ) {
      slider.setMinimum( minimumSpinner.getSelection() );
    }
    if( checkControl( maximumSpinner ) ) {
      slider.setMaximum( maximumSpinner.getSelection() );
    }
    if( checkControl( selectionSpinner ) ) {
      slider.setSelection( selectionSpinner.getSelection() );
    }
    if( checkControl( thumbSpinner ) ) {
      slider.setThumb( thumbSpinner.getSelection() );
    }
    if( checkControl( incrementSpinner ) ) {
      slider.setIncrement( incrementSpinner.getSelection() );
    }
    if( checkControl( pageIncrementSpinner ) ) {
      slider.setPageIncrement( pageIncrementSpinner.getSelection() );
    }
    registerControl( slider );
  }

  protected Button createStyleButton( Composite parent,
                                      String name,
                                      int style,
                                      int buttonStyle,
                                      boolean checked )
  {
    Button button = new Button( parent, buttonStyle );
    button.setText( name );
    button.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected( final SelectionEvent event ) {
        createNew();
      }
    } );
    button.setData( "style", new Integer( style ) );
    button.setSelection( checked );
    return button;
  }

  private Spinner createSpinnerControl( Composite parent,
                                        String labelText,
                                        int minimum,
                                        int maximum,
                                        int selection ) {
    Composite composite = new Composite( parent, SWT.NONE );
    composite.setLayout( new GridLayout( 4, false ) );
    Label label = new Label( composite, SWT.NONE );
    label.setText( labelText );
    final Spinner spinner = new Spinner( composite, SWT.BORDER );
    spinner.setSelection( selection );
    spinner.setMinimum( minimum );
    spinner.setMaximum( maximum );
    return spinner;
  }

}
