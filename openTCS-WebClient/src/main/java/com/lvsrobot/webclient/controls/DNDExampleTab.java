/*******************************************************************************
 * Copyright (c) 2000, 2014 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    IBM Corporation - initial API and implementation
 *    EclipseSource - modified to fit into ControlsDemo
 *******************************************************************************/
package com.lvsrobot.webclient.controls;

import org.eclipse.rap.rwt.client.ClientFile;
import org.eclipse.rap.rwt.dnd.ClientFileTransfer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTError;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSource;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DragSourceListener;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.DropTargetListener;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.dnd.HTMLTransfer;
import org.eclipse.swt.dnd.RTFTransfer;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Item;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;


public class DNDExampleTab extends ExampleTab {

  private static final int BUTTON_TOGGLE = 0;
  private static final int BUTTON_RADIO = 1;
  private static final int BUTTON_CHECK = 2;
  private static final int CANVAS = 3;
  private static final int LABEL = 4;
  private static final int LIST = 5;
  private static final int TABLE = 6;
  private static final int TREE = 7;
  private static final int TEXT = 8;

  private int dragOperation;
  private Transfer[] dragTypes = new Transfer[ 0 ];
  private Control dragControl;
  private int dragControlType;
  private DragSource dragSource;
  private String dragDataText;
  private String dragDataRTF;
  private String dragDataHTML;
  private boolean dragEnabled;
  private int dropOperation;
  private int dropFeedback;
  private int dropDefaultOperation;
  private Transfer[] dropTypes = new Transfer[ 0 ];
  private DropTarget dropTarget;
  private Control dropControl;
  private int dropControlType;
  private Composite defaultParent;
  private boolean dropEnabled;
  private Text dragConsole;
  private boolean dragEventDetail;
  private Text dropConsole;
  private boolean dropEventDetail;

  public DNDExampleTab() {
    super( "Drag & Drop" );
    setHorizontalSashFormWeights( new int[] { 100, 0 } );
    dragEnabled = true;
    dropEnabled = true;
    addDragTransfer( TextTransfer.getInstance() );
    addDropTransfer( TextTransfer.getInstance() );
  }

  @Override
  protected void createExampleControls( final Composite container ) {
    container.setLayout( new FillLayout() );
    ScrolledComposite sc = new ScrolledComposite( container, SWT.H_SCROLL
                                                          | SWT.V_SCROLL );
    Composite parent = new Composite( sc, SWT.NONE );
    sc.setContent( parent );
    parent.setLayout( new FormLayout() );
    Label dragLabel = new Label( parent, SWT.LEFT );
    dragLabel.setText( "Drag Source:" );
    Group dragWidgetGroup = new Group( parent, SWT.NONE );
    dragWidgetGroup.setText( "Widget" );
    createDragWidget( dragWidgetGroup );
    Composite cLeft = new Composite( parent, SWT.NONE );
    cLeft.setLayout( new GridLayout( 2, false ) );
    Group dragOperationsGroup = new Group( cLeft, SWT.NONE );
    dragOperationsGroup.setLayoutData( new GridData( SWT.LEFT,
                                                     SWT.FILL,
                                                     false,
                                                     false,
                                                     1,
                                                     1 ) );
    dragOperationsGroup.setText( "Allowed Operation(s):" );
    createDragOperations( dragOperationsGroup );
    Group dragTypesGroup = new Group( cLeft, SWT.NONE );
    dragTypesGroup.setLayoutData( new GridData( SWT.FILL,
                                                SWT.TOP,
                                                true,
                                                false,
                                                1,
                                                1 ) );
    dragTypesGroup.setText( "Transfer Type(s):" );
    createDragTypes( dragTypesGroup );
    dragConsole = new Text( cLeft, SWT.READ_ONLY
                                   | SWT.BORDER
                                   | SWT.V_SCROLL
                                   | SWT.H_SCROLL
                                   | SWT.MULTI );
    dragConsole.setLayoutData( new GridData( SWT.FILL,
                                             SWT.FILL,
                                             true,
                                             true,
                                             2,
                                             1 ) );
    Menu menu = new Menu( container.getShell(), SWT.POP_UP );
    MenuItem item = new MenuItem( menu, SWT.PUSH );
    item.setText( "Clear" );
    item.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected( final SelectionEvent e ) {
        dragConsole.setText( "" );
      }
    } );
    item = new MenuItem( menu, SWT.CHECK );
    item.setText( "Show Event detail" );
    item.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected( final SelectionEvent e ) {
        MenuItem item = ( MenuItem )e.widget;
        dragEventDetail = item.getSelection();
      }
    } );
    dragConsole.setMenu( menu );
    Label dropLabel = new Label( parent, SWT.LEFT );
    dropLabel.setText( "Drop Target:" );
    Group dropWidgetGroup = new Group( parent, SWT.NONE );
    dropWidgetGroup.setText( "Widget" );
    createDropWidget( dropWidgetGroup );
    Composite cRight = new Composite( parent, SWT.NONE );
    cRight.setLayout( new GridLayout( 2, false ) );
    Group dropOperationsGroup = new Group( cRight, SWT.NONE );
    dropOperationsGroup.setLayoutData( new GridData( SWT.LEFT,
                                                     SWT.FILL,
                                                     false,
                                                     false,
                                                     1,
                                                     2 ) );
    dropOperationsGroup.setText( "Allowed Operation(s):" );
    createDropOperations( dropOperationsGroup );
    Group dropTypesGroup = new Group( cRight, SWT.NONE );
    dropTypesGroup.setText( "Transfer Type(s):" );
    createDropTypes( dropTypesGroup );
    Group feedbackTypesGroup = new Group( cRight, SWT.NONE );
    feedbackTypesGroup.setText( "Feedback Type(s):" );
    createFeedbackTypes( feedbackTypesGroup );
    dropConsole = new Text( cRight, SWT.READ_ONLY
                                    | SWT.BORDER
                                    | SWT.V_SCROLL
                                    | SWT.H_SCROLL
                                    | SWT.MULTI );
    dropConsole.setLayoutData( new GridData( SWT.FILL,
                                             SWT.FILL,
                                             true,
                                             true,
                                             2,
                                             1 ) );
    menu = new Menu( container.getShell(), SWT.POP_UP );
    item = new MenuItem( menu, SWT.PUSH );
    item.setText( "Clear" );
    item.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected( final SelectionEvent e ) {
        dropConsole.setText( "" );
      }
    } );
    item = new MenuItem( menu, SWT.CHECK );
    item.setText( "Show Event detail" );
    item.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected( final SelectionEvent e ) {
        MenuItem item = ( MenuItem )e.widget;
        dropEventDetail = item.getSelection();
      }
    } );
    dropConsole.setMenu( menu );
    int height = 200;
    FormData data = new FormData();
    data.top = new FormAttachment( 0, 10 );
    data.left = new FormAttachment( 0, 10 );
    dragLabel.setLayoutData( data );
    data = new FormData();
    data.top = new FormAttachment( dragLabel, 10 );
    data.left = new FormAttachment( 0, 10 );
    data.right = new FormAttachment( 50, -10 );
    data.height = height;
    dragWidgetGroup.setLayoutData( data );
    data = new FormData();
    data.top = new FormAttachment( dragWidgetGroup, 10 );
    data.left = new FormAttachment( 0, 10 );
    data.right = new FormAttachment( 50, -10 );
    data.bottom = new FormAttachment( 100, -10 );
    cLeft.setLayoutData( data );
    data = new FormData();
    data.top = new FormAttachment( 0, 10 );
    data.left = new FormAttachment( cLeft, 10 );
    dropLabel.setLayoutData( data );
    data = new FormData();
    data.top = new FormAttachment( dropLabel, 10 );
    data.left = new FormAttachment( cLeft, 10 );
    data.right = new FormAttachment( 100, -10 );
    data.height = height;
    dropWidgetGroup.setLayoutData( data );
    data = new FormData();
    data.top = new FormAttachment( dropWidgetGroup, 10 );
    data.left = new FormAttachment( cLeft, 10 );
    data.right = new FormAttachment( 100, -10 );
    data.bottom = new FormAttachment( 100, -10 );
    cRight.setLayoutData( data );
    sc.setMinSize( parent.computeSize( SWT.DEFAULT, SWT.DEFAULT ) );
    sc.setExpandHorizontal( true );
    sc.setExpandVertical( true );
  }

  @Override
  protected void createStyleControls( final Composite parent ) {
  }

  private void addDragTransfer( final Transfer transfer ) {
    Transfer[] newTypes = new Transfer[ dragTypes.length + 1 ];
    System.arraycopy( dragTypes, 0, newTypes, 0, dragTypes.length );
    newTypes[ dragTypes.length ] = transfer;
    dragTypes = newTypes;
    if( dragSource != null ) {
      dragSource.setTransfer( dragTypes );
    }
  }

  private void addDropTransfer( final Transfer transfer ) {
    Transfer[] newTypes = new Transfer[ dropTypes.length + 1 ];
    System.arraycopy( dropTypes, 0, newTypes, 0, dropTypes.length );
    newTypes[ dropTypes.length ] = transfer;
    dropTypes = newTypes;
    if( dropTarget != null ) {
      dropTarget.setTransfer( dropTypes );
    }
  }

  private void createDragOperations( final Composite parent ) {
    parent.setLayout( new RowLayout( SWT.VERTICAL ) );
    final Button moveButton = new Button( parent, SWT.CHECK );
    moveButton.setText( "DND.DROP_MOVE" );
    moveButton.setSelection( true );
    dragOperation = DND.DROP_MOVE;
    moveButton.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected( final SelectionEvent e ) {
        Button b = ( Button )e.widget;
        if( b.getSelection() ) {
          dragOperation |= DND.DROP_MOVE;
        } else {
          dragOperation = dragOperation & ~DND.DROP_MOVE;
          if( dragOperation == 0 ) {
            dragOperation = DND.DROP_MOVE;
            moveButton.setSelection( true );
          }
        }
        if( dragEnabled ) {
          createDragSource();
        }
      }
    } );
    Button b = new Button( parent, SWT.CHECK );
    b.setText( "DND.DROP_COPY" );
    b.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected( final SelectionEvent e ) {
        Button b = ( Button )e.widget;
        if( b.getSelection() ) {
          dragOperation |= DND.DROP_COPY;
        } else {
          dragOperation = dragOperation & ~DND.DROP_COPY;
          if( dragOperation == 0 ) {
            dragOperation = DND.DROP_MOVE;
            moveButton.setSelection( true );
          }
        }
        if( dragEnabled ) {
          createDragSource();
        }
      }
    } );
    b = new Button( parent, SWT.CHECK );
    b.setText( "DND.DROP_LINK" );
    b.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected( final SelectionEvent e ) {
        Button b = ( Button )e.widget;
        if( b.getSelection() ) {
          dragOperation |= DND.DROP_LINK;
        } else {
          dragOperation = dragOperation & ~DND.DROP_LINK;
          if( dragOperation == 0 ) {
            dragOperation = DND.DROP_MOVE;
            moveButton.setSelection( true );
          }
        }
        if( dragEnabled ) {
          createDragSource();
        }
      }
    } );
  }

  private void createDragSource() {
    if( dragSource != null ) {
      dragSource.dispose();
    }
    dragSource = new DragSource( dragControl, dragOperation );
    dragSource.setTransfer( dragTypes );
    dragSource.addDragListener( new DragSourceListener() {
      public void dragFinished( final DragSourceEvent event ) {
        dragConsole.append( ">>dragFinished\n" );
        printEvent( event );
        dragDataText = dragDataRTF = dragDataHTML = null;
        if( event.detail == DND.DROP_MOVE ) {
          switch( dragControlType ) {
            case BUTTON_CHECK:
            case BUTTON_TOGGLE:
            case BUTTON_RADIO: {
              Button b = ( Button )dragControl;
              b.setText( "" );
              break;
            }
            case TABLE: {
              Table table = ( Table )dragControl;
              TableItem[] items = table.getSelection();
              for( int i = 0; i < items.length; i++ ) {
                items[ i ].dispose();
              }
              break;
            }
            case TEXT: {
              Text text = ( Text )dragControl;
              text.clearSelection();
              break;
            }
            case TREE: {
              Tree tree = ( Tree )dragControl;
              TreeItem[] items = tree.getSelection();
              for( int i = 0; i < items.length; i++ ) {
                items[ i ].dispose();
              }
              break;
            }
            case CANVAS: {
              dragControl.setData( "STRINGS", null );
              dragControl.redraw();
              break;
            }
            case LABEL: {
              Label label = ( Label )dragControl;
              label.setText( "" );
              break;
            }
            case LIST: {
              List list = ( List )dragControl;
              int[] indices = list.getSelectionIndices();
              list.remove( indices );
              break;
            }
          }
        }
      }

      public void dragSetData( final DragSourceEvent event ) {
        dragConsole.append( ">>dragSetData\n" );
        printEvent( event );
        if( TextTransfer.getInstance().isSupportedType( event.dataType ) ) {
          event.data = dragDataText;
        }
        if( RTFTransfer.getInstance().isSupportedType( event.dataType ) ) {
          event.data = dragDataRTF;
        }
        if( HTMLTransfer.getInstance().isSupportedType( event.dataType ) ) {
          event.data = dragDataHTML;
        }
      }

      public void dragStart( final DragSourceEvent event ) {
        dragConsole.append( ">>dragStart\n" );
        printEvent( event );
        switch( dragControlType ) {
          case BUTTON_CHECK:
          case BUTTON_TOGGLE:
          case BUTTON_RADIO: {
            Button b = ( Button )dragControl;
            dragDataText = b.getSelection()
                                           ? "true"
                                           : "false";
            break;
          }
          case TABLE: {
            Table table = ( Table )dragControl;
            TableItem[] items = table.getSelection();
            if( items.length == 0 ) {
              event.doit = false;
            } else {
              dragDataText = join( "\n", items );
            }
            break;
          }
          case TEXT: {
            Text text = ( Text )dragControl;
            String s = text.getSelectionText();
            if( s.length() == 0 ) {
              event.doit = false;
            } else {
              dragDataText = s;
            }
            break;
          }
          case TREE: {
            Tree tree = ( Tree )dragControl;
            TreeItem[] items = tree.getSelection();
            if( items.length == 0 ) {
              event.doit = false;
            } else {
              dragDataText = join( "\n", items );
            }
            break;
          }
          case CANVAS: {
            String[] strings = ( String[] )dragControl.getData( "STRINGS" );
            if( strings == null || strings.length == 0 ) {
              event.doit = false;
            } else {
              dragDataText = join( "\n", strings );
            }
            break;
          }
          case LABEL: {
            Label label = ( Label )dragControl;
            String string = label.getText();
            if( string.length() == 0 ) {
              event.doit = false;
            } else {
              dragDataText = string;
            }
            break;
          }
          case LIST: {
            List list = ( List )dragControl;
            String[] selection = list.getSelection();
            if( selection.length == 0 ) {
              event.doit = false;
            } else {
              dragDataText = join( "\n", selection );
            }
            break;
          }
          default:
            throw new SWTError( SWT.ERROR_NOT_IMPLEMENTED );
        }
        if( dragDataText != null ) {
          dragDataRTF = "{\\rtf1{\\colortbl;\\red255\\green0\\blue0;}\\cf1\\b "
                        + dragDataText
                        + "}";
          dragDataHTML = "<b>" + dragDataText + "</b>";
        }
        for( int i = 0; i < dragTypes.length; i++ ) {
          if( dragTypes[ i ] instanceof TextTransfer && dragDataText == null ) {
            event.doit = false;
          }
          if( dragTypes[ i ] instanceof RTFTransfer && dragDataRTF == null ) {
            event.doit = false;
          }
          if( dragTypes[ i ] instanceof HTMLTransfer && dragDataHTML == null ) {
            event.doit = false;
          }
        }
      }
    } );
  }

  private void createDragTypes( final Composite parent ) {
    parent.setLayout( new GridLayout() );
    Button b = new Button( parent, SWT.CHECK );
    b.setText( "Text Transfer" );
    b.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected( final SelectionEvent e ) {
        Button b = ( Button )e.widget;
        if( b.getSelection() ) {
          addDragTransfer( TextTransfer.getInstance() );
        } else {
          removeDragTransfer( TextTransfer.getInstance() );
        }
      }
    } );
    b.setSelection( true );
    b = new Button( parent, SWT.CHECK );
    b.setText( "RTF Transfer" );
    b.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected( final SelectionEvent e ) {
        Button b = ( Button )e.widget;
        if( b.getSelection() ) {
          addDragTransfer( RTFTransfer.getInstance() );
        } else {
          removeDragTransfer( RTFTransfer.getInstance() );
        }
      }
    } );
    b = new Button( parent, SWT.CHECK );
    b.setText( "HTML Transfer" );
    b.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected( final SelectionEvent e ) {
        Button b = ( Button )e.widget;
        if( b.getSelection() ) {
          addDragTransfer( HTMLTransfer.getInstance() );
        } else {
          removeDragTransfer( HTMLTransfer.getInstance() );
        }
      }
    } );
  }

  private void createDragWidget( final Composite parent ) {
    parent.setLayout( new FormLayout() );
    Combo combo = new Combo( parent, SWT.READ_ONLY );
    combo.setItems( new String[]{
      "Toggle Button",
      "Radio Button",
      "Checkbox",
      "Canvas",
      "Label",
      "List",
      "Table",
      "Tree"
    } );
    combo.select( LABEL );
    dragControlType = combo.getSelectionIndex();
    dragControl = createWidget( dragControlType, parent, "Drag Source" );
    combo.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected( final SelectionEvent e ) {
        Object data = dragControl.getLayoutData();
        Composite parent = dragControl.getParent();
        dragControl.dispose();
        Combo c = ( Combo )e.widget;
        dragControlType = c.getSelectionIndex();
        dragControl = createWidget( dragControlType, parent, "Drag Source" );
        dragControl.setLayoutData( data );
        updateDragSource();
        parent.layout();
      }
    } );
    Button b = new Button( parent, SWT.CHECK );
    b.setText( "DragSource" );
    b.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected( final SelectionEvent e ) {
        dragEnabled = ( ( Button )e.widget ).getSelection();
        updateDragSource();
      }
    } );
    b.setSelection( dragEnabled );
    FormData data = new FormData();
    data.top = new FormAttachment( 0, 10 );
    data.bottom = new FormAttachment( combo, -10 );
    data.left = new FormAttachment( 0, 10 );
    data.right = new FormAttachment( 100, -10 );
    dragControl.setLayoutData( data );
    data = new FormData();
    data.bottom = new FormAttachment( 100, -10 );
    data.left = new FormAttachment( 0, 10 );
    combo.setLayoutData( data );
    data = new FormData();
    data.bottom = new FormAttachment( 100, -10 );
    data.left = new FormAttachment( combo, 10 );
    b.setLayoutData( data );
    updateDragSource();
  }

  private void updateDragSource() {
    if( dragSource != null ) {
      dragSource.dispose();
    }
    if( dragEnabled ) {
      createDragSource();
    } else {
      if( dragSource != null ) {
        dragSource.dispose();
      }
      dragSource = null;
    }
  }

  private void createDropOperations( final Composite parent ) {
    parent.setLayout( new RowLayout( SWT.VERTICAL ) );
    final Button moveButton = new Button( parent, SWT.CHECK );
    moveButton.setText( "DND.DROP_MOVE" );
    moveButton.setSelection( true );
    dropOperation = DND.DROP_MOVE;
    moveButton.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected( final SelectionEvent e ) {
        Button b = ( Button )e.widget;
        if( b.getSelection() ) {
          dropOperation |= DND.DROP_MOVE;
        } else {
          dropOperation = dropOperation & ~DND.DROP_MOVE;
          if( dropOperation == 0
              || ( dropDefaultOperation & DND.DROP_MOVE ) != 0 )
          {
            dropOperation |= DND.DROP_MOVE;
            moveButton.setSelection( true );
          }
        }
        if( dropEnabled ) {
          createDropTarget();
        }
      }
    } );
    final Button copyButton = new Button( parent, SWT.CHECK );
    copyButton.setText( "DND.DROP_COPY" );
    copyButton.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected( final SelectionEvent e ) {
        Button b = ( Button )e.widget;
        if( b.getSelection() ) {
          dropOperation |= DND.DROP_COPY;
        } else {
          dropOperation = dropOperation & ~DND.DROP_COPY;
          if(    dropOperation == 0
              || ( dropDefaultOperation & DND.DROP_COPY ) != 0 )
          {
            dropOperation = DND.DROP_COPY;
            copyButton.setSelection( true );
          }
        }
        if( dropEnabled ) {
          createDropTarget();
        }
      }
    } );
    final Button linkButton = new Button( parent, SWT.CHECK );
    linkButton.setText( "DND.DROP_LINK" );
    linkButton.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected( final SelectionEvent e ) {
        Button b = ( Button )e.widget;
        if( b.getSelection() ) {
          dropOperation |= DND.DROP_LINK;
        } else {
          dropOperation = dropOperation & ~DND.DROP_LINK;
          if(    dropOperation == 0
              || ( dropDefaultOperation & DND.DROP_LINK ) != 0 )
          {
            dropOperation = DND.DROP_LINK;
            linkButton.setSelection( true );
          }
        }
        if( dropEnabled ) {
          createDropTarget();
        }
      }
    } );
    Button b = new Button( parent, SWT.CHECK );
    b.setText( "DND.DROP_DEFAULT" );
    defaultParent = new Composite( parent, SWT.NONE );
    b.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected( final SelectionEvent e ) {
        Button b = ( Button )e.widget;
        if( b.getSelection() ) {
          dropOperation |= DND.DROP_DEFAULT;
          defaultParent.setVisible( true );
        } else {
          dropOperation = dropOperation & ~DND.DROP_DEFAULT;
          defaultParent.setVisible( false );
        }
        if( dropEnabled ) {
          createDropTarget();
        }
      }
    } );
    defaultParent.setVisible( false );
    GridLayout layout = new GridLayout();
    layout.marginWidth = 20;
    defaultParent.setLayout( layout );
    Label label = new Label( defaultParent, SWT.NONE );
    label.setText( "Value for default operation is:" );
    b = new Button( defaultParent, SWT.RADIO );
    b.setText( "DND.DROP_MOVE" );
    b.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected( final SelectionEvent e ) {
        if( ( ( Button )e.widget ).getSelection() ) {
          dropDefaultOperation = DND.DROP_MOVE;
          dropOperation |= DND.DROP_MOVE;
          moveButton.setSelection( true );
          if( dropEnabled ) {
            createDropTarget();
          }
        }
      }
    } );
    b = new Button( defaultParent, SWT.RADIO );
    b.setText( "DND.DROP_COPY" );
    b.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected( final SelectionEvent e ) {
        Button b = ( Button )e.widget;
        if( b.getSelection() ) {
          dropDefaultOperation = DND.DROP_COPY;
          dropOperation |= DND.DROP_COPY;
          copyButton.setSelection( true );
          if( dropEnabled ) {
            createDropTarget();
          }
        }
      }
    } );
    b = new Button( defaultParent, SWT.RADIO );
    b.setText( "DND.DROP_LINK" );
    b.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected( final SelectionEvent e ) {
        Button b = ( Button )e.widget;
        if( b.getSelection() ) {
          dropDefaultOperation = DND.DROP_LINK;
          dropOperation |= DND.DROP_LINK;
          linkButton.setSelection( true );
          if( dropEnabled ) {
            createDropTarget();
          }
        }
      }
    } );
    b = new Button( defaultParent, SWT.RADIO );
    b.setText( "DND.DROP_NONE" );
    b.setSelection( true );
    b.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected( final SelectionEvent e ) {
        Button b = ( Button )e.widget;
        if( b.getSelection() ) {
          dropDefaultOperation = DND.DROP_NONE;
          dropOperation &= ~DND.DROP_DEFAULT;
          if( dropEnabled ) {
            createDropTarget();
          }
        }
      }
    } );
  }

  private void createDropTarget() {
    if( dropTarget != null ) {
      dropTarget.dispose();
    }
    dropTarget = new DropTarget( dropControl, dropOperation );
    dropTarget.setTransfer( dropTypes );
    dropTarget.addDropListener( new DropTargetListener() {

      public void dragEnter( final DropTargetEvent event ) {
        dropConsole.append( ">>dragEnter\n" );
        printEvent( event );
        if( event.detail == DND.DROP_DEFAULT ) {
          event.detail = dropDefaultOperation;
        }
        event.feedback = dropFeedback;
      }

      public void dragLeave( final DropTargetEvent event ) {
        dropConsole.append( ">>dragLeave\n" );
        printEvent( event );
      }

      public void dragOperationChanged( final DropTargetEvent event ) {
        dropConsole.append( ">>dragOperationChanged\n" );
        printEvent( event );
        if( event.detail == DND.DROP_DEFAULT ) {
          event.detail = dropDefaultOperation;
        }
        event.feedback = dropFeedback;
      }

      public void dragOver( final DropTargetEvent event ) {
        dropConsole.append( ">>dragOver\n" );
        printEvent( event );
        event.feedback = dropFeedback;
      }

      public void drop( final DropTargetEvent event ) {
        dropConsole.append( ">>drop\n" );
        printEvent( event );
        String[] strings = null;
        if( TextTransfer.getInstance().isSupportedType( event.currentDataType )
            || RTFTransfer.getInstance()
              .isSupportedType( event.currentDataType )
            || HTMLTransfer.getInstance()
              .isSupportedType( event.currentDataType ) )
        {
          strings = new String[]{
            ( String )event.data
          };
        }
        if( FileTransfer.getInstance().isSupportedType( event.currentDataType ) )
        {
          strings = ( String[] )event.data;
        }
        if( ClientFileTransfer.getInstance().isSupportedType( event.currentDataType ) ) {
          ClientFile[] files = ( ClientFile[] )event.data;
          strings = new String[ files.length ];
          for( int i = 0; i < files.length; i++ ) {
            strings[ i ] = files[ i ].toString();
          }
        }
        if( strings == null || strings.length == 0 ) {
          dropConsole.append( "!!Invalid data dropped" );
          return;
        }
        if( strings.length == 1
            && ( dropControlType == TABLE || dropControlType == TREE || dropControlType == LIST ) )
        {
          // convert string separated by "\n" into an array of strings
          String string = strings[ 0 ];
          int count = 0;
          int offset = string.indexOf( "\n", 0 );
          while( offset > 0 ) {
            count++;
            offset = string.indexOf( "\n", offset + 1 );
          }
          if( count > 0 ) {
            strings = new String[ count + 1 ];
            int start = 0;
            int end = string.indexOf( "\n" );
            int index = 0;
            while( start < end ) {
              strings[ index++ ] = string.substring( start, end );
              start = end + 1;
              end = string.indexOf( "\n", start );
              if( end == -1 ) {
                end = string.length();
              }
            }
          }
        }
        switch( dropControlType ) {
          case BUTTON_CHECK:
          case BUTTON_TOGGLE:
          case BUTTON_RADIO: {
            Button b = ( Button )dropControl;
            b.setText( strings[ 0 ] );
            break;
          }
          case TABLE: {
            Table table = ( Table )dropControl;
            Point p = event.display.map( null, table, event.x, event.y );
            TableItem dropItem = table.getItem( p );
            int index = dropItem == null
                                        ? table.getItemCount()
                                        : table.indexOf( dropItem );
            for( int i = 0; i < strings.length; i++ ) {
              TableItem item = new TableItem( table, SWT.NONE, index );
              item.setText( 0, strings[ i ] );
              item.setText( 1, "dropped item" );
            }
            TableColumn[] columns = table.getColumns();
            for( int i = 0; i < columns.length; i++ ) {
              columns[ i ].pack();
            }
            break;
          }
          case TEXT: {
            Text text = ( Text )dropControl;
            for( int i = 0; i < strings.length; i++ ) {
              text.append( strings[ i ] + "\n" );
            }
            break;
          }
          case TREE: {
            Tree tree = ( Tree )dropControl;
            Point p = event.display.map( null, tree, event.x, event.y );
            TreeItem parentItem = tree.getItem( p );
            for( int i = 0; i < strings.length; i++ ) {
              TreeItem item = parentItem != null
                                                ? new TreeItem( parentItem,
                                                                SWT.NONE )
                                                : new TreeItem( tree, SWT.NONE );
              item.setText( strings[ i ] );
            }
            break;
          }
          case CANVAS: {
            dropControl.setData( "STRINGS", strings );
            dropControl.redraw();
            break;
          }
          case LABEL: {
            Label label = ( Label )dropControl;
            label.setText( join( "\n", strings ) );
            break;
          }
          case LIST: {
            List list = ( List )dropControl;
            for( int i = 0; i < strings.length; i++ ) {
              list.add( strings[ i ] );
            }
            break;
          }
          default:
            throw new SWTError( SWT.ERROR_NOT_IMPLEMENTED );
        }
      }

      public void dropAccept( final DropTargetEvent event ) {
        dropConsole.append( ">>dropAccept\n" );
        printEvent( event );
      }
    } );
  }

  private void createFeedbackTypes( final Group parent ) {
    parent.setLayout( new RowLayout( SWT.VERTICAL ) );
    Button b = new Button( parent, SWT.CHECK );
    b.setText( "FEEDBACK_SELECT" );
    b.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected( final SelectionEvent e ) {
        Button b = ( Button )e.widget;
        if( b.getSelection() ) {
          dropFeedback |= DND.FEEDBACK_SELECT;
        } else {
          dropFeedback &= ~DND.FEEDBACK_SELECT;
        }
      }
    } );
    b = new Button( parent, SWT.CHECK );
    b.setText( "FEEDBACK_SCROLL" );
    b.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected( final SelectionEvent e ) {
        Button b = ( Button )e.widget;
        if( b.getSelection() ) {
          dropFeedback |= DND.FEEDBACK_SCROLL;
        } else {
          dropFeedback &= ~DND.FEEDBACK_SCROLL;
        }
      }
    } );
    b = new Button( parent, SWT.CHECK );
    b.setText( "FEEDBACK_INSERT_BEFORE" );
    b.addSelectionListener( new SelectionAdapter() {

      @Override
      public void widgetSelected( final SelectionEvent e ) {
        Button b = ( Button )e.widget;
        if( b.getSelection() ) {
          dropFeedback |= DND.FEEDBACK_INSERT_BEFORE;
        } else {
          dropFeedback &= ~DND.FEEDBACK_INSERT_BEFORE;
        }
      }
    } );
    b = new Button( parent, SWT.CHECK );
    b.setText( "FEEDBACK_INSERT_AFTER" );
    b.addSelectionListener( new SelectionAdapter() {

      @Override
      public void widgetSelected( final SelectionEvent e ) {
        Button b = ( Button )e.widget;
        if( b.getSelection() ) {
          dropFeedback |= DND.FEEDBACK_INSERT_AFTER;
        } else {
          dropFeedback &= ~DND.FEEDBACK_INSERT_AFTER;
        }
      }
    } );
    b = new Button( parent, SWT.CHECK );
    b.setText( "FEEDBACK_EXPAND" );
    b.addSelectionListener( new SelectionAdapter() {

      @Override
      public void widgetSelected( final SelectionEvent e ) {
        Button b = ( Button )e.widget;
        if( b.getSelection() ) {
          dropFeedback |= DND.FEEDBACK_EXPAND;
        } else {
          dropFeedback &= ~DND.FEEDBACK_EXPAND;
        }
      }
    } );
  }

  private void createDropTypes( final Composite parent ) {
    parent.setLayout( new RowLayout( SWT.VERTICAL ) );
    createDropTypeButton( parent, "Text Transfer", TextTransfer.getInstance() )
      .setSelection( true );
    createDropTypeButton( parent, "RTF Transfer", RTFTransfer.getInstance() );
    createDropTypeButton( parent, "HTML Transfer", HTMLTransfer.getInstance() );
    createDropTypeButton( parent, "ClientFile Transfer", ClientFileTransfer.getInstance() );
  }

  private Button createDropTypeButton( Composite parent, String name, final Transfer transfer ) {
    final Button button = new Button( parent, SWT.CHECK );
    button.setText( name );
    button.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected( final SelectionEvent e ) {
        if( button.getSelection() ) {
          addDropTransfer( transfer );
        } else {
          removeDropTransfer( transfer );
        }
      }
    } );
    return button;
  }

  private void createDropWidget( final Composite parent ) {
    parent.setLayout( new FormLayout() );
    Combo combo = new Combo( parent, SWT.READ_ONLY );
    combo.setItems( new String[]{
      "Toggle Button",
      "Radio Button",
      "Checkbox",
      "Canvas",
      "Label",
      "List",
      "Table",
      "Tree",
      "Text"
    } );
    combo.select( LABEL );
    dropControlType = combo.getSelectionIndex();
    dropControl = createWidget( dropControlType, parent, "Drop Target" );
    combo.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected( final SelectionEvent e ) {
        Object data = dropControl.getLayoutData();
        Composite parent = dropControl.getParent();
        dropControl.dispose();
        Combo c = ( Combo )e.widget;
        dropControlType = c.getSelectionIndex();
        dropControl = createWidget( dropControlType, parent, "Drop Target" );
        dropControl.setLayoutData( data );
        updateDropTarget();
        parent.layout();
      }
    } );
    Button b = new Button( parent, SWT.CHECK );
    b.setText( "DropTarget" );
    b.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected( final SelectionEvent e ) {
        dropEnabled = ( ( Button )e.widget ).getSelection();
        updateDropTarget();
      }
    } );
    b.setSelection( dropEnabled );
    FormData data = new FormData();
    data.top = new FormAttachment( 0, 10 );
    data.bottom = new FormAttachment( combo, -10 );
    data.left = new FormAttachment( 0, 10 );
    data.right = new FormAttachment( 100, -10 );
    dropControl.setLayoutData( data );
    data = new FormData();
    data.bottom = new FormAttachment( 100, -10 );
    data.left = new FormAttachment( 0, 10 );
    combo.setLayoutData( data );
    data = new FormData();
    data.bottom = new FormAttachment( 100, -10 );
    data.left = new FormAttachment( combo, 10 );
    b.setLayoutData( data );
    updateDropTarget();
  }

  private void updateDropTarget() {
    if( dropTarget != null ) {
      dropTarget.dispose();
    }
    if( dropEnabled ) {
      createDropTarget();
    } else {
      if( dropTarget != null ) {
        dropTarget.dispose();
      }
      dropTarget = null;
    }
  }

  private Control createWidget( final int type, final Composite parent, final String prefix ) {
    switch( type ) {
      case BUTTON_CHECK: {
        Button button = new Button( parent, SWT.CHECK );
        button.setText( prefix + " Check box" );
        return button;
      }
      case BUTTON_TOGGLE: {
        Button button = new Button( parent, SWT.TOGGLE );
        button.setText( prefix + " Toggle button" );
        return button;
      }
      case BUTTON_RADIO: {
        Button button = new Button( parent, SWT.RADIO );
        button.setText( prefix + " Radio button" );
        return button;
      }
      case TABLE: {
        Table table = new Table( parent, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL  );
        TableColumn column1 = new TableColumn( table, SWT.NONE );
        TableColumn column2 = new TableColumn( table, SWT.NONE );
        for( int i = 0; i < 10; i++ ) {
          TableItem item = new TableItem( table, SWT.NONE );
          item.setText( 0, prefix + " name " + i );
          item.setText( 1, prefix + " value " + i );
        }
        column1.pack();
        column2.pack();
        return table;
      }
      case TEXT: {
        Text text = new Text( parent, SWT.BORDER
                                      | SWT.MULTI
                                      | SWT.V_SCROLL
                                      | SWT.H_SCROLL );
        text.setText( prefix + " Text" );
        return text;
      }
      case TREE: {
        Tree tree = new Tree( parent, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL  );
        for( int i = 0; i < 3; i++ ) {
          TreeItem item = new TreeItem( tree, SWT.NONE );
          item.setText( prefix + " item " + i );
          for( int j = 0; j < 3; j++ ) {
            TreeItem subItem = new TreeItem( item, SWT.NONE );
            subItem.setText( prefix + " item " + j );
            for( int k = 0; k < 3; k++ ) {
              TreeItem subsubItem = new TreeItem( subItem, SWT.NONE );
              subsubItem.setText( prefix + " item " + k );
            }
          }
        }
        return tree;
      }
      case CANVAS: {
        Canvas canvas = new Canvas( parent, SWT.BORDER );
        canvas.setData( "STRINGS", new String[]{
          prefix + " Canvas widget"
        } );
        return canvas;
      }
      case LABEL: {
        Label label = new Label( parent, SWT.BORDER );
        label.setText( prefix + " Label" );
        return label;
      }
      case LIST: {
        List list = new List( parent, SWT.BORDER | SWT.V_SCROLL );
        list.setItems( new String[]{
          prefix + " Item a",
          prefix + " Item b",
          prefix + " Item c",
          prefix + " Item d"
        } );
        return list;
      }
      default:
        throw new SWTError( SWT.ERROR_NOT_IMPLEMENTED );
    }
  }
  private void printEvent( final DragSourceEvent e ) {
    if( !dragEventDetail ) {
      return;
    }
    StringBuilder sb = new StringBuilder();
    sb.append( "widget: " );
    sb.append( e.widget );
    sb.append( ", time: " );
    sb.append( e.time );
    sb.append( ", operation: " );
    sb.append( e.detail );
    sb.append( ", type: " );
    sb.append( e.dataType != null ? e.dataType.type : 0 );
    sb.append( ", doit: " );
    sb.append( e.doit );
    sb.append( ", data: " );
    sb.append( e.data );
    sb.append( "\n" );
    dragConsole.append( sb.toString() );
  }

  private void printEvent( final DropTargetEvent e ) {
    if( !dropEventDetail ) {
      return;
    }
    StringBuilder sb = new StringBuilder();
    sb.append( "widget; " );
    sb.append( e.widget );
    sb.append( ", time: " );
    sb.append( e.time );
    sb.append( ", x: " );
    sb.append( e.x );
    sb.append( ", y: " );
    sb.append( e.y );
    sb.append( ", item: " );
    sb.append( e.item );
    sb.append( ", operations: " );
    sb.append( e.operations );
    sb.append( ", operation: " );
    sb.append( e.detail );
    sb.append( ", feedback: " );
    sb.append( e.feedback );
    if( e.dataTypes != null ) {
      for( int i = 0; i < e.dataTypes.length; i++ ) {
        sb.append( ", dataType " );
        sb.append( i );
        sb.append( ": " );
        sb.append( e.dataTypes[ i ].type );
      }
    } else {
      sb.append( ", dataTypes: none" );
    }
    sb.append( ", currentDataType: " );
    sb.append( e.currentDataType );
    sb.append( ", data: " );
    sb.append( e.data );
    sb.append( "\n" );
    dropConsole.append( sb.toString() );
  }

  private void removeDragTransfer( final Transfer transfer ) {
    if( dragTypes.length == 1 ) {
      dragTypes = new Transfer[ 0 ];
    } else {
      int index = -1;
      for( int i = 0; i < dragTypes.length; i++ ) {
        if( dragTypes[ i ] == transfer ) {
          index = i;
          break;
        }
      }
      if( index == -1 ) {
        return;
      }
      Transfer[] newTypes = new Transfer[ dragTypes.length - 1 ];
      System.arraycopy( dragTypes, 0, newTypes, 0, index );
      System.arraycopy( dragTypes, index + 1, newTypes, index, dragTypes.length
                                                               - index
                                                               - 1 );
      dragTypes = newTypes;
    }
    if( dragSource != null ) {
      dragSource.setTransfer( dragTypes );
    }
  }

  private void removeDropTransfer( final Transfer transfer ) {
    if( dropTypes.length == 1 ) {
      dropTypes = new Transfer[ 0 ];
    } else {
      int index = -1;
      for( int i = 0; i < dropTypes.length; i++ ) {
        if( dropTypes[ i ] == transfer ) {
          index = i;
          break;
        }
      }
      if( index == -1 ) {
        return;
      }
      Transfer[] newTypes = new Transfer[ dropTypes.length - 1 ];
      System.arraycopy( dropTypes, 0, newTypes, 0, index );
      System.arraycopy( dropTypes, index + 1, newTypes, index, dropTypes.length
                                                               - index
                                                               - 1 );
      dropTypes = newTypes;
    }
    if( dropTarget != null ) {
      dropTarget.setTransfer( dropTypes );
    }
  }

    private static String join( String glue, Item[] items ) {
      String[] strings = new String[ items.length ];
      for( int i = 0; i < items.length; i++ ) {
        strings[ i ] = items[ i ].getText();
      }
      return join( glue, strings );
    }

    private static String join( String glue, String[] strings ) {
    StringBuilder stringBuilder = new StringBuilder();
    for( int i = 0; i < strings.length; i++ ) {
      stringBuilder.append( strings[ i ] );
      if( i != strings.length -1 ) {
        stringBuilder.append( glue );
      }
    }
    return stringBuilder.toString();
  }
}