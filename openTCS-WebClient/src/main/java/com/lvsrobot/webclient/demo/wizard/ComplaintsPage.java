/*******************************************************************************
 * Copyright (c) 2008, 2012 Innoopract Informationssysteme GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
package com.lvsrobot.webclient.demo.wizard;

import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;

class ComplaintsPage extends WizardPage {

  private Button yes;
  private Button no;

  /**
   * ComplaintsPage constructor
   */
  public ComplaintsPage() {
    super( "Complaints" );
    setTitle( "Complaints" );
  }

  /**
   * Creates the page controls
   */
  public void createControl( Composite parent ) {
    Composite composite = new Composite( parent, SWT.NONE );
    composite.setLayout( new GridLayout( 1, true ) );
    new Label( composite, SWT.LEFT ).setText( "Do you have complaints?" );
    Composite yesNo = new Composite( composite, SWT.NONE );
    yesNo.setLayout( new FillLayout( SWT.VERTICAL ) );
    yes = new Button( yesNo, SWT.RADIO );
    yes.setText( "Yes" );
    yes.addSelectionListener( new SelectionAdapter() {

      public void widgetSelected( SelectionEvent e ) {
        setPageComplete( true );
        setErrorMessage( null );
      }
    } );
    no = new Button( yesNo, SWT.RADIO );
    no.setText( "No" );
    no.addSelectionListener( new SelectionAdapter() {

      public void widgetSelected( SelectionEvent e ) {
        setPageComplete( true );
        setErrorMessage( null );
      }
    } );
    setControl( composite );
  }

  public IWizardPage getNextPage() {
    // If they have complaints, go to the normal next page
    if( yes.getSelection() ) {
      return super.getNextPage();
    }
    // No complaints? Short-circuit the rest of the pages
    return getWizard().getPage( "Thanks" );
  }

  public boolean canFlipToNextPage() {
    if( yes.getSelection() || no.getSelection() ) {
      return true;
    }
    setErrorMessage( "You need to select at least one entry" );
    return false;
  }
}