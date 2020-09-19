/*******************************************************************************
 * Copyright (c) 2002, 2013 Innoopract Informationssysteme GmbH and others.
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
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

public class ContainmentTab extends ExampleTab {

  private Composite comp2;
  private Composite comp3;
  private Composite comp1;

  public ContainmentTab() {
    super( "Containment" );
  }

  @Override
  protected void createStyleControls( final Composite parent ) {
    Button visibleButton = createVisibilityButton();
    visibleButton.setText( "Blue Visible" );
    Button enabledButton = createEnablementButton();
    enabledButton.setText( "Blue Enabled" );
    createFgColorButton();
    createFontChooser();
  }

  @Override
  protected void createExampleControls( final Composite parent ) {
    parent.setLayout( new FillLayout() );
    int style = getStyle();
    FillLayout layout = new FillLayout();
    layout.marginWidth = 20;
    layout.marginHeight = 20;
    comp1 = new Composite( parent, style );
    comp1.setBackground( bgColors[ BG_COLOR_GREEN ] );
    comp1.setLayout( layout );
    comp2 = new Composite( comp1, style );
    comp2.setBackground( bgColors[ BG_COLOR_BLUE ] );
    comp2.setLayout( layout );
    comp3 = new Composite( comp2, style );
    comp3.setBackground( bgColors[ BG_COLOR_BROWN ] );
    comp3.setLayout( layout );
    Button button = new Button( comp3, SWT.PUSH );
    button.setText( "Button" );
    registerControl( comp2 );
  }
}
