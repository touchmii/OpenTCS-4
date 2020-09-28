/*******************************************************************************
 * Copyright (c) 2002, 2014 Innoopract Informationssysteme GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
package com.lvsrobot.webclient.demo;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import com.lvsrobot.webclient.demo.DemoTreeViewPart.TreeObject;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.*;
import org.eclipse.ui.part.ViewPart;

public class DemoBrowserViewPart extends ViewPart {

  Browser browser;
  private static String URL = "http://xkcd.com/";

  @Override
  public void createPartControl( final Composite parent ) {
    browser = new Browser( parent, SWT.NONE );
    IWorkbench workbench = PlatformUI.getWorkbench();
    IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
    ISelection selection = window.getSelectionService().getSelection();
    setUrlFromSelection( selection );
    createSelectionListener();
  }

  @Override
  public void setFocus() {
    browser.setFocus();
  }

  private void createSelectionListener() {
    IWorkbench workbench = PlatformUI.getWorkbench();
    IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
    ISelectionService selectionService = window.getSelectionService();
    selectionService.addSelectionListener( new ISelectionListener() {

      public void selectionChanged( final IWorkbenchPart part,
                                    final ISelection selection )
      {
        setUrlFromSelection( selection );
      }
    } );
  }

  private void setUrlFromSelection( final ISelection selection ) {
    if( !browser.isDisposed() ) {
      browser.setUrl( URL );
      if( selection != null ) {
        IStructuredSelection sselection = ( IStructuredSelection )selection;
        Object firstElement = sselection.getFirstElement();
        if( firstElement instanceof TreeObject ) {
          String location = ( ( TreeObject )firstElement ).getLocation();
          if( location != null ) {
            browser.setUrl( location );
          }
        }
      }
    }
  }
}
