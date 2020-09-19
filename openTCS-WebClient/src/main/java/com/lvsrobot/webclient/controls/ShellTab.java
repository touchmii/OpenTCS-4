/*******************************************************************************
 * Copyright (c) 2002, 2015 Innoopract Informationssysteme GmbH.
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
import java.util.Iterator;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;


public class ShellTab extends ExampleTab {

  private static final String ICON_IMAGE_PATH = "resources/newfile_wiz.gif";

  private final java.util.List<Shell> shells;
  private int shellCounter;
  private final ShellAdapter confirmCloseListener;
  private Image shellImage;
  private Button createRTLButton;
  private Button createInvisibleButton;
  private Button createAsDialogButton;
  private Button createWithMenuButton;
  private Button showClientAreaButton;
  private Button confirmCloseButton;
//  private Button customBgColorButton;

  private int alpha = 255;

  public ShellTab() {
    super( "Shell" );
    shells = new ArrayList<Shell>();
    confirmCloseListener = new ShellAdapter() {
      @Override
      public void shellClosed( final ShellEvent event ) {
        Shell shell = ( Shell )event.widget;
        String msg = "Close " + shell.getText() + "?";
        boolean canClose = MessageDialog.openConfirm( shell, "Confirm", msg );
        event.doit = canClose;
      }
    };
    setDefaultStyle( SWT.SHELL_TRIM );
  }

  @Override
  protected void createStyleControls( Composite parent ) {
    createStyleButton( "BORDER", SWT.BORDER );
    createStyleButton( "SHELL_TRIM", SWT.SHELL_TRIM, true );
    createStyleButton( "DIALOG_TRIM", SWT.DIALOG_TRIM );
    createStyleButton( "APPLICATION_MODAL", SWT.APPLICATION_MODAL );
    createStyleButton( "PRIMARY_MODAL", SWT.PRIMARY_MODAL );
    createStyleButton( "TITLE", SWT.TITLE );
    createStyleButton( "MIN", SWT.MIN );
    createStyleButton( "MAX", SWT.MAX );
    createStyleButton( "CLOSE", SWT.CLOSE );
    createStyleButton( "RESIZE", SWT.RESIZE );
    createStyleButton( "TOOL", SWT.TOOL );
    createStyleButton( "SHEET", SWT.SHEET );
    createStyleButton( "ON_TOP", SWT.ON_TOP );
    createRTLButton = createOrientationButton();
    createInvisibleButton = createPropertyButton( "Create invisible" );
    createAsDialogButton = createPropertyButton( "Create as dialog" );
    createWithMenuButton = createPropertyButton( "Add menu" );
    showClientAreaButton = createPropertyButton( "Show client area" );
    confirmCloseButton = createPropertyButton( "Confirm Close" );
//    customBgColorButton = createPropertyButton( "Custom background" );
    createAlphaControls( parent );
  }

  @Override
  protected void createExampleControls( Composite parent ) {
    parent.setLayout( new RowLayout( SWT.VERTICAL ) );
    if( shellImage == null ) {
      shellImage = Util.loadImage( parent.getDisplay(), ICON_IMAGE_PATH );
    }
    Button openShellButton = new Button( parent, SWT.PUSH );
    openShellButton.setText( "Open Shell" );
    openShellButton.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected( final SelectionEvent event ) {
        createShell();
      }} );

    Button bringFirstToTopButton = new Button( parent, SWT.PUSH );
    bringFirstToTopButton.setText( "Bring first Shell to top" );
    bringFirstToTopButton.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected( SelectionEvent event ) {
        Shell[] shells = getShells();
        if( shells.length > 0 ) {
          shells[ 0 ].open();
        }
      }
    } );

    Button showAllButton = new Button( parent, SWT.PUSH );
    showAllButton.setText( "Show All Shells" );
    showAllButton.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected( SelectionEvent event ) {
        setShellsVisible( true );
      }
    } );

    Button hideAllButton = new Button( parent, SWT.PUSH );
    hideAllButton.setText( "Hide All Shells" );
    hideAllButton.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected( SelectionEvent event ) {
        setShellsVisible( false );
      }
    } );

    Button MaximizeAllButton = new Button( parent, SWT.PUSH );
    MaximizeAllButton.setText( "Maximize All Shells" );
    MaximizeAllButton.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected( SelectionEvent event ) {
        setShellsMaximized( true );
      }
    } );

    Button minimizeAllButton = new Button( parent, SWT.PUSH );
    minimizeAllButton.setText( "Minimize All Shells" );
    minimizeAllButton.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected( SelectionEvent event ) {
        setShellsMinimized( true );
      }
    } );

    Button restoreAllButton = new Button( parent, SWT.PUSH );
    restoreAllButton.setText( "Restore All Shells" );
    restoreAllButton.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected( SelectionEvent event ) {
        setShellsMinimized( false );
        setShellsMaximized( false );
      }
    } );

    Button enableAllButton = new Button( parent, SWT.PUSH );
    enableAllButton.setText( "Enable All Shells" );
    enableAllButton.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected( SelectionEvent event ) {
        setShellsEnabled( true );
      }
    } );

    Button disableAllButton = new Button( parent, SWT.PUSH );
    disableAllButton.setText( "Disable All Shells" );
    disableAllButton.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected( SelectionEvent event ) {
        setShellsEnabled( false );
      }
    } );

    Button closeAllButton = new Button( parent, SWT.PUSH );
    closeAllButton.setText( "Close All Shells" );
    closeAllButton.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected( SelectionEvent event ) {
        closeShells();
      }} );
  }

  private void createAlphaControls( Composite parent ) {
    Composite comp = new Composite( parent, SWT.NONE );
    comp.setLayout( new GridLayout( 3, false ) );
    Label label = new Label( comp, SWT.NONE );
    label.setText( "Alpha" );
    final Text text = new Text( comp, SWT.BORDER );
    text.setText( "255" );
    Button button = new Button( comp, SWT.PUSH );
    button.setText( "set" );
    button.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected( SelectionEvent event ) {
        try {
          alpha = Integer.parseInt( text.getText() );
        } catch( NumberFormatException ignore ) {
          // ignore
        }
      }
    } );
  }

  private void createShell() {
    Shell shell;
    if( createAsDialogButton.getSelection() ) {
      shell = new Shell( getShell(), getStyle() );
    } else {
      shell = new Shell( getShell().getDisplay(), getStyle() );
    }
    if( createRTLButton.getSelection() ) {
      shell.setOrientation( SWT.RIGHT_TO_LEFT );
    }
    shell.setLocation( getNextShellLocation() );
    createShellContents( shell );
    shellCounter++;
    shell.setText( "Test Shell " + shellCounter );
    shell.setAlpha( alpha );
    shell.setImage( shellImage );
    if( confirmCloseButton.getSelection() ) {
      shell.addShellListener( confirmCloseListener );
    }
    if( !createInvisibleButton.getSelection() ) {
      shell.open();
    }
    shells.add( shell );
  }

  private void createShellContents( final Shell shell ) {
    shell.setSize( 300, 200 );
    if( createWithMenuButton.getSelection() ) {
      createMenuBar( shell );
    }
    final Composite comp1 = new Composite( shell, SWT.NONE );
    final Composite comp2 = new Composite( shell, SWT.NONE );
    comp2.moveAbove( comp1 );
    if( showClientAreaButton.getSelection() ) {
      comp1.setBackground( new Color( shell.getDisplay(), 200, 0, 0 ) );
      comp2.setBackground( new Color( shell.getDisplay(), 200, 200, 200 ) );
    }
    Rectangle area = shell.getClientArea();
    comp1.setBounds( area.x, area.y, area.width, area.height );
    comp2.setBounds( area.x + 1, area.y + 1, area.width - 2, area.height - 2 );
    final Button closeButton = new Button( comp2, SWT.PUSH );
    closeButton.setText( "Close This Shell" );
    closeButton.pack();
    int centerX = ( area.width - area.x ) / 2;
    closeButton.setLocation( centerX - closeButton.getSize().x / 2, area.height - 45 );
    closeButton.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected( final SelectionEvent event ) {
        shell.close();
      }
    } );
    shell.addControlListener( new ControlAdapter() {
      @Override
      public void controlResized( final ControlEvent event ) {
        Rectangle area = shell.getClientArea();
        comp1.setBounds( area.x, area.y, area.width, area.height );
        comp2.setBounds( area.x + 1, area.y + 1, area.width - 2, area.height - 2 );
        int centerX = ( area.width - area.x ) / 2;
        closeButton.setLocation( centerX - closeButton.getSize().x / 2, area.height - 45 );
      }
    } );
  }

  private void createMenuBar( Shell shell ) {
    // menu bar
    Menu menuBar = new Menu( shell, SWT.BAR );
    shell.setMenuBar( menuBar );
    MenuItem fileItem = new MenuItem( menuBar, SWT.CASCADE );
    fileItem.setText( "File" );
    MenuItem editItem = new MenuItem( menuBar, SWT.CASCADE );
    editItem.setText( "Edit" );
    MenuItem searchItem = new MenuItem( menuBar, SWT.CASCADE );
    searchItem.setText( "Search" );
    MenuItem disabledItem = new MenuItem( menuBar, SWT.CASCADE );
    disabledItem.setText( "Disabled" );
    disabledItem.setEnabled( false );
    new MenuItem( menuBar, SWT.CASCADE ).setText( "Item 6" );
    new MenuItem( menuBar, SWT.CASCADE ).setText( "Item 7" );
    new MenuItem( menuBar, SWT.CASCADE ).setText( "Item 8" );
    new MenuItem( menuBar, SWT.CASCADE ).setText( "Item 9" );
    // file menu
    Menu fileMenu = new Menu( shell, SWT.DROP_DOWN );
    fileItem.setMenu( fileMenu );
    MenuItem newItem = new MenuItem( fileMenu, SWT.PUSH );
    newItem.setText( "&New\tStrg+FOO" );
    newItem.setImage( Util.loadImage( shell.getDisplay(), "resources/newfile_wiz.gif" ) );
    new MenuItem( fileMenu, SWT.PUSH ).setText( "Open" );
    new MenuItem( fileMenu, SWT.PUSH ).setText( "Close" );
    // edit menu
    Menu editMenu = new Menu( shell, SWT.DROP_DOWN );
    editItem.setMenu( editMenu );
    MenuItem item;
    new MenuItem( editMenu, SWT.PUSH ).setText( "Copy" );
    new MenuItem( editMenu, SWT.PUSH ).setText( "Paste" );
    new MenuItem( editMenu, SWT.SEPARATOR );
    // cascade menu
    item = new MenuItem( editMenu, SWT.CASCADE );
    item.setText( "Insert" );
    Menu cascadeMenu = new Menu( shell, SWT.DROP_DOWN );
    item.setMenu( cascadeMenu );
    new MenuItem( cascadeMenu, SWT.PUSH ).setText( "Date" );
    new MenuItem( cascadeMenu, SWT.PUSH ).setText( "Line Break" );
    // search
    Menu searchMenu = new Menu( shell, SWT.DROP_DOWN );
    searchItem.setMenu( searchMenu );
    new MenuItem( searchMenu, SWT.PUSH ).setText( "Enabled" );
    item = new MenuItem( searchMenu, SWT.PUSH );
    item.setText( "Disabled" );
    item.setEnabled( false );
    item = new MenuItem( searchMenu, SWT.PUSH );
    item.setText( "Push\tCtrl+Shift+P" );
    item.setAccelerator( SWT.MOD1 + SWT.MOD2 + 'P' );
    new MenuItem( searchMenu, SWT.SEPARATOR );
    item = new MenuItem( searchMenu, SWT.CHECK );
    item.setText( "Check\tCtrl+Shift+C" );
    item.setAccelerator( SWT.MOD1 + SWT.MOD2 + 'C' );
    item = new MenuItem( searchMenu, SWT.RADIO );
    item.setText( "Radio 1\tCtrl+Shift+1" );
    item.setAccelerator( SWT.MOD1 + SWT.MOD2 + '1' );
    item = new MenuItem( searchMenu, SWT.RADIO );
    item.setText( "Radio 2\tCtrl+Shift+2" );
    item.setAccelerator( SWT.MOD1 + SWT.MOD2 + '2' );
    item = new MenuItem( searchMenu, SWT.RADIO );
    item.setText( "Radio 3\tCtrl+Shift+3" );
    item.setAccelerator( SWT.MOD1 + SWT.MOD2 + '3' );
    item.setEnabled( false );
    // disabled
    Menu disabledMenu = new Menu( shell, SWT.DROP_DOWN );
    disabledMenu.setEnabled( false );
    disabledItem.setMenu( disabledMenu );
    new MenuItem( disabledMenu, SWT.PUSH ).setText( "Import" );
    new MenuItem( disabledMenu, SWT.PUSH ).setText( "Export" );
  }

  private Point getNextShellLocation() {
    Point result = getShell().getLocation();
    int count = getShells().length % 12;
    result.x += 50 + count * 10;
    result.y += 50 + count * 10;
    return result ;
  }

  private void closeShells() {
    Shell[] shells2 = getShells();
    for( int i = 0; i < shells2.length; i++ ) {
      shells2[ i ].removeShellListener( confirmCloseListener );
      shells2[ i ].close();
      shells2[ i ].dispose();
    }
  }

  private void setShellsVisible( boolean visible ) {
    Shell[] shells = getShells();
    for( int i = 0; i < shells.length; i++ ) {
      shells[ i ].setVisible( visible );
    }
  }

  private void setShellsEnabled( boolean enabled ) {
    Shell[] shells = getShells();
    for( int i = 0; i < shells.length; i++ ) {
      shells[ i ].setEnabled( enabled );
    }
  }

  private void setShellsMinimized( boolean minimized ) {
    Shell[] shells = getShells();
    for( int i = 0; i < shells.length; i++ ) {
      shells[ i ].setMinimized( minimized );
    }
  }

  private void setShellsMaximized( boolean maximized ) {
    Shell[] shells = getShells();
    for( int i = 0; i < shells.length; i++ ) {
      shells[ i ].setMaximized( maximized );
    }
  }

  private Shell[] getShells() {
    // remove eventually disposed of shells (may happen when shells without
    // ShellListeners are created and close by user interaction)
    Shell[] result;
    Iterator<Shell> iter = shells.iterator();
    while( iter.hasNext() ) {
      Shell shell = iter.next();
      if( shell.isDisposed() ) {
        iter.remove();
      }
    }
    result = new Shell[ shells.size() ];
    shells.toArray( result );
    return result;
  }
}
