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

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.TreeAdapter;
import org.eclipse.swt.events.TreeEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.*;
import org.eclipse.ui.*;
import org.eclipse.ui.part.EditorPart;

public class BazEditor extends EditorPart {

  public BazEditor() {
    super();
  }

  public void doSave( final IProgressMonitor monitor ) {
    MessageDialog.openInformation( getSite().getShell(),
                                   "Foo Editor",
                                   "Saved :" );
  }

  public void doSaveAs() {
  }

  public void init( final IEditorSite site, final IEditorInput input )
    throws PartInitException
  {
    setSite( site );
    setInput( input );
  }
  protected boolean dirty = false;
  private Tree tree;

  public boolean isDirty() {
    return dirty;
  }

  protected void setDirty( final boolean value ) {
    dirty = value;
    firePropertyChange( PROP_DIRTY );
  }

  public boolean isSaveAsAllowed() {
    return false;
  }

  public void createPartControl( final Composite parent ) {
    parent.setLayout( new FillLayout() );
    tree = new Tree( parent, SWT.SINGLE );
    for( int i = 0; i < 4; i++ ) {
      TreeItem item = new TreeItem( tree, SWT.NONE );
      item.setText( "Node_" + ( i + 1 ) );
      if( i < 3 ) {
        TreeItem subitem = new TreeItem( item, SWT.NONE );
        subitem.setText( "Subnode_" + ( i + 1 ) );
      }
    }
    tree.addTreeListener( new TreeAdapter() {

      public void treeExpanded( final TreeEvent e ) {
        super.treeExpanded( e );
        setDirty( true );
      }
    } );
  }

  public void setFocus() {
    tree.setFocus();
  }
}
