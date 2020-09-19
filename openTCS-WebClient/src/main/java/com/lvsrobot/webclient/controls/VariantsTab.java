/*******************************************************************************
 * Copyright (c) 2008, 2013 Innoopract Informationssysteme GmbH and others.
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
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;


public class VariantsTab extends ExampleTab {

  private static final String BUTTON_IMAGE_PATH = "resources/button-image.gif";

  private static final String[] VARIANTS_SPECIAL = new String[] {
    "none",
    "special-red",
    "special-blue"
  };

  private Combo variantsCombo;

  private Button myButton;
  private Combo myCombo;
  private Label myLabel;
  private Link myLink;
  private Text myText;
  private List myList;
  private Tree myTree;

  public VariantsTab() {
    super( "Variants" );
  }

  @Override
  protected void createStyleControls( Composite parent ) {
    createStyleButton( "BORDER", SWT.BORDER );
    createStyleButton( "FLAT", SWT.FLAT );
    createVisibilityButton();
    createEnablementButton();
    createFgColorButton();
    createBgColorButton();
    createFontChooser();
    String text = "Custom Variant:";
    variantsCombo = createVariantsCombo( text );
  }

  @Override
  protected void createExampleControls( final Composite parent ) {
    parent.setLayout( new GridLayout( 1, false ) );
    Label label = new Label( parent, SWT.NONE );
    label.setText( "Use custom theme to see the effect of widget variants." );

    // myButton
    int style = getStyle();
    myButton = new Button( parent, style | SWT.PUSH );
    myButton.setText( "Push Button" );
    myButton.setImage( Util.loadImage( myButton.getDisplay(), BUTTON_IMAGE_PATH ) );
    myButton.setData( RWT.CUSTOM_VARIANT, getVariant() );
    registerControl( myButton );

    // myCombo
    myCombo = new Combo( parent, style );
    myCombo.setItems( new String[] { "Item 1", "Item 2", "Item 3" } );
    myCombo.setData( RWT.CUSTOM_VARIANT, getVariant() );
    registerControl( myCombo );

    // myLabel
    myLabel = new Label( parent, style );
    myLabel.setLayoutData( new GridData( GridData.FILL_HORIZONTAL ) );
    myLabel.setText( "Customized Label" );
    myLabel.setData( RWT.CUSTOM_VARIANT, getVariant() );
    registerControl( myLabel );

    // myLink
    myLink = new Link( parent, style );
    myLink.setLayoutData( new GridData( GridData.FILL_HORIZONTAL ) );
    myLink.setText( "Customized <a>Link</a>" );
    myLink.setData( RWT.CUSTOM_VARIANT, getVariant() );
    registerControl( myLink );

    // myText
    myText = new Text( parent, style );
    myText.setLayoutData( new GridData( GridData.FILL_HORIZONTAL ) );
    myText.setText( "Customized Text" );
    myText.setData( RWT.CUSTOM_VARIANT, getVariant() );
    registerControl( myText );

    // myList
    myList = new List( parent, style );
    myList.setLayoutData( new GridData( GridData.FILL_HORIZONTAL ) );
    myList.add( "List Item 1" );
    myList.add( "List Item 2" );
    myList.add( "List Item 3" );
    myList.setData( RWT.CUSTOM_VARIANT, getVariant() );
    registerControl( myList );

    // myTree
    myTree = new Tree( parent, style );
    myTree.setLayoutData( new GridData( GridData.FILL_HORIZONTAL ) );
    for( int i = 0; i < 3; i++ ) {
      TreeItem item = new TreeItem( myTree, SWT.NONE );
      item.setText( "Node_" + ( i + 1 ) );
      if( i < 1 ) {
        TreeItem subitem = new TreeItem( item, SWT.NONE );
        subitem.setText( "Subnode_" + ( i + 1 ) );
      }
    }
    myTree.setData( RWT.CUSTOM_VARIANT, getVariant() );
    registerControl( myTree );

    // myShell
    Button myShellButton = new Button( parent, style | SWT.PUSH );
    myShellButton.setText( "Open customized Shell" );
    myShellButton.addSelectionListener( new SelectionAdapter() {

      @Override
      public void widgetSelected( SelectionEvent e ) {
        Shell myShell = new Shell( parent.getShell(), SWT.CLOSE | SWT.APPLICATION_MODAL );
        myShell.setText( "My Shell" );
        myShell.setSize( 200, 150 );
        myShell.setData( RWT.CUSTOM_VARIANT, getVariant() );
        myShell.open();
      }
    } );
  }

  protected Combo createVariantsCombo( String text ) {
    Composite group = new Composite( styleComp, SWT.NONE );
    group.setLayout( new GridLayout( 2, false ) );
    new Label( group, SWT.NONE ).setText( text );
    final Combo combo = new Combo( group, SWT.READ_ONLY );
    combo.setItems( VARIANTS_SPECIAL );
    combo.select( 0 );
    combo.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected( SelectionEvent e ) {
        setCustomVariant( getVariant() );
      }
      @Override
      public void widgetDefaultSelected( SelectionEvent evt ) {
        setCustomVariant( getVariant() );
      }
    } );
    return combo;
  }

  private void setCustomVariant( String variant ) {
    myButton.setData( RWT.CUSTOM_VARIANT, variant );
    myCombo.setData( RWT.CUSTOM_VARIANT, variant );
    myLabel.setData( RWT.CUSTOM_VARIANT, variant );
    myLink.setData( RWT.CUSTOM_VARIANT, variant );
    myText.setData( RWT.CUSTOM_VARIANT, variant );
    myList.setData( RWT.CUSTOM_VARIANT, variant );
    myTree.setData( RWT.CUSTOM_VARIANT, variant );
  }

  private String getVariant() {
    String selection = null;
    if( checkControl( variantsCombo ) ) {
      int index = variantsCombo.getSelectionIndex();
      if( index > 0 ) {
        selection = variantsCombo.getItem( index );
      }
    }
    return selection;
  }
}
