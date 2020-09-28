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

import org.eclipse.jface.wizard.Wizard;

public class SurveyWizard extends Wizard {

  public SurveyWizard() {
    // Add the pages
    addPage( new ComplaintsPage() );
    addPage( new MoreInformationPage() );
    addPage( new ThanksPage() );
    setWindowTitle( "RAP Survey Wizard" );
  }

  public boolean canFinish() {
    return    getContainer() != null
           && getContainer().getCurrentPage() instanceof ThanksPage;
  }

  /**
   * Called when user clicks Finish
   *
   * @return boolean
   */
  public boolean performFinish() {
    // Dismiss the wizard
    return true;
  }
}