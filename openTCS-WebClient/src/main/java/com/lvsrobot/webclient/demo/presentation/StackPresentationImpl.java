/*******************************************************************************
 * Copyright (c) 2002, 2013 Innoopract Informationssysteme GmbH and others.
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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.PopupDialog;
import org.eclipse.jface.util.Geometry;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;
import org.eclipse.ui.internal.dnd.DragUtil;
import org.eclipse.ui.presentations.*;

class StackPresentationImpl extends StackPresentation {

  private static final int TITLE_HEIGHT = 30;
  private List<IPresentablePart> presentableParts = new ArrayList<IPresentablePart>();
  private Control control;
  private IPresentablePart current;
  private Label content;
  private Button head;

  protected StackPresentationImpl( final IStackPresentationSite stackSite,
                                   final Composite parent )
  {
    super( stackSite );
    Display display = parent.getDisplay();
    Composite background = new Composite( parent, SWT.NONE );
    background.setBackgroundMode( SWT.INHERIT_FORCE );
    background.setLayout( new FormLayout() );
    content = new Label( background, SWT.NONE );
    content.setBackground( display.getSystemColor( SWT.COLOR_WHITE ) );
    FormData fdContent = createFormData( content );
    fdContent.top = new FormAttachment( 0, TITLE_HEIGHT );
    fdContent.left = new FormAttachment( 0, 2 );
    fdContent.right = new FormAttachment( 100, -2 );
    fdContent.bottom = new FormAttachment( 100, -2 );
    content.addControlListener( new ControlAdapter() {

      @Override
      public void controlResized( final ControlEvent evt ) {
        layout();
      }
    } );
    head = new Button( background, SWT.PUSH | SWT.FLAT | SWT.LEAD );
    FontData fontData = head.getFont().getFontData()[ 0 ];
    head.setFont( new Font( display,
                            fontData.getName(),
                            fontData.getHeight() + 2,
                            fontData.getStyle() ) );
    FormData fdHead = createFormData( head );
    fdHead.top = new FormAttachment( 0, -1 );
    fdHead.left = new FormAttachment( 0, -1 );
    fdHead.height = TITLE_HEIGHT + 1;
    fdHead.right = new FormAttachment( 100, 1 );
    head.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected( final SelectionEvent evt ) {
        Object[] parts = presentableParts.toArray();
        SelectionListener listener = new SelectionAdapter() {
          @Override
          public void widgetSelected( final SelectionEvent evt ) {
            TableItem item = ( TableItem )evt.item;
            IPresentablePart part = ( IPresentablePart )item.getData();
            getSite().selectPart( part );
            Control control = ( Control )evt.widget;
            control.getShell().close();
          }
        };
        PopupDialog popupDialog = new StackPopup( head.getShell(),
                                                  content,
                                                  parts,
                                                  listener );
        popupDialog.open();
      }
    } );
    Label topLeft = createImageLabel( background, Images.IMG_TOP_LEFT );
    FormData fdTopLeft = createFormData( topLeft );
    fdTopLeft.left = new FormAttachment( 0, 0 );
    fdTopLeft.top = new FormAttachment( 0, 0 );
    Label topRight = createImageLabel( background, Images.IMG_TOP_RIGHT );
    FormData fdTopRight = createFormData( topRight );
    fdTopRight.top = new FormAttachment( 0, 0 );
    fdTopRight.left = new FormAttachment( 100, -topRight.getSize().x );
    Label topCenter = new Label( background, SWT.NONE );
    topCenter.setBackgroundImage( Images.IMG_TOP_CENTER );
    FormData fdTopCenter = createFormData( topCenter );
    fdTopCenter.top = new FormAttachment( 0, 0 );
    fdTopCenter.left = new FormAttachment( topLeft );
    fdTopCenter.right = new FormAttachment( topRight );
    fdTopCenter.height = Images.IMG_TOP_CENTER.getBounds().height;
    Label middleLeft = new Label( background, SWT.NONE );
    middleLeft.setBackgroundImage( Images.IMG_MIDDLE_LEFT );
    FormData fdMiddleLeft = createFormData( middleLeft );
    fdMiddleLeft.top = new FormAttachment( topLeft );
    fdMiddleLeft.left = new FormAttachment( 0, 0 );
    fdMiddleLeft.width = Images.IMG_MIDDLE_LEFT.getBounds().width;
    fdMiddleLeft.bottom
      = new FormAttachment( 100, -Images.IMG_BOTTOM_LEFT.getBounds().height );
    Label middleRight = new Label( background, SWT.NONE );
    middleRight.setBackgroundImage( Images.IMG_MIDDLE_RIGHT );
    FormData fdMiddleRight = createFormData( middleRight );
    fdMiddleRight.top = new FormAttachment( topRight );
    fdMiddleRight.left
      = new FormAttachment( 100, -Images.IMG_MIDDLE_RIGHT.getBounds().width );
    fdMiddleRight.width = Images.IMG_MIDDLE_RIGHT.getBounds().width;
    int height = -Images.IMG_BOTTOM_RIGHT.getBounds().height;
    fdMiddleRight.bottom = new FormAttachment( 100, height );
    Label bottomLeft = createImageLabel( background, Images.IMG_BOTTOM_LEFT );
    FormData fdBottomLeft = createFormData( bottomLeft );
    fdBottomLeft.left = new FormAttachment( 0, 0 );
    fdBottomLeft.top = new FormAttachment( middleLeft );
    Label bottomRight = createImageLabel( background, Images.IMG_BOTTOM_RIGHT );
    FormData fdBottomRight = createFormData( bottomRight );
    fdBottomRight.top = new FormAttachment( middleRight );
    fdBottomRight.left = new FormAttachment( 100, -bottomRight.getSize().x );
    Label bottomCenter = new Label( background, SWT.NONE );
    bottomCenter.setBackgroundImage( Images.IMG_BOTTOM_CENTER );
    FormData fdBottomCenter = createFormData( bottomCenter );
    fdBottomCenter.bottom = new FormAttachment( 100, 0 );
    fdBottomCenter.left = new FormAttachment( bottomLeft );
    fdBottomCenter.right = new FormAttachment( bottomRight );
    fdBottomCenter.height = Images.IMG_BOTTOM_CENTER.getBounds().height;
    Label middleCenter = new Label( background, SWT.NONE );
    middleCenter.setBackgroundImage( Images.IMG_MIDDLE_CENTER );
    FormData fdMiddleCenter = createFormData( middleCenter );
    fdMiddleCenter.top = new FormAttachment( topCenter );
    fdMiddleCenter.left = new FormAttachment( middleLeft );
    fdMiddleCenter.right = new FormAttachment( middleRight );
    fdMiddleCenter.bottom = new FormAttachment( bottomCenter );
    control = background;
  }

  private FormData createFormData( final Control control ) {
    FormData result = new FormData();
    control.setLayoutData( result );
    return result;
  }

  private Label createImageLabel( final Composite background,
                                  final Image cornerImage )
  {
    Label result = new Label( background, SWT.NONE );
    result.setImage( cornerImage );
    Rectangle boundsLeft = cornerImage.getBounds();
    result.setSize( boundsLeft.width, boundsLeft.height );
    return result;
  }

  @Override
  public void addPart( final IPresentablePart newPart, final Object cookie ) {
    presentableParts.add( newPart );
  }

  @Override
  public void dispose() {
  }

  @Override
  public Control getControl() {
    return control;
  }

  @Override
  public Control[] getTabList( final IPresentablePart part ) {
    return null;
  }

  @Override
  public void removePart( final IPresentablePart oldPart ) {
    presentableParts.remove( oldPart );
  }

  @Override
  public void selectPart( final IPresentablePart toSelect ) {
    if( toSelect != null ) {
      toSelect.setVisible( true );
      head.setImage( toSelect.getTitleImage() );
      head.setText( toSelect.getTitle() );
    } else {
      head.setText( "" );
      head.setImage( null );
    }
    if( current != null ) {
      current.setVisible( false );
    }
    current = toSelect;
    layout();
  }

  protected void layout() {
    if( current != null ) {
      Rectangle clientArea = DragUtil.getDisplayBounds( content );
      Rectangle bounds = Geometry.toControl( current.getControl().getParent(),
                                             clientArea );
      current.setBounds( bounds );
    }
  }

  @Override
  public void setActive( final int newState ) {
  }

  @Override
  public void setBounds( final Rectangle bounds ) {
    control.setBounds( bounds.x + 4,
                       bounds.y + 4,
                       bounds.width - 8,
                       bounds.height - 8 );
  }

  @Override
  public void setState( final int state ) {
  }

  @Override
  public void setVisible( final boolean isVisible ) {
  }

  @Override
  public void showPaneMenu() {
  }

  @Override
  public void showSystemMenu() {
  }

  @Override
  public StackDropResult dragOver( final Control currentControl,
                                   final Point location )
  {
    return null;
  }
}