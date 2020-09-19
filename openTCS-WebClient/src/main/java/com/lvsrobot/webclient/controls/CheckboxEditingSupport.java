/*******************************************************************************
 * Copyright (c) 2011, 2012 Rüdiger Herrmann and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Rüdigier Herrmann - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
package com.lvsrobot.webclient.controls;

public abstract class CheckboxEditingSupport {
  public abstract boolean canEdit( Object element ); 
  public abstract void setValue( Object element, Boolean value );
  public abstract Boolean getValue( Object element );
}
