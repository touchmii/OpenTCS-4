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

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnViewerEditor;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationEvent;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationListener;
import org.eclipse.jface.viewers.ColumnViewerEditorDeactivationEvent;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.ICellEditorListener;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.TableColumn;


public class CheckboxTableViewerColumn {

  private final TableViewer tableViewer;
  private final TableViewerColumn tableViewerColumn;
  private final int columnIndex;
  private final ColumnViewerEditorActivationListener editorActivationListener;
  private final CheckboxColumnLabelProvider checkboxLabelProvider;
  private final CheckboxColumnEditingSupport checkboxEditingSupport;
  private ILabelProvider labelProviderDelegate;
  private CheckboxEditingSupport editingSupportDelegate;
  private ColumnViewerEditor editor;
  private boolean editing;

  public CheckboxTableViewerColumn( TableViewer tableViewer, int columnStyle ) {
    this.tableViewer = tableViewer;
    checkboxLabelProvider = new CheckboxColumnLabelProvider();
    checkboxEditingSupport = new CheckboxColumnEditingSupport();
    editorActivationListener = new CheckboxEditorActivationListener();
    tableViewerColumn = createViewerColumn( columnStyle );
    columnIndex = computeColumnIndex();
    registerDisposeListener();
  }

  public TableColumn getColumn() {
    return tableViewerColumn.getColumn();
  }

  public void setLabelProvider( ILabelProvider labelProvider ) {
    labelProviderDelegate = labelProvider;
  }

  public ILabelProvider getLabelProvider() {
    return labelProviderDelegate;
  }

  public void setEditingSupport( CheckboxEditingSupport editingSupport ) {
    editingSupportDelegate = editingSupport;
  }

  public CheckboxEditingSupport getEditingSupport() {
    return editingSupportDelegate;
  }

  public void attachToEditor() {
    editor = tableViewer.getColumnViewerEditor();
    editor.addEditorActivationListener( editorActivationListener );
  }

  private TableViewerColumn createViewerColumn( int columnStyle ) {
    TableViewerColumn result = new TableViewerColumn( tableViewer, columnStyle );
    result.setLabelProvider( checkboxLabelProvider );
    result.setEditingSupport( checkboxEditingSupport );
    return result;
  }

  private int computeColumnIndex() {
    return getColumn().getParent().indexOf( getColumn() );
  }

  private void registerDisposeListener() {
    getColumn().addDisposeListener( new DisposeListener() {
      public void widgetDisposed( DisposeEvent event ) {
        dispose();
      }
    } );
  }

  private void dispose() {
    if( editor != null ) {
      editor.removeEditorActivationListener( editorActivationListener );
    }
    checkboxLabelProvider.dispose();
    if( labelProviderDelegate != null ) {
      labelProviderDelegate.dispose();
    }
  }

  private class CheckboxColumnEditingSupport extends EditingSupport {
    CheckboxColumnEditingSupport() {
      super( tableViewer );
    }

    @Override
    protected void setValue( Object element, Object value ) {
      editingSupportDelegate.setValue( element, ( Boolean )value );
      tableViewer.refresh( element );
    }

    @Override
    protected Object getValue( Object element ) {
      return editingSupportDelegate.getValue( element );
    }

    @Override
    protected CellEditor getCellEditor( final Object element ) {
      CellEditor cellEditor = new CheckWidgetCellEditor( getColumn().getParent() );
      cellEditor.addListener( new CellEditorAdapter() {
        @Override
        public void cancelEditor() {
          tableViewer.refresh( element );
        }
      } );
      return cellEditor;
    }

    @Override
    protected boolean canEdit( Object element ) {
      return editingSupportDelegate != null && editingSupportDelegate.canEdit( element );
    }

    @Override
    protected void initializeCellEditorValue( CellEditor cellEditor, ViewerCell cell ) {
      cell.setImage( null );
      super.initializeCellEditorValue( cellEditor, cell );
    }
  }

  private static class CellEditorAdapter implements ICellEditorListener {
    public void applyEditorValue() {
    }

    public void cancelEditor() {
    }

    public void editorValueChanged( boolean oldValidState, boolean newValidState ) {
    }
  }

  private class CheckboxColumnLabelProvider extends ColumnLabelProvider {

    @Override
    public String getText( Object element ) {
      String result = "";
      if( !editing ) {
        result = labelProviderDelegate.getText( element );
      }
      return result;
    }

    @Override
    public Image getImage( Object element ) {
      Image result = null;
      if( !editing ) {
        result = labelProviderDelegate.getImage( element );
      }
      return result;
    }
  }

  private class CheckboxEditorActivationListener extends ColumnViewerEditorActivationListener {
    @Override
    public void beforeEditorActivated( ColumnViewerEditorActivationEvent event ) {
      ViewerCell cell = ( ViewerCell )event.getSource();
      if( cell.getColumnIndex() == columnIndex ) {
        editing = true;
      }
    }

    @Override
    public void afterEditorActivated( ColumnViewerEditorActivationEvent event ) {
    }

    @Override
    public void beforeEditorDeactivated( ColumnViewerEditorDeactivationEvent event ) {
      ViewerCell cell = ( ViewerCell )event.getSource();
      if( cell.getColumnIndex() == columnIndex ) {
        editing = false;
      }
    }

    @Override
    public void afterEditorDeactivated( ColumnViewerEditorDeactivationEvent event ) {
    }
  }
}
