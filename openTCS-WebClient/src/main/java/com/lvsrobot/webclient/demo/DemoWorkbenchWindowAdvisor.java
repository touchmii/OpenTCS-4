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
package com.lvsrobot.webclient.demo;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.*;
import org.eclipse.ui.application.*;


public class DemoWorkbenchWindowAdvisor extends WorkbenchWindowAdvisor {

  public DemoWorkbenchWindowAdvisor(
    final IWorkbenchWindowConfigurer configurer )
  {
    super( configurer );
  }
  
  public ActionBarAdvisor createActionBarAdvisor(
    final IActionBarConfigurer configurer )
  {
    return new DemoActionBarAdvisor( configurer );
  }
  
  public void preWindowOpen() {
    IWorkbenchWindowConfigurer configurer = getWindowConfigurer();
    configurer.setInitialSize( new Point( 800, 600 ) );
    configurer.setShowCoolBar( true );
    configurer.setShowPerspectiveBar( true );
    configurer.setTitle( "RAP Workbench Demo" );
    configurer.setShellStyle( SWT.TITLE | SWT.MAX | SWT.RESIZE );
    configurer.setShowProgressIndicator( true );
  }
  
  public void postWindowOpen() {
    IWorkbench workbench = PlatformUI.getWorkbench();
    IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
    Shell shell = window.getShell();
    Rectangle shellBounds = shell.getBounds();
    if( !shell.getMaximized() && shellBounds.x == 0 && shellBounds.y == 0 ) {
      shell.setLocation( 70, 25 );
    }
  }
}
