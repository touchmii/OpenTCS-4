/*******************************************************************************
 * Copyright (c) 2002, 2012 Innoopract Informationssysteme GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 *     EclipseSource - ongoing development
 ******************************************************************************/

package com.lvsrobot.webclient.demo;

import org.eclipse.ui.*;

public class PlanningPerspective implements IPerspectiveFactory {

  public void createInitialLayout( final IPageLayout layout ) {
    String editorArea = layout.getEditorArea();
    layout.setEditorAreaVisible( true );
    IFolderLayout topLeft = layout.createFolder( "topLeft",
                                                 IPageLayout.LEFT,
                                                 0.25f,
                                                 editorArea );
    topLeft.addView( "org.eclipse.rap.demo.DemoSelectionViewPart" );
    topLeft.addView( "org.eclipse.rap.demo.DemoBrowserViewPart" );
    IFolderLayout bottomLeft = layout.createFolder( "bottomLeft",
                                                    IPageLayout.BOTTOM,
                                                    0.50f,
                                                    "topLeft" );
    bottomLeft.addView( "org.eclipse.rap.demo.DemoTreeViewPart" );
    IFolderLayout right = layout.createFolder( "right",
                                               IPageLayout.RIGHT,
                                               0.70f,
                                               editorArea );
    right.addView( "org.eclipse.rap.demo.DemoTableViewPart" );
    // add shortcuts to show view menu
    layout.addShowViewShortcut( "org.eclipse.rap.demo.DemoChartViewPart" );
    layout.addShowViewShortcut( "org.eclipse.rap.demo.DemoTreeViewPartII" );
    // add shortcut for other perspective
    layout.addPerspectiveShortcut( "org.eclipse.rap.demo.perspective" );
  }
}
