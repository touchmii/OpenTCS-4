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
import org.eclipse.swt.widgets.Scale;
import org.eclipse.swt.widgets.Spinner;

public class ScaleTab extends ExampleTab {

  private static final String PROP_CONTEXT_MENU = "contextMenu";
  private static final String PROP_SELECTION_LISTENER = "selectionListener";

  Scale scale;
  Spinner minimumSpinner, maximumSpinner, selectionSpinner,
          incrementSpinner, pageIncrementSpinner;

  public ScaleTab() {
    super( "Scale" );
    setDefaultStyle( SWT.HORIZONTAL );
  }

  @Override
  protected void createStyleControls( Composite parent ) {
    createStyleButton( parent, "HORIZONTAL", SWT.HORIZONTAL, SWT.RADIO, true );
    createStyleButton( parent, "VERTICAL", SWT.VERTICAL, SWT.RADIO, false );
    createStyleButton( "BORDER", SWT.BORDER );
    createOrientationButton();
    createVisibilityButton();
    createEnablementButton();
    createBgColorButton();
    createBgImageButton();
    createCursorCombo();
    minimumSpinner = createSpinnerControl( parent, "Minimum", 0, 100000, 0 );
    minimumSpinner.addModifyListener( new ModifyListener() {
      @Override
      public void modifyText( ModifyEvent event ) {
        int minimum = minimumSpinner.getSelection();
        scale.setMinimum( minimum );
      }
    } );
    maximumSpinner = createSpinnerControl( parent, "Maximum", 0, 100000, 100 );
    maximumSpinner.addModifyListener( new ModifyListener() {
      @Override
      public void modifyText( ModifyEvent event ) {
        int maximum = maximumSpinner.getSelection();
        scale.setMaximum( maximum );
      }
    } );
    selectionSpinner = createSpinnerControl( parent, "Selection", 0, 100000, 0 );
    selectionSpinner.addModifyListener( new ModifyListener() {
      @Override
      public void modifyText( ModifyEvent event ) {
        int selection = selectionSpinner.getSelection();
        scale.setSelection( selection );
      }
    } );
    incrementSpinner = createSpinnerControl( parent, "Increment", 0, 100000, 1 );
    incrementSpinner.addModifyListener( new ModifyListener() {
      @Override
      public void modifyText( ModifyEvent event ) {
        int increment = incrementSpinner.getSelection();
        scale.setIncrement( increment );
      }
    } );
    pageIncrementSpinner = createSpinnerControl( parent, "Page Increment", 0, 100000, 10 );
    pageIncrementSpinner.addModifyListener( new ModifyListener() {
      @Override
      public void modifyText( ModifyEvent event ) {
        int pageIncrement = pageIncrementSpinner.getSelection();
        scale.setPageIncrement( pageIncrement );
      }
    } );
    createPropertyCheckbox( "Add Context Menu", PROP_CONTEXT_MENU );
    createPropertyCheckbox( "Add Selection Listener", PROP_SELECTION_LISTENER );
  }

  @Override
  protected void createExampleControls( Composite parent ) {
    parent.setLayout( new RowLayout( SWT.VERTICAL ) );
    int style = getStyle();
    scale = new Scale( parent, style );
    if( hasCreateProperty( PROP_CONTEXT_MENU ) ) {
      Menu scaleMenu = new Menu( scale );
      MenuItem scaleMenuItem = new MenuItem( scaleMenu, SWT.PUSH );
      scaleMenuItem.addSelectionListener( new SelectionAdapter() {
        @Override
        public void widgetSelected( final SelectionEvent event ) {
          String message = "You requested a context menu for the Scale";
          MessageDialog.openInformation( scale.getShell(), "Information", message );
        }
      } );
      scaleMenuItem.setText( "Scale context menu item" );
      scale.setMenu( scaleMenu );
    }
    if( hasCreateProperty( PROP_SELECTION_LISTENER ) ) {
      scale.addSelectionListener( new SelectionListener() {
        @Override
        public void widgetSelected( final SelectionEvent event ) {
          String message = "Scale WidgetSelected! Current selection: " + scale.getSelection();
          log( message );
          selectionSpinner.setSelection( scale.getSelection() );
          scale.setToolTipText( String.valueOf( scale.getSelection() ) );
        }

        @Override
        public void widgetDefaultSelected( final SelectionEvent event ) {
          String message = "Scale WidgetDefaultSelected! Current selection: " + scale.getSelection();
          log( message );
          selectionSpinner.setSelection( scale.getSelection() );
          scale.setToolTipText( String.valueOf( scale.getSelection() ) );
        }
      } );
    }
    if( checkControl( minimumSpinner ) ) {
      scale.setMinimum( minimumSpinner.getSelection() );
    }
    if( checkControl( maximumSpinner ) ) {
      scale.setMaximum( maximumSpinner.getSelection() );
    }
    if( checkControl( selectionSpinner ) ) {
      scale.setSelection( selectionSpinner.getSelection() );
    }
    if( checkControl( incrementSpinner ) ) {
      scale.setIncrement( incrementSpinner.getSelection() );
    }
    if( checkControl( pageIncrementSpinner ) ) {
      scale.setPageIncrement( pageIncrementSpinner.getSelection() );
    }
    registerControl( scale );
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
    composite.setLayout( new GridLayout( 3, false ) );
    Label label = new Label( composite, SWT.NONE );
    label.setText( labelText );
    final Spinner spinner = new Spinner( composite, SWT.BORDER );
    spinner.setSelection( selection );
    spinner.setMinimum( minimum );
    spinner.setMaximum( maximum );
    return spinner;
  }

}
