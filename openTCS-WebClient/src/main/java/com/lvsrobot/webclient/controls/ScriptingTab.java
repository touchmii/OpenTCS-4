/*******************************************************************************
 * Copyright (c) 2013, 2014 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package com.lvsrobot.webclient.controls;

import java.lang.reflect.Field;
import java.util.Arrays;

import org.eclipse.rap.rwt.scripting.ClientListener;
import org.eclipse.rap.rwt.widgets.WidgetUtil;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Widget;


public class ScriptingTab extends ExampleTab {

  private static final String[] WIDGETS = new String[]{ "Text", "Button", "Label", "Canvas" };
  private static final int WIDGET_TEXT = 0;
  private static final int WIDGET_BUTTON = 1;
  private static final int WIDGET_LABEL = 2;
  private static final int WIDGET_CANVAS = 3;
  private static final String[] EVENT_TYPES = new String[] {
    "KeyDown",
    "KeyUp",
    "MouseDown",
    "MouseUp",
    "MouseMove",
    "MouseEnter",
    "MouseExit",
    "MouseDoubleClick",
    "Paint",
    "Selection",
    "DefaultSelection",
    "FocusIn",
    "FocusOut",
    "Show",
    "Hide",
    "Modify",
    "Verify",
    "MouseWheel"
  };

  private Button button;
  private String buttonScript;
  private int[] buttonEventTypeIndex;
  private Text text;
  private String textScript;
  private int[] textEventTypeIndex;
  private Label label;
  private String labelScript;
  private int[] labelEventTypeIndex;
  private Canvas canvas;
  private String canvasScript;
  private int[] canvasEventTypeIndex;
  private Text script;
  private Combo widgetCombo;
  private List typeList;


  public ScriptingTab() {
    super( "Scripting" );
    WidgetUtil.registerDataKeys( "text", "button", "label", "canvas" );
    initListener();
  }

  @Override
  protected void createStyleControls( Composite parent ) {
    parent.setLayout( new GridLayout( 1, false ) );
    widgetCombo = new Combo( parent, SWT.DROP_DOWN | SWT.READ_ONLY );
    widgetCombo.setItems( WIDGETS );
    widgetCombo.select( 1 );
    widgetCombo.addListener( SWT.Selection, new Listener() {
      public void handleEvent( Event event ) {
        fillStyleControls();
      }
    } );
    script = new Text( parent, SWT.MULTI | SWT.BORDER );
    script.setLayoutData( new GridData( 500, 300 ) );
    script.setFont( new Font( script.getDisplay(), "Courier New", 12, SWT.NORMAL ) );
    script.addListener( SWT.Modify, new Listener() {
      public void handleEvent( Event event ) {
        storeScript();
      }
    } );
    typeList = new List( parent, SWT.BORDER | SWT.V_SCROLL | SWT.MULTI );
    typeList.setItems( EVENT_TYPES );
    typeList.addListener( SWT.Selection, new Listener() {
      public void handleEvent( Event event ) {
        storeEventType();
      }
    } );
    typeList.setLayoutData( new GridData( SWT.DEFAULT, 100 ) );
    Button set = new Button( parent, SWT.PUSH );
    set.setText( "set" );
    set.addListener( SWT.Selection, new Listener() {
      public void handleEvent( Event event ) {
        createNew();
      }
    } );
    fillStyleControls();
  }

  private void connectWidgets() {
    Widget[] widgets = new Widget[]{ text, button, label, canvas };
    for( Widget widget : widgets ) {
      widget.setData( "text", WidgetUtil.getId( text ) );
      widget.setData( "label", WidgetUtil.getId( label ) );
      widget.setData( "button", WidgetUtil.getId( button ) );
      widget.setData( "canvs", WidgetUtil.getId( canvas ) );
    }
  }

  @Override
  protected void createExampleControls( Composite parent ) {
    parent.setLayout( new GridLayout( 1, false ) );
    text = new Text( parent, SWT.BORDER | SWT.SINGLE );
    text.setText( "Text" );
    text.setLayoutData( new GridData( 150, 25 ) );
    attachTextListener();
    button = new Button( parent, getStyle() | SWT.PUSH );
    button.setText( "Button" );
    button.setLayoutData( new GridData( 150, 25 ) );
    attachButtonListener();
    label = new Label( parent, SWT.BORDER );
    label.setLayoutData( new GridData( 150, 25 ) );
    label.setText( "Label" );
    attachLabelListener();
    canvas = new Canvas( parent, SWT.BORDER );
    canvas.setLayoutData( new GridData( 300, 300 ) );
    attachCanvasListener();
    connectWidgets();
  }

  private void fillStyleControls() {
    switch( widgetCombo.getSelectionIndex() ) {
      case WIDGET_TEXT:
        script.setText( textScript );
        typeList.setSelection( textEventTypeIndex );
      break;
      case WIDGET_BUTTON:
        script.setText( buttonScript );
        typeList.setSelection( buttonEventTypeIndex );
      break;
      case WIDGET_LABEL:
        script.setText( labelScript );
        typeList.setSelection( labelEventTypeIndex );
      break;
      case WIDGET_CANVAS:
        script.setText( canvasScript );
        typeList.setSelection( canvasEventTypeIndex );
      break;
    }
    typeList.showSelection();
  }

  private void storeEventType() {
    switch( widgetCombo.getSelectionIndex() ) {
      case WIDGET_TEXT:
        textEventTypeIndex = typeList.getSelectionIndices();
      break;
      case WIDGET_BUTTON:
        buttonEventTypeIndex = typeList.getSelectionIndices();
      break;
      case WIDGET_LABEL:
        labelEventTypeIndex = typeList.getSelectionIndices();
      break;
      case WIDGET_CANVAS:
        canvasEventTypeIndex = typeList.getSelectionIndices();
      break;
    }
  }

  private void storeScript() {
    switch( widgetCombo.getSelectionIndex() ) {
      case WIDGET_TEXT:
        textScript = script.getText();
      break;
      case WIDGET_BUTTON:
        buttonScript = script.getText();
      break;
      case WIDGET_LABEL:
        labelScript = script.getText();
      break;
      case WIDGET_CANVAS:
        canvasScript = script.getText();
      break;
    }
  }

  private void attachCanvasListener() {
    ClientListener listener = new ClientListener( canvasScript );
    for( int eventTypeIndex : canvasEventTypeIndex ) {
      canvas.addListener( getEventType( eventTypeIndex ), listener );
    }
  }

  private void attachLabelListener() {
    ClientListener listener = new ClientListener( labelScript );
    for( int eventTypeIndex : labelEventTypeIndex ) {
      label.addListener( getEventType( eventTypeIndex ), listener );
    }
  }

  private void attachButtonListener() {
    ClientListener listener = new ClientListener( buttonScript );
    for( int eventTypeIndex : buttonEventTypeIndex ) {
      button.addListener( getEventType( eventTypeIndex ), listener );
    }
  }

  private void attachTextListener() {
    ClientListener listener = new ClientListener( textScript );
    for( int eventTypeIndex : textEventTypeIndex ) {
      text.addListener( getEventType( eventTypeIndex ), listener );
    }
  }

  private int getEventType( int typeIndex ) {
    String typeString = EVENT_TYPES[ typeIndex ];
    try {
      Field field = SWT.class.getField( typeString );
      return field.getInt( null );
    } catch( SecurityException e ) {
      throw new RuntimeException( e );
    } catch( NoSuchFieldException e ) {
      throw new RuntimeException( e );
    } catch( IllegalArgumentException e ) {
      throw new RuntimeException( e );
    } catch( IllegalAccessException e ) {
      throw new RuntimeException( e );
    }
  }

  private void initListener() {
    initTextListener();
    initButtonListener();
    initLabelListener();
    initCanvasListener();
  }

  private void initTextListener() {
    textScript =   "var handleEvent = function( event ) {\n"
                 + "  event.doit = isNumber( event.character ) || event.text.length == 0;\n"
                 + "};\n"
                 + "\n"
                 + "var isNumber = function( character ) {\n"
                 + "  var charCode = character.charCodeAt( 0 );\n"
                 + "  return charCode >=48 && charCode <= 57;\n"
                 + "};";
    textEventTypeIndex = new int[] {
      Arrays.asList( EVENT_TYPES ).indexOf( "Verify" )
    };
  }

  private void initButtonListener() {
    buttonScript =   "var handleEvent = function( event ) {\n"
                   + "  var widget = event.widget;\n"
                   + "  var label = rap.getObject( widget.getData( \"label\" ) );\n"
                   + "  label.setBackground( [ randomByte(), randomByte(), randomByte() ] );\n"
                   + "};\n"
                   + "\n"
                   + "var randomByte = function() {\n"
                   + "  return Math.round( Math.random() * 255 );\n"
                   + "}\n";
    buttonEventTypeIndex = new int[] {
      Arrays.asList( EVENT_TYPES ).indexOf( "MouseDown" )
    };
  }

  private void initCanvasListener() {
    canvasScript =   "var points = [];\n"
                   + "\n"
                   + "var handleEvent = function( event ) {\n"
                   + "  switch( event.type ) {\n"
                   + "    case SWT.MouseMove:\n"
                   + "      points.push( [ event.x, event.y ] );\n"
                   + "      event.widget.redraw();\n"
                   + "    break;\n"
                   + "    case SWT.Paint:\n"
                   + "      if( points.length > 1 ) {\n"
                   + "        event.gc.lineWidth = 4;\n"
                   + "        event.gc.beginPath();\n"
                   + "        event.gc.moveTo( points[ 0 ][ 0 ], points[ 0 ][ 1 ] );\n"
                   + "        for( var i = 1; i < points.length; i++ ) {\n"
                   + "          event.gc.lineTo( points[ i ][ 0 ] , points[ i ][ 1 ] );\n"
                   + "        }\n"
                   + "        event.gc.stroke();\n"
                   + "      }\n"
                   + "    break;\n"
                   + "  }\n"
                   + "};";
    canvasEventTypeIndex  = new int[] {
      Arrays.asList( EVENT_TYPES ).indexOf( "Paint" ),
      Arrays.asList( EVENT_TYPES ).indexOf( "MouseMove" )
    };
  }

  private void initLabelListener() {
    labelScript =   "function handleEvent( event ){\n"
                  + "  var color = event.type == SWT.MouseEnter ? [ 255, 0, 0 ] : null;\n"
                  + "  event.widget.setForeground( color );\n"
                  + "}\n";
    labelEventTypeIndex = new int[] {
      Arrays.asList( EVENT_TYPES ).indexOf( "MouseEnter" ),
      Arrays.asList( EVENT_TYPES ).indexOf( "MouseExit" )
    };
  }


}
