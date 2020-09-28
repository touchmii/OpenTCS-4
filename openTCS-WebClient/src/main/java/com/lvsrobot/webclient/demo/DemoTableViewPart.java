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
import org.eclipse.jface.viewers.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.*;
import org.eclipse.ui.part.ViewPart;


public class DemoTableViewPart extends ViewPart {

  private static final int ROWS = 40;
  private static final int COLUMNS = 10;
  private TableViewer viewer;

  private class ViewContentProvider implements IStructuredContentProvider {
    public Object[] getElements( Object inputElement ) {
      List<String[]> buffer = new ArrayList<String[]>();
      for( int i = 0; i < ROWS; i++ ) {
        String[] row = new String[ COLUMNS ];
        for( int j = 0; j < COLUMNS; j++ ) {
          row[ j ] = "Item" + i + "-" + j ;
        }
        buffer.add( row );
      }
      Object[] result = new Object[ buffer.size() ];
      buffer.toArray( result );
      return result;
    }
    public void dispose() {
    }
    public void inputChanged( Viewer viewer, Object oldInput, Object newInput ) {
    }
  }

  private class ViewLabelProvider extends LabelProvider implements ITableLabelProvider {
    public Image getColumnImage( Object element, int columnIndex ) {
      return null;
    }
    public String getColumnText( Object element, int columnIndex ) {
      String[] row = ( String[] )element;
      String result = row[ columnIndex ];
      return result;
    }
  }

  @Override
  public void createPartControl( Composite parent ) {
    viewer = new TableViewer( parent, SWT.NONE );
    viewer.setContentProvider( new ViewContentProvider() );
    viewer.setLabelProvider( new ViewLabelProvider() );
    final Table table = viewer.getTable();
    viewer.setColumnProperties( initColumnProperties( table ) );
    viewer.setInput( this );
    viewer.getTable().setHeaderVisible( true );
    getSite().setSelectionProvider( viewer );
  }

  @Override
  public void setFocus() {
    viewer.getTable().setFocus();
  }

  private String[] initColumnProperties( Table table ) {
    String[] result = new String[ COLUMNS ];
    for( int i = 0; i < COLUMNS; i++ ) {
      TableColumn tableColumn = new TableColumn( table, SWT.NONE );
      result[ i ] = "Column" + i ;
      tableColumn.setText( result[ i ] );
      if( i == 2 ) {
        tableColumn.setWidth( 190 );
      } else {
        tableColumn.setWidth( 70 );
      }
    }
    return result;
  }
}
