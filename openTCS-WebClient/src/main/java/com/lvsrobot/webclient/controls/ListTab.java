/*******************************************************************************
 * Copyright (c) 2007, 2015 Innoopract Informationssysteme GmbH and others.
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

import java.util.ArrayList;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.rap.rwt.RWT;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MenuAdapter;
import org.eclipse.swt.events.MenuEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;


public class ListTab extends ExampleTab {

  private static final java.util.List<String> ELEMENTS;

  static {
    ELEMENTS = new ArrayList<String>();
    String text = "A very long item that demonstrates horizontal scrolling in a List";
    ELEMENTS.add( text );
    text = "An item with a linebreak\n(converted to a whitespace)";
    ELEMENTS.add( text );
    text = "...and other control chars: \u0003 \t \u0004 \u000F";
    ELEMENTS.add( text );
    for( int i = 1; i <= 25; i++ ) {
      ELEMENTS.add( "Item " + i );
    }
  }

  private List list;
  private List list2;
  private ListViewer listViewer;
  private boolean markup;

  public ListTab() {
    super( "List" );
  }

  @Override
  protected void createStyleControls( Composite parent ) {
    createStyleButton( "BORDER", SWT.BORDER );
    createStyleButton( "SINGLE", SWT.SINGLE );
    createStyleButton( "MULTI", SWT.MULTI );
    createStyleButton( "H_SCROLL", SWT.H_SCROLL );
    createStyleButton( "V_SCROLL", SWT.V_SCROLL );
    createOrientationButton();
    createVisibilityButton();
    createEnablementButton();
    createMarkupButton();
    createFgColorButton();
    createBgColorButton();
    createBgImageButton();
    createFontChooser();
    createCursorCombo();
    createSelectionButton();
    Group group = new Group( parent, SWT.NONE );
    group.setText( "Manipulate Right List" );
    group.setLayout( new GridLayout() );
    createAddItemsControls( group );
    createSetTopIndexControls( group );
    createGetTopIndexControls( group );
    createShowSelectionControls( group );
    createRemoveFirstItemButton( group );
    createSelectAllButton( group );
    createDeselectAllButton( group );
    createSelectButton( group );
    createDeselectButton( group );
    createSetSelectionButton( group );
  }

  @Override
  protected void createExampleControls( final Composite parent ) {
    parent.setLayout( new GridLayout( 2, true ) );
    int style = getStyle();

    list = new List( parent, style );
    list.setLayoutData( new GridData( GridData.FILL_BOTH ) );
    Menu menu = new Menu( list );
    MenuItem menuItem = new MenuItem( menu, SWT.PUSH );
    menuItem.setText( "Context menu item" );
    list.setMenu( menu );
    listViewer = new ListViewer( list );
    listViewer.setContentProvider( new ArrayContentProvider() );
    listViewer.setLabelProvider( new LabelProvider() );
    listViewer.setInput( ELEMENTS );
    list.addListener( SWT.DefaultSelection, new Listener() {
      @Override
      public void handleEvent( Event event ) {
        String item = list.getItem( list.getSelectionIndex() );
        String message = "Selected Item: " + item;
        MessageDialog.openInformation( getShell(), "Selection", message );
      }
    } );
    registerControl( list );

    // List 2
    list2 = new List( parent, style );
    list2.setData( RWT.MARKUP_ENABLED, new Boolean( markup ) );
    list2.add( "Item 0" );
    list2.add( "Item 1" );
    list2.add( "Item 2" );
    if( markup ) {
      list2.setData( RWT.CUSTOM_ITEM_HEIGHT, new Integer( 60 ) );
      list2.add( "<b>Some Markup Text</b><br/><i>This is italic</i>" );
      list2.add( "A real <a href='http://eclipse.org/rap'>link</a>" );
      list2.add( "This one opens <a href='http://eclipse.org/rap' target='_blank'>a new tab</a>" );
      list2.add( "This is a special <a href='value_of_href' target='_rwt'>RWT Hyperlink</a>" );
      list2.add( "This RWT Hyperlik <a href='http://eclipse.org/rap' "
               + "target='_rwt'>has an <i>URL</i></a>" );
      list2.addListener( SWT.Selection, new Listener() {
        @Override
        public void handleEvent( Event event ) {
          if( event.detail == RWT.HYPERLINK ) {
            log( "Clicked link \"" + event.text + "\"" );
          }
        }
      } );
    } else {
      createPopupMenu( parent.getShell(), list2 );
    }
    list2.setLayoutData( new GridData( GridData.FILL_BOTH ) );
    registerControl( list2 );

    // Code
    int separatorStyle = SWT.SEPARATOR | SWT.HORIZONTAL | SWT.SHADOW_OUT;
    Label separator = new Label( parent, separatorStyle );
    separator.setLayoutData( createGridDataWithSpan() );
    Label codeLabel = new Label( parent, SWT.WRAP );
    String codeLabelText
      = "Please note that the content of the left List is provided by a "
      + "ListViewer with JFace API.";
    codeLabel.setText( codeLabelText );
    codeLabel.setLayoutData( createGridDataWithSpan() );
    Link link = new Link( parent, SWT.NONE );
    link.setText( "See <a>example code</a>" );
    link.setLayoutData( createGridDataWithSpan() );
    link.addSelectionListener( new SelectionAdapter() {
      private final String code = getExampleCode();
      @Override
      public void widgetSelected( final SelectionEvent event ) {
        String title = "ListViewer Example Code";
        HtmlDialog dialog = new HtmlDialog( parent.getShell(), title, code );
        dialog.setSize( 550, 400 );
        dialog.open();
      }
    } );
  }

  private GridData createGridDataWithSpan() {
    GridData gridData = new GridData( GridData.FILL_HORIZONTAL );
    gridData.horizontalSpan = 2;
    return gridData;
  }

  private void createPopupMenu( final Shell parent, final List list ) {
    final Menu menu = new Menu( parent, SWT.POP_UP );
    String[] listItems = list.getItems();
    for( int i = 0; i < listItems.length; i++ ) {
      MenuItem item = new MenuItem( menu, SWT.PUSH );
      item.setText( listItems[ i ] );
    }
    menu.addMenuListener( new MenuAdapter() {
      @Override
      public void menuShown( MenuEvent e ) {
        MenuItem[] items = menu.getItems();
        for( int i = 0; i < items.length; i++ ) {
          MenuItem item = items[ i ];
          item.setEnabled( list.isSelected( i ) );
        }
      }
    } );
    list.setMenu( menu );
  }

  private void createSelectionButton() {
    Button button = new Button( styleComp, SWT.PUSH );
    button.setText( "Select first" );
    button.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected( SelectionEvent e ) {
        listViewer.setSelection( new StructuredSelection( ELEMENTS.get( 0 ) ) );
      }
    } );
  }
  protected Button createMarkupButton( ) {
    final Button button = new Button( styleComp, SWT.CHECK );
    button.setText( "Markup" );
    button.setSelection( markup );
    button.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected( SelectionEvent event ) {
        markup = button.getSelection();
        createNew();
      }
    } );
    return button;
  }

  private void createAddItemsControls( Composite parent ) {
    Composite composite = new Composite( parent, SWT.NONE );
    composite.setLayout( new GridLayout( 3, false ) );
    Label lblAddItem = new Label( composite, SWT.NONE );
    lblAddItem.setText( "Add" );
    final Text txtAddItem = new Text( composite, SWT.BORDER );
    txtAddItem.setLayoutData( new GridData( 50, SWT.DEFAULT) );
    Button btnAddItem = new Button( composite, SWT.PUSH );
    btnAddItem.setText( "Item(s)" );
    btnAddItem.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected( final SelectionEvent event ) {
        int count = -1;
        String[] listItems = list2.getItems();
        int existingItems = listItems.length;
        try {
          count = Integer.parseInt( txtAddItem.getText() );
        } catch( NumberFormatException e ) {
          //
        }
        if( count < 0 ) {
          String msg = "Invalid number of ListItems: " + txtAddItem.getText();
          MessageDialog.openInformation( getShell(), "Information", msg );
        } else {
          for( int i = 0; i < count; i++ ) {
            list2.add( "Item " + ( existingItems + i ) );
          }
        }
      }
    } );
  }

  private void createSetTopIndexControls( Composite parent ) {
    Composite composite = new Composite( parent, SWT.NONE );
    composite.setLayout( new GridLayout( 2, false ) );
    final Text txtTopIndex = new Text( composite, SWT.BORDER );
    txtTopIndex.setLayoutData( new GridData( 50, SWT.DEFAULT) );
    Button button = new Button( composite, SWT.PUSH );
    button.setText( "setTopIndex" );
    button.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected( final SelectionEvent event ) {
        try {
          int topIndex = Integer.parseInt( txtTopIndex.getText() );
          list2.setTopIndex( topIndex );
        } catch( NumberFormatException e ) {
          String msg = "Invalid number of topIndex: " + txtTopIndex.getText();
          MessageDialog.openInformation( getShell(), "Information", msg );
        }
      }
    } );
  }

  private void createGetTopIndexControls( Composite parent ) {
    Composite composite = new Composite( parent, SWT.NONE );
    composite.setLayout( new GridLayout( 2, false ) );
    final Text txtTopIndex = new Text( composite, SWT.BORDER );
    txtTopIndex.setLayoutData( new GridData( 50, SWT.DEFAULT) );
    txtTopIndex.setEditable( false );
    Button button = new Button( composite, SWT.PUSH );
    button.setText( "getTopIndex" );
    button.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected( final SelectionEvent event ) {
        int topIndex = list2.getTopIndex();
        txtTopIndex.setText( String.valueOf( topIndex ) );
      }
    } );
  }

  private void createShowSelectionControls( Composite parent ) {
    Composite composite = new Composite( parent, SWT.NONE );
    composite.setLayout( new GridLayout( 2, false ) );
    Button button = new Button( composite, SWT.PUSH );
    button.setText( "showSelection" );
    button.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected( final SelectionEvent event ) {
        list2.showSelection();
      }
    } );
  }

  private void createRemoveFirstItemButton( Composite parent ) {
    Button button = new Button( parent , SWT.PUSH );
    button.setText( "Remove First Item" );
    button.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected( SelectionEvent event ) {
        if( list2.getItemCount() > 0 ) {
          list2.remove( 0 );
        }
      }
    } );
  }

  private void createSelectAllButton( Composite parent ) {
    Button button = new Button( parent, SWT.PUSH );
    button.setText( "Select All" );
    button.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected( final SelectionEvent event ) {
        list2.selectAll();
      }
    } );
  }

  private void createDeselectAllButton( Composite parent ) {
    Button button = new Button( parent, SWT.PUSH );
    button.setText( "Deselect All" );
    button.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected( final SelectionEvent event ) {
        list2.deselectAll();
      }
    } );
  }

  private void createSelectButton( Composite parent ) {
    Button button = new Button( parent, SWT.PUSH );
    button.setText( "Select 100th item" );
    button.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected( final SelectionEvent event ) {
        if( list2.getItemCount() > 100 ) {
          list2.select( 100 );
        }
      }
    } );
  }

  private void createDeselectButton( Composite parent ) {
    Button button = new Button( parent, SWT.PUSH );
    button.setText( "Deselect second item" );
    button.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected( final SelectionEvent event ) {
        if( list2.getItemCount() > 1 ) {
          list2.deselect( 1 );
        }
      }
    } );
  }

  private void createSetSelectionButton( Composite parent ) {
    Button button = new Button( parent, SWT.PUSH );
    button.setText( "Set selection to first item" );
    button.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected( final SelectionEvent event ) {
        if( list2.getItemCount() > 0 ) {
          list2.setSelection( new int[] { 0 } );
        }
      }
    } );
  }

  private String getExampleCode() {
    String result
      = "<html><head></head></body>"
      + "<pre>"
      + "class ListContentProvider implements IStructuredContentProvider {\n"
      + "  public Object[] getElements( final Object inputElement ) {\n"
      + "    return ( ( java.util.List )inputElement ).toArray();\n"
      + "  }\n"
      + "}\n"
      + "...\n"
      + "java.util.List elements = ...\n"
      + "...\n"
      + "ListViewer viewer = new ListViewer( parent );\n"
      + "viewer.setContentProvider( new ListContentProvider() );\n"
      + "viewer.setLabelProvider( new LabelProvider() );\n"
      + "java.util.List input = new ArrayList();\n"
      + "... // populate list\n"
      + "viewer.setInput( input );\n"
      + "</pre>"
      + "</body>";
    return result;
  }
}
