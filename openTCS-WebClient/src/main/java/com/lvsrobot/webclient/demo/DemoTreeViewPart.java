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
package com.lvsrobot.webclient.demo;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.*;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.views.properties.*;


public class DemoTreeViewPart extends ViewPart implements IDoubleClickListener {

  private TreeViewer viewer;
  private IPropertySheetPage propertyPage;

//  TODO [rst] Add via extension
//  private final class LeafStarLabelDecorator extends LabelProvider
//    implements ILabelDecorator
//  {
//
//    public String decorateText( String text, Object element ) {
//      if( text.startsWith( "Leaf" ) ) {
//        return text + "*";
//      }
//      return text;
//    }
//
//    public Image decorateImage( Image image, Object element ) {
//      return null;
//    }
//  }

  private static final class ViewLabelProvider extends LabelProvider {
    @Override
    public Image getImage( Object element ) {
      IWorkbench workbench = PlatformUI.getWorkbench();
      ISharedImages sharedImages = workbench.getSharedImages();
      return sharedImages.getImage( ISharedImages.IMG_OBJ_ELEMENT );
    }
  }

  class TreeObject implements IPropertySource {
    private static final String PROP_ID_LOCATION = "location";
    private static final String PROP_ID_NAME = "name";
    private String name;
    private String location;
    private TreeParent parent;

    public TreeObject( String name ) {
      this( name, "" );
    }

    public TreeObject( String name, String location ) {
      this.name = name;
      this.location = location;
    }

    public String getName() {
      return name;
    }

    public String getLocation() {
      return location;
    }

    public void setParent( TreeParent parent ) {
      this.parent = parent;
    }

    public TreeParent getParent() {
      return parent;
    }

    @Override
    public String toString() {
      return getName();
    }

    public Object getEditableValue() {
      return this;
    }

    public IPropertyDescriptor[] getPropertyDescriptors() {
      return new IPropertyDescriptor[] {
        new TextPropertyDescriptor( PROP_ID_NAME, "Name" ),
        new TextPropertyDescriptor( PROP_ID_LOCATION, "Location" ),
      };
    }

    public Object getPropertyValue( Object id ) {
      Object result = null;
      if( PROP_ID_NAME.equals( id ) ) {
        result = name;
      } else if( PROP_ID_LOCATION.equals( id ) ) {
        result = location;
      }
      return result;
    }

    public boolean isPropertySet( Object id ) {
      boolean result = false;
      if( PROP_ID_NAME.equals( id ) ) {
        result = name != null && !"".equals( name );
      } else if( PROP_ID_LOCATION.equals( id ) ) {
        result = location != null && !"".equals( location );
      }
      return result;
    }

    public void resetPropertyValue( Object id ) {
      if( PROP_ID_NAME.equals( id ) ) {
        name = "";
      } else if( PROP_ID_LOCATION.equals( id ) ) {
        location = "";
      }
    }

    public void setPropertyValue( Object id, Object value ) {
      if( PROP_ID_NAME.equals( id ) ) {
        name = ( String )value;
      } else if( PROP_ID_LOCATION.equals( id ) ) {
        location = ( String )value;
      }
      update( this );
    }
  }

  /**
   * Instances of this type are decorated with an error marker
   */
  private class BrokenTreeObject extends TreeObject {
    public BrokenTreeObject( String name ) {
      super( name );
    }
  }

  private class TreeParent extends TreeObject {

    private final List<TreeObject> children;

    public TreeParent( String name ) {
      super( name );
      children = new ArrayList<TreeObject>();
    }

    public void addChild( TreeObject child ) {
      children.add( child );
      child.setParent( this );
    }

    public TreeObject[] getChildren() {
      TreeObject[] result = new TreeObject[ children.size() ];
      children.toArray( result );
      return result;
    }

    public boolean hasChildren() {
      return children.size() > 0;
    }
  }
  private final class TreeViewerContentProvider
    implements IStructuredContentProvider, ITreeContentProvider
  {

    private TreeParent invisibleRoot;

    public void inputChanged( Viewer v, Object oldInput, Object newInput ) {
    }

    public void dispose() {
    }

    public Object[] getElements( Object parent ) {
      if( parent instanceof IViewPart ) {
        if( invisibleRoot == null ) {
          initialize();
        }
        return getChildren( invisibleRoot );
      }
      return getChildren( parent );
    }

    public Object getParent( Object child ) {
      if( child instanceof TreeObject ) {
        return ( ( TreeObject )child ).getParent();
      }
      return null;
    }

    public Object[] getChildren( Object parent ) {
      if( parent instanceof TreeParent ) {
        return ( ( TreeParent )parent ).getChildren();
      }
      return new Object[ 0 ];
    }

    public boolean hasChildren( Object parent ) {
      if( parent instanceof TreeParent ) {
        return ( ( TreeParent )parent ).hasChildren();
      }
      return false;
    }

    /*
     * We will set up a dummy model to initialize tree heararchy. In a real
     * code, you will connect to a real model and expose its hierarchy.
     */
    private void initialize() {
      TreeObject to1 = new TreeObject( "EclipseCon location",
                                       "http://maps.google.com/maps?q=5001%20Great%20America%20Pkwy%20Santa%20Clara%20CA%2095054" );
      TreeObject to2 = new TreeObject( "Eclipse Foundation",
                                       "http://maps.google.com/maps?q=Ottawa" );
      TreeObject to3 = new TreeObject( "Innoopract Inc",
                                       "http://maps.google.com/maps?q=Portland" );
      TreeParent p1 = new TreeParent( "Locate in browser view" );
      p1.addChild( to1 );
      p1.addChild( to2 );
      p1.addChild( to3 );
      TreeObject to4 = new BrokenTreeObject( "Leaf 4" );
      TreeParent p2 = new TreeParent( "Parent 2" );
      p2.addChild( to4 );
      TreeParent root = new TreeParent( "Root" );
      TreeParent p3 = new TreeParent( "Child X - filter me!" );
      root.addChild( p1 );
      root.addChild( p2 );
      root.addChild( p3 );
      invisibleRoot = new TreeParent( "" );
      invisibleRoot.addChild( root );
    }
  }

  @Override
  public void createPartControl( Composite parent ) {
    viewer = new TreeViewer( parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL );
    viewer.setContentProvider( new TreeViewerContentProvider() );
    ILabelDecorator labelDecorator
      = PlatformUI.getWorkbench().getDecoratorManager().getLabelDecorator();
    ILabelProvider labelProvider
      = new DecoratingLabelProvider( new ViewLabelProvider(), labelDecorator );
    viewer.setLabelProvider( labelProvider );
    viewer.setInput( this );
    viewer.addDoubleClickListener( this );
    getSite().setSelectionProvider( viewer );
  }

  @Override
  public Object getAdapter( Class adapter ) {
    Object result = super.getAdapter( adapter );
    if( adapter == IPropertySheetPage.class ) {
      if( propertyPage == null ) {
        propertyPage = new PropertySheetPage();
      }
      result = propertyPage;
    }
    return result;
  }

  @Override
  public void setFocus() {
    viewer.getTree().setFocus();
  }

  public void doubleClick( DoubleClickEvent event ) {
    String msg = "You doubleclicked on " + event.getSelection().toString();
    Shell shell = viewer.getTree().getShell();
    MessageDialog.openInformation( shell, "Treeviewer", msg );
  }

  private void update( TreeObject treeObject ) {
    getViewer().update( treeObject, null );
  }

  public TreeViewer getViewer() {
    return viewer;
  }
}
