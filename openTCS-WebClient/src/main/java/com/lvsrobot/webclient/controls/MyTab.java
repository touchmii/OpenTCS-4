/*******************************************************************************
 * Copyright (c) 2002, 2019 Innoopract Informationssysteme GmbH and others.
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
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.*;
import org.opentcs.access.KernelServicePortal;
import org.opentcs.access.rmi.KernelServicePortalBuilder;
import org.opentcs.components.kernel.services.PlantModelService;
import org.opentcs.components.kernel.services.TCSObjectService;
import org.opentcs.components.kernel.services.TransportOrderService;
import org.opentcs.components.kernel.services.VehicleService;
import org.opentcs.data.order.TransportOrder;

import java.util.Iterator;
import java.util.List;


public class MyTab extends ExampleTab {

  private static final String PROP_SELECTION_LISTENER = "selectionListener";
  private static final String BUTTON_IMAGE_PATH = "resources/button-image.gif";

  private Image buttonImage;

  private boolean showImage;
  private boolean setGrayed;
  private boolean markupEnabled;

  private Button pushButton;
  private Button toggleButton;
  private Button arrowButton;
  private Button checkButton1;
  private Button checkButton2;
  private Button radioButton1;
  private Button radioButton2;
  private Button radioButton3;
  private Button defaultButton;
  private Text  modelName;



  public MyTab() {
    super( "Button" );
  }

  @Override
  protected void createStyleControls( Composite parent ) {
    createStyleButton( "BORDER", SWT.BORDER );
    createStyleButton( "FLAT", SWT.FLAT );
    createStyleButton( "LEFT", SWT.LEFT );
    createStyleButton( "CENTER", SWT.CENTER );
    createStyleButton( "RIGHT", SWT.RIGHT );
    createStyleButton( "UP", SWT.UP );
    createStyleButton( "DOWN", SWT.DOWN );
    createStyleButton( "WRAP", SWT.WRAP );
    createOrientationButton();
    createVisibilityButton();
    createEnablementButton();
    createImageButton( parent );
    createMarkupButton( parent );
    createGrayedButton( parent );
    createFgColorButton();
    createBgColorButton();
    createBgImageButton();
    createFontChooser();
    createCursorCombo();
    createBadgeComposite( parent );
    createPropertyCheckbox( "Add Selection Listener", PROP_SELECTION_LISTENER );
    Button button = createPropertyButton( "Toggle Button", SWT.PUSH );
    button.setToolTipText( "Remote control the toggle button" );
    button.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected( SelectionEvent event ) {
        toggleButton.setSelection( !toggleButton.getSelection() );
      }
    } );
  }

  @Override
  protected void createExampleControls( Composite parent ) {

    KernelServicePortal servicePortal = new KernelServicePortalBuilder().build();

    // Connect and log in with a kernel somewhere.
    servicePortal.login("127.0.0.1", 1099);
//  servicePortal.l

    // Get a reference to the plant model service...
    PlantModelService plantModelService = servicePortal.getPlantModelService();
    VehicleService vehicleService = servicePortal.getVehicleService();
    TransportOrderService transportOrderService = servicePortal.getTransportOrderService();
    TCSObjectService tcsObjectService = servicePortal.getTransportOrderService();
    // ...and find out the name of the currently loaded model.
    String modelNamee = plantModelService.getLoadedModelName();


    // Poll events, waiting up to a second if none are currently there.
    // This should be done periodically, and probably in a separate thread.
    List<Object> events = servicePortal.fetchEvents(1000);

    parent.setLayout( new GridLayout( 1, false ) );
    int style = getStyle();
    pushButton = new Button( parent, style | SWT.PUSH );
    pushButton.setData( RWT.MARKUP_ENABLED, markupEnabled ? Boolean.TRUE : null );
    pushButton.setText( markupEnabled ? "<b>Push</b> <i>Button</i>" : "Push\n Button" );
    updateButtonImage( pushButton );
    modelName = new Text(parent, style);
    modelName.setText("modlename:" + modelNamee);
    toggleButton = new Button( parent, style | SWT.TOGGLE );
    toggleButton.setText( "Toggle" );
    checkButton1 = new Button( parent, style | SWT.CHECK );
    checkButton1.setText( "Check" );
    checkButton2 = new Button( parent, style | SWT.CHECK );
    checkButton2.setText( "Check with image" );
    createButtonImage( parent.getDisplay() );
    checkButton2.setImage( buttonImage );
    radioButton1 = new Button( parent, style | SWT.RADIO );
    radioButton1.setText( "Radio 1" );
    radioButton2 = new Button( parent, style | SWT.RADIO );
    radioButton2.setText( "Radio 2" );
    radioButton3 = new Button( parent, style | SWT.RADIO );
    radioButton3.setText( "Radio 3" );
    arrowButton = new Button( parent, style | SWT.ARROW );
    arrowButton.setData( RWT.TOOLTIP_MARKUP_ENABLED, Boolean.TRUE );
    arrowButton.setToolTipText( getToolTipText() );
    if( hasCreateProperty( PROP_SELECTION_LISTENER ) ) {
      Listener listener = new Listener() {
        @Override
        public void handleEvent( Event event ) {
          log( event.toString() );
        }
      };
      pushButton.addListener( SWT.Selection, listener );
      toggleButton.addListener( SWT.Selection, listener );
      checkButton1.addListener( SWT.Selection, listener );
      checkButton2.addListener( SWT.Selection, listener );
      radioButton1.addListener( SWT.Selection, listener );
      radioButton2.addListener( SWT.Selection, listener );
      radioButton3.addListener( SWT.Selection, listener );
      arrowButton.addListener( SWT.Selection, listener );
    }
    registerControl( pushButton );
    registerControl( toggleButton );
    registerControl( checkButton1 );
    registerControl( checkButton2 );
    registerControl( radioButton1 );
    registerControl( radioButton2 );
    registerControl( radioButton3 );
    registerControl( arrowButton );
    // default button
    final Group group = new Group( parent, SWT.NONE );
    group.setLayoutData( new GridData( GridData.FILL_HORIZONTAL ) );
    group.setText( "Default Button" );
    group.setLayout( new RowLayout( SWT.HORIZONTAL ) );
    final Label label = new Label( group, SWT.NONE );
    label.setText( "Enter some text and press Return" );
    final Text text = new Text( group, SWT.BORDER | SWT.SINGLE );
    text.setLayoutData( new RowData( 100, SWT.DEFAULT ) );
    defaultButton = new Button( group, style | SWT.PUSH );
    defaultButton.setText( "Default Button" );
    defaultButton.getShell().setDefaultButton( defaultButton );
    defaultButton.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected( final SelectionEvent event ) {
        String message = "The text You entered: " + text.getText();
        MessageDialog.openInformation( group.getShell(),
                                       "Information",
                                       message );
      }
    } );

    // Set a context menu
    Menu menu = new Menu( parent );
    for( int i = 0; i < 5; i++ ) {
      MenuItem item = new MenuItem( menu, SWT.PUSH );
      item.setText( "Item " + ( i + 1 ) );
    }
    parent.setMenu( menu );
  }

  private String getToolTipText() {
    StringBuilder builder = new StringBuilder();
    builder.append( "<span style='color:yellow; font-weight:bold;'>" );
    builder.append( "This is tooltip with markup <br/> &#38; <br/> <i>additional line</i>" );
    builder.append( "</span>" );
    return builder.toString();
  }

  private void createButtonImage( Display display ) {
    if( buttonImage == null ) {
      buttonImage = Util.loadImage( display, BUTTON_IMAGE_PATH );
    }
  }

  private Button createMarkupButton( Composite parent ) {
    final Button button = new Button( parent, SWT.CHECK );
    button.setText( "Push Button with markup" );
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

  private void createImageButton( Composite parent ) {
    final Button imageButton = new Button( parent, SWT.CHECK );
    imageButton.setText( "Push Button with image" );
    imageButton.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected( final SelectionEvent event ) {
        showImage = imageButton.getSelection();
        updateButtonImage( pushButton );
        pushButton.getParent().layout();
      }
    } );
  }

  private void updateButtonImage( Button button ) {
    if( showImage ) {
      createButtonImage( button.getDisplay() );
      button.setImage( buttonImage );
    } else {
      button.setImage( null );
    }
  }

  private void createGrayedButton( Composite parent ) {
    final Button grayedButton = new Button( parent, SWT.CHECK );
    grayedButton.setText( "Grayed Check Buttons" );
    grayedButton.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected( final SelectionEvent event ) {
        setGrayed = grayedButton.getSelection();
        updateButtonGrayed();
      }
    } );
  }

  private void updateButtonGrayed( ) {
    Iterator<Control> iter = controls.iterator();
    while( iter.hasNext() ) {
      Button button = ( Button )iter.next();
      button.setGrayed( setGrayed );
    }
  }

  private void createBadgeComposite( Composite parent ) {
    Composite composite = new Composite( parent, SWT.NONE );
    composite.setLayout( new GridLayout( 3, false ) );
    new Label( composite, SWT.NONE ).setText( "Badge:" );
    final Text text = new Text( composite, SWT.BORDER );
    Listener setBadgeListener = new Listener() {
      @Override
      public void handleEvent( Event event ) {
        pushButton.setData( RWT.BADGE, text.getText() );
      }
    };
    Button button = new Button( composite, SWT.PUSH );
    button.setText( "Set" );
    button.addListener( SWT.Selection, setBadgeListener );
    text.addListener( SWT.DefaultSelection, setBadgeListener );
  }

}
