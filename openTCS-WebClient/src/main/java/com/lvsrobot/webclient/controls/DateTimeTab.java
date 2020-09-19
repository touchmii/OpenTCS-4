/*******************************************************************************
 * Copyright (c) 2008, 2017 Innoopract Informationssysteme GmbH and others.
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

import java.util.Calendar;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;


class DateTimeTab extends ExampleTab {

  private static final String PROP_CONTEXT_MENU = "contextMenu";
  private static final String PROP_SELECTION_LISTENER = "selectionListener";

  private DateTime dateTime;
  private Group group1, group2, group3;

  DateTimeTab() {
    super( "DateTime" );
    setDefaultStyle( SWT.BORDER | SWT.DATE | SWT.MEDIUM );
  }

  @Override
  protected void createStyleControls( Composite parent ) {
    group1 = new Group( styleComp, SWT.SHADOW_IN );
    group1.setText( "Type" );
    group1.setLayout( new RowLayout( SWT.VERTICAL ) );
    createStyleButton( group1, "DATE", SWT.DATE, SWT.RADIO, true );
    createStyleButton( group1, "TIME", SWT.TIME, SWT.RADIO, false );
    createStyleButton( group1, "CALENDAR", SWT.CALENDAR, SWT.RADIO, false );
    group2 = new Group( styleComp, SWT.SHADOW_IN );
    group2.setText( "Details" );
    group2.setLayout( new RowLayout( SWT.VERTICAL ) );
    createStyleButton( group2, "SHORT", SWT.SHORT, SWT.RADIO, false );
    createStyleButton( group2, "MEDIUM", SWT.MEDIUM, SWT.RADIO, true );
    createStyleButton( group2, "LONG", SWT.LONG, SWT.RADIO, false );
    createStyleButton( "DROP_DOWN", SWT.DROP_DOWN, false );
    createStyleButton( "BORDER", SWT.BORDER, true );
    createOrientationButton();
    createVisibilityButton();
    createEnablementButton();
    createFontChooser();
    createFgColorButton();
    createBgColorButton();
    createPropertyCheckbox( "Add Context Menu", PROP_CONTEXT_MENU );
    createPropertyCheckbox( "Add Selection Listener", PROP_SELECTION_LISTENER );
    group3 = new Group( parent, SWT.NONE );
    group3.setText( "Min/Max Limit" );
    group3.setLayout( new GridLayout( 3, false ) );
    createMinMaxLimit( group3 );
  }

  @Override
  protected void createExampleControls( Composite parent ) {
    parent.setLayout( new RowLayout( SWT.VERTICAL ) );
    int style = getStyle() | getStyle( group1 ) | getStyle( group2 );
    dateTime = new DateTime( parent, style );
    if( hasCreateProperty( PROP_CONTEXT_MENU ) ) {
      Menu dateTimeMenu = new Menu( dateTime );
      MenuItem dateTimeMenuItem = new MenuItem( dateTimeMenu, SWT.PUSH );
      dateTimeMenuItem.addSelectionListener( new SelectionAdapter() {

        @Override
        public void widgetSelected( SelectionEvent event ) {
          String message = "You requested a context menu for the DateTime";
          MessageDialog.openInformation( dateTime.getShell(), "Information", message );
        }
      } );
      dateTimeMenuItem.setText( "DateTime context menu item" );
      dateTime.setMenu( dateTimeMenu );
    }
    if( hasCreateProperty( PROP_SELECTION_LISTENER ) ) {
      dateTime.addSelectionListener( new SelectionListener() {

        @Override
        public void widgetSelected( SelectionEvent event ) {
          String message = "DateTime WidgetSelected! Selected date: "
                         + dateTime.getDay()
                         + "/" + ( dateTime.getMonth() + 1 )
                         + "/" + dateTime.getYear()
                         + " " + dateTime.getHours()
                         + ":" + dateTime.getMinutes()
                         + ":" + dateTime.getSeconds();
          log( message );
        }

        @Override
        public void widgetDefaultSelected( SelectionEvent event ) {
          log( "DateTime WidgetDefaultSelected!" );
        }
      } );
    }
    registerControl( dateTime );
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
      public void widgetSelected( SelectionEvent event ) {
        createNew();
      }
    } );
    button.setData( "style", new Integer( style ) );
    button.setSelection( checked );
    return button;
  }

  private void createMinMaxLimit( Composite parent ) {
    Label minLabel = new Label(parent, SWT.NONE);
    minLabel.setText( "Minimum" );
    final DateTime minDate = new DateTime( parent, SWT.DATE | SWT.MEDIUM | SWT.BORDER );
    final DateTime minTime = new DateTime( parent, SWT.TIME | SWT.SHORT | SWT.BORDER );
    Label maxLabel = new Label(parent, SWT.NONE);
    maxLabel.setText( "Maximum" );
    final DateTime maxDate = new DateTime( parent, SWT.DATE | SWT.MEDIUM | SWT.BORDER );
    final DateTime maxTime = new DateTime( parent, SWT.TIME | SWT.SHORT | SWT.BORDER );
    Button clearButton = new Button( group3, SWT.PUSH );
    clearButton.setText( "Clear" );
    clearButton.addListener( SWT.Selection, new Listener() {
      @Override
      public void handleEvent( Event event ) {
        dateTime.setMinimum( null );
        dateTime.setMaximum( null );
      }
    } );
    Button applyButton = new Button( group3, SWT.PUSH );
    applyButton.setText( "Apply" );
    applyButton.addListener( SWT.Selection, new Listener() {
      @Override
      public void handleEvent( Event event ) {
        Calendar limit = Calendar.getInstance();
        limit.set( minDate.getYear(),
                   minDate.getMonth(),
                   minDate.getDay(),
                   minTime.getHours(),
                   minTime.getMinutes(),
                   minTime.getSeconds() );
        dateTime.setMinimum( limit.getTime() );
        limit.set( maxDate.getYear(),
                   maxDate.getMonth(),
                   maxDate.getDay(),
                   maxTime.getHours(),
                   maxTime.getMinutes(),
                   maxTime.getSeconds() );
        dateTime.setMaximum( limit.getTime() );
      }
    } );
  }

  protected int getStyle( Composite comp ) {
    int result = SWT.NONE;
    if( checkControl( comp ) ) {
      Control[] ctrls = comp.getChildren();
      if( ctrls.length != 0 ) {
        for( int i = 0; i < ctrls.length; i++ ) {
          if( ctrls[ i ] instanceof Button ) {
            Button button = ( Button )ctrls[ i ];
            if( button.getSelection() ) {
              Object data = button.getData( "style" );
              if( data != null && data instanceof Integer ) {
                int style = ( ( Integer )data ).intValue();
                result |= style;
              }
            }
          }
        }
      }
    }
    return result;
  }

}
