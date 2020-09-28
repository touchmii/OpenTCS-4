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

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.*;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;

public class DemoTreeViewActionDelegate implements IViewActionDelegate {

  private DemoTreeViewPart viewpart;

  public void init( IViewPart view ) {
    if( view instanceof DemoTreeViewPart ) {
      viewpart = ( DemoTreeViewPart )view;
    } else {
      throw new IllegalArgumentException();
    }
  }

  public void run( IAction action ) {
    if( action.isChecked() ) {
      viewpart.getViewer().addFilter( new ViewerFilter() {

        public boolean select( Viewer viewer,
                               Object parentElement,
                               Object element )
        {
          if( element.toString().startsWith( "Child X" ) ) {
            return false;
          }
          return true;
        }
      } );
    } else {
      viewpart.getViewer().resetFilters();
    }
  }

  public void selectionChanged( IAction action, ISelection selection ) {
  }
}
