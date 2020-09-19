/*******************************************************************************
 * Copyright (c) 2002, 2016 Innoopract Informationssysteme GmbH and others.
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
import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.client.service.UrlLauncher;
import org.eclipse.rap.rwt.widgets.BrowserCallback;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.BrowserFunction;
import org.eclipse.swt.browser.ProgressEvent;
import org.eclipse.swt.browser.ProgressListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

final class BrowserTab extends ExampleTab {

  private static final String PROP_PROGRESS_LISTENER = "progressListener";

  private static final String DEFAULT_URL = "http://developer.eclipsesource.com/technology/crossplatform/#rap";
  private static final String DEFAULT_HTML
    = "<html>\n"
    + "<head>\n"
    + "<script type=\"text/javascript\">\n"
    + "  function show( msg ) {\n"
    + "    alert( msg );\n"
    + "}\n"
    + "</script>\n"
    + "</head>\n"
    + "<body>\n"
    + "  <p id=\"a\">Hello World</p>\n"
    + "</body>\n"
    + "</html>";

  private Browser browser;
  private BrowserFunction function;
  private boolean useBrowserCallback;

  public BrowserTab() {
    super( "Browser" );
  }

  @Override
  protected void createStyleControls( Composite parent ) {
    createStyleButton( "BORDER", SWT.BORDER );
    createVisibilityButton();
    createEnablementButton();
    createBgColorButton();
    createBgImageButton();
    createUrlAndHTMLSelector( parent );
    createPropertyCheckbox( "Add Progress Listener", PROP_PROGRESS_LISTENER );
    final Button cbUseBrowserCallback = new Button( parent, SWT.CHECK );
    cbUseBrowserCallback.setText( "Use BrowserCallback" );
    cbUseBrowserCallback.setSelection( useBrowserCallback );
    cbUseBrowserCallback.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected( SelectionEvent event ) {
        useBrowserCallback = cbUseBrowserCallback.getSelection();
      }
    } );
    createExternalBrowserSelector( parent );
    createBrowserFunctionSelector( parent );
  }

  @Override
  protected void createExampleControls( Composite parent ) {
    parent.setLayout( new FillLayout() );
    browser = new Browser( parent, getStyle() );
    if( hasCreateProperty( PROP_PROGRESS_LISTENER ) ) {
      browser.addProgressListener( new ProgressListener() {
        @Override
        public void changed( final ProgressEvent event ) {
          log( "changed: " + event );
        }

        @Override
        public void completed( final ProgressEvent event ) {
          log( "completed: " + event );
        }
      } );
    }
    registerControl( browser );
  }

  private void createUrlAndHTMLSelector( Composite parent ) {
    Composite composite = new Composite( parent, SWT.NONE );
    composite.setLayout( new GridLayout( 3, false ) );
    Label lblURL = new Label( composite, SWT.NONE );
    lblURL.setText( "URL" );
    final Text txtURL = new Text( composite, SWT.BORDER );
    txtURL.setText( DEFAULT_URL );
    txtURL.setLayoutData( new GridData( 300, SWT.DEFAULT ) );
    Button btnURL = new Button( composite, SWT.PUSH );
    btnURL.setText( "Go" );
    btnURL.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected( final SelectionEvent event ) {
        browser.setUrl( txtURL.getText() );
      }
    } );

    final Label lblHTML = new Label( composite, SWT.NONE );
    lblHTML.setText( "HTML" );
    lblHTML.setLayoutData( new GridData( GridData.VERTICAL_ALIGN_BEGINNING ) );
    final Text txtHTML = new Text( composite, SWT.BORDER | SWT.MULTI );
    txtHTML.setText( DEFAULT_HTML );
    txtHTML.setLayoutData( new GridData( 300, 100 ) );

    Button btnHTML = new Button( composite, SWT.PUSH );
    btnHTML.setText( "Go" );
    btnHTML.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected( final SelectionEvent event ) {
        browser.setText( txtHTML.getText() );
      }
    } );
    btnHTML.setLayoutData( new GridData( GridData.VERTICAL_ALIGN_BEGINNING ) );

    Label lblExecute = new Label( composite, SWT.NONE );
    lblExecute.setText( "Execute" );
    final Text txtExecute = new Text( composite, SWT.BORDER | SWT.MULTI );
    txtExecute.setLayoutData( new GridData( 300, 100 ) );
    Button btnExecButton = new Button( composite, SWT.PUSH );
    btnExecButton.setText( "Go" );
    btnExecButton.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected( final SelectionEvent event ) {
        if( useBrowserCallback ) {
          BrowserCallback browserCallback = new BrowserCallback() {
            @Override
            public void evaluationSucceeded( Object result ) {
              log( "Execution was successful." );
            }
            @Override
            public void evaluationFailed( Exception exception ) {
              log( "Execution was not successful." );
            }
          };
          browser.evaluate( txtExecute.getText(), browserCallback );
        } else {
          boolean result = browser.execute( txtExecute.getText() );
          String msg = result
                     ? "Execution was successful."
                     : "Execution was not successful.";
          log( msg );
        }
      }
    });
  }

  private void createExternalBrowserSelector( Composite parent ) {
    Group group = new Group( parent, SWT.NONE );
    group.setLayout( new GridLayout( 2, false ) );
    group.setText( "UrlLauncher" );
    Label lblUrl = new Label( group, SWT.NONE );
    lblUrl.setText( "URL" );
    final Text txtUrl = new Text( group, SWT.BORDER );
    txtUrl.setLayoutData( grapExcessHorizontalSpace() );
    txtUrl.setText( DEFAULT_URL );
    txtUrl.setLayoutData( new GridData( 300, SWT.DEFAULT ) );
    Button btnOpen = new Button( group, SWT.PUSH );
    btnOpen.setLayoutData( horizontalSpan2() );
    btnOpen.setText( "openURL( url )" );
    btnOpen.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected( SelectionEvent event ) {
        UrlLauncher service = RWT.getClient().getService( UrlLauncher.class );
        if( service != null ) {
          service.openURL( txtUrl.getText() );
        }
      }
    } );
    Button btnMailTo = new Button( group, SWT.PUSH );
    btnMailTo.setText( "mailto:..." );
    btnMailTo.setLayoutData( horizontalSpan2() );
    btnMailTo.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected( final SelectionEvent event ) {
        UrlLauncher service = RWT.getClient().getService( UrlLauncher.class );
        if( service != null ) {
          service.openURL( "mailto:someone@nowhere.org" );
        }
      }
    } );
  }

  private void createBrowserFunctionSelector( Composite parent ) {
    Group group = new Group( parent, SWT.NONE );
    group.setText( "BrowserFunction" );
    group.setLayout( new GridLayout( 3, false ) );
    final Label lblHTML = new Label( group, SWT.NONE );
    lblHTML.setText( "HTML" );
    lblHTML.setLayoutData( new GridData( GridData.VERTICAL_ALIGN_BEGINNING ) );
    final Text txtHTML = new Text( group, SWT.BORDER | SWT.MULTI );
    txtHTML.setText( createBrowserFunctionHTML() );
    txtHTML.setLayoutData( new GridData( 200, 200 ) );

    Button btnHTML = new Button( group, SWT.PUSH );
    btnHTML.setText( "Go" );
    btnHTML.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected( final SelectionEvent event ) {
        browser.setText( txtHTML.getText() );
        function = new CustomFunction( browser, "theJavaFunction" );
      }
    } );
    btnHTML.setLayoutData( new GridData( GridData.VERTICAL_ALIGN_BEGINNING ) );
    GridData buttonsGridData = new GridData();
    buttonsGridData.horizontalSpan = 3;
    Button createButton = new Button( group, SWT.PUSH );
    createButton.setLayoutData( buttonsGridData );
    createButton.setText( "Create theJavaFunction" );
    createButton.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected( final SelectionEvent event) {
        function = new CustomFunction( browser, "theJavaFunction" );
      }
    } );
    Button disposeButton = new Button( group, SWT.PUSH );
    disposeButton.setLayoutData( buttonsGridData );
    disposeButton.setText( "Dispose theJavaFunction" );
    disposeButton.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected( final SelectionEvent event) {
        function.dispose();
      }
    } );
  }

  private static GridData horizontalSpan2() {
    GridData result = new GridData();
    result.horizontalSpan = 2;
    return result;
  }

  private static GridData grapExcessHorizontalSpace() {
    GridData result = new GridData( SWT.FILL, SWT.CENTER, true, false );
//    result.grabExcessHorizontalSpace = true;
    return result;
  }

  private String createBrowserFunctionHTML() {
    StringBuffer buffer = new StringBuffer();
    buffer.append( "<html>\n" );
    buffer.append( "<head>\n" );
    buffer.append( "<script language=\"JavaScript\">\n" );
    buffer.append( "function function1() {\n" );
    buffer.append( "    var result;\n" );
    buffer.append( "    try {\n" );
    buffer.append( "        result = theJavaFunction(12, false, null, [3.6, ['swt', true]], 'eclipse');\n" );
    buffer.append( "    } catch (e) {\n" );
    buffer.append( "        alert('a java error occurred: ' + e.message);\n" );
    buffer.append( "        return;\n" );
    buffer.append( "    }\n" );
//    buffer.append( "    for (var i = 0; i < result.length; i++) {\n" );
//    buffer.append( "        alert('returned ' + i + ': ' + result[i]);\n" );
//    buffer.append( "    }\n" );
    buffer.append( "}\n" );
    buffer.append( "</script>\n" );
    buffer.append( "</head>\n" );
    buffer.append( "<body>\n" );
    buffer.append( "<input id=button type=\"button\" value=\"Push to Invoke Java\" onclick=\"function1();\">\n" );
    buffer.append( "</body>\n" );
    buffer.append( "</html>\n" );
    return buffer.toString();
  }

  private class CustomFunction extends BrowserFunction {

    CustomFunction( Browser browser, String name ) {
      super( browser, name );
    }

    @Override
    public Object function( Object[] arguments ) {
      StringBuffer buffer = new StringBuffer();
      buffer.append( "theJavaFunction() called from javascript with args:\n" );
      dumpArguments( arguments, "", buffer );
      String title = "BrowserFunction called";
      MessageDialog.openInformation( getShell(), title, buffer.toString() );

      Object returnValue = new Object[]{
        new Short( ( short )3 ),
        new Boolean( true ),
        null,
        new Object[] { "a string", new Boolean( false ) },
        "hi",
        new Float( 2.0f / 3.0f )
      };
      //int z = 3 / 0; // uncomment to cause a java error instead
      return returnValue;
    }

    private void dumpArguments( Object[] arguments, String tabString, StringBuffer buffer ) {
      String tab = tabString + "    ";
      for( int i = 0; i < arguments.length; i++ ) {
        Object arg = arguments[ i ];
        if( arg == null ) {
          buffer.append( tab );
          buffer.append( "-->null\n" );
        } else {
          buffer.append( tab );
          buffer.append( "-->" );
          buffer.append( arg.getClass().getName() );
          buffer.append( ": " );
          buffer.append( arg.toString() );
          buffer.append( "\n" );
          if( arg.getClass().isArray() ) {
            Object[] arg1 = ( Object[] )arg;
            dumpArguments( arg1, tab, buffer );
          }
        }
      }
    }
  }
}
