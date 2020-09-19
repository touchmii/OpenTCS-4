/*******************************************************************************
 * Copyright (c) 2007, 2012 Innoopract Informationssysteme GmbH and others.
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
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Text;


public class LinkTab extends ExampleTab {

  private static final String PROP_CONTEXT_MENU = "contextMenu";

  private Link link1;
  private Link customLink;
  private Link fixedSizeLink;

  public LinkTab() {
    super( "Link" );
  }

  @Override
  protected void createStyleControls( final Composite parent ) {
    createStyleButton( "BORDER", SWT.BORDER );
    createVisibilityButton();
    createEnablementButton();
    createFgColorButton();
    createBgColorButton();
    createBgImageButton();
    createFontChooser();
    createCustomLinkControl( parent );
    createPropertyCheckbox( "Add Context Menu On First Link",
                            PROP_CONTEXT_MENU );
  }

  @Override
  protected void createExampleControls( final Composite parent ) {
    parent.setLayout( new GridLayout() );
    int style = getStyle();
    link1 = new Link( parent, style );
    link1.setText( "Lorem <a>ipsum</a> dolor <a>sit amet</a>" );
    link1.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected( final SelectionEvent event ) {
        String msg = "Link widget selected, text=" + event.text;
        MessageDialog.openInformation( getShell(), "Information", msg );
      }
    } );
    if( hasCreateProperty( PROP_CONTEXT_MENU ) ) {
      Menu linkMenu = new Menu( link1 );
      MenuItem linkMenuItem = new MenuItem( linkMenu, SWT.PUSH );
      linkMenuItem.addSelectionListener( new SelectionAdapter() {
        @Override
        public void widgetSelected( final SelectionEvent event ) {
          String message = "You requested a context menu for the Link";
          MessageDialog.openInformation( link1.getShell(),
                                         "Information",
                                         message );
       }
      } );
      linkMenuItem.setText( "Link context menu item" );
      link1.setMenu( linkMenu );
    }
    Link link2 = new Link( parent, style );
    link2.setText( "Link without href" );
    Link link3 = new Link( parent, style );
    link3.setText( "<a>Link with one href \"&&<>\\\"</a>" );
    customLink = new Link( parent, style );
    customLink.setText( "Custom link, use com.lvsrobot.webclient.controls to your right to change" );
    customLink.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected( final SelectionEvent event ) {
        String msg = "Link widget selected, text=" + event.text;
        MessageDialog.openInformation( getShell(), "Information", msg );
      }
    } );
    fixedSizeLink = new Link( parent, style );
    fixedSizeLink.setText( "Fixed size Link with some very long text <a>that should be wrapped.</a>" );
    fixedSizeLink.setLayoutData( new GridData( 100, 100 ) );
    registerControl( link1 );
    registerControl( link2 );
    registerControl( link3 );
    registerControl( customLink );
    registerControl( fixedSizeLink );
  }

  private void createCustomLinkControl( final Composite parent ) {
    Composite composite = new Composite( parent, SWT.NONE );
    composite.setLayout( new GridLayout( 3, false ) );
    Label lblText = new Label( composite, SWT.NONE );
    lblText.setText( "Text" );
    final Text txtText = new Text( composite, SWT.BORDER );
    Button btnChange = new Button( composite, SWT.PUSH );
    btnChange.setText( "Change" );
    btnChange.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected( final SelectionEvent event ) {
        customLink.setText( txtText.getText() );
        customLink.getParent().layout();
      }
    } );

  }
}
