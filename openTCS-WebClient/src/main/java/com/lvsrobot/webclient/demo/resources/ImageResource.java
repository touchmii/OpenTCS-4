/*******************************************************************************
 * Copyright (c) 2012 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package com.lvsrobot.webclient.demo.resources;

import org.eclipse.rap.ui.resources.IResource;


/*
 * Ununsed resource for testing resource extensions.
 */
public class ImageResource implements IResource {

  public ImageResource() {
  }

  public ClassLoader getLoader() {
    return ImageResource.class.getClassLoader();
  }

  public String getLocation() {
    return "com/lvsrobot/webclient/demo/resources/eclipse.svg";
  }

  public boolean isJSLibrary() {
    return false;
  }

  public boolean isExternal() {
    return false;
  }

}
