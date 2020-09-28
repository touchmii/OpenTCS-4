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

import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.internal.provisional.action.*;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.internal.provisional.presentations.IActionBarPresentationFactory;
import org.eclipse.ui.presentations.*;


@SuppressWarnings( "restriction" )
public class PresentationFactory
  extends AbstractPresentationFactory
  implements IActionBarPresentationFactory
{

  @Override
  public StackPresentation createEditorPresentation( Composite parent,
                                                     IStackPresentationSite site )
  {
    return new StackPresentationImpl( site, parent );
  }

  @Override
  public StackPresentation createStandaloneViewPresentation( Composite parent,
                                                             IStackPresentationSite site,
                                                             boolean showTitle )
  {
    return new StackPresentationImpl( site, parent );
  }

  @Override
  public StackPresentation createViewPresentation( Composite parent,
                                                   IStackPresentationSite site )
  {
    return new StackPresentationImpl( site, parent );
  }

  public ICoolBarManager2 createCoolBarManager() {
    return new DemoCoolBarManager();
  }

  public IToolBarContributionItem createToolBarContributionItem( IToolBarManager toolBarManager,
                                                                 String id )
  {
    return new DemoToolBarContributionItem();
  }

  public IToolBarManager2 createToolBarManager() {
    return new DemoToolBarManager();
  }

  public IToolBarManager2 createViewToolBarManager() {
    return new DemoToolBarManager();
  }
}
