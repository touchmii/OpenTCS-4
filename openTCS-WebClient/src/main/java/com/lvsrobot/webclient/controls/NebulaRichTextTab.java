/*******************************************************************************
 * Copyright (c) 2016 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package com.lvsrobot.webclient.controls;

import static org.eclipse.nebula.widgets.richtext.RichTextEditorConfiguration.TOOLBAR_GROUPS;

import org.eclipse.nebula.widgets.richtext.RichTextEditor;
import org.eclipse.nebula.widgets.richtext.RichTextEditorConfiguration;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;


public class NebulaRichTextTab extends ExampleTab {

  private RichTextEditor editor;
  private Button editableButton;
  private boolean reorderToolbarGroups;
  private boolean removeToolbarButtons;

  public NebulaRichTextTab() {
    super( "Nebula RichText" );
    setDefaultStyle( SWT.BORDER );
  }

  @Override
  protected void createStyleControls( Composite parent ) {
    createStyleButton( "BORDER", SWT.BORDER, true );
    createVisibilityButton();
    createEditableButton( parent );
    createReorderToolbarGroups( parent );
    createRemoveToolbarButtons( parent );
    createBgColorButton();
    createFontChooser();
    createText( parent );
    createDispose( parent );
  }

  @Override
  protected void createExampleControls( Composite parent ) {
    parent.setLayout( new GridLayout( 1, false ) );
    createRichTextEditor( parent );
    registerControl( editor );
  }

  private void createRichTextEditor( Composite parent ) {
    RichTextEditorConfiguration config = new RichTextEditorConfiguration();
    if( removeToolbarButtons ) {
      config.removeDefaultToolbarButton( "Subscript" );
      config.removeDefaultToolbarButton( "Superscript" );
    }
    if( reorderToolbarGroups ) {
      StringBuilder builder = new StringBuilder( "[" );
      builder.append( "{\"name\":\"styles\"}," );
      builder.append( "{\"name\":\"colors\" }," );
      builder.append( "{\"name\":\"basicstyles\",\"groups\":[\"basicstyles\",\"cleanup\"]}" );
      builder.append( "]" );
      config.setOption( TOOLBAR_GROUPS, builder.toString() );
    }
    editor = new RichTextEditor( parent, config, getStyle() );
    editor.setLayoutData( new GridData( SWT.FILL, SWT.FILL, true, true ) );
    editor.setFont( new Font( parent.getDisplay(), "fantasy", 19, 0 ) );
    editor.setText( "Hello Fantasy Font" );
    editor.setEditable( checkControl( editableButton ) ? editableButton.getSelection() : true );
  }

  private void createEditableButton( Composite parent ) {
    editableButton = new Button( parent, SWT.CHECK );
    editableButton.setText( "Editable" );
    editableButton.setSelection( true );
    editableButton.addListener( SWT.Selection, new Listener() {
      @Override
      public void handleEvent( Event event ) {
        editor.setEditable( editableButton.getSelection() );
      }
    } );
  }

  private void createText( Composite parent ) {
    Group group = new Group( parent, SWT.NONE );
    group.setText( "Text" );
    group.setLayout( new GridLayout( 2, false ) );
    final Text setText = new Text( group, SWT.BORDER );
    setText.setLayoutData( new GridData( 200, SWT.DEFAULT ) );
    Button setButton = new Button( group, SWT.PUSH );
    setButton.setText( "Set" );
    setButton.addListener( SWT.Selection, new Listener() {
      @Override
      public void handleEvent( Event event ) {
        editor.setText( setText.getText() );
      }
    } );
    Button getButton = new Button( group, SWT.PUSH );
    getButton.setText( "Get" );
    getButton.addListener( SWT.Selection, new Listener() {
      @Override
      public void handleEvent( Event event ) {
        log( editor.getText() );
      }
    } );
  }

  private void createDispose( Composite parent ) {
    Button disposeButton = new Button( parent, SWT.PUSH );
    disposeButton.setText( "Dispose" );
    disposeButton.addListener( SWT.Selection, new Listener() {
      @Override
      public void handleEvent( Event event ) {
        editor.dispose();
      }
    } );
  }

  private void createReorderToolbarGroups( Composite parent ) {
    final Button reorderButton = new Button( parent, SWT.CHECK );
    reorderButton.setText( "Remove and reorder toolbar groups" );
    reorderButton.addListener( SWT.Selection, new Listener() {
      @Override
      public void handleEvent( Event event ) {
        reorderToolbarGroups = reorderButton.getSelection();
        createNew();
      }
    } );
  }

  private void createRemoveToolbarButtons( Composite parent ) {
    final Button removeButton = new Button( parent, SWT.CHECK );
    removeButton.setText( "Remove Subscript/Superscript toolbar buttons" );
    removeButton.addListener( SWT.Selection, new Listener() {
      @Override
      public void handleEvent( Event event ) {
        removeToolbarButtons = removeButton.getSelection();
        createNew();
      }
    } );
  }

}
