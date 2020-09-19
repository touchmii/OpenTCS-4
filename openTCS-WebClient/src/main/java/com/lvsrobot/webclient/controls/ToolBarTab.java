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

import org.eclipse.rap.rwt.RWT;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;


public class ToolBarTab extends ExampleTab {

  private ToolBar toolBar;
  private ToolItem pushItem1;
  private ToolItem pushItem2;
  private ToolItem pushItem3;
  private ToolItem checkItem;
  private ToolItem radioItem1;
  private ToolItem radioItem2;
  private ToolItem dropDownItem;
  private int count = 0;

  public ToolBarTab() {
    super( "ToolBar" );
  }

  @Override
  protected void createStyleControls( Composite parent ) {
    createStyleButton( "HORIZONTAL", SWT.HORIZONTAL );
    createStyleButton( "VERTICAL", SWT.VERTICAL );
    createStyleButton( "RIGHT", SWT.RIGHT );
    createStyleButton( "BORDER", SWT.BORDER );
    createStyleButton( "FLAT", SWT.FLAT );
    createStyleButton( "NO_RADIO_GROUP", SWT.NO_RADIO_GROUP );
    createOrientationButton();
    createVisibilityButton();
    createEnablementButton();
    createFgColorButton();
    createBgColorButton();
    createBgImageButton();
    createFontChooser();
    createNewItemButton();
    createBadgeComposite( parent );
  }

  private void createNewItemButton() {
    Group group = new Group( styleComp, SWT.NONE );
    group.setLayout( new GridLayout( 2, false ) );
    group.setText( "New Item" );
    Label label = new Label( group, SWT.NONE );
    label.setLayoutData( new GridData( SWT.LEFT, SWT.CENTER, false, false ) );
    label.setText( "Index:" );
    final Text index = new Text( group, SWT.SINGLE | SWT.LEAD | SWT.BORDER );
    index.setLayoutData( new GridData( SWT.FILL, SWT.CENTER, true, false ) );
    index.setText( "0" );
    Button addItemButton = new Button( group, SWT.PUSH );
    GridData gridData = new GridData( SWT.BEGINNING, SWT.CENTER, false, false );
    gridData.horizontalSpan = 2;
    addItemButton.setLayoutData( gridData );
    addItemButton.setText( "Add Item" );
    addItemButton.addSelectionListener( new SelectionAdapter() {

      @Override
      public void widgetSelected( SelectionEvent e ) {
        int newIndex = Integer.parseInt( index.getText() );
        ToolItem toolItem = new ToolItem( toolBar, SWT.RADIO, newIndex );
        toolItem.setText( "Item" );
      }
    } );
  }

  @Override
  protected void createExampleControls( Composite parent ) {
    parent.setLayout( new RowLayout() );
    Image imageNewFile = loadImage( "resources/newfile_wiz.gif" );
    Image imagenewFolder = loadImage( "resources/newfolder_wiz.gif" );
    Image imageNewProj = loadImage( "resources/newprj_wiz.gif" );
    Image imageSearch = loadImage( "resources/search_src.gif" );
    toolBar = new ToolBar( parent, getStyle() );
    addContextMenu( toolBar );
    registerControl( toolBar );
    pushItem1 = new ToolItem( toolBar, SWT.PUSH );
    pushItem1.setText( "new" );
    pushItem1.setImage( imageNewFile );
    pushItem2 = new ToolItem( toolBar, SWT.PUSH );
    pushItem2.setText( "open" );
    pushItem2.setEnabled( false );
    pushItem2.setImage( imagenewFolder );
    new ToolItem( toolBar, SWT.SEPARATOR );
    dropDownItem = new ToolItem( toolBar, SWT.DROP_DOWN );
    dropDownItem.setText( "select" );
    dropDownItem.setImage( imageNewProj );
    new ToolItem( toolBar, SWT.SEPARATOR );

    // Text item
    ToolItem itemText = new ToolItem( toolBar, SWT.SEPARATOR );
    Text text = new Text( toolBar, SWT.BORDER );
    text.setText( "A Text Field" );
    itemText.setControl( text );
    itemText.setWidth( 100 );

    checkItem = new ToolItem( toolBar, SWT.CHECK );
    checkItem.setImage( imageSearch );
    checkItem.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected( final SelectionEvent event ) {
        log( "check changed" + event );
      }
    } );
    radioItem1 = new ToolItem( toolBar, SWT.RADIO );
    radioItem1.setImage( imageSearch );
    radioItem2 = new ToolItem( toolBar, SWT.RADIO );
    SelectionAdapter radioSelectionListener = new SelectionAdapter() {
      @Override
      public void widgetSelected( SelectionEvent event ) {
        log( "radio changed - " + event );
      }
    };
    radioItem2.setImage( imageSearch );
    radioItem1.addSelectionListener( radioSelectionListener);
    radioItem2.addSelectionListener( radioSelectionListener);
    final Menu dropDownMenu = new Menu( toolBar.getShell(), SWT.POP_UP );
    for( int i = 0; i < 5; i++ ) {
      MenuItem item = new MenuItem( dropDownMenu, SWT.PUSH );
      item.setText( "Item " + count++ );
    }
    dropDownItem.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected( final SelectionEvent event ) {
        if( event.detail == SWT.ARROW ) {
          Point point = toolBar.toDisplay( event.x, event.y );
          dropDownMenu.setOrientation( toolBar.getOrientation() );
          dropDownMenu.setLocation( point );
          dropDownMenu.setVisible( true );
        }
      }
    } );
    pushItem3 = new ToolItem( toolBar, SWT.PUSH );
    pushItem3.setText( "w/o <image>" );
  }

  private static void addContextMenu( ToolBar toolbar ) {
    Menu menu = new Menu( toolbar );
    MenuItem item = new MenuItem( menu, SWT.PUSH );
    item.setText( "ToolBar context menu item" );
    toolbar.setMenu( menu );
  }

  private void createBadgeComposite( Composite parent ) {
    Composite composite = new Composite( parent, SWT.NONE );
    composite.setLayout( new GridLayout( 3, false ) );
    new Label( composite, SWT.NONE ).setText( "Badge:" );
    final Text text = new Text( composite, SWT.BORDER );
    Listener setBadgeListener = new Listener() {
      @Override
      public void handleEvent( Event event ) {
        pushItem1.setData( RWT.BADGE, text.getText() );
        pushItem2.setData( RWT.BADGE, text.getText() );
        pushItem3.setData( RWT.BADGE, text.getText() );
      }
    };
    Button button = new Button( composite, SWT.PUSH );
    button.setText( "Set" );
    button.addListener( SWT.Selection, setBadgeListener );
    text.addListener( SWT.DefaultSelection, setBadgeListener );
  }

}
