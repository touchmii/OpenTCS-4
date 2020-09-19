/*******************************************************************************
 * Copyright (c) 2002, 2013 Innoopract Informationssysteme GmbH.
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
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;


class RowLayoutTab extends ExampleTab {

  private boolean propPrefSize;
  private boolean propWrap;

  public RowLayoutTab() {
    super( "RowLayout" );
  }

  @Override
  protected void createStyleControls( Composite parent ) {
    createStyleButton( "HORIZONTAL", SWT.HORIZONTAL );
    createStyleButton( "VERTICAL", SWT.VERTICAL );
    final Button prefSizeButton = createPropertyButton( "Preferred Size" );
    prefSizeButton.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected( final SelectionEvent event ) {
        propPrefSize = prefSizeButton.getSelection();
        createNew();
      }
    } );
    final Button wrapButton = createPropertyButton( "Wrap" );
    wrapButton.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected( SelectionEvent event ) {
        propWrap = wrapButton.getSelection();
        createNew();
      }
    } );
//    createPropertyButton( "pack" );
//    createPropertyButton( "justify" );
  }

  @Override
  protected void createExampleControls( Composite parent ) {
    int style = getStyle();
    GridLayout parentLayout = new GridLayout();
    parentLayout.marginWidth = 5;
    parent.setLayout( parentLayout );
    Composite comp = new Composite( parent, SWT.NONE );
    comp.setBackground( bgColors[ BG_COLOR_BROWN ] );
    RowLayout layout = new RowLayout( style );
    layout.wrap = propWrap;
    comp.setLayout( layout );
    Button b1 = new Button( comp, SWT.PUSH );
    b1.setText( "Button 1" );
    Button b2 = new Button( comp, SWT.PUSH );
    b2.setText( "Button 2" );
    Button b3 = new Button( comp, SWT.PUSH );
    b3.setText( "Button 3" );
    Label l1 = new Label( comp, SWT.BORDER );
    l1.setText( "Label" );
    Text t1 = new Text( comp, SWT.BORDER | SWT.SINGLE );
    t1.setText( "Lorem ipsum dolor sit amet" );
    createSomeImages( comp );
    createTabFolder( comp );
    new Text( comp, SWT.BORDER | SWT.SINGLE );
    if( propPrefSize ) {
      comp.setLayoutData( new GridData() );
    } else {
      comp.setLayoutData( new GridData( GridData.FILL_BOTH ) );
    }
    comp.layout();
    registerControl( comp );
  }

  private void createSomeImages( Composite parent ) {
    Image image1 = Util.loadImage( parent.getDisplay(), "icons/info.gif" );
    Image image2 = Util.loadImage( parent.getDisplay(), "icons/lockkey.gif" );
    Image image3 = Util.loadImage( parent.getDisplay(), "icons/test-100x50.png" );
    Image image4 = Util.loadImage( parent.getDisplay(), "icons/test-50x100.png" );
    Label l1 = new Label( parent, SWT.BORDER );
    l1.setImage( image1 );
    Label l2 = new Label( parent, SWT.BORDER );
    l2.setImage( image2 );
    Label l3 = new Label( parent, SWT.BORDER );
    l3.setImage( image3 );
    Label l4 = new Label( parent, SWT.BORDER );
    l4.setImage( image4 );
  }

  private TabFolder createTabFolder( Composite parent ) {
    TabFolder tf = new TabFolder( parent, SWT.BORDER );
    TabItem tab1 = new TabItem( tf, SWT.NONE );
    tab1.setText( "Tab1" );
    Label tla1 = new Label( tf, SWT.NONE );
    tla1.setText( "Content 1" );
    tab1.setControl( tla1 );
    TabItem tab2 = new TabItem( tf, SWT.NONE );
    tab2.setText( "Tab2" );
    Label tla2 = new Label( tf, SWT.NONE );
    tla2.setText( "Content 2\nwith two lines" );
    tab2.setControl( tla2 );
    TabItem tab3 = new TabItem( tf, SWT.NONE );
    tab3.setText( "Tab3" );
    Label tla3 = new Label( tf, SWT.NONE );
    tla3.setText( "Content 3 is somewhat longer" );
    tab3.setControl( tla3 );
    tf.setSelection( 0 );
    return tf;
  }
}
