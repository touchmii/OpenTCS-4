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
 * See https://developers.google.com/speed/libraries/
 */
public class ExternalJsResource implements IResource {

  public ExternalJsResource() {
  }

  public ClassLoader getLoader() {
    return null;
  }

  public String getLocation() {
    return "//ajax.googleapis.com/ajax/libs/swfobject/2.2/swfobject.js";
  }

  public boolean isJSLibrary() {
    return true;
  }

  public boolean isExternal() {
    return true;
  }

}
