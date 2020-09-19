/*******************************************************************************
 * Copyright (c) 2012, 2014 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package com.lvsrobot.webclient.controls;

import java.io.IOException;
import java.io.InputStream;

import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.client.service.BrowserNavigation;
import org.eclipse.rap.rwt.client.service.BrowserNavigationEvent;
import org.eclipse.rap.rwt.client.service.BrowserNavigationListener;
import org.eclipse.rap.rwt.client.service.ClientInfo;
import org.eclipse.rap.rwt.client.service.ExitConfirmation;
import org.eclipse.rap.rwt.client.service.JavaScriptExecutor;
import org.eclipse.rap.rwt.client.service.JavaScriptLoader;
import org.eclipse.rap.rwt.client.service.UrlLauncher;
import org.eclipse.rap.rwt.service.ResourceManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;


public class ClientServicesTab extends ExampleTab {

  private static final String MINI_PDF = "clientservices/mini.pdf";
  private static final String MINI_JS = "clientservices/mini.js";

  private BrowserNavigationListener navigationListener = new BrowserNavigationListener() {
    public void navigated( BrowserNavigationEvent event ) {
      log( "Navigated to " + event.getState() );
    }
  };

  public ClientServicesTab() {
    super( "Client Services" );
    setHorizontalSashFormWeights( new int[] { 100, 0 } );
  }

  @Override
  protected void createStyleControls( final Composite parent ) {
  }

  @Override
  protected void createExampleControls( final Composite parent ) {
    parent.setLayout( new GridLayout( 1, false ) );
    registerResources();
    createClientInfoExample( parent );
    createBrowserNavigationExample( parent );
    createExitConfirmationExample( parent );
    createUrlLauncherExample( parent );
    createJavaScriptLoaderExample( parent );
    createJavaScriptExecuterExample( parent );
  }

  private void registerResources() {
    register( MINI_PDF, "resources/mini.pdf" );
    register( MINI_JS, "resources/mini.js" );
  }

  private void register( String url, String filepath ) {
    ResourceManager manager = RWT.getResourceManager();
    ClassLoader classLoader = this.getClass().getClassLoader();
    if( !manager.isRegistered( url ) ) {
      InputStream stream = classLoader.getResourceAsStream( filepath );
      manager.register( url, stream );
      try {
        stream.close();
      } catch( IOException e ) {
        throw new RuntimeException();
      }
    }
  }

  private void createClientInfoExample( Composite parent ) {
    Group group = createGroup( parent, "ClientInfo", 2 );
    ClientInfo info = RWT.getClient().getService( ClientInfo.class );
    createLabel( group, "Locale: " + info.getLocale() );
    createLabel( group, "Timezone Offset: " + info.getTimezoneOffset() );
  }

  private void createBrowserNavigationExample( Composite parent ) {
    Group group = createGroup( parent, "BrowserNavigation", 3 );
    createLabel( group, "Id:" );
    createLabel( group, "Title:" );
    final Button listen = new Button( group, SWT.CHECK );
    listen.setText( "Listener" );
    final Text id = new Text( group, SWT.BORDER );
    id.setText( "foo1" );
    id.setLayoutData( new GridData( SWT.FILL, SWT.FILL, true, false ) );
    final Text title = new Text( group, SWT.BORDER );
    title.setText( "Title of foo1" );
    title.setLayoutData( new GridData( SWT.FILL, SWT.FILL, true, false ) );
    Button add = new Button( group, SWT.PUSH );
    add.setText( "Add to History" );
    add.setLayoutData( createButtonGridData() );
    add.addListener( SWT.Selection, new Listener() {
      public void handleEvent( Event event ) {
        final BrowserNavigation navigation = RWT.getClient().getService( BrowserNavigation.class );
        navigation.pushState( id.getText(), title.getText() );
      }
    } );
    listen.addListener( SWT.Selection, new Listener() {
      public void handleEvent( Event event ) {
        final BrowserNavigation navigation = RWT.getClient().getService( BrowserNavigation.class );
        if( listen.getSelection() ) {
          navigation.addBrowserNavigationListener( navigationListener );
        } else {
          navigation.removeBrowserNavigationListener( navigationListener );
        }
      }
    } );
  }

  private void createUrlLauncherExample( Composite parent ) {
    Group group = createGroup( parent, "UrlLauncher", 2 );
    final Combo url = new Combo( group, SWT.NONE );
    url.add( "http://www.eclipse.org/" );
    url.add( RWT.getResourceManager().getLocation( MINI_PDF ) );
    url.add( RWT.getResourceManager().getLocation( MINI_JS ) );
    url.add( "mailto:someone@nowhere.org" );
    url.add(    "mailto:otherone@nowhere.org?cc=third@nowhere.org"
             +  "&subject=Did%20you%20know%3F&body=RAP%20is%20awesome!" );
    url.add( "skype:echo123" );
    url.add( "tel:555-123456" );
    url.select( 0 );
    GridData layoutData = new GridData( SWT.FILL, SWT.FILL, true, false );
    url.setLayoutData( layoutData );
    Button launch = new Button( group, SWT.PUSH );
    launch.setText( "Launch" );
    launch.setLayoutData( createButtonGridData() );
    Listener executeListener = new Listener() {
      public void handleEvent( Event event ) {
        UrlLauncher launcher = RWT.getClient().getService( UrlLauncher.class );
        launcher.openURL( url.getText() );
      }
    };
    url.addListener( SWT.DefaultSelection, executeListener );
    launch.addListener( SWT.Selection, executeListener );
  }

  private void createExitConfirmationExample( Composite parent ) {
    Group group = createGroup( parent, "ExitConfirmation", 2 );
    final Text text = new Text( group, SWT.BORDER );
    text.setText( "Do you really want to exit?" );
    GridData layoutData = new GridData( SWT.FILL, SWT.FILL, true, false );
    text.setLayoutData( layoutData );
    Button require = new Button( group, SWT.PUSH );
    require.setText( "Set" );
    require.setLayoutData( createButtonGridData() );
    Listener executeListener = new Listener() {
      public void handleEvent( Event event ) {
        ExitConfirmation conf = RWT.getClient().getService( ExitConfirmation.class );
        conf.setMessage( text.getText() );
      }
    };
    text.addListener( SWT.DefaultSelection, executeListener );
    require.addListener( SWT.Selection, executeListener );
  }

  private void createJavaScriptLoaderExample( Composite parent ) {
    Group group = createGroup( parent, "JavaScriptLoader", 2 );
    final Text url = new Text( group, SWT.BORDER );
    url.setText( RWT.getResourceManager().getLocation( MINI_JS ) );
    GridData layoutData = new GridData( SWT.FILL, SWT.FILL, true, false );
    url.setLayoutData( layoutData );
    Button require = new Button( group, SWT.PUSH );
    require.setText( "Require" );
    require.setLayoutData( createButtonGridData() );
    Listener executeListener = new Listener() {
      public void handleEvent( Event event ) {
        JavaScriptLoader loader = RWT.getClient().getService( JavaScriptLoader.class );
        loader.require( url.getText() );
      }
    };
    url.addListener( SWT.DefaultSelection, executeListener );
    require.addListener( SWT.Selection, executeListener );
  }

  private void createJavaScriptExecuterExample( Composite parent ) {
    Group group = createGroup( parent, "JavaScriptExecuter", 2 );
    final Combo script = new Combo( group, SWT.NONE );
    script.add( "alert( \"foo\" );" );
    script.add( "document.title = \"bar\";" );
    script.add( "window.location = \"http://www.eclipse.org/\";" );
    script.add( "alert( typeof globalData === \"undefined\" ? null : globalData );" );
    script.select( 0 );
    GridData layoutData = new GridData( SWT.FILL, SWT.FILL, true, false );
    script.setLayoutData( layoutData );
    Button execute = new Button( group, SWT.PUSH );
    execute.setText( "Execute" );
    execute.setLayoutData( createButtonGridData() );
    Listener executeListener = new Listener() {
      public void handleEvent( Event event ) {
        JavaScriptExecutor executor = RWT.getClient().getService( JavaScriptExecutor.class );
        executor.execute( script.getText() );
      }
    };
    script.addListener( SWT.DefaultSelection, executeListener );
    execute.addListener( SWT.Selection, executeListener );
  }


  private Group createGroup( Composite parent, String title, int cols ) {
    Group group = new Group( parent, SWT.NONE );
    group.setLayoutData( new GridData( SWT.FILL, SWT.FILL, true, false ) );
    group.setLayout( new GridLayout( cols, false ) );
    group.setText( title );
    return group;
  }

  private void createLabel( Composite parent, String text ) {
    final Label locale = new Label( parent, SWT.NONE );
    locale.setLayoutData( new GridData( SWT.LEFT, SWT.FILL, true, false ) );
    locale.setText( text );
  }

  private static GridData createButtonGridData() {
    return new GridData( 150, SWT.DEFAULT );
  }

}
