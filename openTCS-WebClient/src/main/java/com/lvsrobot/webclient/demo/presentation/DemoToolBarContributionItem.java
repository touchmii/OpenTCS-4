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

import org.eclipse.jface.action.*;
import org.eclipse.jface.internal.provisional.action.IToolBarContributionItem;

@SuppressWarnings( "restriction" )
class DemoToolBarContributionItem extends ContributionItem implements IToolBarContributionItem {

  private final class ToolBarManager
    extends ContributionManager
    implements IToolBarManager
  {

    @Override
    public void add( final IAction action ) {
    }

    @Override
    public void add( final IContributionItem item ) {
    }

    @Override
    public void appendToGroup( final String groupName, final IAction action ) {
    }

    @Override
    public void appendToGroup( final String groupName,
                               final IContributionItem item )
    {
    }

    @Override
    public IContributionItem find( final String id ) {
      return null;
    }

    @Override
    public IContributionItem[] getItems() {
      return new IContributionItem[ 0 ];
    }

    @Override
    public IContributionManagerOverrides getOverrides() {
      return null;
    }

    @Override
    public void insertAfter( final String id, final IAction action ) {
    }

    @Override
    public void insertAfter( final String id, final IContributionItem item ) {
    }

    @Override
    public void insertBefore( final String id, final IAction action ) {
    }

    @Override
    public void insertBefore( final String id, final IContributionItem item ) {
    }

    @Override
    public boolean isDirty() {
      return false;
    }

    @Override
    public boolean isEmpty() {
      return false;
    }

    @Override
    public void markDirty() {
    }

    @Override
    public void prependToGroup( final String groupName, final IAction action ) {
    }

    @Override
    public void prependToGroup( final String groupName,
                                final IContributionItem item )
    {
    }

    @Override
    public IContributionItem remove( final String id ) {
      return null;
    }

    @Override
    public IContributionItem remove( final IContributionItem item ) {
      return null;
    }

    @Override
    public void removeAll() {
    }

    public void update( final boolean force ) {
    }
  }

  public int getCurrentHeight() {
    return 0;
  }

  public int getCurrentWidth() {
    return 0;
  }

  public int getMinimumItemsToShow() {
    return 0;
  }

  public IToolBarManager getToolBarManager() {
    return new ToolBarManager();
  }

  public boolean getUseChevron() {
    return false;
  }

  public void setCurrentHeight( final int currentHeight ) {
  }

  public void setCurrentWidth( final int currentWidth ) {
  }

  public void setMinimumItemsToShow( final int minimumItemsToShow ) {
  }

  public void setUseChevron( final boolean value ) {
  }
}