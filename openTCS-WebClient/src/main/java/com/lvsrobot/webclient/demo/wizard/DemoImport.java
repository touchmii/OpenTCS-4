/*******************************************************************************
 * Copyright (c) 2002, 2012 Innoopract Informationssysteme GmbH and others.
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

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IImportWizard;
import org.eclipse.ui.IWorkbench;


public class DemoImport extends Wizard implements IImportWizard {
  private final class DemoImportWizardPage extends WizardPage {
    private DemoImportWizardPage() {
      super( "Demo Import Page" );
      setTitle( "Demo Import Page" );
      setDescription( "This is the demo import wizard page." );
    }
    public void createControl( final Composite parent ) {
      Label label = new Label( parent, SWT.NONE );
      label.setText( "Demo Import Page Content" );
      setControl( label );
    }
  }

  public boolean performFinish() {
    return true;
  }

  public void init( final IWorkbench workbench,
                    final IStructuredSelection selection )
  {
  }
 
  public void addPages() {
    super.addPages();
    addPage( new DemoImportWizardPage() );
  }
}
