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

import com.lvsrobot.webclient.demo.presentation.DemoPresentationWorkbenchAdvisor;
import org.eclipse.rap.rwt.application.EntryPoint;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchPreferenceConstants;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.application.WorkbenchAdvisor;
import org.eclipse.ui.internal.util.PrefUtil;
import org.eclipse.ui.preferences.ScopedPreferenceStore;


public class DemoWorkbench implements EntryPoint {

  private static final String DEMO_PRESENTATION = "org.eclipse.rap.demo.presentation";

  public int createUI() {
    ScopedPreferenceStore prefStore = ( ScopedPreferenceStore )PrefUtil.getAPIPreferenceStore();
    String keyPresentationId = IWorkbenchPreferenceConstants.PRESENTATION_FACTORY_ID;
    String presentationId = prefStore.getString( keyPresentationId );

    WorkbenchAdvisor worbenchAdvisor = new DemoWorkbenchAdvisor();
    if( DEMO_PRESENTATION.equals( presentationId ) ) {
      worbenchAdvisor = new DemoPresentationWorkbenchAdvisor();
    }

    Display display = PlatformUI.createDisplay();
    int result = PlatformUI.createAndRunWorkbench( display, worbenchAdvisor );
    display.dispose();
    return result;
  }
}
