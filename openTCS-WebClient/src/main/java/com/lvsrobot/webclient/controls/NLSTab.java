/*******************************************************************************
 * Copyright (c) 2002, 2013 Innoopract Informationssysteme GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing implementation
 ******************************************************************************/
package com.lvsrobot.webclient.controls;

import java.util.Locale;

import org.eclipse.rap.rwt.RWT;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;


public final class NLSTab extends ExampleTab {

  private static final String LOCALE_DATA = "locale";

  final static class NLSTabMessages {

    private static final String BUNDLE_NAME
      = "org.eclipse.rap.demo.com.lvsrobot.webclient.controls.NLSTabMessages";

    public String TranslatableMessage;

    public static NLSTabMessages get() {
      return RWT.NLS.getUTF8Encoded( BUNDLE_NAME, NLSTabMessages.class );
    }

    private NLSTabMessages() {
    }
  }

  private Label lblTranslatable;

  public NLSTab() {
    super( "NLS" );
  }

  @Override
  protected void createStyleControls( final Composite parent ) {
    String info = "Select one of the locales below to be set for the current session:";
    new Label( parent, SWT.NONE ).setText( info );
    String text = "Default (" + RWT.getLocale().getDisplayLanguage() + ")";
    createLocaleButton( parent, text, null ).setSelection( true );
    createLocaleButton( parent, "English", Locale.ENGLISH );
    createLocaleButton( parent, "German", Locale.GERMAN );
    createLocaleButton( parent, "Spanish", new Locale( "es" ) );
    createLocaleButton( parent, "Japanese (missing)", Locale.JAPANESE );
    Locale.setDefault( Locale.CHINESE );
  }

  @Override
  protected void createExampleControls( final Composite parent ) {
    parent.setLayout( new FillLayout() );
    lblTranslatable = new Label( parent, SWT.NONE );
    updateTranslatable();
  }

  private Button createLocaleButton( Composite parent, String text, Locale locale ) {
    SelectionListener listener = new SelectionAdapter() {
      @Override
      public void widgetSelected( final SelectionEvent event ) {
        Locale locale = ( Locale )event.widget.getData( LOCALE_DATA );
        if( ( ( Button )event.widget ).getSelection() ) {
          RWT.setLocale( locale );
          updateTranslatable();
        }
      }
    };
    Button button = new Button( parent, SWT.RADIO );
    button.setText( text );
    button.setData( LOCALE_DATA, locale );
    button.addSelectionListener( listener );
    return button;
  }

  private void updateTranslatable() {
    lblTranslatable.setText( NLSTabMessages.get().TranslatableMessage );
  }

}
