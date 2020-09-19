/*******************************************************************************
 * Copyright (c) 2007, 2015 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 *     EclipseSource - ongoing development
 ******************************************************************************/
package com.lvsrobot.webclient.controls;

import org.eclipse.rap.rwt.service.ServerPushSession;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.ProgressBar;


public class ProgressBarTab extends ExampleTab {

  private static final int COUNT = 20;
  private ProgressBar progressBar;
  private ServerPushSession serverPush;

  public ProgressBarTab() {
    super( "ProgressBar" );
    serverPush = new ServerPushSession();
  }

  @Override
  protected void createExampleControls( final Composite parent ) {
    int style = getStyle() == 0 ? SWT.HORIZONTAL : getStyle();

    parent.setLayout( new GridLayout() );
    progressBar = new ProgressBar( parent, style );
    registerControl( progressBar );
    progressBar.setMaximum( COUNT );

    final Button button = new Button( parent, SWT.PUSH );
    button.setText( "Start Background Process" );
    button.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected( final SelectionEvent evt ) {
        button.setEnabled( false );
        // activate server push mechanism
        serverPush.start();
        // create and start background thread that updates the progress bar
        Thread thread = new Thread( createRunnable( progressBar, button ) );
        thread.setDaemon( true );
        thread.start();
      }
    } );
    button.addControlListener( new ControlAdapter() {
      @Override
      public void controlResized( final ControlEvent evt ) {
        Point size = button.getSize();
        if( ( progressBar.getStyle() & SWT.HORIZONTAL ) != 0 ) {
          progressBar.setLayoutData( new GridData( Math.max( 200, size.x ), SWT.DEFAULT ) );
        } else {
          progressBar.setLayoutData( new GridData( SWT.DEFAULT, 150 ) );
        }
      }
    } );
    parent.layout();
  }

  @Override
  protected void createStyleControls( final Composite parent ) {
    createStyleButton( "HORIZONTAL", SWT.HORIZONTAL, true );
    createStyleButton( "VERTICAL", SWT.VERTICAL, false );
    createStyleButton( "INDETERMINATE", SWT.INDETERMINATE, false );
    createOrientationButton();
    createVisibilityButton();
    createEnablementButton();
    createBgImageButton();
    createBgColorButton();
    createStateControl();
  }

  private void createStateControl() {
    final Combo combo = new Combo( styleComp, SWT.BORDER | SWT.READ_ONLY );
    combo.setItems( new String[] { "SWT.NORMAL", "SWT.PAUSED", "SWT.ERROR" } );
    combo.select( 0 );
    combo.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected( final SelectionEvent event ) {
        int index = combo.getSelectionIndex();
        if( index == 2 ) {
          progressBar.setState( SWT.ERROR );
        } else if( index == 1 ) {
          progressBar.setState( SWT.PAUSED );
        } else {
          progressBar.setState( SWT.NORMAL );
        }
      }
    } );
  }

  private Runnable createRunnable( final ProgressBar progressBar, final Button button ) {
    final int maximum = progressBar.getMaximum();
    final Display display = progressBar.getDisplay();
    Runnable result = new Runnable() {
      @Override
      public void run() {
        final Composite panel[] = new Composite[ 1 ];
        for( int i = 0; i <= maximum; i++ ) {
          final int selection = i;
          try {
            // simulate some work
            Thread.sleep( 250 );
          } catch( final Throwable shouldNotHappen ) {
            shouldNotHappen.printStackTrace();
          }
          if( !display.isDisposed() ) {
            // perform process bar update
            display.syncExec( new Runnable() {

              @Override
              public void run() {
                if( !progressBar.isDisposed() ) {
                  progressBar.setSelection( selection );
                  if( selection == maximum ) {
                    button.setEnabled( true );
                    // deactivate server push mechanism
                    serverPush.stop();
                    if( panel[ 0 ] != null ) {
                      panel[ 0 ].dispose();
                    }
                  }
                }
              }
            } );
          }
        }
      }
    };
    return result;
  }
}
