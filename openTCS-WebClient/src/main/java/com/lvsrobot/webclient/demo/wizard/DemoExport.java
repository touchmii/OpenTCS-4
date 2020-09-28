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
import org.eclipse.ui.IExportWizard;
import org.eclipse.ui.IWorkbench;


public class DemoExport extends Wizard implements IExportWizard {
  private final class DemoExportWizardPage extends WizardPage {
    private DemoExportWizardPage() {
      super( "Demo Export Page" );
      setTitle( "Demo Export Page" );
      setDescription( "This is the demo export wizard page." );
    }
    public void createControl( final Composite parent ) {
      Label label = new Label( parent, SWT.NONE );
      label.setText( "Demo Export Page Content" );
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
    addPage( new DemoExportWizardPage() );
  }
}
