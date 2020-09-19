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

import org.eclipse.rap.rwt.RWT;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;


public class TabFolderTab extends ExampleTab {

  protected static final int MAX_ITEMS = 3;

  private boolean onDemandContent;
  private TabFolder folder;
  private Button[] tabRadios;
  private int counter;

  public TabFolderTab() {
    super( "TabFolder" );
  }

  @Override
  protected void createStyleControls( Composite parent ) {
    createStyleButton( "BORDER", SWT.BORDER );
    createStyleButton( "TOP", SWT.TOP );
    createStyleButton( "BOTTOM", SWT.BOTTOM );
    createOrientationButton();
    createVisibilityButton();
    createEnablementButton();
    createOnDemandButton( parent );
    createFgColorButton();
    createBgColorButton();
    createFontChooser();
    tabRadios = new Button[ MAX_ITEMS ];
    for( int i = 0; i < MAX_ITEMS; i++ ) {
      tabRadios[ i ] = createPropertyButton( "Select TabItem at index " + i, SWT.RADIO );
      final int itemIndex = i;
      tabRadios[ i ].addSelectionListener( new SelectionAdapter() {
        @Override
        public void widgetSelected( SelectionEvent event ) {
          Button radio = ( Button )event.getSource();
          if( radio.getSelection() ) {
            folder.setSelection( itemIndex );
          }
        }
      } );
    }
    tabRadios[ 0 ].setSelection( true );
    createChangeContentButton( parent );
    createInsertItemButton( parent );
    createDisposeSelectedItemButton( parent );
    createDisposeFirstItemButton( parent );
    createSetFirstItemBadge( parent );
  }

  @Override
  protected void createExampleControls( Composite parent ) {
    parent.setLayout( new FillLayout() );
    counter = 0;
    folder = new TabFolder( parent, getStyle() );
    folder.setToolTipText( "Tab Folder Tooltip" );
    folder.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected( SelectionEvent event ) {
        TabItem item = ( TabItem )event.item;
        if( tabRadios != null ) {
          int index = item.getParent().indexOf( item );
          for( int i = 0; i < MAX_ITEMS; i++ ) {
            if( checkControl( tabRadios[ i ] ) ) {
              tabRadios[ i ].setSelection( index == i );
            }
          }
        }
        if( item.getData( "id" ) == null ) {
          item.setData( "id", Integer.valueOf( counter ) );
        }
        createItemContent( item );
      }
    } );
    for( int i = 0; i < MAX_ITEMS; i++ ) {
      TabItem item = new TabItem( folder, SWT.NONE );
      item.setData( "id", Integer.valueOf( counter ) );
      item.setText( "TabItem " + counter );
      item.setImage( loadImage( "resources/newfolder_wiz.gif" ) );
      item.setToolTipText( "Tooltip for TabItem " + counter );
      counter++;
      if( !onDemandContent ) {
        createItemContent( item );
      }
    }
    registerControl( folder );
  }

  private void createOnDemandButton( Composite parent ) {
    Button button = new Button( parent, SWT.CHECK );
    button.setText( "Create Item Content on Demand" );
    button.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected( SelectionEvent event ) {
        Button button = ( Button )event.widget;
        onDemandContent = button.getSelection();
        createNew();
      }
    } );
  }

  private void createChangeContentButton( Composite parent ) {
    Button btnChangeContent = new Button( parent, SWT.PUSH );
    btnChangeContent.setText( "Change Content for Selected Item" );
    btnChangeContent.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected( SelectionEvent event ) {
        TabItem item = folder.getSelection()[ 0 ];
        Label content = new Label( folder, SWT.NONE );
        int index = folder.indexOf( item );
        content.setText( "Alternate content for tab item " + index );
        item.setControl( content );
      }
    } );
  }

  private void createInsertItemButton( Composite parent ) {
    Button button = new Button( parent, SWT.PUSH );
    button.setText( "Insert item before first item" );
    button.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected( SelectionEvent event ) {
        TabItem item = new TabItem( folder, SWT.NONE, 0 );
        item.setData( "id", Integer.valueOf( counter ) );
        item.setText( "TabItem " + counter );
        item.setToolTipText( "Tooltip for TabItem " + counter );
        counter++;
        if( !onDemandContent ) {
          createItemContent( item );
        }
      }
    } );
  }

  private void createDisposeSelectedItemButton( Composite parent ) {
    Button button = new Button( parent, SWT.PUSH );
    button.setText( "Dispose selected item" );
    button.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected( SelectionEvent event ) {
        TabItem[] selection = folder.getSelection();
        if( selection.length > 0 ) {
          selection[ 0 ].dispose();
        }
      }
    } );
  }

  private void createDisposeFirstItemButton( Composite parent ) {
    Button button = new Button( parent, SWT.PUSH );
    button.setText( "Dispose first item" );
    button.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected( SelectionEvent event ) {
        if( folder.getItemCount() > 0 ) {
          folder.getItem( 0 ).dispose();
        }
      }
    } );
  }

  private void createSetFirstItemBadge( Composite parent ) {
    Composite composite = new Composite( parent, SWT.NONE );
    composite.setLayout( new GridLayout( 3, false ) );
    new Label( composite, SWT.NONE ).setText( "Badge:" );
    final Text text = new Text( composite, SWT.BORDER );
    Listener setBadgeListener = new Listener() {
      @Override
      public void handleEvent( Event event ) {
        if( folder.getItemCount() > 0 ) {
          folder.getItem( 0 ).setData( RWT.BADGE, text.getText() );
        }
      }
    };
    Button button = new Button( composite, SWT.PUSH );
    button.setText( "Set" );
    button.addListener( SWT.Selection, setBadgeListener );
    text.addListener( SWT.DefaultSelection, setBadgeListener );
  }

  private void createItemContent( TabItem item ) {
    if( item.getControl() == null ) {
      TabFolder folder = item.getParent();
      Text content = new Text( folder, SWT.WRAP | SWT.MULTI | SWT.READ_ONLY );
      String text = "This is the content for TabItem " + item.getData( "id" );
      if( onDemandContent ) {
        text += "\nIt was created on demand, when the item was selected "
             +  "for the first time through user interaction.";
      }
      content.setText( text );
      item.setControl( content );
    }
  }
}
