/*******************************************************************************
 * Copyright (c) 2007, 2012 Innoopract Informationssysteme GmbH and others.
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

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Sash;


public class SashTab extends ExampleTab {

  public SashTab() {
    super( "Sash" );
    setDefaultStyle( SWT.BORDER );
  }

  @Override
  protected void createStyleControls( final Composite parent ) {
    createStyleButton( "BORDER", SWT.BORDER, true );
    createStyleButton( "VERTICAL", SWT.VERTICAL );
    createStyleButton( "HORIZONTAL", SWT.HORIZONTAL );
    createVisibilityButton();
    createEnablementButton();
    createBgColorButton();
    createBgImageButton();
  }

  @Override
  protected void createExampleControls( final Composite top ) {
    top.setLayout( new FillLayout() );
    Color white = top.getDisplay().getSystemColor( SWT.COLOR_WHITE );
    int style = getStyle();
    Label label1 = new Label( top, SWT.NONE );
    label1.setBackground( white );
    label1.setText( "Sash:" );
    Sash sash = new Sash( top, style );
    if( ( sash.getStyle() & SWT.HORIZONTAL ) != 0 ){
      top.setLayout( new FillLayout( SWT.VERTICAL ) );
    }
    Label label2 = new Label( top, SWT.NONE );
    label2.setBackground( white );
    registerControl( sash );
  }
}
