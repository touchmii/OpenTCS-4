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
package com.lvsrobot.webclient.demo.presentation;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.plugin.AbstractUIPlugin;


final class Images {
  final static Image IMG_TOP_LEFT = registerImage( "top_left.gif" );
  final static Image IMG_TOP_CENTER = registerImage( "top_center.gif" );
  final static Image IMG_TOP_RIGHT = registerImage( "top_right.gif" );
  final static Image IMG_MIDDLE_LEFT = registerImage( "middle_left.gif" );
  final static Image IMG_MIDDLE_CENTER = registerImage( "middle_center.gif" );
  final static Image IMG_MIDDLE_RIGHT = registerImage( "middle_right.gif" );
  final static Image IMG_BOTTOM_LEFT = registerImage( "bottom_left.gif" );
  final static Image IMG_BOTTOM_CENTER = registerImage( "bottom_center.gif" );
  final static Image IMG_BOTTOM_RIGHT = registerImage( "bottom_right.gif" );
  final static Image IMG_BANNER_ROUNDED_LEFT
    = registerImage( "banner_rounded_left.png" );
  final static Image IMG_BANNER_ROUNDED_RIGHT
    = registerImage( "banner_rounded_right.png" );
  final static Image IMG_BANNER_BG
    = registerImage( "banner_bg.png" );

  
  private Images() {
    // prevent instance creation
  }
  
  
  private static Image registerImage( final String imageName ) {
    String id = "org.eclipse.rap.demo";
    String folder = "icons/presentation/";
    ImageDescriptor descriptor
    = AbstractUIPlugin.imageDescriptorFromPlugin( id, folder + imageName );
    return descriptor.createImage();
  }

}
