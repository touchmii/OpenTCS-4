/*******************************************************************************
 * Copyright (c) 2008, 2016 Innoopract Informationssysteme GmbH and others.
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
import org.eclipse.rap.rwt.RWT;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ExpandEvent;
import org.eclipse.swt.events.ExpandListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.ExpandBar;
import org.eclipse.swt.widgets.ExpandItem;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;


class ExpandBarTab extends ExampleTab {

  private static final String PROP_CONTEXT_MENU = "contextMenu";
  private static final String PROP_EXPAND_LISTENER = "expandListener";

  private ExpandBar expandBar;
  private Spinner spinner;
  private boolean markupEnabled;

  ExpandBarTab() {
    super( "ExpandBar" );
    setDefaultStyle( SWT.BORDER | SWT.V_SCROLL );
    markupEnabled = true;
  }

  @Override
  protected void createStyleControls( Composite parent ) {
    createStyleButton( "V_SCROLL", SWT.V_SCROLL, true );
    createStyleButton( "BORDER", SWT.BORDER, true );
    createOrientationButton();
    createMarkupButton();
    createVisibilityButton();
    createEnablementButton();
    spinner = createSpacingControl( parent );
    createFontChooser();
    createFgColorButton();
    createBgColorButton();
    createBgImageButton();
    createInsertItemButton( parent );
    createRemoveItemButton( parent );
    createPropertyCheckbox( "Add Context Menu", PROP_CONTEXT_MENU );
    createPropertyCheckbox( "Add Expand Listener", PROP_EXPAND_LISTENER );
  }

  @Override
  protected void createExampleControls( Composite parent ) {
    parent.setLayout( new RowLayout( SWT.VERTICAL ) );
    expandBar = new ExpandBar( parent, getStyle() );
    expandBar.setData( RWT.MARKUP_ENABLED, markupEnabled ? Boolean.TRUE : null );
    if( hasCreateProperty( PROP_CONTEXT_MENU ) ) {
      Menu expandBarMenu = new Menu( expandBar );
      MenuItem expandBarMenuItem = new MenuItem( expandBarMenu, SWT.PUSH );
      expandBarMenuItem.addSelectionListener( new SelectionAdapter() {
        @Override
        public void widgetSelected( SelectionEvent event ) {
          String message = "You requested a context menu for the expand bar";
          MessageDialog.openInformation( expandBar.getShell(),
                                         "Information",
                                         message );
        }
      } );
      expandBarMenuItem.setText( "Expand Bar context menu item" );
      expandBar.setMenu( expandBarMenu );
    }
    if( hasCreateProperty( PROP_EXPAND_LISTENER ) ) {
      expandBar.addExpandListener( new ExpandListener() {
        @Override
        public void itemCollapsed( ExpandEvent event ) {
          int index = 0;
          int itemCount = expandBar.getItemCount();
          for( int i = 0; i < itemCount; i++ ) {
            if( expandBar.getItem( i ) == event.item ) {
              index = i;
            }
          }
          String message = "Expand item " + index + " collapsed!";
          MessageDialog.openInformation( expandBar.getShell(),
                                         "Information",
                                         message );
        }

        @Override
        public void itemExpanded( ExpandEvent event ) {
          int index = 0;
          int itemCount = expandBar.getItemCount();
          for( int i = 0; i < itemCount; i++ ) {
            if( expandBar.getItem( i ) == event.item ) {
              index = i;
            }
          }
          String message = "Expand item " + index + " expanded!";
          MessageDialog.openInformation( expandBar.getShell(),
                                         "Information",
                                         message );
        }
      } );
    }
    Display display = expandBar.getDisplay();
    Composite composite = new Composite( expandBar, SWT.NONE );
    composite.setLayout( new GridLayout() );
    new Button( composite, SWT.PUSH ).setText( "SWT.PUSH" );
    new Button( composite, SWT.RADIO ).setText( "SWT.RADIO" );
    new Button( composite, SWT.CHECK ).setText( "SWT.CHECK" );
    new Button( composite, SWT.TOGGLE ).setText( "SWT.TOGGLE" );
    ExpandItem item = new ExpandItem( expandBar, SWT.NONE, 0 );
    item.setText( "What is your <br/><b>favorite</b> button?" );
    item.setHeight( composite.computeSize( SWT.DEFAULT, SWT.DEFAULT ).y );
    item.setControl( composite );
    item.setImage( Util.loadImage( display, "resources/newfolder_wiz.gif" ) );
    composite = new Composite( expandBar, SWT.NONE );
    composite.setLayout( new GridLayout( 2, false ) );
    Image image = display.getSystemImage( SWT.ICON_ERROR );
    new Label( composite, SWT.NONE ).setImage( image );
    new Label( composite, SWT.NONE ).setText( "SWT.ICON_ERROR" );
    image = display.getSystemImage( SWT.ICON_INFORMATION );
    new Label( composite, SWT.NONE ).setImage( image );
    new Label( composite, SWT.NONE ).setText( "SWT.ICON_INFORMATION" );
    image = display.getSystemImage( SWT.ICON_WARNING );
    new Label( composite, SWT.NONE ).setImage( image );
    new Label( composite, SWT.NONE ).setText( "SWT.ICON_WARNING" );
    image = display.getSystemImage( SWT.ICON_QUESTION );
    new Label( composite, SWT.NONE ).setImage( image );
    new Label( composite, SWT.NONE ).setText( "SWT.ICON_QUESTION" );
    item = new ExpandItem( expandBar, SWT.NONE, 1 );
    item.setText( "What is your favorite icon?" );
    item.setHeight( composite.computeSize( SWT.DEFAULT, SWT.DEFAULT ).y );
    item.setControl( composite );
    item.setImage( Util.loadImage( display, "resources/newprj_wiz.gif" ) );
    item.setExpanded( true );
    expandBar.computeSize( SWT.DEFAULT, SWT.DEFAULT );
    registerControl( expandBar );
    if( checkControl( spinner ) ) {
      expandBar.setSpacing( spinner.getSelection() );
    }
  }

  private Spinner createSpacingControl( Composite parent ) {
    Composite composite = new Composite( parent, SWT.NONE );
    composite.setLayout( new GridLayout( 3, false ) );
    Label label = new Label( composite, SWT.NONE );
    label.setText( "Spacing" );
    final Spinner spinner = new Spinner( composite, SWT.BORDER );
    spinner.setSelection( 4 );
    spinner.setMinimum( 0 );
    spinner.setMaximum( 20 );
    spinner.addModifyListener( new ModifyListener() {
      @Override
      public void modifyText( ModifyEvent event ) {
        int spacing = spinner.getSelection();
        expandBar.setSpacing( spacing );
      }
    } );
    return spinner;
  }

  private void createInsertItemButton( Composite parent ) {
    Button button = new Button( parent, SWT.PUSH );
    button.setText( "Insert ExpandItem before first item" );
    button.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected( SelectionEvent event ) {
        ExpandItem item = new ExpandItem( expandBar, SWT.NONE, 0 );
        item.setText( "ExpandItem text" );
        item.setImage( Util.loadImage( item.getDisplay(), "resources/newfile_wiz.gif" ) );
        item.setExpanded( false );
        createItemContent( item );
      }
    } );
  }

  private void createRemoveItemButton( Composite parent ) {
    Button button = new Button( parent, SWT.PUSH );
    button.setText( "Remove first ExpandItem" );
    button.addSelectionListener( new SelectionAdapter() {

      @Override
      public void widgetSelected( SelectionEvent event ) {
        ExpandItem item = expandBar.getItem( 0 );
        item.dispose();
      }
    } );
  }

  private void createItemContent( ExpandItem item ) {
    if( item.getControl() == null ) {
      ExpandBar bar = item.getParent();
      Text content = new Text( bar, SWT.WRAP | SWT.MULTI | SWT.READ_ONLY );
      String text = "This is the item's content";
      content.setText( text );
      item.setHeight( content.computeSize( SWT.DEFAULT, SWT.DEFAULT ).y );
      item.setControl( content );
    }
  }

  private Button createMarkupButton() {
    final Button button = new Button( styleComp, SWT.CHECK );
    button.setText( "Enable Markup" );
    button.setSelection( markupEnabled );
    button.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected( final SelectionEvent event ) {
        markupEnabled = button.getSelection();
        createNew();
      }
    } );
    return button;
  }

}
