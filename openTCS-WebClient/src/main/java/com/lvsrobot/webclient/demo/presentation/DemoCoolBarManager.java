/*******************************************************************************
 * Copyright (c) 2002, 2013 Innoopract Informationssysteme GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
package com.lvsrobot.webclient.demo.presentation;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.action.*;
import org.eclipse.jface.internal.provisional.action.ICoolBarManager2;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

@SuppressWarnings( "restriction" )
class DemoCoolBarManager extends ContributionManager implements ICoolBarManager2 {

  private Composite control;

  public Control createControl2( Composite parent ) {
    control = new Composite( parent, SWT.NONE );
    return control;
  }

  public void dispose() {
  }

  public Control getControl2() {
    return control;
  }

  public void refresh() {
  }

  public void resetItemOrder() {
  }

  public void setItems( IContributionItem[] newItems ) {
  }

  public void add( IToolBarManager toolBarManager ) {
  }

  public IMenuManager getContextMenuManager() {
    return null;
  }

  public boolean getLockLayout() {
    return false;
  }

  public int getStyle() {
    return 0;
  }

  public void setContextMenuManager( IMenuManager menuManager ) {
  }

  public void setLockLayout( boolean value ) {
  }

  public void update( boolean force ) {
    IContributionItem[] contributionItems = getItems();
    List<Object> actions = new ArrayList<Object>();
    for( int i = 0; i < contributionItems.length; i++ ) {
      if( contributionItems[ i ] instanceof ToolBarContributionItem ) {
        ToolBarContributionItem item = ( ToolBarContributionItem )contributionItems[ i ];
        if( item.isVisible() ) {
          IToolBarManager toolBarManager = item.getToolBarManager();
          IContributionItem[] toolBarItems = toolBarManager.getItems();
          for( int j = 0; j < toolBarItems.length; j++ ) {
            ActionContributionItem actionItem = ( ActionContributionItem )toolBarItems[ j ];
            actions.add( actionItem.getAction() );
          }
        }
      } else {
        // TODO [fappel]
      }
      if( i + 2 < contributionItems.length ) {
        actions.add( "Separator" );
      }
    }

    ActionBar.create( actions, control );
  }
}