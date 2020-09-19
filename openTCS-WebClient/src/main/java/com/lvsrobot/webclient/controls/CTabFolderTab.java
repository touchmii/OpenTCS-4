/*******************************************************************************
 * Copyright (c) 2007, 2016 Innoopract Informationssysteme GmbH and others.
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

import java.util.Iterator;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.rap.rwt.RWT;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;


public class CTabFolderTab extends ExampleTab {

  protected static final int MAX_ITEMS = 3;

  private static final String PROP_CONTEXT_MENU = "contextMenu";
  private static final String CTAB_IMAGE_PATH = "resources/newfolder_wiz.gif";

  private Image ctabImage;

  private CTabFolder folder;
  private boolean showClose;
  private boolean unselectedCloseVisible;
  private boolean setImage;
  private boolean unselectedImageVisible;
  private boolean showTopRightControl;
  private boolean minVisible;
  private boolean maxVisible;
  private int selFgIndex;
  private int selBgIndex;
  private int tabHeight = -1;
  private boolean showSelectionBgGradient = false;
  private boolean showSelectionBgImage = false;
  private boolean customFontOnItem;
  private Font customFont;
  private Button[] tabRadios;

  public CTabFolderTab() {
    super( "CTabFolder" );
  }

  @Override
  protected void createStyleControls( Composite parent ) {
    createStyleButton( "FLAT", SWT.FLAT );
    createStyleButton( "BORDER", SWT.BORDER );
    createStyleButton( "TOP", SWT.TOP );
    createStyleButton( "BOTTOM", SWT.BOTTOM );
    createStyleButton( "CLOSE", SWT.CLOSE );
    createStyleButton( "SINGLE", SWT.SINGLE );
    createStyleButton( "MULTI", SWT.MULTI );
    createOrientationButton();
    createVisibilityButton();
    createEnablementButton();
    createFontChooser();
    createFgColorButton();
    createBgColorButton();
    createSelectionFgColorButton();
    createSelectionBgColorButton();
    createSelectionBgGradientButton();
    createSelectionBgImageButton();
    createBgImageButton();
    createTabHeightControl( styleComp );
    createTopRightControl( styleComp );
    final Button cbMin = createPropertyButton( "Minimize visible", SWT.CHECK );
    cbMin.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected( SelectionEvent event ) {
        minVisible = cbMin.getSelection();
        updateProperties();
      }
    } );
    final Button cbMax = createPropertyButton( "Maximize visible", SWT.CHECK );
    cbMax.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected( SelectionEvent event ) {
        maxVisible = cbMax.getSelection();
        updateProperties();
      }
    } );
    tabRadios = new Button[ MAX_ITEMS ];
    for( int i = 0; i < MAX_ITEMS; i++ ) {
      final int index = i;
      String rbText = "Select " + folder.getItem( index ).getText();
      tabRadios[ i ] = createPropertyButton( rbText, SWT.RADIO );
      tabRadios[ i ].addSelectionListener( new SelectionAdapter() {
        @Override
        public void widgetSelected( SelectionEvent event ) {
          Button radio = ( Button )event.getSource();
          if( radio.getSelection() ) {
            folder.setSelection( index );
          }
        }
      } );
    }
    tabRadios[ 0 ].setSelection( true );
    String text = "Set Image";
    Button cbSetImage = createPropertyButton( text, SWT.CHECK );
    cbSetImage.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected( SelectionEvent event ) {
        Button button = ( Button )event.widget;
        setImage = button.getSelection();
        updateProperties();
      }
    } );
    text = "UnselectedImageVisible";
    Button cbUnselectedImageVisible = createPropertyButton( text, SWT.CHECK );
    cbUnselectedImageVisible.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected( SelectionEvent event ) {
        Button button = ( Button )event.widget;
        unselectedImageVisible = button.getSelection();
        updateProperties();
      }
    } );
    text = "UnselectedCloseVisible";
    Button cbUnselectedCloseVisible = createPropertyButton( text, SWT.CHECK );
    cbUnselectedCloseVisible.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected( SelectionEvent event ) {
        Button button = ( Button )event.widget;
        unselectedCloseVisible = button.getSelection();
        updateProperties();
      }
    } );
    text = "showClose on Tab 2";
    Button cbShowClose = createPropertyButton( text, SWT.CHECK );
    cbShowClose.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected( SelectionEvent event ) {
        Button button = ( Button )event.widget;
        showClose = button.getSelection();
        updateProperties();
      }
    } );
    text = "Custom font on Tab 2";
    Button cbCustomFont = createPropertyButton( text, SWT.CHECK );
    cbCustomFont.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected( SelectionEvent event ) {
        Button button = ( Button )event.widget;
        customFontOnItem = button.getSelection();
        updateProperties();
      }
    } );
    createPropertyCheckbox( "Add Context Menu", PROP_CONTEXT_MENU );
    text = "Switch tabPosition";
    Button btnSwitchTabPosition = createPropertyButton( text, SWT.PUSH );
    btnSwitchTabPosition.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected( SelectionEvent event ) {
        boolean isTop = folder.getTabPosition() == SWT.TOP;
        folder.setTabPosition( isTop ? SWT.BOTTOM : SWT.TOP );
      }
    } );
    Button borderVisibleButton = createPropertyButton( "Switch borderVisible", SWT.PUSH );
    borderVisibleButton.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected( SelectionEvent event ) {
        folder.setBorderVisible( !folder.getBorderVisible() );
      }
    } );
    Button btnAddTabItem = new Button( parent, SWT.PUSH );
    btnAddTabItem.setText( "Add Item (SWT.NONE)" );
    btnAddTabItem.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected( SelectionEvent event ) {
        createTabItem( SWT.NONE );
      }
    } );
    Button btnAddCloseableTabItem = new Button( parent, SWT.PUSH );
    btnAddCloseableTabItem.setText( "Add Item (SWT.CLOSE)" );
    btnAddCloseableTabItem.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected( SelectionEvent event ) {
        createTabItem( SWT.CLOSE );
      }
    } );
    createSetFirstItemBadge( parent );
  }

  @Override
  protected void createExampleControls( Composite parent ) {
    GridLayout layout = new GridLayout();
    layout.marginHeight = 5;
    layout.marginWidth = 5;
    parent.setLayout( layout );
    folder = new CTabFolder( parent, getStyle() );
    folder.setLayoutData( new GridData( 300, 300 ) );
    for( int i = 0; i < 3; i++ ) {
      createTabItem( SWT.NONE );
    }
    folder.setSelection( 0 );
    folder.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected( SelectionEvent event ) {
        CTabItem item = ( CTabItem )event.item;
        if( tabRadios != null ) {
          int index = item.getParent().indexOf( item );
          for( int i = 0; i < MAX_ITEMS; i++ ) {
            tabRadios[ i ].setSelection( index == i );
          }
        }
      }
      @Override
      public void widgetDefaultSelected( SelectionEvent event ) {
      }
    } );
    registerControl( folder );
    if( tabHeight >= 0 ) {
      folder.setTabHeight( tabHeight );
    }
    updateContextMenu();
    updateTopRightControl();
    updateProperties();
    updateSelFgColor();
    updateSelBgGradient();
    updateSelBgColor();
    updateSelBgImage();
  }

  private void createTabItem( int style ) {
    CTabItem item = new CTabItem( folder, style );
    int count = folder.getItemCount();
    item.setText( "Tab " + count );
    if( setImage ) {
      ctabImage = Util.loadImage( item.getDisplay(), CTAB_IMAGE_PATH );
      item.setImage( ctabImage );
    } else {
      item.setImage( null );
    }
    if( count != 3 ) {
      Text content = new Text( folder, SWT.WRAP | SWT.MULTI );
      if( count % 2 != 0 ) {
        content.setBackground( bgColors[ BG_COLOR_BROWN ] );
        content.setForeground( fgColors[ FG_COLOR_BLUE ] );
      }
      content.setText( "Some Content " + count );
      item.setControl( content );
    }
  }

  private void createTabHeightControl( Composite parent ) {
    Composite composite = new Composite( parent, SWT.NONE );
    composite.setLayout( new GridLayout( 3, false ) );
    Label label = new Label( composite, SWT.NONE );
    label.setText( "Tab Height" );
    final Spinner spinner = new Spinner( composite, SWT.BORDER );
    spinner.setSelection( folder.getTabHeight() );
    spinner.setMinimum( 0 );
    spinner.setMaximum( 100 );
    spinner.addModifyListener( new ModifyListener() {
      @Override
      public void modifyText( ModifyEvent event ) {
        tabHeight = spinner.getSelection();
        folder.setTabHeight( tabHeight );
      }
    } );
  }

  private void createTopRightControl( Composite parent ) {
    final Button button = new Button( parent, SWT.CHECK );
    button.setText( "Top Right Control" );
    button.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected( SelectionEvent event ) {
        showTopRightControl = button.getSelection();
        updateTopRightControl();
      }
    } );
  }

  private Button createSelectionFgColorButton() {
    final Button button = new Button( styleComp, SWT.PUSH );
    button.setText( "Selection Foreground" );
    button.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected( SelectionEvent event ) {
        selFgIndex = ( selFgIndex + 1 ) % fgColors.length;
        updateSelFgColor();
      }
    } );
    return button;
  }

  private Button createSelectionBgColorButton() {
    final Button button = new Button( styleComp, SWT.PUSH );
    button.setText( "Selection Background" );
    button.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected( SelectionEvent event ) {
        selBgIndex = ( selBgIndex + 1 ) % bgColors.length;
        updateSelBgColor();
      }
    } );
    return button;
  }

  private Button createSelectionBgGradientButton() {
    final Button button = new Button( styleComp, SWT.CHECK );
    button.setText( "Selection Background Gradient" );
    button.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected( SelectionEvent event ) {
        showSelectionBgGradient = button.getSelection();
        updateSelBgGradient();
      }
    } );
    return button;
  }

  private Button createSelectionBgImageButton() {
    final Button button = new Button( styleComp, SWT.CHECK );
    button.setText( "Selection Background Image" );
    button.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected( SelectionEvent event ) {
        showSelectionBgImage = button.getSelection();
        updateSelBgImage();
      }
    } );
    return button;
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

  private void updateProperties() {
    CTabItem[] items = folder.getItems();
    for( int i = 0; i < items.length; i++ ) {
      if( setImage ) {
        ctabImage = Util.loadImage( folder.getDisplay(), CTAB_IMAGE_PATH );
        items[ i ].setImage( ctabImage );
      } else {
        items[ i ].setImage( null );
      }
    }
    if( items.length > 1 ) {
      items[ 1 ].setShowClose( showClose );
      items[ 1 ].setFont( customFontOnItem ? getCustomFont( folder.getDisplay() ) : null );
    }
    folder.setMinimizeVisible( minVisible );
    folder.setMaximizeVisible( maxVisible );
    folder.setUnselectedCloseVisible( unselectedCloseVisible );
    folder.setUnselectedImageVisible( unselectedImageVisible );
  }

  private Font getCustomFont( Display display ) {
    if( customFont == null ) {
      customFont = new Font( display, "Courier", 12, SWT.ITALIC );
    }
    return customFont;
  }

  private void updateTopRightControl() {
    if( showTopRightControl ) {
      Label label = new Label( folder, SWT.NONE );
      label.setText( "topRight" );
      Display display = label.getDisplay();
      label.setBackground( display.getSystemColor( SWT.COLOR_DARK_CYAN ) );
      folder.setTopRight( label );
    } else {
      Control topRight = folder.getTopRight();
      if( topRight != null && !topRight.isDisposed() ) {
        topRight.dispose();
      }
      folder.setTopRight( null );
    }
  }

  private void updateSelFgColor() {
    Iterator<Control> iter = controls.iterator();
    while( iter.hasNext() ) {
      Control control = iter.next();
      if( control instanceof CTabFolder ) {
        CTabFolder folder = ( CTabFolder )control;
        folder.setSelectionForeground( fgColors[ selFgIndex ] );
      }
    }
  }

  private void updateSelBgColor() {
    Iterator<Control> iter = controls.iterator();
    while( iter.hasNext() ) {
      Control control = iter.next();
      if( control instanceof CTabFolder ) {
        CTabFolder folder = ( CTabFolder )control;
        folder.setSelectionBackground( bgColors[ selBgIndex ] );
      }
    }
  }

  private void updateSelBgGradient() {
    Iterator<Control> iter = controls.iterator();
    while( iter.hasNext() ) {
      Control control = iter.next();
      if( control instanceof CTabFolder ) {
        CTabFolder folder = ( CTabFolder )control;
        if( showSelectionBgGradient ) {
          Color[] colors = new Color[] {
            gradientColors[ BGG_COLOR_BLUE ],
            gradientColors[ BGG_COLOR_GREEN ],
            gradientColors[ BGG_COLOR_BLUE ]
          };
          int[] percents = new int[] { 50, 100 };
          folder.setSelectionBackground( colors, percents );
        } else {
          folder.setSelectionBackground( null, null );
        }
      }
    }
  }

  private void updateSelBgImage() {
    for( Control control : controls ) {
      if( control instanceof CTabFolder ) {
        CTabFolder folder = ( CTabFolder )control;
        Image image = Util.loadImage( control.getDisplay(), "resources/pattern.png" );
        if( showSelectionBgImage ) {
          folder.setSelectionBackground( image );
        } else {
          folder.setSelectionBackground( ( Image )null );
        }
      }
    }
  }

  private void updateContextMenu() {
    if( hasCreateProperty( PROP_CONTEXT_MENU ) ) {
      Menu folderMenu = new Menu( folder );
      MenuItem folderMenuItem = new MenuItem( folderMenu, SWT.PUSH );
      folderMenuItem.addSelectionListener( new SelectionAdapter() {
        @Override
        public void widgetSelected( SelectionEvent event ) {
          String message = "You requested a context menu for the CTabFolder";
          MessageDialog.openInformation( folder.getShell(), "Information", message );
        }
      } );
      folderMenuItem.setText( "CTabFolder context menu item" );
      folder.setMenu( folderMenu );
    }
  }
}

