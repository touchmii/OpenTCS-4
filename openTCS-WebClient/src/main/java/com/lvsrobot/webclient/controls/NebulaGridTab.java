/*******************************************************************************
 * Copyright (c) 2014, 2020 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package com.lvsrobot.webclient.controls;

import java.util.Arrays;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.nebula.widgets.grid.Grid;
import org.eclipse.nebula.widgets.grid.GridColumn;
import org.eclipse.nebula.widgets.grid.GridColumnGroup;
import org.eclipse.nebula.widgets.grid.GridItem;
import org.eclipse.rap.rwt.RWT;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.TreeEvent;
import org.eclipse.swt.events.TreeListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;


public class NebulaGridTab extends ExampleTab {

  private static int COLUMN_COUNT = 5;
  private static int ROOT_ITEM_COUNT = 20;
  private static int SUB_ITEM_COUNT = 10;

  private Grid grid;
  private Image image;
  private boolean headerVisible = true;
  private boolean footerVisible = true;
  private boolean rowHeadersVisible = true;
  private boolean cellSelectionEnabled;
  public NebulaGridTab() {
    super( "Nebula Grid" );
    setDefaultStyle( SWT.CHECK | SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL );
  }

  @Override
  protected void createStyleControls( Composite parent ) {
    createStyleButton( "BORDER", SWT.BORDER );
    createStyleButton( "CHECK", SWT.CHECK, true );
    createStyleButton( "SINGLE", SWT.SINGLE, false );
    createStyleButton( "MULTI", SWT.MULTI, true );
    createStyleButton( "H_SCROLL", SWT.H_SCROLL, true );
    createStyleButton( "V_SCROLL", SWT.V_SCROLL, true );
    createVisibilityButton();
    createEnablementButton();
    createFgColorButton();
    createBgColorButton();
    createBgImageButton();
    createFontChooser();
    createAddRemoveItemButton( parent );
    createTopIndexButton( parent );
    createShowItemGroup( parent );
    createShowColumnGroup( parent );
    createTreeColumnButton( parent );
    createSetFooterSpanGroup( parent );
    createSetColumnSpanGroup( parent );
    createShowHeaderButton( parent );
    createShowFooterButton( parent );
    createShowRowHeadersButton( parent );
    createAutoHeightButton( parent );
    createWordWrapButton( parent );
    createHeaderWordWrapButton( parent );
    createQueryFocusItem( parent );
    createQueryFocusColumn( parent );
    createEnableCellSelection( parent );
    createSetCellSelection( parent );
    createAddCellsToSelection( parent );
    createSelectCellsInColumn( parent );
    createSelectCellsInColumnGroup( parent );
    createDeselectAllCells( parent );
    createGetCellsSelection( parent );
    createSetColumnOrder( parent );
  }

  @Override
  protected void createExampleControls( Composite parent ) {
    initializeImages();
    parent.setLayout( new GridLayout( 1, false ) );
    createGrid( parent );
    registerControl( grid );
  }

  private void initializeImages() {
    if( image == null ) {
      image = loadImage( "resources/shell.gif" );
    }
  }

  private void createGrid( Composite parent ) {
    grid = new Grid( parent, getStyle() );
    grid.setLayoutData( new GridData( SWT.FILL, SWT.FILL, true, true, 1, 20 ) );
    grid.setHeaderVisible( headerVisible );
    grid.setFooterVisible( footerVisible );
//    grid.setRowHeaderVisible( rowHeadersVisible, 50 );
    grid.setCellSelectionEnabled( cellSelectionEnabled );
    addGridListeners();
    createGridColumns();
    createGridItems();
    updateRowHeaders();
  }

  private void addGridListeners() {
    grid.addTreeListener( new TreeListener() {
      @Override
      public void treeExpanded( TreeEvent event ) {
        log( "grid treeExpanded: " + event );
        updateRowHeaders();
      }
      @Override
      public void treeCollapsed( TreeEvent event ) {
        log( "grid treeExpanded: " + event );
        updateRowHeaders();
      }
    } );
    grid.addSelectionListener( new SelectionListener() {
      @Override
      public void widgetSelected( SelectionEvent event ) {
        log( "grid widgetSelected: " + event );
      }

      @Override
      public void widgetDefaultSelected( SelectionEvent event ) {
        log( "grid widgetDefaultSelected: " + event );
      }
    } );
  }

  private void createGridColumns() {
    GridColumnGroup group = new GridColumnGroup( grid, SWT.TOGGLE );
    group.setText( "Column Group" );
    group.setImage( image );
    group.setHeaderFont( new Font( group.getDisplay(), "Verdana", 16, SWT.BOLD ) );
    for( int i = 0; i < COLUMN_COUNT; i++ ) {
      GridColumn column;
      if( i > 0 && i < 4 ) {
        column = new GridColumn( group, i == 1 ? SWT.CHECK : SWT.NONE );
      } else {
        column = new GridColumn( grid, i == 4 ? SWT.CHECK : SWT.NONE );
      }
      column.setData( RWT.TOOLTIP_MARKUP_ENABLED, Boolean.TRUE );
      column.setText( "Column " + i );
      column.setHeaderTooltip( "<span style='font-weight:bold'><i>Column " + i + "</i></span>" );
      column.setFooterText( "Footer " + i );
      column.setWidth( 200 );
      column.setMoveable( true );
      column.addSelectionListener( new SelectionListener() {

        @Override
        public void widgetSelected( SelectionEvent event ) {
          log( "column widgetSelected: " + event );
        }

        @Override
        public void widgetDefaultSelected( SelectionEvent event ) {
          log( "column widgetDefaultSelected: " + event );
        }
      } );
      switch( i ) {
        case 0:
          column.setImage( image );
          column.setSort( SWT.DOWN );
          column.setFooterImage( image );
          break;
        case 1:
          column.setWidth( 100 );
          column.setAlignment( SWT.CENTER );
          column.setImage( image );
          column.setHeaderFont( new Font( column.getDisplay(), "Comic Sans MS", 16, SWT.NORMAL ) );
          column.setFooterFont( new Font( column.getDisplay(), "Segoe Script", 16, SWT.NORMAL ) );
          column.setSummary( false );
          break;
        case 2:
          column.setMinimumWidth( 100 );
          column.setSummary( false );
          break;
        case 3:
          column.setAlignment( SWT.RIGHT );
          column.setDetail( false );
          break;
      }
    }
  }

  private void createGridItems() {
    for( int i = 0; i < ROOT_ITEM_COUNT; i++ ) {
      GridItem item = new GridItem( grid, SWT.NONE );
      item.setImage( image );
      int gridItemIndex = grid.indexOf( item );
      for( int k = 0; k < COLUMN_COUNT; k++ ) {
        if( k != 1 ) {
          item.setText( k, "Item (" + gridItemIndex + "." + k + ")" );
        }
      }
      if( gridItemIndex % 5 == 0 ) {
        grid.getItem( gridItemIndex ).setHeight( 2 * grid.getItemHeight() );
      }
      for( int j = 0; j < SUB_ITEM_COUNT; j++ ) {
        GridItem subitem = new GridItem( item, SWT.NONE );
        gridItemIndex = grid.indexOf( subitem );
        subitem.setImage( 2, image );
        for( int k = 0; k < COLUMN_COUNT; k++ ) {
          if( k != 1 ) {
            subitem.setText( k, "Subitem (" + gridItemIndex + "." + k + ")" );
          }
        }
        if( gridItemIndex % 5 == 0 ) {
          grid.getItem( gridItemIndex ).setHeight( 2 * grid.getItemHeight() );
        }
      }
    }
    grid.getItem( 0 ).setExpanded( true );
  }

  private void updateRowHeaders() {
    for( GridItem item : grid.getItems() ) {
      if( item != null && item.isVisible() ) {
        if( item.getParentItem() != null ) {
//          item.setHeaderImage( image );
        }
      }
    }
//    grid.getItem( 0 ).setHeaderText( "X" );
  }

  private void createAddRemoveItemButton( Composite parent ) {
    Composite composite = new Composite( parent, SWT.NONE );
    composite.setLayout( new GridLayout( 2, false ) );
    Button addButton = new Button( composite, SWT.PUSH );
    addButton.setLayoutData( new GridData( 100, SWT.DEFAULT ) );
    addButton.setText( "Add item" );
    addButton.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected( SelectionEvent event ) {
        GridItem selectedItem = grid.getSelectionCount() > 0 ? grid.getSelection()[ 0 ] : null;
        GridItem item;
        int gridItemIndex;
        if( selectedItem == null ) {
          item = new GridItem( grid, SWT.NONE );
          item.setImage( image );
          gridItemIndex = grid.indexOf( item );
          for( int k = 0; k < COLUMN_COUNT; k++ ) {
            item.setText( k, "Item (" + gridItemIndex + "." + k + ")" );
          }
        } else {
          item = new GridItem( selectedItem, SWT.NONE );
          item.setImage( 1, image );
          gridItemIndex = grid.indexOf( item );
          for( int k = 0; k < COLUMN_COUNT; k++ ) {
            item.setText( k, "Subitem (" + gridItemIndex + "." + k + ")" );
          }
        }
        updateItemsText( gridItemIndex + 1 );
      }
    } );
    Button removeButton = new Button( composite, SWT.PUSH );
    removeButton.setLayoutData( new GridData( 100, SWT.DEFAULT ) );
    removeButton.setText( "Remove item" );
    removeButton.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected( SelectionEvent event ) {
        GridItem selectedItem = grid.getSelectionCount() > 0 ? grid.getSelection()[ 0 ] : null;
        if( selectedItem != null ) {
          int selectedItemIndex = grid.indexOf( selectedItem );
          grid.remove( selectedItemIndex );
          updateItemsText( selectedItemIndex );
        }
      }
    } );
  }

  private void createTopIndexButton( Composite parent ) {
    Composite composite = new Composite( parent, SWT.NONE );
    composite.setLayout( new GridLayout( 3, false ) );
    Label topIndexLabel = new Label( composite, SWT.NONE );
    topIndexLabel.setLayoutData( new GridData( 100, SWT.DEFAULT ) );
    topIndexLabel.setText( "Top index" );
    final Text topIndexText = new Text( composite, SWT.BORDER );
    topIndexText.setLayoutData( new GridData( 50, SWT.DEFAULT ) );
    Button button = new Button( composite, SWT.PUSH );
    button.setLayoutData( new GridData( 100, SWT.DEFAULT ) );
    button.setText( "Change" );
    button.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected( SelectionEvent event ) {
        int topIndex = -1;
        try {
          topIndex = Integer.parseInt( topIndexText.getText() );
        } catch( NumberFormatException e ) {
        }
        grid.setTopIndex( topIndex );
      }
    } );
  }

  private void createShowItemGroup( Composite parent ) {
    Composite composite = new Composite( parent, SWT.NONE );
    composite.setLayout( new GridLayout( 3, false ) );
    Label showItemLabel = new Label( composite, SWT.NONE );
    showItemLabel.setLayoutData( new GridData( 100, SWT.DEFAULT ) );
    showItemLabel.setText( "Show item" );
    final Text showItemText = new Text( composite, SWT.BORDER );
    showItemText.setLayoutData( new GridData( 50, SWT.DEFAULT ) );
    Button button = new Button( composite, SWT.PUSH );
    button.setLayoutData( new GridData( 100, SWT.DEFAULT ) );
    button.setText( "Show" );
    button.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected( SelectionEvent event ) {
        int index = -1;
        try {
          index = Integer.parseInt( showItemText.getText() );
        } catch( NumberFormatException e ) {
        }
        if( index >= 0 && index < grid.getItemCount() ) {
          grid.showItem( grid.getItem( index ) );
        }
      }
    } );
  }

  private void createShowColumnGroup( Composite parent ) {
    Composite composite = new Composite( parent, SWT.NONE );
    composite.setLayout( new GridLayout( 3, false ) );
    Label showColumnLabel = new Label( composite, SWT.NONE );
    showColumnLabel.setLayoutData( new GridData( 100, SWT.DEFAULT ) );
    showColumnLabel.setText( "Show column" );
    final Text showColumnText = new Text( composite, SWT.BORDER );
    showColumnText.setLayoutData( new GridData( 50, SWT.DEFAULT ) );
    Button button = new Button( composite, SWT.PUSH );
    button.setLayoutData( new GridData( 100, SWT.DEFAULT ) );
    button.setText( "Show" );
    button.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected( SelectionEvent event ) {
        int index = -1;
        try {
          index = Integer.parseInt( showColumnText.getText() );
        } catch( NumberFormatException e ) {
        }
        if( index >= 0 && index < grid.getColumnCount() ) {
          grid.showColumn( grid.getColumn( index ) );
        }
      }
    } );
  }

  private void createTreeColumnButton( Composite parent ) {
    Composite composite = new Composite( parent, SWT.NONE );
    composite.setLayout( new GridLayout( 3, false ) );
    Label treeColumnIndexLabel = new Label( composite, SWT.NONE );
    treeColumnIndexLabel.setLayoutData( new GridData( 100, SWT.DEFAULT ) );
    treeColumnIndexLabel.setText( "Tree column" );
    final Text treeColumnIndexText = new Text( composite, SWT.BORDER );
    treeColumnIndexText.setLayoutData( new GridData( 50, SWT.DEFAULT ) );
    Button button = new Button( composite, SWT.PUSH );
    button.setLayoutData( new GridData( 100, SWT.DEFAULT ) );
    button.setText( "Change" );
    button.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected( SelectionEvent event ) {
        int treeColumnIndex = -1;
        try {
          treeColumnIndex = Integer.parseInt( treeColumnIndexText.getText() );
        } catch( NumberFormatException e ) {
        }
        if( treeColumnIndex >= 0 && treeColumnIndex < grid.getColumnCount() ) {
          grid.getColumn( treeColumnIndex ).setTree( true );
        }
      }
    } );
  }

  private void createSetFooterSpanGroup( Composite parent ) {
    Composite composite = new Composite( parent, SWT.NONE );
    composite.setLayout( new GridLayout( 3, false ) );
    final Text columnText = new Text( composite, SWT.BORDER );
    columnText.setLayoutData( new GridData( 80, SWT.DEFAULT ) );
    columnText.setMessage( "column" );
    final Text spanText = new Text( composite, SWT.BORDER );
    spanText.setLayoutData( new GridData( 80, SWT.DEFAULT ) );
    spanText.setMessage( "footerSpan" );
    final Button set = new Button( composite, SWT.PUSH );
    set.setText( "Set" );
    set.setLayoutData( new GridData( 67, SWT.DEFAULT ) );
    set.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected( SelectionEvent event ) {
        int index = -1;
        int span = -1;
        try {
          index = Integer.parseInt( columnText.getText() );
          span = Integer.parseInt( spanText.getText() );
        } catch( NumberFormatException e ) {
        }
        if( index >= 0 && index < grid.getColumnCount() && span > 0 ) {
          grid.getColumn( index ).setData( "footerSpan", Integer.valueOf( span ) );
        }
      }
    } );
  }

  private void createSetColumnSpanGroup( Composite parent ) {
    Composite composite = new Composite( parent, SWT.NONE );
    composite.setLayout( new GridLayout( 3, false ) );
    Label label = new Label( composite, SWT.NONE );
    label.setLayoutData( new GridData( SWT.BEGINNING, SWT.BEGINNING, true, false, 3, 1 ) );
    label.setText( "Column span on selected item" );
    final Text columnText = new Text( composite, SWT.BORDER );
    columnText.setLayoutData( new GridData( 80, SWT.DEFAULT ) );
    columnText.setMessage( "column" );
    final Text spanText = new Text( composite, SWT.BORDER );
    spanText.setLayoutData( new GridData( 90, SWT.DEFAULT ) );
    spanText.setMessage( "columnSpan" );
    final Button set = new Button( composite, SWT.PUSH );
    set.setText( "Set" );
    set.setLayoutData( new GridData( 67, SWT.DEFAULT ) );
    set.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected( SelectionEvent event ) {
        int index = -1;
        int span = -1;
        try {
          index = Integer.parseInt( columnText.getText() );
          span = Integer.parseInt( spanText.getText() );
        } catch( NumberFormatException e ) {
        }
        GridItem[] selection = grid.getSelection();
        if( selection.length > 0 && index >= 0 && index < grid.getColumnCount() && span >= 0 ) {
          selection[ 0 ].setColumnSpan( index, span );
        }
      }
    } );
  }

  private void createShowHeaderButton( Composite parent ) {
    final Button button = new Button( parent, SWT.CHECK );
    button.setText( "Show header" );
    button.setSelection( headerVisible );
    button.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected( SelectionEvent event ) {
        headerVisible = button.getSelection();
        grid.setHeaderVisible( headerVisible );
      }
    } );
  }

  private void createShowFooterButton( Composite parent ) {
    final Button button = new Button( parent, SWT.CHECK );
    button.setText( "Show footer" );
    button.setSelection( footerVisible );
    button.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected( SelectionEvent event ) {
        footerVisible = button.getSelection();
        grid.setFooterVisible( footerVisible );
      }
    } );
  }

  private void createShowRowHeadersButton( Composite parent ) {
    final Button button = new Button( parent, SWT.CHECK );
    button.setText( "Show row headers" );
    button.setSelection( rowHeadersVisible );
    button.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected( SelectionEvent event ) {
        rowHeadersVisible = button.getSelection();
//        grid.setRowHeaderVisible( rowHeadersVisible, 50 );
      }
    } );
  }

  private void createAutoHeightButton( Composite parent ) {
    final Button button = new Button( parent, SWT.CHECK );
    button.setText( "Item/Header/Footer auto height" );
    button.setSelection( false );
    button.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected( SelectionEvent event ) {
        grid.setAutoHeight( button.getSelection() );
      }
    } );
  }

  private void createWordWrapButton( Composite parent ) {
    final Button button = new Button( parent, SWT.CHECK );
    button.setText( "Word wrap cells text" );
    button.setSelection( false );
    button.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected( SelectionEvent event ) {
        for( GridColumn column : grid.getColumns() ) {
          column.setWordWrap( button.getSelection() );
        }
      }
    } );
  }

  private void createHeaderWordWrapButton( Composite parent ) {
    final Button button = new Button( parent, SWT.CHECK );
    button.setText( "Word wrap headers text" );
    button.setSelection( false );
    button.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected( SelectionEvent event ) {
        for( GridColumn column : grid.getColumns() ) {
          column.setHeaderWordWrap( button.getSelection() );
        }
        for( GridColumnGroup group : grid.getColumnGroups() ) {
          group.setHeaderWordWrap( button.getSelection() );
        }
      }
    } );
  }

  private void createQueryFocusItem( Composite parent ) {
    Button button = new Button( parent, SWT.PUSH );
    button.setText( "Query focusItem" );
    button.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected( SelectionEvent e ) {
        Shell shell = grid.getShell();
        String msg = "Current focusItem: " + grid.getFocusItem();
        MessageDialog.openInformation( shell, "Information", msg );
      }
    } );
  }

  private void createQueryFocusColumn( Composite parent ) {
    Button button = new Button( parent, SWT.PUSH );
    button.setText( "Query focusColumn" );
    button.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected( SelectionEvent e ) {
        Shell shell = grid.getShell();
        String msg = "Current focusColumn: " + grid.getFocusColumn();
        MessageDialog.openInformation( shell, "Information", msg );
      }
    } );
  }

  private void createEnableCellSelection( Composite parent ) {
    final Button button = new Button( parent, SWT.CHECK );
    button.setText( "Enable cell selection" );
    button.setSelection( cellSelectionEnabled );
    button.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected( SelectionEvent event ) {
        cellSelectionEnabled = button.getSelection();
        grid.setCellSelectionEnabled( cellSelectionEnabled );
      }
    } );
  }

  private void createSetCellSelection( Composite parent ) {
    Button button = new Button( parent, SWT.PUSH );
    button.setText( "Set cell selection" );
    button.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected( SelectionEvent e ) {
        grid.setCellSelection( new Point[] { new Point( 0, 3 ), new Point( 1, 5 ), new Point( 4, 0 ) } );
      }
    } );
  }

  private void createAddCellsToSelection( Composite parent ) {
    Button button = new Button( parent, SWT.PUSH );
    button.setText( "Add to cell selection" );
    button.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected( SelectionEvent e ) {
        grid.selectCells( new Point[] { new Point( 0, 5 ), new Point( 2, 7 ) } );
      }
    } );
  }

  private void createSelectCellsInColumn( Composite parent ) {
    Button button = new Button( parent, SWT.PUSH );
    button.setText( "Select all cells in column 4" );
    button.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected( SelectionEvent e ) {
        grid.selectColumn( 4 );
      }
    } );
  }

  private void createSelectCellsInColumnGroup( Composite parent ) {
    Button button = new Button( parent, SWT.PUSH );
    button.setText( "Select all cells in column group" );
    button.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected( SelectionEvent e ) {
        grid.selectColumnGroup( 0 );
      }
    } );
  }

  private void createDeselectAllCells( Composite parent ) {
    Button button = new Button( parent, SWT.PUSH );
    button.setText( "Deselect all cells" );
    button.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected( SelectionEvent e ) {
        grid.deselectAllCells();
      }
    } );
  }

  private void createGetCellsSelection( Composite parent ) {
    Button button = new Button( parent, SWT.PUSH );
    button.setText( "Get cell selection" );
    button.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected( SelectionEvent e ) {
        log( "Selected cells: " + Arrays.toString( grid.getCellSelection() ) );
      }
    } );
  }

  private void createSetColumnOrder( Composite parent ) {
    Button button = new Button( parent, SWT.PUSH );
    button.setText( "Change column order" );
    button.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected( SelectionEvent e ) {
        grid.setColumnOrder( new int[] { 4, 2, 1, 3, 0 } );
      }
    } );
  }

  private void updateItemsText( int startIndex ) {
    for( int index = startIndex; index < grid.getItemCount(); index++ ) {
      GridItem item = grid.getItem( index );
      String text = item.getText();
      text = text.substring( 0, text.indexOf( "(" ) + 1 );
      for( int k = 0; k < COLUMN_COUNT; k++ ) {
        item.setText( k, text + grid.indexOf( item ) + "." + k + ")" );
      }
    }
  }

}
