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

import org.eclipse.core.runtime.*;
import org.eclipse.jface.preference.ComboFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.ui.*;
import org.eclipse.ui.internal.util.PrefUtil;


public class DemoPreferencePage
  extends FieldEditorPreferencePage
  implements IWorkbenchPreferencePage
{
  private static final String PRESENTATION_FACTORY_ID
    = IWorkbenchPreferenceConstants.PRESENTATION_FACTORY_ID;
  private static final String ID_PRESENTATION_FACTORIES
    = "presentationFactories";
  private static final String LABEL_PRESENTATION = "Presentation";

  public DemoPreferencePage() {
    super( GRID );
    setPreferenceStore( PrefUtil.getAPIPreferenceStore() );
  }

  public void init( final IWorkbench workbench ) {
  }

  protected void createFieldEditors() {
    IExtensionRegistry registry = Platform.getExtensionRegistry();
    String nameSpace = PlatformUI.PLUGIN_EXTENSION_NAME_SPACE;
    IExtensionPoint extensionPoint
      = registry.getExtensionPoint( nameSpace, ID_PRESENTATION_FACTORIES );

    if( extensionPoint != null ) {
      IConfigurationElement[] elements
        = extensionPoint.getConfigurationElements();
      String[][] namesAndIds = new String[ elements.length ][ 2 ];
      for( int i = 0; i < elements.length; i++ ) {
        IConfigurationElement element = elements[ i ];
        namesAndIds[ i ][ 0 ] = element.getAttribute( "name" );
        namesAndIds[ i ][ 1 ] = element.getAttribute( "id" );
      }
      ComboFieldEditor comboEditor
        = new ComboFieldEditor( PRESENTATION_FACTORY_ID,
                                LABEL_PRESENTATION,
                                namesAndIds,
                                getFieldEditorParent() );
      addField( comboEditor );
    }
  }
}
