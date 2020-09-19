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

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.CoolBar;
import org.eclipse.swt.widgets.CoolItem;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;


public class CoolBarTab extends ExampleTab {

  private static final String TOOL_ITEM_IMAGE_PATH = "resources/newfile_wiz.gif";

  private CoolBar coolBar;

  public CoolBarTab() {
    super( "CoolBar" );
  }

  @Override
  protected void createStyleControls( final Composite parent ) {
    // TODO [rst] Allow for vertical CoolBars
//  createStyleButton( "HORIZONTAL", SWT.HORIZONTAL );
//  createStyleButton( "VERTICAL", SWT.VERTICAL );
    createStyleButton( "BORDER", SWT.BORDER );
    createStyleButton( "FLAT", SWT.FLAT );
    createOrientationButton();
    createVisibilityButton();
    createEnablementButton();
    createLockedButton( parent );
    createBgColorButton();
    createBgImageButton();
  }

  @Override
  protected void createExampleControls( final Composite parent ) {
    int style = getStyle();
    coolBar = new CoolBar( parent, style );
    createItem( coolBar, 1 );
    createItem( coolBar, 2 );
    createItem( coolBar, 3 );
    coolBar.setLocation( 5, 5 );
    coolBar.setSize( coolBar.computeSize( parent.getSize().x - 10, SWT.DEFAULT ) );
    registerControl( coolBar );
    final ControlAdapter controlListener = new ControlAdapter() {
      @Override
      public void controlResized( final ControlEvent e ) {
        coolBar.setSize( coolBar.computeSize( parent.getSize().x - 10, SWT.DEFAULT ) );
      }
    };
    parent.addControlListener( controlListener );
    coolBar.addDisposeListener( new DisposeListener() {
      @Override
      public void widgetDisposed( final DisposeEvent event ) {
        parent.removeControlListener( controlListener );
      }
    } );
  }

  private CoolItem createItem( final CoolBar coolBar, int id ) {
    ToolBar toolBar = createToolBar( coolBar, id );
    toolBar.pack();
    Point size = toolBar.getSize();
    // TODO [fappel]: check whether size adjustment is really needed or a bug .
    size = new Point( size.x + ( int )( size.x * 0.1 ), size.y );
    CoolItem item = new CoolItem( coolBar, SWT.NONE );
    item.setControl( toolBar );
    Point preferred = item.computeSize( size.x, size.y );
    item.setPreferredSize( preferred );
    return item;
  }

  private ToolBar createToolBar( final Composite parent, final int id ) {
    int style = ( getStyle() & ( SWT.HORIZONTAL | SWT.VERTICAL ) );
    ToolBar toolBar = new ToolBar( parent, style );
    ToolItem item1 = new ToolItem( toolBar, SWT.PUSH );
    item1.setText( "new" + id );
    Image image = Util.loadImage( parent.getDisplay(), TOOL_ITEM_IMAGE_PATH );
    item1.setImage( image );
    ToolItem item2 = new ToolItem( toolBar, SWT.PUSH );
    item2.setText( "open" + id );
    ToolItem item3 = new ToolItem( toolBar, SWT.PUSH );
    item3.setText( "save as" + id );
    new ToolItem( toolBar, SWT.SEPARATOR );
    ToolItem item4 = new ToolItem( toolBar, SWT.PUSH );
    item4.setText( "print" + id );
    return toolBar;
  }

  private void createLockedButton( final Composite parent ) {
    final Button button = new Button( parent, SWT.CHECK );
    button.setText( "Locked" );
    button.setSelection( coolBar.getLocked() );
    button.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected( final SelectionEvent event ) {
        coolBar.setLocked( button.getSelection() );
      }
    } );
  }

}
