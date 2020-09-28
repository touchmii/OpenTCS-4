/*******************************************************************************
 * Copyright (c) 2012, 2020 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package com.lvsrobot.webclient.demo;

import org.eclipse.rap.rwt.RWT;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.widgets.*;
import org.eclipse.ui.part.ViewPart;


public class DemoFormViewPart extends ViewPart {

  @Override
  public void createPartControl( Composite parent ) {
    Composite composite = new Composite( parent, SWT.NONE );
    composite.setLayout( new FillLayout() );
    FormToolkit toolkit = new FormToolkit( composite.getDisplay() );
    ScrolledForm form = toolkit.createScrolledForm( composite );
    form.getBody().setLayout( new TableWrapLayout() );
    toolkit.decorateFormHeading( form.getForm() );
    form.setText( "User Form" );
    int sectionStyle = Section.TITLE_BAR | Section.DESCRIPTION | Section.TWISTIE | Section.EXPANDED;
    Section section = toolkit.createSection( form.getBody(), sectionStyle );
    section.setText( "User Details" );
    section.setDescription( "Contains details of the current logged user" );
    TableWrapData tableWrapData = new TableWrapData( TableWrapData.FILL_GRAB );
    tableWrapData.colspan = 1;
    tableWrapData.grabHorizontal = true;
    section.setLayoutData( tableWrapData );
    Composite content = toolkit.createComposite( section );
    content.setLayout( new GridLayout( 2, false ) );
    Label firstNameLabel = toolkit.createLabel( content, "" );
    firstNameLabel.setData( RWT.MARKUP_ENABLED, Boolean.TRUE );
    firstNameLabel.setText( "<b>First Name:</b> " );
    toolkit.createLabel( content, "John" );
    Label lastNameLabel = toolkit.createLabel( content, "" );
    lastNameLabel.setData( RWT.MARKUP_ENABLED, Boolean.TRUE );
    lastNameLabel.setText( "<b>Last Name:</b> " );
    toolkit.createLabel( content, "Smith" );
    Label roleLabel = toolkit.createLabel( content, "" );
    roleLabel.setData( RWT.MARKUP_ENABLED, Boolean.TRUE );
    roleLabel.setText( "<b>Role:</b> " );
    toolkit.createLabel( content, "Administrator" );
    section.setClient( content );
  }

  @Override
  public void setFocus() {
  }
}
