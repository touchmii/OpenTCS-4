/*******************************************************************************
 * Copyright (c) 2008, 2013 Innoopract Informationssysteme GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
package com.lvsrobot.webclient.demo.presentation;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.action.*;
import org.eclipse.jface.dialogs.PopupDialog;
import org.eclipse.jface.window.ApplicationWindow;
import com.lvsrobot.webclient.demo.DemoActionBarAdvisor;
import org.eclipse.rap.rwt.RWT;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.application.*;


/**
 * Configures the initial size and appearance of a workbench window.
 */
public class DemoPresentationWorkbenchWindowAdvisor extends WorkbenchWindowAdvisor {

  private static final int BANNER_HEIGTH = 88;

  public DemoPresentationWorkbenchWindowAdvisor( IWorkbenchWindowConfigurer configurer ) {
    super( configurer );
  }

  @Override
  public ActionBarAdvisor createActionBarAdvisor( IActionBarConfigurer configurer ) {
    return new DemoActionBarAdvisor( configurer );
  }

  @Override
  public void preWindowOpen() {
    IWorkbenchWindowConfigurer configurer = getWindowConfigurer();
    configurer.setShowCoolBar( true );
    configurer.setShowStatusLine( false );
    configurer.setTitle( "Presentation Prototype" );
    configurer.setShellStyle( SWT.NONE );
    Rectangle bounds = Display.getDefault().getBounds();
    configurer.setInitialSize( new Point( bounds.width, bounds.height ) );
  }

  @Override
  public void postWindowOpen() {
    final IWorkbenchWindow window = getWindowConfigurer().getWindow();
    Shell shell = window.getShell();
    shell.setMaximized( true );
  }

  @Override
  public void createWindowContents( Shell shell ) {
    shell.setBackground( shell.getDisplay().getSystemColor( SWT.COLOR_WHITE ) );
    shell.setLayout( new FormLayout() );
    createBanner( shell );
    createPageComposite( shell );
  }

  private void createBanner( Shell shell ) {
    Display display = shell.getDisplay();
    Composite banner = new Composite( shell, SWT.NONE );
    banner.setBackgroundMode( SWT.INHERIT_DEFAULT );
    banner.setData( RWT.CUSTOM_VARIANT, "banner" );
    FormData fdBanner = new FormData();
    banner.setLayoutData( fdBanner );
    fdBanner.top = new FormAttachment( 0, 0 );
    fdBanner.left = new FormAttachment( 0, 50 );
    fdBanner.height = BANNER_HEIGTH;
    fdBanner.right = new FormAttachment( 100, -50 );
    banner.setLayout( new FormLayout() );
//    banner.setBackground( COLOR_BANNER_BG );
    banner.setBackgroundImage( Images.IMG_BANNER_BG );

    Label label = new Label( banner, SWT.NONE );
    label.setText( "RAP Demo" );
    label.setForeground( display.getSystemColor( SWT.COLOR_WHITE ) );
    label.setFont( new Font( display, "Verdana", 38, SWT.BOLD ) );
    label.pack();
    FormData fdLabel = new FormData();
    label.setLayoutData( fdLabel );
    fdLabel.top = new FormAttachment( 0, 5 );
    fdLabel.left = new FormAttachment( 0, 10 );

    Label roundedCornerLeft = new Label( banner, SWT.NONE );
    roundedCornerLeft.setImage( Images.IMG_BANNER_ROUNDED_LEFT );
    roundedCornerLeft.pack();
    FormData fdRoundedCornerLeft = new FormData();
    roundedCornerLeft.setLayoutData( fdRoundedCornerLeft );
    fdRoundedCornerLeft.top = new FormAttachment( 100, -5 );
    fdRoundedCornerLeft.left = new FormAttachment( 0, 0 );
    roundedCornerLeft.moveAbove( banner );

    Label roundedCornerRight = new Label( banner, SWT.NONE );
    roundedCornerRight.setImage( Images.IMG_BANNER_ROUNDED_RIGHT );
    roundedCornerRight.pack();
    FormData fdRoundedCornerRight = new FormData();
    roundedCornerRight.setLayoutData( fdRoundedCornerRight );
    fdRoundedCornerRight.top = new FormAttachment( 100, -5 );
    fdRoundedCornerRight.left = new FormAttachment( 100, -5 );
    roundedCornerRight.moveAbove( banner );

    createMenuBar( banner );
    createCoolBar( banner, label );

//    fakeBannerButtons( banner );
//    createActionBar( banner );
//    createPerspectiveSwitcher( banner );
//    createSearch( banner );
  }

//  private void createSearch( Composite banner ) {
//    Composite search = new Composite( banner, SWT.NONE );
//    search.setLayout( new FormLayout() );
//    final Text text = new Text( search, SWT.NONE );
//    FormData fdText = new FormData();
//    text.setLayoutData( fdText );
//    text.setText( TXT_SEARCH );
//    FontData fontData = text.getFont().getFontData()[ 0 ];
//    text.setForeground( Graphics.getColor( 128, 128, 128 ) );
//    text.addFocusListener( new FocusListener() {
//      public void focusGained( FocusEvent event ) {
//        if( TXT_SEARCH.equals( ( text.getText() ) ) ) {
//          text.setText( "" );
//        }
//      }
//      public void focusLost( FocusEvent event ) {
//        if( "".equals( ( text.getText() ) ) ) {
//          text.setText( TXT_SEARCH );
//        }
//      }
//    } );
//
//    Button button = new Button( search, SWT.PUSH | SWT.FLAT );
//    FormData fdButton = new FormData();
//    button.setLayoutData( fdButton );
//    button.setImage( IMAGE_SEARCH );
//    button.setData( WidgetUtil.CUSTOM_APPEARANCE, "banner-button" );
//    button.pack();
//    button.moveAbove( text );
//
//    fdButton.top = new FormAttachment( 0, 0 );
//    fdButton.left = new FormAttachment( 0, 140 );
//
//    fdText.top = new FormAttachment( 0, 3 );
//    fdText.left = new FormAttachment( 0, 0 );
//    fdText.width = 150;
//    fdText.height = button.getSize().y - 8;
//
//    FormData fdSearch = new FormData();
//    search.setLayoutData( fdSearch );
//    fdSearch.top = new FormAttachment( 0, 10 );
//    fdSearch.left = new FormAttachment( 100, -175 );
//  }

//  private void createActionBar( Composite banner ) {
//
//    IAction[] actions = new IAction[] {
//      new Action( "In" ) {
//        public void run() {
//          System.out.println( "In pressed" );
//        }
//      },
//      new Action( "Out" ) {
//        public void run() {
//          System.out.println( "Out pressed" );
//        }
//      },
//      new Action( "Over" ) {
//        public void run() {
//          System.out.println( "Over pressed" );
//        }
//      },
//      new Action( "Under" ) {
//        public void run() {
//          System.out.println( "Under pressed" );
//        }
//      },
//      new Action( "Through" ) {
//        public void run() {
//          System.out.println( "Through pressed" );
//        }
//      }
//    };
//
//    ActionBarButton actionBar = new ActionBarButton( banner, SWT.NONE, actions );
//    FormData fdActionBar = new FormData();
//    actionBar.setLayoutData( fdActionBar );
//    fdActionBar.top = new FormAttachment( 0, 44 );
//    fdActionBar.left = new FormAttachment( 0, 5 );
//    actionBar.pack();
//  }

//  private void createPerspectiveSwitcher( Composite banner ) {
//    IAction[] actions = new IAction[] {
//      new Action( "Perspective 1" ) {
//        public void run() {
//          switchPerspective( 0 );
//        }
//
//      },
//      new Action( "Perspective 2" ) {
//        public void run() {
//          switchPerspective( 1 );
//        }
//      }
//    };
//
//    ActionBarButton actionBar = new ActionBarButton( banner, SWT.NONE, actions );
//    actionBar.pack();
//
//    FormData fdActionBar = new FormData();
//    actionBar.setLayoutData( fdActionBar );
//    fdActionBar.top = new FormAttachment( 0, 44 );
//    fdActionBar.left = new FormAttachment( 100, -actionBar.getSize().x );
//  }

//  private void switchPerspective( int perspectiveIndex ) {
//    IWorkbench workbench = PlatformUI.getWorkbench();
//    IPerspectiveRegistry registry = workbench.getPerspectiveRegistry();
//    final IPerspectiveDescriptor[] perspectives = registry.getPerspectives();
//    IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
//    final IWorkbenchPage page = window.getActivePage();
//    page.setPerspective( perspectives[ perspectiveIndex ] );
//  }

  private void createCoolBar( Composite banner, Control leftControl ) {
    IWorkbenchWindowConfigurer configurer = getWindowConfigurer();
    Composite coolBar = ( Composite )configurer.createCoolBarControl( banner );
    coolBar.setBackgroundMode( SWT.INHERIT_FORCE );
    FormData fdCoolBar = new FormData();
    coolBar.setLayoutData( fdCoolBar );
    fdCoolBar.top = new FormAttachment( 0, 8 );
    fdCoolBar.left = new FormAttachment( leftControl, 35 );
    fdCoolBar.bottom = new FormAttachment( 0, 26 );
//    fdCoolBar.right = new FormAttachment( 100, -100 );
  }

  private void createMenuBar( final Composite banner ) {
    final Composite menuBar = new Composite( banner, SWT.NONE );
    menuBar.setBackgroundMode( SWT.INHERIT_FORCE );
    FormData fdMenuBar = new FormData();
    menuBar.setLayoutData( fdMenuBar );
    fdMenuBar.top = new FormAttachment( 100, -26 );
    fdMenuBar.left = new FormAttachment( 0, 10 );
    fdMenuBar.bottom = new FormAttachment( 100, -8 );

    final ApplicationWindow window = ( ApplicationWindow )getWindowConfigurer().getWindow();
    MenuManager menuBarManager = window.getMenuBarManager();
    IContributionItem[] menuBarItems = menuBarManager.getItems();
    List<Action> actions = new ArrayList<Action>();
    for( int i = 0; i < menuBarItems.length; i++ ) {
      final MenuManager menuManager = ( MenuManager )menuBarItems[ i ];
      actions.add( new Action() {
        @Override
        public String getId() {
          return menuManager.getId();
        }
        @Override
        public String getText() {
          return menuManager.getMenuText();
        }

        @Override
        public void run() {
          final Shell shell = window.getShell();
          final Color background = new Color( shell.getDisplay(), 9, 34, 60 );
          final PopupDialog popupDialog = new PopupDialog( shell,
                           SWT.RESIZE | SWT.ON_TOP,
                           false,
                           false,
                           false,
                           false,
                           null,
                           null )
          {
            @Override
            protected Control createDialogArea( Composite parent ) {
              final Composite popup = new Composite( parent, SWT.NONE );
              popup.setBackgroundMode( SWT.INHERIT_FORCE );
              popup.setLayout( new FormLayout() );
              popup.setBackground( background );

              Label roundedCornerLeft = new Label( popup, SWT.NONE );
              roundedCornerLeft.setImage( Images.IMG_BANNER_ROUNDED_LEFT );
              roundedCornerLeft.pack();
              FormData fdRoundedCornerLeft = new FormData();
              roundedCornerLeft.setLayoutData( fdRoundedCornerLeft );
              fdRoundedCornerLeft.top = new FormAttachment( 100, -5 );
              fdRoundedCornerLeft.left = new FormAttachment( 0, 0 );

              Label roundedCornerRight = new Label( popup, SWT.NONE );
              roundedCornerRight.setImage( Images.IMG_BANNER_ROUNDED_RIGHT );
              roundedCornerRight.pack();
              FormData fdRoundedCornerRight = new FormData();
              roundedCornerRight.setLayoutData( fdRoundedCornerRight );
              fdRoundedCornerRight.top = new FormAttachment( 100, -5 );
              fdRoundedCornerRight.left = new FormAttachment( 100, -5 );

              final Composite content = new Composite( popup, SWT.NONE );
              FormData fdContent = new FormData();
              content.setLayoutData( fdContent );
              fdContent.top = new FormAttachment( 0, 5 );
              fdContent.left = new FormAttachment( 0, 14 );

              content.setLayout( new FillLayout( SWT.VERTICAL ) );
              IContributionItem[] menuItems = menuManager.getItems();
              for( int j = 0; j < menuItems.length; j++ ) {
                IContributionItem contributionItem = menuItems[ j ];
                if( contributionItem instanceof ActionContributionItem ) {
                  ActionContributionItem actionItem = ( ActionContributionItem )contributionItem;
                  Action action = ( Action )actionItem.getAction();
                  new ActionBarButton( action, content ) {
                    @Override
                    public void run() {
                      close();
                      super.run();
                    }
                  };
                }

              }
              content.pack();

              return popup;
            }
          };

          final Composite popup = new Composite( shell, SWT.NONE );
          popup.setBackgroundMode( SWT.INHERIT_FORCE );
          popup.setLayout( new FormLayout() );

          Label roundedCornerLeft = new Label( popup, SWT.NONE );
          roundedCornerLeft.setImage( Images.IMG_BANNER_ROUNDED_LEFT );
          roundedCornerLeft.pack();
          FormData fdRoundedCornerLeft = new FormData();
          roundedCornerLeft.setLayoutData( fdRoundedCornerLeft );
          fdRoundedCornerLeft.top = new FormAttachment( 100, -5 );
          fdRoundedCornerLeft.left = new FormAttachment( 0, 0 );
          roundedCornerLeft.moveAbove( banner );

          Label roundedCornerRight = new Label( popup, SWT.NONE );
          roundedCornerRight.setImage( Images.IMG_BANNER_ROUNDED_RIGHT );
          roundedCornerRight.pack();
          FormData fdRoundedCornerRight = new FormData();
          roundedCornerRight.setLayoutData( fdRoundedCornerRight );
          fdRoundedCornerRight.top = new FormAttachment( 100, -5 );
          fdRoundedCornerRight.left = new FormAttachment( 100, -5 );
          roundedCornerRight.moveAbove( banner );

          final Composite content = new Composite( popup, SWT.NONE );
          FormData fdContent = new FormData();
          content.setLayoutData( fdContent );
          fdContent.top = new FormAttachment( 0, 5 );
          fdContent.left = new FormAttachment( 0, 14 );

          content.setLayout( new FillLayout( SWT.VERTICAL ) );
          IContributionItem[] menuItems = menuManager.getItems();
          for( int j = 0; j < menuItems.length; j++ ) {
            IContributionItem contributionItem = menuItems[ j ];
            if( contributionItem instanceof ActionContributionItem ) {
              ActionContributionItem actionItem
                = ( ActionContributionItem )contributionItem;
              Action action = ( Action )actionItem.getAction();
              new ActionBarButton( action, content );
            }

          }
          content.pack();

          popup.setBackground( background );
          Rectangle popUpBounds = calculatePopUpBounds( banner,
                                                        menuBar,
                                                        content );
          popup.setBounds( popUpBounds );
          shell.addControlListener( new ControlAdapter() {
            @Override
            public void controlResized( ControlEvent e ) {
              Rectangle popUpBounds = calculatePopUpBounds( banner,
                                                            menuBar,
                                                            content );
              popup.setBounds( popUpBounds );
            }
          } );
          popup.moveAbove( null );

          popupDialog.open();
          Listener closeListener = new Listener() {
            public void handleEvent( Event event ) {
              if( popupDialog.getShell() != null ) {
                popupDialog.getShell().removeListener( SWT.Close, this );
                popupDialog.getShell().removeListener( SWT.Deactivate, this );
                popupDialog.getShell().removeListener( SWT.Dispose, this );
                popupDialog.close();
              }
              if( !popup.isDisposed() ) {
                popup.dispose();
              }
            }
          };
          popupDialog.getShell().addListener( SWT.Deactivate, closeListener );
          popupDialog.getShell().addListener( SWT.Close, closeListener );
          popupDialog.getShell().addListener( SWT.Dispose, closeListener );
//          content.addListener( SWT.Dispose, closeListener );
//          Shell controlShell = content.getShell();
//          controlShell.addListener( SWT.Move, closeListener );

          popupDialog.getShell().setAlpha( 0 );
          popupDialog.getShell().setActive();
          popupDialog.getShell().setBounds( popUpBounds );

//          shell.addMouseListener( new MouseAdapter() {
//            public void mouseUp( final MouseEvent e ) {
//
//System.out.println( "mouseup" );
//              shell.removeMouseListener( this );
//              popup.dispose();
//            }
//          } );

        }

        private Rectangle calculatePopUpBounds( Composite banner,
                                                Composite menuBar,
                                                Composite content )
        {
          Rectangle menuBarBounds = menuBar.getBounds();
          Rectangle bannerBounds = banner.getBounds();
          Display display = menuBar.getDisplay();
          Shell shell = menuBar.getShell();
          Point bannerPosition = display.map( banner.getParent(), shell, banner.getLocation() );

          return new Rectangle( bannerPosition.x,
                                bannerBounds.height - 5,
                                menuBarBounds.width + 10,
                                content.getSize().y + 10 );
        }
      } );
    }
    ActionBar.create( actions, menuBar );
  }

  private void createPageComposite( Shell shell ) {
    final Color shellBackground = shell.getDisplay().getSystemColor( SWT.COLOR_WHITE );
    Composite content = new Composite( shell, SWT.NONE );
    content.setBackground( shellBackground );
    FormData fdContent = new FormData();
    content.setLayoutData( fdContent );
    fdContent.top = new FormAttachment( 0, BANNER_HEIGTH + 4 );
    fdContent.left = new FormAttachment( 0, 43 );
    fdContent.right = new FormAttachment( 100, -43 );
    fdContent.bottom = new FormAttachment( 100, 0 );
    FillLayout fillLayout = new FillLayout();
    fillLayout.marginWidth = 3;
    content.setLayout( fillLayout );
    IWorkbenchWindowConfigurer configurer = getWindowConfigurer();
    // add a hack to set the bgcolor of the inner page composite
    final Composite composite = ( Composite )configurer.createPageComposite( content );
    composite.addControlListener( new ControlAdapter() {
      @Override
      public void controlResized( final ControlEvent e ) {
        Control[] children = composite.getChildren();
        for( int i = 0; i < children.length; i++ ) {
          children[ i ].setBackground( shellBackground );
        }
      }
    } );
  }
}
