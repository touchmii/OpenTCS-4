/*******************************************************************************
 * Copyright (c) 2002, 2015 Innoopract Informationssysteme GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
package com.lvsrobot.webclient.controls;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.ColumnViewerEditor;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationEvent;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationStrategy;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.FocusCellOwnerDrawHighlighter;
import org.eclipse.jface.viewers.ICellEditorValidator;
import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.ILazyContentProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TableViewerEditor;
import org.eclipse.jface.viewers.TableViewerFocusCellManager;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.rap.rwt.RWT;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;


public class TableViewerTab extends ExampleTab {

  private static final int ADD_ITEMS = 100;

  private static final int COL_FIRST_NAME = 0;
  private static final int COL_LAST_NAME = 1;
  private static final int COL_AGE = 2;
  private static final int COL_MARRIED = 3;

  private final PersonFilter viewerFilter;
  private final List<Person> persons;
  private Image uncheckedImage;
  private Image checkedImage;
  private TableViewer viewer;
  private TableViewerColumn firstNameColumn;
  private TableViewerColumn lastNameColumn;
  private TableViewerColumn ageColumn;
  private CheckboxTableViewerColumn marriedColumn;
  private Label lblSelection;
  private Button btnCreateCellEditor;

  public TableViewerTab() {
    super( "TableViewer" );
    persons = new ArrayList<Person>();
    viewerFilter = new PersonFilter();
  }

  @Override
  protected void createStyleControls( Composite parent ) {
    createStyleButton( "MULTI", SWT.MULTI );
    createStyleButton( "VIRTUAL", SWT.VIRTUAL );
    createOrientationButton();
    createAddItemsButton();
    createSelectYoungestPersonButton();
    createRemoveButton();
    createCellEditorButton();
    lblSelection = new Label( styleComp, SWT.WRAP );
  }

  private void initializeImages() {
    if( uncheckedImage == null || checkedImage == null ) {
      uncheckedImage = loadImage( "resources/unchecked.png" );
      checkedImage = loadImage( "resources/checked.png" );
    }
  }

  @Override
  protected void createExampleControls( Composite parent ) {
    initializeImages();
    if( btnCreateCellEditor != null && !btnCreateCellEditor.isDisposed() ) {
      btnCreateCellEditor.setEnabled( true );
    }
    parent.setLayout( new GridLayout( 2, false ) );
    Label lblFilter = new Label( parent, SWT.NONE );
    lblFilter.setText( "Filter" );
    lblFilter.setEnabled( ( getStyle() & SWT.VIRTUAL ) == 0 );
    Text txtFilter = new Text( parent, SWT.BORDER );
    txtFilter.setEnabled( ( getStyle() & SWT.VIRTUAL ) == 0 );
    txtFilter.setLayoutData( new GridData( SWT.FILL, SWT.CENTER, true, false ) );
    txtFilter.addModifyListener( new ModifyListener() {
      @Override
      public void modifyText( ModifyEvent event ) {
        Text text = ( Text )event.widget;
        viewerFilter.setText( text.getText() );
        viewer.refresh();
      }
    } );
    if( viewer != null && !viewer.getControl().isDisposed() ) {
      viewer.getControl().dispose();
    }
    initPersons();
    createViewer( parent );
    registerControl( viewer.getControl() );
  }

  private void initPersons() {
    persons.clear();
    persons.add( new Person( "Ada", "Lovelace", 1815, true ) );
    persons.add( new Person( "John", "von Neumann", 1903, true ) );
    persons.add( new Person( "Kurt", "Gödel", 1906, true ) );
    persons.add( new Person( "Grace Murray", "Hopper", 1906, true ) );
    persons.add( new Person( "Alan", "Turing", 1912, false ) );
    persons.add( new Person( "Claude", "Shannon", 1916, true ) );
    persons.add( new Person( "John", "Backus", 1924, true ) );
    persons.add( new Person( "Alan", "Kay", 1940, true ) );
    persons.add( new Person( "Dennis", "Ritchie", 1941, true ) );
    persons.add( new Person( "David", "Parnas", 1941, true ) );
    persons.add( new Person( "Larry", "Wall", 1954 , true ) );
    persons.add( new Person( "Linus", "Torvalds", 1969 , true ) );
  }

  private void createViewer( Composite parent ) {
    viewer = new TableViewer( parent, getStyle() );
    viewer.setContentProvider( createContentProvider() );
    firstNameColumn = createFirstNameColumn();
    lastNameColumn = createLastNameColumn();
    ageColumn = createAgeColumn();
    marriedColumn = createMarriedColumn();
    viewer.setInput( persons );
    viewer.setItemCount( persons.size() );
    viewer.addFilter( viewerFilter );
    viewer.addSelectionChangedListener( new ISelectionChangedListener() {
      @Override
      public void selectionChanged( SelectionChangedEvent event ) {
        lblSelection.setText( "Selection: " + event.getSelection() );
        lblSelection.getParent().layout( new Control[] { lblSelection } );
      }
    } );
    ColumnViewerToolTipSupport.enableFor( viewer );
    Table table = viewer.getTable();
    table.setHeaderVisible( true );
    table.setData( RWT.TOOLTIP_MARKUP_ENABLED, Boolean.TRUE );
    GridData tableData = new GridData( SWT.FILL, SWT.FILL, true, true );
    tableData.horizontalSpan = 2;
    table.setLayoutData( tableData );
  }

  private IContentProvider createContentProvider() {
    IContentProvider result;
    if( ( getStyle() & SWT.VIRTUAL ) == 0 ) {
      result = new PersonContentProvider();
    } else {
      result = new LazyPersonContentProvider();
    }
    return result;
  }

  private TableViewerColumn createFirstNameColumn() {
    TableViewerColumn result = new TableViewerColumn( viewer, SWT.NONE );
    result.setLabelProvider( new PersonLabelProvider( COL_FIRST_NAME ) );
    TableColumn column = result.getColumn();
    column.setText( "First Name" );
    column.setWidth( 170 );
    column.setMoveable( true );
    column.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected( SelectionEvent event ) {
        int sortDirection = updateSortDirection( ( TableColumn )event.widget );
        sort( viewer, COL_FIRST_NAME, sortDirection == SWT.DOWN );
      }
    } );
    return result;
  }

  private TableViewerColumn createLastNameColumn() {
    TableViewerColumn result = new TableViewerColumn( viewer, SWT.NONE );
    result.setLabelProvider( new PersonLabelProvider( COL_LAST_NAME ) );
    TableColumn column = result.getColumn();
    column.setText( "Last Name" );
    column.setWidth( 120 );
    column.setMoveable( true );
    column.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected( SelectionEvent event ) {
        int sortDirection = updateSortDirection( ( TableColumn )event.widget );
        sort( viewer, COL_LAST_NAME, sortDirection == SWT.DOWN );
      }
    } );
    return result;
  }

  private TableViewerColumn createAgeColumn() {
    TableViewerColumn result = new TableViewerColumn( viewer, SWT.NONE );
    result.setLabelProvider( new PersonLabelProvider( COL_AGE ) );
    TableColumn column = result.getColumn();
    column.setText( "Born" );
    column.setWidth( 80 );
    column.setMoveable( true );
    column.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected( SelectionEvent event ) {
        int sortDirection = updateSortDirection( ( TableColumn )event.widget );
        sort( viewer, COL_AGE, sortDirection == SWT.DOWN );
      }
    } );
    return result;
  }

  private CheckboxTableViewerColumn createMarriedColumn() {
    CheckboxTableViewerColumn result = new CheckboxTableViewerColumn( viewer, SWT.NONE );
    result.setLabelProvider( new PersonLabelProvider( COL_MARRIED ) );
    TableColumn column = result.getColumn();
    column.setText( "Married" );
    column.setWidth( 60 );
    column.setMoveable( true );
    column.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected( SelectionEvent event ) {
        int sortDirection = updateSortDirection( ( TableColumn )event.widget );
        sort( viewer, COL_MARRIED, sortDirection == SWT.DOWN );
      }
    } );
    return result;
  }

  private void createAddItemsButton() {
    Button button = new Button( styleComp, SWT.PUSH );
    button.setText( "Add " + ADD_ITEMS + " Items" );
    button.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected( SelectionEvent event ) {
        for( int i = 0; i < ADD_ITEMS; i++ ) {
          int counter = 11 + i;
          persons.add( new Person( "new " + counter, "person " + counter, counter, false ) );
        }
        getViewer().setInput( persons );
        if( ( getStyle() & SWT.VIRTUAL ) != 0 ) {
          getViewer().setItemCount( persons.size() );
        }
      }
    } );
  }

  private void createSelectYoungestPersonButton() {
    Button button = new Button( styleComp, SWT.PUSH );
    button.setText( "Select youngest Person" );
    button.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected( SelectionEvent event ) {
        Person youngestPerson = null;
        int minAge = Integer.MAX_VALUE;
        for( int i = 0; i < persons.size(); i++ ) {
          Person person = persons.get( i );
          if( person.age < minAge ) {
            minAge = person.age;
            youngestPerson = person;
          }
        }
        getViewer().setSelection( new StructuredSelection( youngestPerson ) );
        getViewer().reveal( youngestPerson );
      }
    } );
  }

  private void createRemoveButton() {
    Button button = new Button( styleComp, SWT.PUSH );
    button.setText( "Remove selected rows" );
    button.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected( SelectionEvent event ) {
        IStructuredSelection selection = ( IStructuredSelection )getViewer().getSelection();
        Iterator<?> iter = selection.iterator();
        while( iter.hasNext() ) {
          Person person = ( Person )iter.next();
          persons.remove( person );
        }
        getViewer().getTable().setTopIndex( 0 );
        if( ( getViewer().getTable().getStyle() & SWT.VIRTUAL ) != 0 ) {
          getViewer().setItemCount( persons.size() );
        }
        getViewer().setInput( persons );
      }
    } );
  }

  private void createCellEditorButton() {
    btnCreateCellEditor = new Button( styleComp, SWT.PUSH );
    btnCreateCellEditor.setText( "Create Cell Editor" );
    btnCreateCellEditor.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected( SelectionEvent event ) {
        createCellEditor();
        btnCreateCellEditor.setEnabled( false );
      }
    } );
  }

  private void createCellEditor() {
    EditingSupport editingSupport;
    editingSupport = new FirstNameEditingSupport( viewer );
    firstNameColumn.setEditingSupport( editingSupport );
    editingSupport = new LastNameEditingSupport( viewer, getLastNames() );
    lastNameColumn.setEditingSupport( editingSupport );
    editingSupport = new AgeEditingSupport( viewer );
    ageColumn.setEditingSupport( editingSupport );
    marriedColumn.setEditingSupport( new MarriedEditingSupport() );
    ColumnViewerEditorActivationStrategy activationStrategy
      = new EditorActivationStrategy( viewer );
    FocusCellOwnerDrawHighlighter highlighter = new FocusCellOwnerDrawHighlighter( viewer );
    TableViewerFocusCellManager focusManager
      = new TableViewerFocusCellManager( viewer, highlighter );
    int feature = ColumnViewerEditor.TABBING_HORIZONTAL
                | ColumnViewerEditor.TABBING_MOVE_TO_ROW_NEIGHBOR;
    TableViewerEditor.create( viewer, focusManager, activationStrategy, feature );
    marriedColumn.attachToEditor();
  }

  private static int updateSortDirection( TableColumn column ) {
    Table table = column.getParent();
    if( column == table.getSortColumn() ) {
      if( table.getSortDirection() == SWT.UP ) {
        table.setSortDirection( SWT.DOWN );
      } else {
        table.setSortDirection( SWT.UP );
      }
    } else {
      table.setSortColumn( column );
      table.setSortDirection( SWT.DOWN );
    }
    return table.getSortDirection();
  }

  @SuppressWarnings("unchecked")
  private static void sort( TableViewer viewer, int property, boolean ascending ) {
    if( ( viewer.getControl().getStyle() & SWT.VIRTUAL ) != 0 ) {
      List<Person> input = ( List<Person> )viewer.getInput();
      Collections.sort( input, new PersonComparator( property, ascending ) );
      viewer.refresh();
    } else {
      viewer.setComparator( new PersonComparator( property, ascending ) );
    }
  }

  private TableViewer getViewer() {
    return viewer;
  }

  private String[] getLastNames() {
    ArrayList<String> list = new ArrayList<String>();
    for( Person person : persons ) {
      if( person.lastName.length() > 0 ) {
        list.add( person.lastName );
      }
    }
    String[] result = new String[ list.size() ];
    list.toArray( result );
    return result;
  }

  private static final class Person {
    String firstName;
    String lastName;
    int age;
    boolean married;

    public Person( String firstName, String lastName, int age, boolean married ) {
      this.firstName = firstName;
      this.lastName = lastName;
      this.age = age;
      this.married = married;
    }

    @Override
    public String toString() {
      return firstName + " " + lastName + " " + age + " " + married;
    }
  }

  private static final class PersonContentProvider implements IStructuredContentProvider {
    Object[] elements;
    @Override
    public Object[] getElements( Object inputElement ) {
      return elements;
    }

    @Override
    public void inputChanged( Viewer viewer, Object oldInput, Object newInput ) {
      if( newInput == null ) {
        elements = new Object[ 0 ];
      } else {
        List<?> personList = ( List<?> )newInput;
        elements = personList.toArray();
      }
    }
    @Override
    public void dispose() {
      // do nothing
    }
  }

  private static final class LazyPersonContentProvider implements ILazyContentProvider {
    private TableViewer tableViewer;
    private List<?> elements;

    @Override
    public void inputChanged( Viewer viewer, Object oldInput, Object newInput ) {
      tableViewer = ( TableViewer )viewer;
      elements = ( List<?> )newInput;
    }
    @Override
    public void updateElement( int index ) {
      tableViewer.replace( elements.get( index ), index );
    }
    @Override
    public void dispose() {
      // do nothing
    }
  }

  private class PersonLabelProvider extends ColumnLabelProvider {
    private final int columnIndex;
    private final Color nameBackground;

    public PersonLabelProvider( int columnIndex ) {
      this.columnIndex = columnIndex;
      nameBackground = new Color( Display.getCurrent(), 248, 248, 248 );
    }

    @Override
    public String getText( Object element ) {
      Person person = ( Person )element;
      String result = person.toString();
      switch( columnIndex ) {
        case COL_FIRST_NAME:
          result = person.firstName;
          break;
        case COL_LAST_NAME:
          result = person.lastName;
          break;
        case COL_AGE:
          result = String.valueOf( person.age );
          break;
        case COL_MARRIED:
          result = person.married ? "yes" : "no";
          break;
      }
      return result;
    }

    @Override
    public Image getImage( Object element ) {
      Image result = null;
      if( columnIndex == COL_MARRIED ) {
        Person person = ( Person )element;
        result = person.married ? checkedImage : uncheckedImage;
      }
      return result;
    }

    @Override
    public Color getBackground( Object element ) {
      Color result = null;
      switch( columnIndex ) {
        case COL_FIRST_NAME:
        case COL_LAST_NAME:
          result = nameBackground;
          break;
      }
      return result;
    }

    @Override
    public String getToolTipText( Object element ) {
      Person person = ( Person )element;
      String text = null;
      switch( columnIndex ) {
        case COL_FIRST_NAME:
          text = "<i>" + person.firstName + "</i>";
          break;
        case COL_LAST_NAME:
          text = person.lastName;
          break;
        case COL_AGE:
          text = String.valueOf( person.age );
          break;
        case COL_MARRIED:
          text = person.married ? "yes" : "no";
          break;
      }
      return text;
    }
  }

  private static final class PersonComparator extends ViewerComparator
    implements Comparator<Person>
  {

    private final boolean ascending;
    private final int property;
    public PersonComparator( int property, boolean ascending ) {
      this.property = property;
      this.ascending = ascending;
    }

    @Override
    public int compare( Viewer viewer, Object object1, Object object2 ) {
      return compare( ( Person )object1, ( Person )object2 );
    }

    @Override
    public boolean isSorterProperty( Object elem, String property ) {
      return true;
    }

    @Override
    public int compare( Person person1, Person person2 ) {
      int result = 0;
      if( property == COL_FIRST_NAME ) {
        result = person1.firstName.compareTo( person2.firstName );
      } else if( property == COL_LAST_NAME ) {
        result = person1.lastName.compareTo( person2.lastName );
      } else if( property == COL_AGE ) {
        result = person1.age - person2.age;
      } else if( property == COL_MARRIED ) {
        if( person1.married && !person2.married ) {
          result = -1;
        } else if( !person1.married && person2.married ) {
          result = +1;
        }
      }
      if( !ascending ) {
        result = result * -1;
      }
      return result;
    }
  }

  private static final class PersonFilter extends ViewerFilter {
    private String text;
    public void setText( final String string ) {
      text = string;
    }

    @Override
    public boolean select( Viewer viewer, Object parentElement, Object element ) {
      boolean result = true;
      Person person = ( Person )element;
      if( text != null && text.length() > 0 ) {
        String personText = person.toString().toLowerCase();
        result = personText.indexOf( text.toLowerCase() ) != -1;
      }
      return result;
    }
    @Override
    public boolean isFilterProperty( Object element, String prop ) {
      return true;
    }
  }

  private static final class EditorActivationStrategy extends ColumnViewerEditorActivationStrategy {

    private EditorActivationStrategy( ColumnViewer viewer ) {
      super( viewer );
      setEnableEditorActivationWithKeyboard( true );
    }

    @Override
    protected boolean isEditorActivationEvent( ColumnViewerEditorActivationEvent event ) {
      boolean result;
      if( event.character == '\r' ) {
        result = true;
      } else {
        result = super.isEditorActivationEvent( event );
      }
      return result;
    }
  }

  private static final class FirstNameEditingSupport extends EditingSupport {
    private final CellEditor editor;
    public FirstNameEditingSupport( TableViewer viewer ) {
      super( viewer );
      editor = new TextCellEditor( viewer.getTable() );
    }

    @Override
    protected boolean canEdit( Object element ) {
      return true;
    }

    @Override
    protected CellEditor getCellEditor( Object element ) {
      return editor;
    }

    @Override
    protected Object getValue( Object element ) {
      Person person = ( Person )element;
      String result;
      result = person.firstName;
      return result;
    }

    @Override
    protected void setValue( Object element, Object value ) {
      Person person = ( Person )element;
      person.firstName = ( String )value;
      getViewer().update( element, null );
    }
  }

  private static final class LastNameEditingSupport extends EditingSupport {
    private final CellEditor editor;
    public LastNameEditingSupport( TableViewer viewer, String[] suggestions ) {
      super( viewer );
      editor = new ComboBoxCellEditor( viewer.getTable(), suggestions, SWT.NONE );
    }

    @Override
    protected boolean canEdit( Object element ) {
      return true;
    }

    @Override
    protected CellEditor getCellEditor( Object element ) {
      return editor;
    }

    @Override
    protected Object getValue( Object element ) {
      Person person = ( Person )element;
      CCombo ccombo = ( CCombo )editor.getControl();
      ccombo.setText( person.lastName );
      return new Integer( -2 );
    }

    @Override
    protected void setValue( Object element, Object value ) {
      Person person = ( Person )element;
      CCombo ccombo = ( CCombo )editor.getControl();
      person.lastName = ccombo.getText();
      getViewer().update( element, null );
    }
  }

  private static final class AgeEditingSupport extends EditingSupport {
    private final CellEditor editor;
    public AgeEditingSupport( TableViewer viewer ) {
      super( viewer );
      editor = new TextCellEditor( viewer.getTable() );
      editor.setValidator( new ICellEditorValidator() {
        @Override
        public String isValid( Object value ) {
          String result = null;
          try {
            Integer.parseInt( ( String )value );
          } catch( NumberFormatException e ) {
            String text = "''{0}'' is not a valid age.";
            result = MessageFormat.format( text, new Object[] { value } );
          }
          return result;
        }
      });
    }

    @Override
    protected boolean canEdit( Object element ) {
      return true;
    }

    @Override
    protected CellEditor getCellEditor( Object element ) {
      return editor;
    }

    @Override
    protected Object getValue( Object element ) {
      Person person = ( Person )element;
      return String.valueOf( person.age );
    }

    @Override
    protected void setValue( Object element, Object value ) {
      if( value != null ) {
        Person person = ( Person )element;
        person.age = Integer.parseInt( ( String )value );
        getViewer().update( element, null );
      }
    }
  }

  private static class MarriedEditingSupport extends CheckboxEditingSupport {
    @Override
    public boolean canEdit( Object element ) {
      return true;
    }

    @Override
    public Boolean getValue( Object element ) {
      Person person = ( Person )element;
      return Boolean.valueOf( person.married );
    }

    @Override
    public void setValue( Object element, Boolean value ) {
      Person person = ( Person )element;
      person.married = value.booleanValue();
    }

  }

}
