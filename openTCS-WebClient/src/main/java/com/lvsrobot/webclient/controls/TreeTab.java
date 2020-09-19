/*******************************************************************************
 * Copyright (c) 2002, 2018 Innoopract Informationssysteme GmbH and others.
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

import java.text.MessageFormat;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.rap.rwt.service.ServerPushSession;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.TreeEvent;
import org.eclipse.swt.events.TreeListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Item;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;


public class TreeTab extends ExampleTab {

  private final static int INITIAL_COLUMNS = 5;
  private static final int INITIAL_ITEMS = 15;

  private boolean headerVisible;
  private boolean linesVisible;
  private boolean updateVirtualItemsDelayed;
  private Tree tree;
  private boolean showImages;
  private Image treeImage;
  private boolean columnImages;
  private Image columnImage;
  private boolean columnsMoveable;
  private boolean addMouseListener;

  public TreeTab() {
    super( "Tree" );
    headerVisible = true;
  }

  @Override
  protected void createStyleControls( Composite parent ) {
    initializeImages();
    createStyleButton( "BORDER", SWT.BORDER );
    createStyleButton( "CHECK", SWT.CHECK );
    createStyleButton( "MULTI", SWT.MULTI );
    createStyleButton( "VIRTUAL", SWT.VIRTUAL );
    createStyleButton( "FULL_SELECTION", SWT.FULL_SELECTION );
    createStyleButton( "NO_SCROLL", SWT.NO_SCROLL );
    createOrientationButton();
    createVisibilityButton();
    createEnablementButton();
    createHeaderVisibleButton();
    createHeaderForegroundControl();
    createHeaderBackgroundControl();
    createLinesVisibleButton();
    createColumnsMoveableButton();
    createColumnImagesButton();
    createImagesButton( parent );
    createAddNodeButton( parent );
    createDisposeNodeButton( parent );
    createSelectAllButton( parent );
    createDeselectAllButton( parent );
    createSelectButton( parent );
    createDeselectButton( parent );
    createSetSelectionButton( parent );
    createChangeItemCountControl();
    createClearButton();
    createShowColumnControl();
    createBgImageButton();
    createFgColorButton();
    createBgColorButton();
    createFontChooser();
    createItemForegroundControl();
    createItemBackgroundControl();
    createItemFontControl();
    createCellForegroundControl();
    createCellBackgroundControl();
    createCellFontControl();
    createGrayOutButton();
    createColumnsAlignmentButton();
    createPackAllColumnsButton();
    createAddMouseListenerButton();
    createQueryTopItemButton();
    createSetSelectionAsTopItemButton();
  }

  @Override
  protected void createExampleControls( Composite parent ) {
    parent.setLayout( new GridLayout( 1, false ) );
    int style = getStyle();
    tree = new Tree( parent, style );
    if( ( style & SWT.VIRTUAL ) != 0 ) {
      tree.addListener( SWT.SetData, new Listener() {
        @Override
        public void handleEvent( Event event ) {
          final TreeItem item = ( TreeItem )event.item;
          if( updateVirtualItemsDelayed ) {
            final Display display = event.display;
            final ServerPushSession pushSession = new ServerPushSession();
            Job job = new Job( "Delayed Tree Item Update" ) {
              @Override
              protected IStatus run( IProgressMonitor monitor ) {
                display.asyncExec( new Runnable() {
                  @Override
                  public void run() {
                    updateItem( item );
                    pushSession.stop();
                  }
                } );
                return Status.OK_STATUS;
              }
            };
            pushSession.start();
            job.schedule( 1000 );
          } else {
            updateItem( item );
          }
        }
      } );
    }
    tree.setLayoutData( new GridData( GridData.FILL_BOTH ) );
    for( int i = 0; i < INITIAL_COLUMNS; i++ ) {
      final TreeColumn column = new TreeColumn( tree, SWT.NONE );
      column.setText( "Column " + i );
      column.setToolTipText( "Column " + i );
      if( columnImages ) {
        column.setImage( columnImage );
      }
      column.setWidth( 150 );
      column.setMoveable( columnsMoveable );
      column.addListener( SWT.Selection, new Listener() {
        @Override
        public void handleEvent( Event event ) {
          Tree tree = column.getParent();
          if( tree.getSortColumn() == column ) {
            if( tree.getSortDirection() == SWT.UP ) {
              tree.setSortDirection( SWT.DOWN );
            } else {
              tree.setSortDirection( SWT.UP );
            }
          } else {
            tree.setSortDirection( SWT.UP );
            tree.setSortColumn( column );
          }
        }
      } );
    }
    for( int i = 0; i < INITIAL_ITEMS; i++ ) {
      TreeItem item = new TreeItem( tree, SWT.NONE );
      for( int j = 0; j < INITIAL_COLUMNS; j++ ) {
        item.setText( j, "Node_" + i + "." + j );
      }
      if( i % 2 == 0 ) {
        TreeItem subitem = new TreeItem( item, SWT.NONE );
        for( int j = 0; j < INITIAL_COLUMNS; j++ ) {
          subitem.setText( j, "Subnode_" + i + "." + j );
        }
      }
    }
    if( showImages ) {
      changeImage( tree, treeImage );
    }
    final Label lblTreeEvent = new Label( parent, SWT.NONE );
    lblTreeEvent.setLayoutData( new GridData( 300, 22 ) );
    Menu treeMenu = new Menu( tree );
    MenuItem treeMenuItem = new MenuItem( treeMenu, SWT.PUSH );
    treeMenuItem.addListener( SWT.Selection, new Listener() {
      @Override
      public void handleEvent( Event event ) {
        TreeItem item = tree.getSelection()[ 0 ];
        String itemText = "null";
        if( item != null ) {
          itemText = item.getText();
        }
        String message = "You requested a context menu for: " + itemText;
        MessageDialog.openInformation( tree.getShell(), "Information", message );
      }
    } );
    treeMenuItem.setText( "TreeContextMenuItem" );
    tree.setMenu( treeMenu );
    tree.addTreeListener( new TreeListener() {
      @Override
      public void treeCollapsed( TreeEvent event ) {
        Item item = ( Item )event.item;
        lblTreeEvent.setText( "Collapsed: "  + item.getText() );
      }
      @Override
      public void treeExpanded( TreeEvent event ) {
        Item item = ( Item )event.item;
        lblTreeEvent.setText( "Expanded: "  + item.getText() );
      }
    } );
    tree.addSelectionListener( new SelectionListener() {
      @Override
      public void widgetSelected( SelectionEvent event ) {
        String msg = "Selected: ";
        TreeItem item = ( TreeItem )event.item;
        if( ( getStyle() & SWT.CHECK ) != 0 ) {
          msg += ( item.getChecked() ? "[x] " : "[ ] " );
        }
        msg += item.getText();
        switch( event.detail ) {
          case SWT.NONE:
            msg += ", detail: SWT.NONE";
            break;
          case SWT.CHECK:
            msg += ", detail: SWT.CHECK";
            break;
        }
        lblTreeEvent.setText( msg );
      }
      @Override
      public void widgetDefaultSelected( SelectionEvent event ) {
        String title = "Widget Default Selected";
        Item item = ( Item )event.item;
        String message = "Widget default selected on " + item.getText() + " received";
        MessageDialog.openInformation( getShell(), title, message );
      }
    } );
    tree.setSelection( tree.getItem( 0 ) );
    tree.setHeaderVisible( true );
    tree.setLinesVisible( linesVisible );
    if( addMouseListener ) {
      MouseListener listener = new MouseListener(  ) {
        @Override
        public void mouseDoubleClick( MouseEvent e ) {
          log( "mouseDoubleClick: " + e );
        }
        @Override
        public void mouseDown( MouseEvent e ) {
          log( "mouseDown: " + e );
        }
        @Override
        public void mouseUp( MouseEvent e ) {
          log( "mouseUp: " + e );
        }
      };
      tree.addMouseListener( listener );
    }

    registerControl( tree );
  }

  private void initializeImages() {
    treeImage = loadImage( "resources/tree_item.gif" );
    columnImage = loadImage( "resources/shell.gif" );
  }

  private void updateItem( TreeItem item ) {
    if( !item.isDisposed() ) {
      int columns = item.getParent().getColumnCount();
      int index = -1;
      if( item.getParentItem() == null ) {
        index = item.getParent().indexOf( item );
      } else {
        index = item.getParentItem().indexOf( item );
      }
      String text = item.getParentItem() == null ? "Node_" : "Subnode_";
      if( columns == 0 ) {
        item.setText( text + index );
        if( showImages ) {
          item.setImage( treeImage );
        }
      } else {
        for( int i = 0; i < columns; i++ ) {
          item.setText( i, text + index + "." + i );
          if( i < 2 && showImages ) {
            item.setImage( i, treeImage );
          }
        }
      }
    }
  }

  private void createHeaderVisibleButton() {
    final Button button = new Button( styleComp, SWT.CHECK );
    button.setText( "headerVisible" );
    button.setSelection( headerVisible );
    button.addListener( SWT.Selection, new Listener() {
      @Override
      public void handleEvent( Event event ) {
        headerVisible = button.getSelection();
        tree.setHeaderVisible( headerVisible );
      }
    } );
  }

  private void createHeaderForegroundControl() {
    final Button button = createPropertyButton( "Header Foreground", SWT.TOGGLE );
    button.addListener( SWT.Selection, new Listener() {
      @Override
      public void handleEvent( Event event ) {
        Color color = button.getSelection() ? fgColors[ FG_COLOR_RED ]  : null;
        tree.setHeaderForeground( color );
      }
    } );
  }

  private void createHeaderBackgroundControl() {
    final Button button = createPropertyButton( "Header Background", SWT.TOGGLE );
    button.addListener( SWT.Selection, new Listener() {
      @Override
      public void handleEvent( Event event ) {
        Color color = button.getSelection() ? bgColors[ BG_COLOR_GREEN ]  : null;
        tree.setHeaderBackground( color );
      }
    } );
  }

  private void createLinesVisibleButton() {
    final Button button = new Button( styleComp, SWT.CHECK );
    button.setText( "linesVisible" );
    button.setSelection( linesVisible );
    button.addListener( SWT.Selection, new Listener() {
      @Override
      public void handleEvent( Event event ) {
        linesVisible = button.getSelection();
        tree.setLinesVisible( linesVisible );
      }
    } );
  }

  private void createImagesButton( Composite parent ) {
    final Button button = new Button( parent, SWT.TOGGLE );
    button.setText( "Show Images" );
    button.setSelection( showImages );
    button.addListener( SWT.Selection, new Listener() {
      @Override
      public void handleEvent( Event event ) {
        showImages = button.getSelection();
        changeImage( tree, showImages ? treeImage : null );
      }
    } );
  }

  private void createAddNodeButton( Composite parent ) {
    Button button = new Button( parent, SWT.PUSH );
    button.setText( "Add child item" );
    button.addListener( SWT.Selection, new Listener() {
      @Override
      public void handleEvent( Event event ) {
        if( tree.getSelectionCount() > 0 ) {
          TreeItem selection = tree.getSelection()[ 0 ];
          TreeItem treeItem = new TreeItem( selection, SWT.NONE );
          Object[] args = new Object[] {
            new Integer( selection.getItemCount() ),
            selection.getText()
          };
          String text = MessageFormat.format( "SubItem {0} of {1}", args );
          treeItem.setText( text  );
          treeItem.setChecked( true );
          if( showImages ) {
            treeItem.setImage( treeImage );
          }
        }
      }
    } );
  }

  private void createDisposeNodeButton( Composite parent ) {
    Button button = new Button( parent, SWT.PUSH );
    button.setText( "Dispose Selected Item" );
    button.addListener( SWT.Selection, new Listener() {
      @Override
      public void handleEvent( Event event ) {
        if( tree.getSelectionCount() > 0 ) {
          TreeItem selection = tree.getSelection()[ 0 ];
          selection.dispose();
        }
      }
    } );
  }

  private void createSelectAllButton( Composite parent ) {
    Button button = new Button( parent, SWT.PUSH );
    button.setText( "Select All" );
    button.addListener( SWT.Selection, new Listener() {
      @Override
      public void handleEvent( Event event ) {
        tree.selectAll();
      }
    } );
  }

  private void createDeselectAllButton( Composite parent ) {
    Button button = new Button( parent, SWT.PUSH );
    button.setText( "Deselect All" );
    button.addListener( SWT.Selection, new Listener() {
      @Override
      public void handleEvent( Event event ) {
        tree.deselectAll();
      }
    } );
  }

  private void createSelectButton( Composite parent ) {
    Button button = new Button( parent, SWT.PUSH );
    button.setText( "Select second node" );
    button.addListener( SWT.Selection, new Listener() {
      @Override
      public void handleEvent( Event event ) {
        if( tree.getItemCount() > 1 ) {
          tree.select( tree.getItem( 1 ) );
        }
      }
    } );
  }

  private void createDeselectButton( Composite parent ) {
    Button button = new Button( parent, SWT.PUSH );
    button.setText( "Deselect second node" );
    button.addListener( SWT.Selection, new Listener() {
      @Override
      public void handleEvent( Event event ) {
        if( tree.getItemCount() > 1 ) {
          tree.deselect( tree.getItem( 1 ) );
        }
      }
    } );
  }

  private void createSetSelectionButton( Composite parent ) {
    Button button = new Button( parent, SWT.PUSH );
    button.setText( "Set selection to first node" );
    button.addListener( SWT.Selection, new Listener() {
      @Override
      public void handleEvent( Event event ) {
        if( tree.getItemCount() > 0 ) {
          tree.setSelection( tree.getItem( 0 ) );
        }
      }
    } );
  }

  private void createShowColumnControl() {
    Composite composite = new Composite( styleComp, SWT.NONE );
    RowLayout layout = new RowLayout(  SWT.HORIZONTAL );
    layout.center = true;
    composite.setLayout( layout );
    Label label = new Label( composite, SWT.NONE );
    label.setText( "Column" );
    final Text text = new Text( composite, SWT.BORDER );
    Util.textSizeAdjustment( label, text );
    text.setText( String.valueOf( tree.getColumnCount() - 1 ) );
    Button button = new Button( composite, SWT.PUSH );
    button.setText( "Show" );
    button.addListener( SWT.Selection, new Listener() {
      @Override
      public void handleEvent( Event event ) {
        try {
          int index = Integer.parseInt( text.getText() );
          tree.showColumn( tree.getColumn( index ) );
        } catch( Exception e ) {
          // ignore invalid column
        }
      }
    } );
  }

  private void createChangeItemCountControl() {
    Composite composite = new Composite( styleComp, SWT.NONE );
    composite.setLayout( new GridLayout( 3, false ) );
    Label lblItemCount = new Label( composite, SWT.NONE );
    lblItemCount.setText( "ItemCount" );
    final Text txtItemCount = new Text( composite, SWT.BORDER );
    Util.textSizeAdjustment( lblItemCount, txtItemCount );
    txtItemCount.setText( String.valueOf( tree.getItemCount() ) );
    Button btnChange = new Button( composite, SWT.PUSH );
    btnChange.setText( "Change" );
    btnChange.addListener( SWT.Selection, new Listener() {
      @Override
      public void handleEvent( Event event ) {
        int itemCount = -1;
        try {
          itemCount = Integer.parseInt( txtItemCount.getText() );
        } catch( NumberFormatException e ) {
          // ignore invalid item count
        }
        TreeItem[] selectedItems = tree.getSelection();
        if( selectedItems.length > 0 ) {
          selectedItems[ 0 ].setItemCount( itemCount );
        } else {
          tree.setItemCount( itemCount );
        }
        tree.redraw();
      }
    } );
    final Button cbDelayedUpdate = new Button( composite, SWT.CHECK );
    GridData gridData = new GridData( SWT.LEFT, SWT.CENTER, true, false, 3, SWT.DEFAULT );
    cbDelayedUpdate.setLayoutData( gridData );
    cbDelayedUpdate.setText( "Update virtual items delayed" );
    cbDelayedUpdate.addListener( SWT.Selection, new Listener() {
      @Override
      public void handleEvent( Event event ) {
        updateVirtualItemsDelayed = cbDelayedUpdate.getSelection();
      }
    } );
  }

  private void createClearButton() {
    Button button = new Button( styleComp, SWT.PUSH );
    button.setText( "ClearAll" );
    button.addListener( SWT.Selection, new Listener() {
      @Override
      public void handleEvent( Event event ) {
        tree.clearAll( true );
      }
    } );
  }

  private static void changeImage( Tree tree, Image image ) {
    for( TreeItem item : tree.getItems() ) {
      changeImage( item, image );
    }
  }

  private static void changeImage( TreeItem item, Image image ) {
    item.setImage( 0, image );
    if( item.getParent().getColumnCount() > 1 ) {
      item.setImage( 1, image );
    }
    for( TreeItem subItem : item.getItems() ) {
      changeImage( subItem, image );
    }
  }

  private void createSetSelectionAsTopItemButton() {
    final Button button = createPropertyButton( "Set selection as topItem", SWT.PUSH );
    button.addListener( SWT.Selection, new Listener() {
      @Override
      public void handleEvent( Event event ) {
        TreeItem[] item = tree.getSelection();
        if( item.length > 0 ) {
          tree.setTopItem( item[ 0 ] );
        }
      }
    } );
  }

  private void createQueryTopItemButton() {
    final Button button = createPropertyButton( "Query topItem", SWT.PUSH );
    button.addListener( SWT.Selection, new Listener() {
      @Override
      public void handleEvent( Event event ) {
        String message = "Current topItem: " + tree.getTopItem().toString();
        MessageDialog.openInformation( tree.getShell(), "Information", message );
      }
    } );
  }

  private void createAddMouseListenerButton() {
    final Button button = createPropertyButton( "Attach MouseListener", SWT.CHECK );
    button.addListener( SWT.Selection, new Listener() {
      @Override
      public void handleEvent( Event event ) {
        addMouseListener = !addMouseListener;
        createNew();
      }
    } );
  }

  private void createGrayOutButton() {
    final Button button = createPropertyButton( "Gray out 2nd item", SWT.CHECK );
    button.addListener( SWT.Selection, new Listener() {
      @Override
      public void handleEvent( Event event ) {
        tree.getItem( 1 ).setGrayed( button.getSelection() );
      }
    } );
  }

  private void createColumnsAlignmentButton() {
    final Button button = createPropertyButton( "Change columns alignment", SWT.PUSH );
    button.addListener( SWT.Selection, new Listener() {
      @Override
      public void handleEvent( Event event ) {
        TreeColumn col1 = tree.getColumn( 1 );
        if( col1.getAlignment() == SWT.RIGHT ) {
          col1.setAlignment( SWT.LEFT );
        } else {
          col1.setAlignment( SWT.RIGHT );
        }
        TreeColumn col2 = tree.getColumn( 2 );
        if( col2.getAlignment() == SWT.CENTER ) {
          col2.setAlignment( SWT.LEFT );
        } else {
          col2.setAlignment( SWT.CENTER );
        }
      }
    } );
  }

  private void createPackAllColumnsButton() {
    final Button button = createPropertyButton( "Pack all columns", SWT.PUSH );
    button.addListener( SWT.Selection, new Listener() {
      @Override
      public void handleEvent( Event event ) {
        for( TreeColumn column : tree.getColumns() ) {
          column.pack();
        }
      }
    } );
  }

  private void createItemForegroundControl() {
    final Button button = createPropertyButton( "Item 0 Foreground", SWT.TOGGLE );
    button.addListener( SWT.Selection, new Listener() {
      @Override
      public void handleEvent( Event event ) {
        if( tree.getItemCount() > 0 ) {
          Color color = button.getSelection() ? fgColors[ FG_COLOR_ORANGE ]  : null;
          tree.getItem( 0 ).setForeground( color );
        }
      }
    } );
  }

  private void createItemBackgroundControl() {
    final Button button = createPropertyButton( "Item 0 Background", SWT.TOGGLE );
    button.addListener( SWT.Selection, new Listener() {
      @Override
      public void handleEvent( Event event ) {
        if( tree.getItemCount() > 0 ) {
          Color color = button.getSelection() ? bgColors[ BG_COLOR_BROWN ]  : null;
          tree.getItem( 0 ).setBackground( color );
        }
      }
    } );
  }

  private void createItemFontControl() {
    final Button button = createPropertyButton( "Item 0 Font", SWT.TOGGLE );
    final Font customFont = new Font( button.getDisplay(), "Courier", 11, SWT.BOLD );
    button.addListener( SWT.Selection, new Listener() {
      @Override
      public void handleEvent( Event event ) {
        if( tree.getItemCount() > 0 ) {
          Font font = button.getSelection() ? customFont : null;
          tree.getItem( 0 ).setFont( font );
        }
      }
    } );
  }

  private void createCellForegroundControl() {
    final Button button = createPropertyButton( "Cell 0,0 Foreground", SWT.TOGGLE );
    button.addListener( SWT.Selection, new Listener() {
      @Override
      public void handleEvent( Event event ) {
        if( tree.getItemCount() > 0 ) {
          Color color = button.getSelection() ? fgColors[ FG_COLOR_RED ]  : null;
          tree.getItem( 0 ).setForeground( 0, color );
        }
      }
    } );
  }

  private void createCellBackgroundControl() {
    final Button button = createPropertyButton( "Cell 0,0 Background", SWT.TOGGLE );
    button.addListener( SWT.Selection, new Listener() {
      @Override
      public void handleEvent( Event event ) {
        if( tree.getItemCount() > 0 ) {
          Color color = button.getSelection() ? bgColors[ BG_COLOR_GREEN ]  : null;
          tree.getItem( 0 ).setBackground( 0, color );
        }
      }
    } );
  }

  private void createCellFontControl() {
    final Button button = createPropertyButton( "Cell 0,0 Font", SWT.TOGGLE );
    final Font cellFont = new Font( button.getDisplay(), "Times", 13, SWT.ITALIC );
    button.addListener( SWT.Selection, new Listener() {
      @Override
      public void handleEvent( Event event ) {
        if( tree.getItemCount() > 0 ) {
          Font font = button.getSelection() ? cellFont : null;
          tree.getItem( 0 ).setFont( 0, font );
        }
      }
    } );
  }

  private void createColumnsMoveableButton() {
    final Button button = createPropertyButton( "Moveable Columns", SWT.CHECK );
    button.setSelection( columnsMoveable );
    button.addListener( SWT.Selection, new Listener() {
      @Override
      public void handleEvent( Event event ) {
        columnsMoveable = button.getSelection();
        for( TreeColumn column : tree.getColumns() ) {
          column.setMoveable( columnsMoveable );
        }
      }
    } );
  }

  private void createColumnImagesButton() {
    final Button button = createPropertyButton( "Column images", SWT.CHECK );
    button.setSelection( columnImages );
    button.addListener( SWT.Selection, new Listener() {
      @Override
      public void handleEvent( Event event ) {
        columnImages = button.getSelection();
        TreeColumn[] columns = tree.getColumns();
        for( int i = 0; i < columns.length; i++ ) {
          columns[ i ].setImage( columnImages? columnImage : null );
        }
      }
    } );
  }

}
