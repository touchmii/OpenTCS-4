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

import org.eclipse.rap.rwt.RWT;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class LabelTab extends ExampleTab {

  private Label varSizeLabel;
  private Label fixedSizeLabel;
  private Image image1;
  private Image image2;
  private final String text1;
  private final String text2;
  private final String markup;
  private Image labelImage;
  private String labelText;
  private boolean markupEnabled;

  public LabelTab() {
    super( "Label" );
    text1 = "Some Text";
    text2 = "Some Other Text";
    markup = "<big><i>Some</i></big> <b>Other</b> <small>Text With Markup</small> - 2<sup>16</sup>";
    markupEnabled = true;
    labelImage = null;
    labelText = "A Label with text";
  }

  @Override
  protected void createStyleControls( Composite parent ) {
    image1 = loadImage( "resources/button-image.gif" );
    image2 = loadImage( "resources/newfile_wiz.gif" );
    createStyleButton( "BORDER", SWT.BORDER );
    createStyleButton( "SEPARATOR", SWT.SEPARATOR );
    createStyleButton( "HORIZONTAL", SWT.HORIZONTAL );
    createStyleButton( "VERTICAL", SWT.VERTICAL );
    createStyleButton( "SHADOW_IN", SWT.SHADOW_IN );
    createStyleButton( "SHADOW_OUT", SWT.SHADOW_OUT );
    createStyleButton( "SHADOW_NONE", SWT.SHADOW_NONE );
    createStyleButton( "LEFT", SWT.LEFT );
    createStyleButton( "CENTER", SWT.CENTER );
    createStyleButton( "RIGHT", SWT.RIGHT );
    createStyleButton( "WRAP", SWT.WRAP );
    createMarkupButton();
    createVisibilityButton();
    createEnablementButton();
    createFgColorButton();
    createBgColorButton();
    createBgImageButton();
    createFontChooser();
    createCursorCombo();
    createChangeTextControl( parent );
    createChangeToolTipControl( parent );
  }

  @Override
  protected void createExampleControls( Composite parent ) {
    int style = getStyle();
    RowLayout rowLayout = new RowLayout( SWT.VERTICAL );
    parent.setLayout( rowLayout );
    fixedSizeLabel = new Label( parent, style );
    fixedSizeLabel.setText( "Fixed size Label with some very long text\nand another line" );
    fixedSizeLabel.setLayoutData( new RowData( 100, 100 ) );
    new Label( parent, SWT.NONE );
    varSizeLabel = new Label( parent, style );
    varSizeLabel.setData( RWT.MARKUP_ENABLED, markupEnabled ? Boolean.TRUE : null );
    registerControl( varSizeLabel );
    registerControl( fixedSizeLabel );


    Composite buttons = new Composite( parent, SWT.NONE );
    buttons.setLayout( new FillLayout() );
    Button text1Button = new Button( buttons, SWT.PUSH );
    text1Button.setText( "Text 1" );
    text1Button.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected( final SelectionEvent e ) {
        labelText = text1;
        labelImage = null;
        updateLabel( varSizeLabel );
      }
    } );
    Button text2Button = new Button( buttons, SWT.PUSH );
    text2Button.setText( "Text 2" );
    text2Button.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected( final SelectionEvent e ) {
        labelText = text2;
        labelImage = null;
        updateLabel( varSizeLabel );
      }
    } );
    Button markupButton = new Button( buttons, SWT.PUSH );
    markupButton.setText( "Markup Text" );
    markupButton.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected( final SelectionEvent e ) {
        labelText = markup;
        labelImage = null;
        updateLabel( varSizeLabel );
      }
    } );
    Button image1Button = new Button( buttons, SWT.PUSH );
    image1Button.setText( "Image 1" );
    image1Button.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected( final SelectionEvent e ) {
        labelImage = image1;
        updateLabel( varSizeLabel );
      }
    } );
    Button image2Button = new Button( buttons, SWT.PUSH );
    image2Button.setText( "Image 2" );
    image2Button.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected( final SelectionEvent e ) {
        labelImage = image2;
        updateLabel( varSizeLabel );
      }
    } );
    updateLabel( varSizeLabel );
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

  private void createChangeTextControl( Composite parent ) {
    Composite composite = new Composite( parent, SWT.NONE );
    composite.setLayout( new GridLayout( 3, false ) );
    Label label = new Label( composite, SWT.NONE );
    label.setText( "Change text" );
    final Text text = new Text( composite, SWT.BORDER );
    Button button = new Button( composite, SWT.PUSH );
    button.setText( "Change" );
    button.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected( final SelectionEvent event ) {
        varSizeLabel.setText( text.getText() );
        text.setText( "" );
        varSizeLabel.pack();
      }
    } );
  }

  private void createChangeToolTipControl( Composite parent ) {
    Composite composite = new Composite( parent, SWT.NONE );
    composite.setLayout( new GridLayout( 3, false ) );
    Label label = new Label( composite, SWT.NONE );
    label.setText( "Change tooltip" );
    final Text text = new Text( composite, SWT.BORDER );
    Button button = new Button( composite, SWT.PUSH );
    button.setText( "Change" );
    button.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected( final SelectionEvent event ) {
        varSizeLabel.setToolTipText( text.getText() );
      }
    } );
  }

  private void updateLabel( Label label ) {
    if( labelImage != null ) {
      label.setImage( labelImage );
    } else {
      label.setText( labelText );
    }
    label.pack();
  }
}
