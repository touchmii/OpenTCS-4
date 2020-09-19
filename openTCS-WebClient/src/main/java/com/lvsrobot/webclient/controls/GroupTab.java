/*******************************************************************************
 * Copyright (c) 2007, 2015 Innoopract Informationssysteme GmbH and others.
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
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;


public class GroupTab extends ExampleTab {

  private Group group;

  public GroupTab() {
    super( "Group" );
  }

  @Override
  protected void createStyleControls(Composite top) {
    createStyleButton( "BORDER", SWT.BORDER );
    createOrientationButton();
    createVisibilityButton();
    createEnablementButton();
    createFgColorButton();
    createBgColorButton();
    createBgImageButton();
    createFontChooser();
  }

  @Override
  protected void createExampleControls( final Composite parent ) {
    parent.setLayout( new FillLayout() );
    group = new Group( parent, getStyle() );
    group.setLayout( new FillLayout() );
    group.setText( "A group with one white label" );
    Label content = new Label( group, SWT.NONE );
    content.setText( "Hello from inside the group box..." );
    Color white = content.getDisplay().getSystemColor( SWT.COLOR_WHITE );
    content.setBackground( white );
    registerControl( group );
  }

}
