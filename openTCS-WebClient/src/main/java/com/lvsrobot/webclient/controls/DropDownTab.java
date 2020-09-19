/*******************************************************************************
 * Copyright (c) 2014 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package com.lvsrobot.webclient.controls;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.widgets.DropDown;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;


public class DropDownTab extends ExampleTab {

  private String[] currentTexts = Names.VALUES;
  private String userText = "";
  private Text text;
  private DropDown dropdown;

  public DropDownTab() {
    super( "DropDown" );
  }

  @Override
  protected void createStyleControls( Composite parent ) {
    createSetVisibleItemCountSpinner( parent );
    createShowDropDownButton( parent );
  }

  @Override
  protected void createExampleControls( Composite parent ) {
    parent.setLayout( new GridLayout() );
    text = new Text( parent, SWT.BORDER );
    text.setLayoutData( new GridData( 300, SWT.DEFAULT ) );
    text.setMessage( "Names" );
    dropdown = new DropDown( text );
    dropdown.setData( RWT.MARKUP_ENABLED, Boolean.TRUE );
    dropdown.setItems( format( currentTexts, "" ) );
    addModifyListener();
    addSelectionListener();
    addDefaultSelectionListener();
  }

  private void createSetVisibleItemCountSpinner( Composite parent ) {
    Composite composite = new Composite( parent, SWT.NONE );
    GridLayout gridLayout = new GridLayout( 2, false );
    gridLayout.marginWidth = 0;
    composite.setLayout( gridLayout );
    Label label = new Label( composite, SWT.NONE );
    label.setText( "Visible Item Count" );
    final Spinner spinner = new Spinner( composite, SWT.BORDER );
    spinner.setSelection( dropdown.getVisibleItemCount() );
    spinner.setLayoutData( new GridData( 40, SWT.DEFAULT ) );
    spinner.addListener( SWT.Selection, new Listener() {
      public void handleEvent( Event event ) {
        dropdown.setVisibleItemCount( spinner.getSelection() );
      }
    } );
  }

  private void createShowDropDownButton( Composite parent ) {
    Button button = new Button( parent, SWT.PUSH );
    button.setText( "Show DropDown" );
    button.addListener( SWT.Selection, new Listener() {
      public void handleEvent( Event event ) {
        dropdown.setVisible( true );
      }
    } );
  }

  private void addModifyListener() {
    text.addListener( SWT.Modify, new Listener() {
      public void handleEvent( Event event ) {
        if( !Boolean.TRUE.equals( text.getData( "selecting" ) ) ) {
          userText = text.getText();
          if( userText.length() == 0 ) {
            currentTexts = Names.VALUES;
            dropdown.setItems( format( currentTexts, "" ) );
          } else {
            String searchStr = userText.toLowerCase();
            currentTexts = filter( Names.VALUES, searchStr, 10 );
            dropdown.setItems( format( currentTexts, searchStr ) );
            if( currentTexts.length > 10 ) {
              dropdown.setSelectionIndex( -1 );
            } else if( currentTexts.length == 1 ) {
              dropdown.setSelectionIndex( 0 );
            }
          }
          dropdown.setVisible( true );
        }
      }
    } );
  }

  private void addSelectionListener() {
    dropdown.addListener( SWT.Selection, new Listener() {
      public void handleEvent( Event event ) {
        if( event.index != -1 ) {
          text.setData( "selecting", Boolean.TRUE );
          text.setText( currentTexts[ event.index ] );
          text.setData( "selecting", Boolean.FALSE );
          text.selectAll();
        } else {
          text.setText( userText );
          text.setSelection( userText.length(), userText.length() );
          text.setFocus();
        }
      }
    } );
  }

  private void addDefaultSelectionListener() {
    dropdown.addListener( SWT.DefaultSelection, new Listener() {
      public void handleEvent( Event event ) {
        if( event.index != -1 ) {
          text.setText( currentTexts[ event.index ] );
          text.setSelection( event.text.length() );
          dropdown.setVisible( false );
        }
      }
    } );
  }

  private static String[] filter( String[] values, String text, int limit ) {
    List<String> result = new ArrayList<String>( limit );
    for( int i = 0; result.size() < limit && i < values.length; i++ ) {
      String item = values[ i ];
      if( item.toLowerCase().startsWith( text ) ) {
        result.add( item );
      }
    }
    return result.toArray( new String[ result.size() ] );
  }

  private static String[] format( String[] values, String text ) {
    String[] result = new String[ values.length ];
    for( int i = 0; i < values.length; i++ ) {
      String item = values[ i ];
      int length = text.length();
      result[ i ] = "<b>" + item.substring( 0, length ) + "</b>" + item.substring( length );
    }
    return result;
  }

}
