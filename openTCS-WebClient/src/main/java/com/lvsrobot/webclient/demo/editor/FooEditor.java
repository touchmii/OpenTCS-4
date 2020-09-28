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
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.*;
import org.eclipse.ui.*;
import org.eclipse.ui.part.MultiPageEditorPart;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;

public class FooEditor extends MultiPageEditorPart {

  private BarEditor editor;
  private BazEditor treeeditor;
  private IContentOutlinePage outlinePage;

  public FooEditor() {
    super();
  }

  public void doSave( final IProgressMonitor monitor ) {
    editor.setDirty( false );
    treeeditor.setDirty( false );
  }

  public void doSaveAs() {
  }

  public boolean isSaveAsAllowed() {
    return false;
  }
  
  public Object getAdapter( final Class adapter ) {
    Object result = super.getAdapter( adapter );
    if( adapter == IContentOutlinePage.class ) {
      if( outlinePage == null ) {
        outlinePage = new IContentOutlinePage() {
          private Tree tree;
          public void createControl( final Composite parent ) {
            tree = new Tree( parent, SWT.SINGLE );
            for( int i = 0; i < 4; i++ ) {
              TreeItem item = new TreeItem( tree, SWT.NONE );
              item.setText( "Node_" + ( i + 1 ) );
              if( i < 3 ) {
                TreeItem subitem = new TreeItem( item, SWT.NONE );
                subitem.setText( "Subnode_" + ( i + 1 ) );
              }
            }
          }
  
          public void dispose() {
          }
  
          public Control getControl() {
            return tree;
          }
  
          public void setActionBars( final IActionBars actionBars ) {
          }
  
          public void setFocus() {
          }
  
          public void addSelectionChangedListener(
            final ISelectionChangedListener listener )
          {
          }
  
          public ISelection getSelection() {
            return null;
          }
  
          public void removeSelectionChangedListener(
            final ISelectionChangedListener listener )
          {
          }
  
          public void setSelection( final ISelection selection ) {
          }
          
        };
      }
      result = outlinePage;
    }
    return result;
  }

  public void init( final IEditorSite site, final IEditorInput input )
    throws PartInitException
  {
    super.init( site, input );
    setPartName( input.getName() );
  }

  protected void createPages() {
    editor = new BarEditor();
    treeeditor = new BazEditor();
    int index;
    try {
      ISharedImages sharedImages = PlatformUI.getWorkbench().getSharedImages();
      index = addPage( editor, getEditorInput() );
      setPageText( index, "Source" );
      setPageImage( index, sharedImages.getImage( ISharedImages.IMG_OBJ_FILE ) );
      index = addPage( treeeditor, getEditorInput() );
      setPageText( index, "Design" );
      setPageImage( index, sharedImages.getImage( ISharedImages.IMG_OBJ_FOLDER ) );
    } catch( PartInitException e ) {
      e.printStackTrace();
    }
  }
}
