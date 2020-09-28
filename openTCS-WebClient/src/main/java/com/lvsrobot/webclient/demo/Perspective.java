/*******************************************************************************
 * Copyright (c) 2007, 2012 Innoopract Informationssysteme GmbH and others.
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

import org.eclipse.ui.*;

public class Perspective implements IPerspectiveFactory {

  public void createInitialLayout( final IPageLayout layout ) {
    String editorArea = layout.getEditorArea();
    layout.setEditorAreaVisible( false );
    IFolderLayout topLeft = layout.createFolder( "topLeft",
                                                 IPageLayout.LEFT,
                                                 0.25f,
                                                 editorArea );
    topLeft.addView( "org.eclipse.rap.demo.DemoChartViewPart" );
    topLeft.addView( "org.eclipse.rap.demo.DemoTreeViewPartII" );
    IFolderLayout bottomLeft = layout.createFolder( "bottomLeft",
                                                    IPageLayout.BOTTOM,
                                                    0.50f,
                                                    "topLeft" );
    bottomLeft.addView( "org.eclipse.rap.demo.DemoTreeViewPart" );
    IFolderLayout bottom = layout.createFolder( "bottom",
                                                 IPageLayout.BOTTOM,
                                                 0.60f,
                                                 editorArea );
    bottom.addView( "org.eclipse.rap.demo.DemoTableViewPart" );
    bottom.addView( "org.eclipse.rap.demo.DemoFormViewPart" );
    IFolderLayout topRight = layout.createFolder( "topRight",
                                                  IPageLayout.RIGHT,
                                                  0.70f,
                                                  editorArea );
    topRight.addView( "org.eclipse.rap.demo.DemoSelectionViewPart" );
    topRight.addView( "org.eclipse.rap.demo.DemoBrowserViewPart" );

    // add shortcuts to show view menu
    layout.addShowViewShortcut("org.eclipse.rap.demo.DemoChartViewPart");
    layout.addShowViewShortcut("org.eclipse.rap.demo.DemoTreeViewPartII");

    // add shortcut for other perspective
    layout.addPerspectiveShortcut( "org.eclipse.rap.demo.perspective.planning" );
  }
}
