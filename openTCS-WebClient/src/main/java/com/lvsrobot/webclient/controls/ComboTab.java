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

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;


public class ComboTab extends ExampleTab {

  private static final String PROP_SELECTION_LISTENER = "selectionListener";
  private static final String PROP_MODIFY_LISTENER = "modifyListener";

  private static final String[] ITEMS = new String[] {
    "Eiffel",
    "Java",
    "Python",
    "Ruby",
    "Simula",
    "Smalltalk"
  };

  private Combo firstCombo;
  private Combo verifyCombo;
  private Combo viewerCombo;
  private CCombo cCombo;
  private Button createEmptyComboButton;
  private Button preselectFirstItemButton;
  private Button editableButton;
  private boolean empty;
  private boolean preselectItem;

  public ComboTab() {
    super( "Combo" );
  }

  @Override
  protected void createStyleControls( Composite parent ) {
    createStyleButton( "BORDER", SWT.BORDER );
    createStyleButton( "READ_ONLY", SWT.READ_ONLY );
    createStyleButton( "FLAT", SWT.FLAT );
    createOrientationButton();
    createVisibilityButton();
    createEnablementButton();
    createFgColorButton();
    createBgColorButton();
    createFontChooser();
    createCursorCombo();
    createPropertyCheckbox( "Add Selection Listener", PROP_SELECTION_LISTENER );
    createPropertyCheckbox( "Add Modify Listener", PROP_MODIFY_LISTENER );
    Group grpManioulateCCombo = new Group( parent, SWT.NONE );
    grpManioulateCCombo.setText( "Manipulate CCombo" );
    grpManioulateCCombo.setLayout( new GridLayout() );
    createSetTextLimitButton( grpManioulateCCombo, false );
    createChangeSizeButton( grpManioulateCCombo );
    createToggleListVisibilityButton( grpManioulateCCombo );
    createEditableButton( grpManioulateCCombo );
    createTextButton( grpManioulateCCombo );
    Group group = new Group( parent, SWT.NONE );
    group.setText( "Manipulate First Combo" );
    group.setLayout( new GridLayout() );
    createAddButton( group );
    createSetVisibleItemCountButton( group );
    createRemoveAllButton( group );
    createDeselectAllButton( group );
    createRemoveFirstItemButton( group );
    createSelectFirstItemButton( group );
    createEmptyComboButton = new Button( group, SWT.CHECK );
    createEmptyComboButton.setText( "Create Empty Combo" );
    createEmptyComboButton.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected( SelectionEvent e ) {
        if( createEmptyComboButton.getSelection() ) {
          empty = true;
        } else {
          empty = false;
        }
        createNew();
      }
    } );
    preselectFirstItemButton = new Button( group, SWT.CHECK );
    preselectFirstItemButton.setText( "Preselect First Item" );
    preselectFirstItemButton.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected( SelectionEvent e ) {
        if( preselectFirstItemButton.getSelection() ) {
          preselectItem = true;
        } else {
          preselectItem = false;
        }
        createNew();
      }
    } );
    Label selectionTitle = new Label( group, SWT.NONE );
    selectionTitle.setText( "Text selection:" );
    createSetSelectionControls( group );
    createGetSelectionControls( group );
    createSetTextLimitButton( group, true );
  }

  @Override
  protected void createExampleControls( final Composite parent ) {
    parent.setLayout( new GridLayout( 3, false ) );
    int style = getStyle();
    // Standard Combo
    Label lblFilledCombo = new Label( parent, SWT.NONE );
    lblFilledCombo.setText( "Filled Combo" );
    firstCombo = new Combo( parent, style );
    if( !empty ) {
      firstCombo.setItems( ITEMS );
    }
    if( !empty && preselectItem ) {
      firstCombo.select( 0 );
    }
    if( hasCreateProperty( PROP_SELECTION_LISTENER ) ) {
      firstCombo.addSelectionListener( new SelectionAdapter() {
        @Override
        public void widgetSelected( SelectionEvent event ) {
          show( event, false );
        }
        @Override
        public void widgetDefaultSelected( SelectionEvent event ) {
          show( event, true );
        }
        private void show( SelectionEvent event, boolean defaultSelection ) {
          String type = defaultSelection ? "DefaultSelection" : "Selection";
          String msg = type + ": " + event + "\n"
                     + "Text: " + firstCombo.getText() + "\n"
                     + "Selection: " + firstCombo.getSelectionIndex();
          log( msg );
        }
      } );
    }
    if( hasCreateProperty( PROP_MODIFY_LISTENER ) ) {
      firstCombo.addModifyListener( new ModifyListener() {
        @Override
        public void modifyText( ModifyEvent event ) {
          log( event.toString() );
        }
      } );
    }
    Button btnShowSelection = new Button( parent, SWT.PUSH );
    btnShowSelection.setText( "Show Selection" );
    btnShowSelection.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected( SelectionEvent event ) {
        showSelection( firstCombo.getItems(), firstCombo.getSelectionIndex() );
      }
    } );
    // -- verify listener --
    Label lblVerifyCombo = new Label( parent, SWT.NONE );
    lblVerifyCombo.setText( "Combo with VerifyListener (only 0-9 allowed)" );
    verifyCombo = new Combo( parent, style );
    verifyCombo.setLayoutData( createGridDataWithSpan( SWT.NONE, 2 ) );
    verifyCombo.add( "0" );
    verifyCombo.add( "1" );
    verifyCombo.add( "2" );
    verifyCombo.add( "3" );
    verifyCombo.addVerifyListener( new VerifyListener() {
      @Override
      public void verifyText( VerifyEvent event ) {
        StringBuffer allowedText = new StringBuffer();
        for( int i = 0; i < event.text.length(); i++ ) {
          char ch = event.text.charAt( i );
          if( ch >= '0' && ch <= '9' ) {
            allowedText.append( ch );
          }
        }
        event.text = allowedText.toString();
      }
    } );
    // Viewer Combo
    Label lblViewerCombo = new Label( parent, SWT.NONE );
    String msg = "ComboViewer with context menu";
    lblViewerCombo.setText( msg );
    viewerCombo = new Combo( parent, style );
    viewerCombo.setLayoutData( createGridDataWithSpan( SWT.NONE, 2 ) );
    ComboViewer viewer = new ComboViewer( viewerCombo );
    viewer.setContentProvider( new IStructuredContentProvider() {
      @Override
      public void dispose() {
      }
      @Override
      public void inputChanged( Viewer viewer, Object oldIn, Object newIn ) {
      }
      @Override
      public Object[] getElements( Object inputElement ) {
        return ( Object[] )inputElement;
      }
    } );
    viewer.setLabelProvider( new LabelProvider() );
    viewer.setInput( ITEMS );
    viewer.addSelectionChangedListener( new ISelectionChangedListener() {
      @Override
      public void selectionChanged( SelectionChangedEvent event ) {
        String message = "Selected item: " + event.getSelection().toString();
        Shell shell = parent.getShell();
        MessageDialog.openInformation( shell, "Info", message );
      }
    } );
    Menu menu = new Menu( viewerCombo );
    MenuItem menuItem = new MenuItem( menu, SWT.NONE );
    menuItem.setText( "MenuItem on ComboViewer" );
    viewerCombo.setMenu( menu );
    // Separator
    int separatorStyle = SWT.SEPARATOR | SWT.HORIZONTAL | SWT.SHADOW_OUT;
    Label separator = new Label( parent, separatorStyle );
    separator.setLayoutData( createGridDataWithSpan( GridData.FILL_HORIZONTAL, 3 ) );
    // CCombo
    Label lblCCombo = new Label( parent, SWT.NONE );
    lblCCombo.setText( "CCombo" );
    cCombo = new CCombo( parent, style );
    cCombo.setItems( ITEMS );
    cCombo.setEditable( checkControl( editableButton ) ? editableButton.getSelection() : true );
    if( hasCreateProperty( PROP_SELECTION_LISTENER ) ) {
      cCombo.addSelectionListener( new SelectionAdapter() {
        @Override
        public void widgetSelected( SelectionEvent event ) {
          show( event, false );
        }
        @Override
        public void widgetDefaultSelected( SelectionEvent event ) {
          show( event, true );
        }
        private void show( SelectionEvent event, boolean defaultSelection ) {
          String type = defaultSelection ? "DefaultSelection" : "Selection";
          String msg = type + ": " + event + "\n"
                     + "Text: " + cCombo.getText() + "\n"
                     + "Selection: " + cCombo.getSelectionIndex();
          log( msg );
        }
      } );
    }
    Button btnShowSelectionCCombo = new Button( parent, SWT.PUSH );
    btnShowSelectionCCombo.setText( "Show Selection" );
    btnShowSelectionCCombo.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected( SelectionEvent event ) {
        showSelection( cCombo.getItems(), cCombo.getSelectionIndex() );
      }
    } );
    // Register com.lvsrobot.webclient.controls
    registerControl( firstCombo );
    registerControl( verifyCombo );
    registerControl( viewerCombo );
    registerControl( cCombo );
  }

  private void createAddButton( Composite parent ) {
    Composite composite = new Composite( parent, SWT.NONE );
    composite.setLayout( new GridLayout( 3, false ) );
    Label lblAddItem = new Label( composite, SWT.NONE );
    lblAddItem.setText( "Add Item" );
    final Text txtAddItem = new Text( composite, SWT.BORDER );
    txtAddItem.setText( "New Item" );
    Button btnAddItem = new Button( composite, SWT.PUSH );
    btnAddItem.setText( "Add" );
    btnAddItem.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected( SelectionEvent event ) {
        firstCombo.add( txtAddItem.getText() );
      }
    } );
  }

  private void createRemoveAllButton( Composite parent ) {
    Button button = new Button( parent , SWT.PUSH );
    button.setText( "Remove All Items" );
    button.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected( SelectionEvent event ) {
        firstCombo.removeAll();
      }
    } );
  }

  private void createDeselectAllButton( Composite parent ) {
    Button button = new Button( parent , SWT.PUSH );
    button.setText( "Deselect All Items" );
    button.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected( SelectionEvent event ) {
        firstCombo.deselectAll();
      }
    } );
  }

  private void createRemoveFirstItemButton( Composite parent ) {
    Button button = new Button( parent , SWT.PUSH );
    button.setText( "Remove First Item" );
    button.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected( SelectionEvent event ) {
        if( firstCombo.getItemCount() > 0 ) {
          firstCombo.remove( 0 );
        }
      }
    } );
  }

  private void createSelectFirstItemButton( Composite parent ) {
    Button button = new Button( parent , SWT.PUSH );
    button.setText( "Select First Item" );
    button.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected( SelectionEvent event ) {
        if( firstCombo.getItemCount() > 0 ) {
          firstCombo.select( 0 );
        }
      }
    } );
  }

  private void createSetVisibleItemCountButton( Composite parent ) {
    Composite composite = new Composite( parent, SWT.NONE );
    composite.setLayout( new GridLayout( 3, false ) );
    final Text text = new Text( composite, SWT.BORDER | SWT.SINGLE );
    text.setText( "3" );
    text.setLayoutData( new GridData( 20, SWT.DEFAULT ) );
    Button button = new Button( composite, SWT.PUSH );
    button.setText( "Set Visible Item Count" );
    button.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected( SelectionEvent event ) {
        try {
          int vic = Integer.parseInt( text.getText() );
          firstCombo.setVisibleItemCount( vic );
        } catch( NumberFormatException e ) {
        }
      }
    } );
  }

  private void createSetSelectionControls( Composite parent ) {
    Composite composite = new Composite( parent, SWT.NONE );
    composite.setLayout( new GridLayout( 5, false ) );
    Label lblSelectionFrom = new Label( composite, SWT.NONE );
    lblSelectionFrom.setText( "From" );
    final Text txtSelectionFrom = new Text( composite, SWT.BORDER | SWT.SINGLE );
    txtSelectionFrom.setTextLimit( 2 );
    txtSelectionFrom.setText( "0" );
    txtSelectionFrom.setLayoutData( new GridData( 20, SWT.DEFAULT ) );
    Label lblSelectionTo = new Label( composite, SWT.NONE );
    lblSelectionTo.setText( "to" );
    final Text txtSelectionTo = new Text( composite, SWT.BORDER | SWT.SINGLE );
    txtSelectionTo.setTextLimit( 2 );
    txtSelectionTo.setText( "0" );
    txtSelectionTo.setLayoutData( new GridData( 20, SWT.DEFAULT ) );
    Button btnSetSelection = new Button( composite, SWT.PUSH );
    btnSetSelection.setText( "set" );
    btnSetSelection.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected( SelectionEvent event ) {
        int from = parseInt( txtSelectionFrom.getText() );
        int to = parseInt( txtSelectionTo.getText() );
        if( from >= 0 && to >= 0 ) {
          firstCombo.setSelection( new Point( from, to ) );
          firstCombo.setFocus();
        } else {
          MessageDialog.openError( getShell(), "Error", "Invalid Selection" );
        }
      }
    } );
  }

  private void createGetSelectionControls( Composite parent ) {
    Composite composite = new Composite( parent, SWT.NONE );
    composite.setLayout( new GridLayout( 2, false ) );
    final Text outputSelection =  new Text( composite, SWT.BORDER );
    outputSelection.setEditable( false );
    Button btnGetSelection = new Button( composite, SWT.PUSH );
    btnGetSelection.setText( "get" );
    btnGetSelection.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected( SelectionEvent event ) {
        Point selection = firstCombo.getSelection();
        outputSelection.setText( selection.x + ", " + selection.y );
      }
    } );
  }

  private int parseInt( String text ) {
    int result;
    try {
      result = Integer.parseInt( text );
    } catch( NumberFormatException e ) {
      result = -1;
    }
    return result;
  }

  private void createSetTextLimitButton( Composite parent, final boolean isCombo ) {
    Composite composite = new Composite( parent, SWT.NONE );
    composite.setLayout( new GridLayout( 2, false ) );
    final Text text = new Text( composite, SWT.BORDER | SWT.SINGLE );
    text.setText( "5" );
    text.setLayoutData( new GridData( 20, SWT.DEFAULT ) );
    Button button = new Button( composite, SWT.PUSH );
    button.setText( "Set Text Limit" );
    button.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected( SelectionEvent event ) {
        try {
          int textLimit = Integer.parseInt( text.getText() );
          if( isCombo ) {
            firstCombo.setTextLimit( textLimit );
          } else {
            cCombo.setTextLimit( textLimit );
          }
        } catch( NumberFormatException e ) {
        }
      }
    } );
  }

  private void createChangeSizeButton( Composite parent ) {
    Button button = new Button( parent, SWT.PUSH );
    button.setText( "Change Size" );
    button.addSelectionListener( new SelectionAdapter() {
      private boolean customSize;
      @Override
      public void widgetSelected( SelectionEvent event ) {
        if ( customSize ) {
          GridData gridData = new GridData( SWT.DEFAULT, SWT.DEFAULT );
          cCombo.setLayoutData( gridData );
          customSize = false;
        } else {
          GridData gridData = new GridData( 100, 100 );
          cCombo.setLayoutData( gridData );
          customSize = true;
        }
        Composite parent = controls.get( 0 ).getParent();
        parent.layout( true, true );
      }
    } );
  }

  private void createToggleListVisibilityButton( Composite parent ) {
    Button button = new Button( parent, SWT.PUSH );
    button.setText( "Toggle List Visibility" );
    button.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected( SelectionEvent event ) {
        boolean listVisible = cCombo.getListVisible();
        cCombo.setListVisible( !listVisible );
      }
    } );
  }

  private void createEditableButton( Composite parent ) {
    editableButton = new Button( parent, SWT.CHECK );
    editableButton.setText( "Editable" );
    editableButton.setSelection( true );
    editableButton.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected( SelectionEvent e ) {
        cCombo.setEditable( editableButton.getSelection() );
      }
    } );
  }

  private void createTextButton( Composite parent ) {
    Button button = new Button( parent, SWT.PUSH );
    button.setText( "Change text" );
    button.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected( SelectionEvent event ) {
        cCombo.setText( "foo" );
      }
    } );
  }

  private void showSelection( String[] items, int selectionIndex ) {
    String selection = "(nothing)";
    if( selectionIndex != -1 ) {
      selection = items[ selectionIndex ];
    }
    String msg = "Your Selection: " + selection;
    MessageDialog.openInformation( getShell(), "Information", msg );
  }

  private GridData createGridDataWithSpan( int style, int span ) {
    GridData gridData = new GridData( style );
    gridData.horizontalSpan = span;
    return gridData;
  }

}
