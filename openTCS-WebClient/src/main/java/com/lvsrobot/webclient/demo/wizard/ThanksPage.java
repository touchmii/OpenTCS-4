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

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

/**
 * This page thanks the user for taking the survey
 */
class ThanksPage extends WizardPage {

  /**
   * ThanksPage constructor
   */
  public ThanksPage() {
    super( "Thanks" );
    setTitle( "Last page" );
  }

  /**
   * Creates the controls for this page
   */
  public void createControl( final Composite parent ) {
    Label label = new Label( parent, SWT.CENTER );
    label.setText( "Thanks!" );
    setControl( label );
  }
}