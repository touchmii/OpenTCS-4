/*******************************************************************************
 * Copyright (c) 2008, 2013 Innoopract Informationssysteme GmbH and others.
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

import java.util.HashSet;
import java.util.Set;

import org.eclipse.jface.viewers.*;
import org.eclipse.rap.rwt.RWT;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;
import org.eclipse.ui.part.PageBook;


public class NavigationPaneViewer extends Composite
  implements ISelectionProvider, ISelectionChangedListener
{

  private final static int BUTTON_HEIGHT = 30;
  private Label title;
  private PageBook pageBook;
  private NavigationPaneContent[] content = {};
  private Composite selectorArea;
  private final Set<ISelectionChangedListener> selectionListener
    = new HashSet<ISelectionChangedListener>();
  private ISelection selection = StructuredSelection.EMPTY;

  private final class Selector extends SelectionAdapter {
    private final NavigationPaneContent page;
    private Selector( final NavigationPaneContent page ) {
      this.page = page;
    }
    @Override
    public void widgetSelected( SelectionEvent e ) {
      for( int i = 0; i < content.length; i++ ) {
        if( content[ i ].isSelectionProvider() ) {
          ISelectionProvider provider = content[ i ].getSelectionProvider();
          provider.removeSelectionChangedListener( NavigationPaneViewer.this );
        }
        Object selector = content[ i ].getSelector();
        ( ( Button )selector ).setSelection( content[ i ] == page );
      }
      pageBook.showPage( page.getControl() );
      title.setText( page.getLabel() );
      page.getControl().setFocus();
      if( page.isSelectionProvider() ) {
        ISelectionProvider provider = page.getSelectionProvider();
        provider.addSelectionChangedListener( NavigationPaneViewer.this );
        setSelection( provider.getSelection() );
      } else {
        setSelection( StructuredSelection.EMPTY );
      }
    }
  }


  public NavigationPaneViewer( Composite parent, int style, NavigationPaneContent[] content ) {
    super( parent, style );
    if( content != null ) {
      this.content = content;
    }
    createControl();
  }


  private void createControl() {
    setLayout( new FormLayout() );
    createTitleArea( this );
    createContentArea( this );
    createSelectorArea( this );
  }

  private void createTitleArea( Composite parent ) {
    title = new Label( parent, SWT.NONE );
    title.setBackground( title.getDisplay().getSystemColor( SWT.COLOR_WHITE ) );
    FontData fontData = title.getFont().getFontData()[ 0 ];
    Font titleFont = new Font( title.getDisplay(),
                               fontData.getName(),
                               18,
                               fontData.getStyle() | SWT.BOLD );
    title.setFont( titleFont );
    FormData fd = new FormData();
    title.setLayoutData( fd );
    fd.top = new FormAttachment( 0, 0 );
    fd.left = new FormAttachment( 0, 0 );
    fd.right = new FormAttachment( 100, 0 );
    fd.bottom = new FormAttachment( 0, BUTTON_HEIGHT );
    title.setText( "Trallala" );
  }

  private void createContentArea( Composite parent ) {
    pageBook = new PageBook( parent, SWT.NONE );
    FormData fd = new FormData();
    pageBook.setLayoutData( fd );
    fd.top = new FormAttachment( 0, BUTTON_HEIGHT + 1 );
    fd.left = new FormAttachment( 0, 0 );
    fd.right = new FormAttachment( 100, 0 );
    int bottom = -BUTTON_HEIGHT * ( content.length + 1 ) - 1;
    fd.bottom = new FormAttachment( 100, bottom );
    pageBook.setBackground( parent.getDisplay().getSystemColor( SWT.COLOR_WHITE ) );

    for( int i = 0; i < content.length; i++ ) {
      createPage( i );
    }
  }

  private Control createPage( int pageIndex ) {
    Composite result = new Composite( pageBook, SWT.NONE );
    result.setBackground( result.getDisplay().getSystemColor( SWT.COLOR_WHITE ) );
    result.setLayout( new FillLayout() );
    content[ pageIndex ].setControl( result );
    content[ pageIndex ].createControl( result );
    return result;
  }

  private void createSelectorArea( Composite parent ) {
    selectorArea = new Composite( parent, SWT.NONE );
    FormData fd = new FormData();
    selectorArea.setLayoutData( fd );
    fd.top = new FormAttachment( 100, -BUTTON_HEIGHT * ( content.length + 1 ) );
    fd.left = new FormAttachment( 0, 0 );
    fd.right = new FormAttachment( 100, 0 );
    fd.bottom = new FormAttachment( 100, 0 );
    FillLayout fillLayout = new FillLayout( SWT.VERTICAL );
    fillLayout.spacing = - 1;
    selectorArea.setLayout( fillLayout );

    for( int i = 0; i < content.length; i++ ) {
      createSelector( i );
    }

    new Label( selectorArea, SWT.NONE );

    if( content.length > 0 ) {
      new Selector( content[ 0 ] ).widgetSelected( null );
    }

  }


  private void createSelector( int i ) {
    Button button = new Button( selectorArea, SWT.TOGGLE | SWT.FLAT );
    content[ i ].setSelector( button );
    button.setText( content[ i ].getLabel() );
    button.addSelectionListener( new Selector( content[ i ] ) );
    button.setData( RWT.CUSTOM_VARIANT, "mybutton" );
    button.setSelection( false );
  }


  ////////////////////////////////
  // interface ISelectionProvider

  public void addSelectionChangedListener( ISelectionChangedListener listener ) {
    selectionListener.add( listener );
  }

  public void removeSelectionChangedListener( ISelectionChangedListener listener ) {
    selectionListener.remove( listener );
  }

  public ISelection getSelection() {
    return selection;
  }

  public void setSelection( ISelection newSelection ) {
    ISelection oldSelection = selection;
    if( newSelection == null ) {
      selection = StructuredSelection.EMPTY;
    } else {
      selection = newSelection;
    }
    if( !oldSelection.equals( selection ) ) {
      SelectionChangedEvent evt = new SelectionChangedEvent( this, selection );
      for( ISelectionChangedListener listener: selectionListener ) {
        try {
          listener.selectionChanged( evt );
        } catch( RuntimeException re ) {
          // TODO Auto-generated catch block
          re.printStackTrace();
        }
      }
    }
  }


  ///////////////////////////////
  // interface ISelectionListener

  public void selectionChanged( SelectionChangedEvent event ) {
    setSelection( event.getSelection() );
  }
}