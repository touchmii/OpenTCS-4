/*******************************************************************************
 * Copyright (c) 2013 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package com.lvsrobot.webclient.controls;

import org.eclipse.rap.rwt.RWT;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;


public class MnemonicsTab extends ExampleTab {

  private static final String DEFAULT_ACTIVATOR = "CTRL+ALT";
  private static final String[] DEFAULT_SHORTCUT = new String[]{ "CTRL+ALT+Y" };
  protected boolean useCTabFolder;

  public MnemonicsTab() {
    super( "Mnemonics" );
  }

  @Override
  protected void createStyleControls( Composite parent ) {
    createActivatorControls( parent );
    createShortcutControls( parent );
    createCTabFolderButton( parent );
  }

  private void createShortcutControls( Composite parent ) {
    final Display display = parent.getDisplay();
    display.setData( RWT.ACTIVE_KEYS, DEFAULT_SHORTCUT );
    display.setData( RWT.CANCEL_KEYS, DEFAULT_SHORTCUT );
    final Text shortcutText = new Text( parent, SWT.BORDER );
    shortcutText.setText( DEFAULT_SHORTCUT[ 0 ] );
    shortcutText.setLayoutData( new RowData( 110, SWT.DEFAULT ) );
    Button setShortcut = new Button( parent, SWT.PUSH );
    setShortcut.setText( "Set global shortcut" );
    setShortcut.addListener( SWT.Selection, new Listener() {
      public void handleEvent( Event event ) {
        String[] shortcut = new String[]{ shortcutText.getText() };
        display.setData( RWT.ACTIVE_KEYS, shortcut );
        display.setData( RWT.CANCEL_KEYS, shortcut );
      }
    } );
    display.addFilter( SWT.KeyDown, new Listener() {
      public void handleEvent( Event event ) {
        log( event.toString() );
      }
    } );
  }

  private void createActivatorControls( Composite parent ) {
    final Display display = parent.getDisplay();
    display.setData( RWT.MNEMONIC_ACTIVATOR, DEFAULT_ACTIVATOR );
    final Text activatorText = new Text( parent, SWT.BORDER );
    activatorText.setText( DEFAULT_ACTIVATOR );
    activatorText.setLayoutData( new RowData( 110, SWT.DEFAULT ) );
    Button setActivator = new Button( parent, SWT.PUSH );
    setActivator.setText( "Set activator" );
    setActivator.addListener( SWT.Selection, new Listener() {
      public void handleEvent( Event event ) {
        display.setData( RWT.MNEMONIC_ACTIVATOR, activatorText.getText() );
      }
    } );
  }

  private void createCTabFolderButton( Composite parent ) {
    final Button ctabFolderButton = new Button( parent, SWT.TOGGLE );
    ctabFolderButton.setText( "Use CTabFolder" );
    ctabFolderButton.addListener( SWT.Selection, new Listener() {
      public void handleEvent( Event event ) {
        useCTabFolder = ctabFolderButton.getSelection();
        createNew();
      }
    } );
  }

  @Override
  protected void createExampleControls( Composite parent ) {
    parent.setLayout( new FillLayout() );
    int tabCount = 4;
    if( useCTabFolder ) {
      CTabFolder folder = new CTabFolder( parent, getStyle() );
      CTabItem[] tabItems = new CTabItem[ tabCount ];
      for( int i = 0; i < tabCount; i++ ) {
        tabItems[ i ] = new CTabItem( folder, SWT.NONE );
        tabItems[ i ].setText( "CTabItem &" + ( i + 1) );
        Composite content = createItemContent( folder, i );
        tabItems[ i ].setControl( content );
      }
      folder.setSelection( 0 );
    } else {
      TabFolder folder = new TabFolder( parent, getStyle() );
      TabItem[] tabItems = new TabItem[ tabCount ];
      for( int i = 0; i < tabCount; i++ ) {
        tabItems[ i ] = new TabItem( folder, SWT.NONE );
        tabItems[ i ].setText( "TabItem &" + ( i + 1 ) );
        Composite content = createItemContent( folder, i );
        tabItems[ i ].setControl( content );
      }
    }
  }

  private Composite createItemContent( Composite folder, int index ) {
    Composite content = new Composite( folder, SWT.NONE );
    content.setLayout( new GridLayout( 4, false ) );
    switch( index ) {
      case 0:
        createButtonExample( content );
      break;
      case 1:
        createToolBarExample( content );
        createLabelExample( content );
      break;
      case 2:
        createGroupExample( content );
      break;
      case 3:
        createMenuExample( content );
      break;
    }
    return content;
  }

  private void createMenuExample( final Composite content ) {
    final Button button = new Button( content, SWT.PUSH );
    button.setText( "Open Shell with Menu" );
    button.addListener( SWT.Selection, new Listener() {
      public void handleEvent( Event event ) {
        createShellWithMenu( content );
      }
    } );
  }

  private void createShellWithMenu( Composite content ) {
    final Shell shell = new Shell( content.getShell(), SWT.BORDER  );
    shell.setLayout( new GridLayout() );
    createMenuBar( shell );
    // Bug? - Without a widget in the shell it doesn't get correctly focused
    Button closeButton = new Button( shell, SWT.PUSH );
    closeButton.setText( "Close" );
    closeButton.addListener( SWT.Selection, new Listener() {
      public void handleEvent( Event event ) {
        shell.dispose();
      }
    } );
    shell.setLocation( content.toDisplay( 0, 0 ) );
    shell.setSize( content.getSize() );
    shell.open();
    content.addListener( SWT.Dispose, new Listener() {
      public void handleEvent( Event event ) {
        shell.dispose();
      }
    } );
  }

  private void createMenuBar( final Shell shell) {
    String[] items = new String[] {
      "&File", "&Edit", "&Lazy", "E&xit"
    };
    Menu bar = new Menu( shell, SWT.BAR );
    shell.setMenuBar( bar );
    for( String text : items ) {
      MenuItem menuItem = new MenuItem( bar, SWT.CASCADE );
      menuItem.setText( text );
      Menu dropdown = new Menu( shell, SWT.DROP_DOWN );
      menuItem.setMenu( dropdown );
    }
    MenuItem pushOne
      = createMenuItem( bar.getItem( 0 ).getMenu(), SWT.PUSH, "Push &One\tCtrl+Shift+1" );
    pushOne.setAccelerator( SWT.CTRL | SWT.SHIFT | '1' );
    MenuItem pushTwo
      = createMenuItem( bar.getItem( 0 ).getMenu(), SWT.PUSH, "Push &Two\tCtrl+Shift+2" );
    pushTwo.setAccelerator( SWT.CTRL | SWT.SHIFT | '2' );
    MenuItem check = createMenuItem( bar.getItem( 0 ).getMenu(), SWT.CHECK, "&Check\tCtrl+C" );
    check.setAccelerator( SWT.CTRL | 'C' );
    createMenuItem( bar.getItem( 0 ).getMenu(), SWT.SEPARATOR, "Separator &W" );
    createMenuItem( bar.getItem( 0 ).getMenu(), SWT.RADIO, "Radio &X" );
    createMenuItem( bar.getItem( 0 ).getMenu(), SWT.RADIO, "Radio &Y" );
    createMenuItem( bar.getItem( 0 ).getMenu(), SWT.RADIO, "Radio &Z" );
    createMenuItem( bar.getItem( 1 ).getMenu(), SWT.PUSH, "Push &Three" );
    MenuItem casc = createMenuItem( bar.getItem( 1 ).getMenu(), SWT.CASCADE, "&Submenu" );
    Menu submenu = new Menu( shell, SWT.DROP_DOWN );
    casc.setMenu( submenu );
    createMenuItem( submenu, SWT.CHECK, "Ch&eck" );
    createMenuItem( submenu, SWT.RADIO, "Radio &8" );
    createMenuItem( submenu, SWT.RADIO, "Radio &9" );
    createMenuItem( bar.getItem( 2 ).getMenu(), SWT.PUSH, "Default &Item" );
    crateLazyMenu( bar.getItem( 2 ) );
    MenuItem close = createMenuItem( bar.getItem( 3 ).getMenu(), SWT.PUSH, "Close &Shell" );
    close.addListener( SWT.Selection, new Listener() {
      public void handleEvent( Event event ) {
        shell.dispose();
      }
    } );
  }

  private void crateLazyMenu( MenuItem item ) {
    final Menu menu = item.getMenu();
    menu.addListener( SWT.Show, new Listener() {
      public void handleEvent( Event event ) {
        menu.getItem( 0 ).dispose();
        createMenuItem( menu, SWT.PUSH, "&Generated Item" );
      }
    } );
  }

  private MenuItem createMenuItem( Menu menu, int style, String text ) {
    final MenuItem item = new MenuItem( menu, style );
    item.setText( text );
    item.addListener( SWT.Selection, new Listener() {
      public void handleEvent( Event e ) {
        log( item.getText() );
      }
    } );
    return item;
  }

  private void createButtonExample( Composite content ) {
    createButton( content, SWT.PUSH, "Push &One" );
    createButton( content, SWT.PUSH, "Push &Two" );
    createButton( content, SWT.PUSH, "Push &Three" );
    createButton( content, SWT.TOGGLE, "To&ggle" );
    createButton( content, SWT.CHECK, "&Checkbox" );
    createButton( content, SWT.RADIO, "Radio &X" );
    createButton( content, SWT.RADIO, "Radio &Y" );
    createButton( content, SWT.RADIO, "Radio &Z" );
  }

  private void createToolBarExample( Composite content ) {
    Label label = new Label( content, SWT.NONE );
    label.setText( "ToolBar:" );
    ToolBar bar = new ToolBar( content, SWT.BORDER );
    createToolItem( bar, SWT.PUSH, "Push &Three" );
    createToolItem( bar, SWT.CHECK, "Toggl&e" );
    createToolItem( bar, SWT.RADIO, "Radio &8" );
    createToolItem( bar, SWT.RADIO, "Radio &9" );
    GridData layoutData = new GridData();
    layoutData.horizontalSpan = 3;
    bar.setLayoutData( layoutData );
  }

  private void createGroupExample( Composite content ) {
    content.setLayout(new GridLayout( 3, false ) );
    Group groupA = new Group( content, SWT.SHADOW_IN );
    groupA.setText( "Group &A" );
    groupA.setLayout( new GridLayout() );
    groupA.setLayoutData( new GridData( SWT.FILL, SWT.FILL, false, false ) );
    createText( groupA );
    Group groupB = new Group( content, SWT.SHADOW_IN );
    groupB.setText( "Group &B with Composite" );
    groupB.setLayoutData( new GridData( SWT.FILL, SWT.FILL, false, false ) );
    groupB.setLayout( new FillLayout() );
    Composite comp = new Composite( groupB, SWT.BORDER );
    comp.setLayout( new GridLayout() );
    createText( comp );
    Group groupC = new Group( content, SWT.SHADOW_IN );
    groupC.setText( "Group &C" );
    groupC.setLayout( new GridLayout() );
    groupC.setLayoutData( new GridData( SWT.FILL, SWT.FILL, false, false ) );
    createText( groupC ).setEnabled( false );
    createText( groupC ).setVisible( false );
    createText( groupC );
  }

  private void createLabelExample( Composite content ) {
    Label label = new Label( content, SWT.NONE );
    label.setText( "L&abel:" );
    createText( content );
    CLabel clabel = new CLabel( content, SWT.NONE );
    clabel.setText( "&CLabel:" );
    Button button = new Button( content, SWT.CHECK );
    button.setText( "Button" );
    Label labelForDisabled = new Label( content, SWT.NONE );
    labelForDisabled.setText( "D&isabled:" );
    createText( content ).setEnabled( false );
    Label labelForInvisible = new Label( content, SWT.NONE );
    labelForInvisible.setText( "In&visible:" );
    createText( content ).setVisible( false );
  }

  private Text createText( Composite content ) {
    Text text = new Text( content, SWT.BORDER );
    text.setText( "Text" );
    return text;
  }

  private void createButton( Composite content, int style, String text ) {
    final Button button = new Button( content, style );
    button.setText( text );
    button.addListener( SWT.Selection, new Listener() {
      public void handleEvent( Event event ) {
        log( button.getText() );
      }
    } );
  }

  private void createToolItem( ToolBar bar, int style, String text ) {
    final ToolItem item = new ToolItem( bar, style );
    item.setText( text );
    item.addListener( SWT.Selection, new Listener() {
      public void handleEvent( Event event ) {
        log( item.getText() );
      }
    } );
  }
}
