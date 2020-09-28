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
package com.lvsrobot.webclient.demo.editor;

import org.eclipse.jface.resource.ImageDescriptor;
import  com.lvsrobot.webclient.demo.DemoActionBarAdvisor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;

public class FooEditorInput implements IEditorInput {

  public FooEditorInput( final DemoActionBarAdvisor demoActionBarAdvisor ) {
  }

  public boolean exists() {
    return true;
  }

  public ImageDescriptor getImageDescriptor() {
    return null;
  }

  public String getName() {
    return this.hashCode() + ".bar";
  }

  public IPersistableElement getPersistable() {
    return null;
  }

  public String getToolTipText() {
    return "/foo/bar/" + getName();
  }

  public Object getAdapter( final Class adapter ) {
    return null;
  }
}