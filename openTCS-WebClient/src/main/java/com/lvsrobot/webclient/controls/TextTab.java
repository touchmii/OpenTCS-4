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

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;


public class TextTab extends ExampleTab {

  private Text text;
  private Label textLabel;
  private Label selectionLabel;
  private Button btnSelectionListener;
  private Button btnBlockingVerifyListener;
  private Button btnNumbersOnlyVerifyListener;
  private Button btnModifyListener;
  private Button btnKeyListener;
  private Button btnEditable;
  private Button btnEchoChar;
  private final SelectionListener selectionListener;
  private final VerifyListener blockingVerifyListener;
  private final VerifyListener numberOnlyVerifyListener;
  private final ModifyListener modifyListener;
  private final KeyListener keyListener;
  private Button searchStyleButton;
  private Button iconSearchStyleButton;
  private Button iconCancelStyleButton;

  public TextTab() {
    super( "Text" );
    selectionListener = new SelectionAdapter() {

      @Override
      public void widgetDefaultSelected( SelectionEvent event ) {
        String msg = "You pressed the Enter key.";
        if( event.detail == SWT.ICON_SEARCH ) {
          msg = "You clicked the search icon.";
        } else if( event.detail == SWT.ICON_CANCEL ) {
          msg = "You clicked the cancel icon.";
        }
        MessageDialog.openInformation( getShell(), "Information", msg );
      }
    };
    blockingVerifyListener = new VerifyListener() {

      @Override
      public void verifyText( VerifyEvent event ) {
        event.doit = false;
      }
    };
    numberOnlyVerifyListener = new VerifyListener() {

      @Override
      public void verifyText( VerifyEvent event ) {
        StringBuffer allowedText = new StringBuffer();
        for( int i = 0; i < event.text.length(); i++ ) {
          char ch = event.text.charAt( i );
          if( ch >= '0' && ch <= '9' ) {
            allowedText.append( ch );
          }
        }
        event.text = allowedText.toString();
      }
    };
    modifyListener = new ModifyListener() {

      @Override
      public void modifyText( ModifyEvent event ) {
        Text text = ( Text )event.widget;
        textLabel.setText( text.getText() );
      }
    };
    keyListener = new KeyAdapter() {};
  }

  @Override
  protected void createStyleControls( Composite parent ) {
    createStyleButton( "BORDER", SWT.BORDER );
    createStyleButton( "WRAP", SWT.WRAP );
    createStyleButton( "SINGLE", SWT.SINGLE );
    createStyleButton( "MULTI", SWT.MULTI );
    createStyleButton( "PASSWORD", SWT.PASSWORD );
    searchStyleButton = createStyleButton( "SEARCH", SWT.SEARCH );
    iconSearchStyleButton = createStyleButton( "ICON_SEARCH", SWT.ICON_SEARCH );
    iconCancelStyleButton = createStyleButton( "ICON_CANCEL", SWT.ICON_CANCEL );
    updateSearchStyleButtons();
    createStyleButton( "READ_ONLY", SWT.READ_ONLY );
    createStyleButton( "LEFT", SWT.LEFT );
    createStyleButton( "CENTER", SWT.CENTER );
    createStyleButton( "RIGHT", SWT.RIGHT );
    createOrientationButton();
    createVisibilityButton();
    createEnablementButton();
    createEditableButton();
    createEchoCharButton();
    createSelectionListenerButton();
    createBlockingVerifyListenerButton();
    createNumbersOnlyVerifyListenerButton();
    createModifyListenerButton();
    createKeyListenerButton();
    createFgColorButton();
    createBgColorButton();
    createFontChooser();
    createCursorCombo();
    createLimitText( parent );
    createSelectionChooser( parent );
    createText( parent );
    createMessage( parent );
  }

  @Override
  protected void createExampleControls( final Composite parent ) {
    parent.setLayout( new GridLayout() );
    Composite textComposite = new Composite( parent, SWT.NONE );
    textComposite.setLayoutData( new GridData( SWT.FILL, SWT.FILL, true, true ) );
    textComposite.setLayout( new GridLayout( 1, false ) );
    text = new Text( textComposite, getStyle() );
    text.setText( "Lorem ipsum dolor sit amet" );
    text.setSelection( 0, 5 );
    text.setMessage( "Please enter something" );
    text.setFocus();
    // button bar
    Composite buttonBar = new Composite( parent, SWT.NONE );
    buttonBar.setLayoutData( new GridData( SWT.FILL, SWT.DEFAULT, true, false ) );
    buttonBar.setLayout( new RowLayout( SWT.HORIZONTAL ) );
    final Button btnGetText = new Button( buttonBar, SWT.PUSH );
    btnGetText.setText( "getText" );
    btnGetText.addSelectionListener( new SelectionAdapter() {

      @Override
      public void widgetSelected( SelectionEvent event ) {
        textLabel.setText( text.getText() );
      }
    } );
    final Button btnGetSelection = new Button( buttonBar, SWT.PUSH );
    btnGetSelection.setText( "getSelection" );
    btnGetSelection.addSelectionListener( new SelectionAdapter() {

      @Override
      public void widgetSelected( SelectionEvent event ) {
        Point selection = text.getSelection();
        selectionLabel.setText( selection.x + ", " + selection.y );
      }
    } );
    final Button btnFixedSize = new Button( buttonBar, SWT.PUSH );
    btnFixedSize.setText( "200 x 100" );
    btnFixedSize.addSelectionListener( new SelectionAdapter() {

      @Override
      public void widgetSelected( SelectionEvent event ) {
        text.setLayoutData( new GridData( 200, 100 ) );
        parent.layout();
      }
    } );
    // output form
    Composite outputForm = new Composite( parent, SWT.NONE );
    outputForm.setLayoutData( new GridData( SWT.FILL, SWT.FILL, true, true ) );
    outputForm.setLayout( new GridLayout( 2, false ) );
    new Label( outputForm, SWT.NONE ).setText( "Text:" );
    textLabel = new Label( outputForm, SWT.BORDER );
    textLabel.setText( "\n\n\n\n\n" );
    textLabel.setLayoutData( new GridData( SWT.FILL, SWT.DEFAULT, true, false ) );
    new Label( outputForm, SWT.NONE ).setText( "Selection:" );
    selectionLabel = new Label( outputForm, SWT.BORDER );
    selectionLabel.setLayoutData( new GridData( SWT.FILL, SWT.DEFAULT, true, false ) );
    updateSelectionListener();
    updateBlockingVerifyListener();
    updateNumbersOnlyVerifyListener();
    updateModifyListener();
    updateEditable();
    registerControl( text );
    createDefaultButton( parent );
  }

  private void createDefaultButton( final Composite parent ) {
    Composite composite = new Composite( parent, SWT.NONE );
    composite.setLayout( new RowLayout( SWT.HORIZONTAL) );
    final Button defaultButton = new Button( composite, SWT.PUSH );
    defaultButton.setText( "Default" );
    defaultButton.addSelectionListener( new SelectionAdapter() {

      @Override
      public void widgetSelected( SelectionEvent e ) {
        String message = "Default button triggered";
        MessageDialog.openInformation( parent.getShell(), "Info", message  );
      }
    });
    final Button setDefaultButton = new Button( composite, SWT.CHECK );
    setDefaultButton.setText( "set as defaultButton" );
    setDefaultButton.addSelectionListener( new SelectionAdapter() {

      @Override
      public void widgetSelected( SelectionEvent e ) {
        if( setDefaultButton.getSelection() ) {
          parent.getShell().setDefaultButton( defaultButton );
        } else {
          // To clear the default button you have to call it twice (same for SWT)
          parent.getShell().setDefaultButton( null );
          parent.getShell().setDefaultButton( null );
        }
      }
    });
  }

  private void createModifyListenerButton() {
    btnModifyListener = createPropertyButton( "ModifyListener" );
    btnModifyListener.addSelectionListener( new SelectionAdapter() {

      @Override
      public void widgetSelected( SelectionEvent event ) {
        updateModifyListener();
      }
    } );
  }

  private void createKeyListenerButton() {
    btnKeyListener = createPropertyButton( "KeyListener" );
    btnKeyListener.addSelectionListener( new SelectionAdapter() {

      @Override
      public void widgetSelected( final SelectionEvent event ) {
        updateKeyListener();
      }
    } );
  }

  private void createNumbersOnlyVerifyListenerButton() {
    btnNumbersOnlyVerifyListener
      = createPropertyButton( "VerifyListener (numbers only)" );
    btnNumbersOnlyVerifyListener.addSelectionListener( new SelectionAdapter() {

      @Override
      public void widgetSelected( SelectionEvent event ) {
        updateNumbersOnlyVerifyListener();
      }
    } );
  }

  private void createBlockingVerifyListenerButton() {
    btnBlockingVerifyListener
      = createPropertyButton( "VerifyListener (reject all)" );
    btnBlockingVerifyListener.addSelectionListener( new SelectionAdapter() {

      @Override
      public void widgetSelected( SelectionEvent event ) {
        updateBlockingVerifyListener();
      }
    } );
  }

  private void createSelectionListenerButton() {
    btnSelectionListener = createPropertyButton( "SelectionListener" );
    btnSelectionListener.addSelectionListener( new SelectionAdapter() {

      @Override
      public void widgetSelected( SelectionEvent event ) {
        updateSelectionListener();
      }
    } );
  }

  private void createEditableButton() {
    btnEditable = createPropertyButton( "Editable" );
    btnEditable.setSelection( true );
    btnEditable.addSelectionListener( new SelectionAdapter() {

      @Override
      public void widgetSelected( SelectionEvent event ) {
        updateEditable();
      }
    } );
  }

  private void createEchoCharButton() {
    btnEchoChar= createPropertyButton( "EchoChar" );
    btnEchoChar.setSelection( false );
    btnEchoChar.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected( SelectionEvent event ) {
        updateEchoChar();
      }
    } );
  }

  private void createSelectionChooser( Composite parent ) {
    Composite composite = new Composite( parent, SWT.NONE );
    composite.setLayout( new GridLayout( 6, false ) );
    Label lblSelectionFrom = new Label( composite, SWT.NONE );
    lblSelectionFrom.setText( "Selection from" );
    final Text txtSelectionFrom = new Text( composite, SWT.BORDER );
    txtSelectionFrom.setLayoutData( new GridData( 30, SWT.DEFAULT ) );
    Label lblSelectionTo = new Label( composite, SWT.NONE );
    lblSelectionTo.setText( "to" );
    final Text txtSelectionTo = new Text( composite, SWT.BORDER );
    txtSelectionTo.setLayoutData( new GridData( 30, SWT.DEFAULT ) );
    Button btnChange = new Button( composite, SWT.PUSH );
    btnChange.setText( "Set" );
    btnChange.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected( SelectionEvent event ) {
        int from = parseInt( txtSelectionFrom.getText() );
        int to = parseInt( txtSelectionTo.getText() );
        if( to >= 0 && from >= 0  ) {
          text.setSelection( from, to );
          text.setFocus();
        } else {
          String msg
            = "Invalid Selection: "
            + txtSelectionFrom.getText()
            + " - "
            + txtSelectionTo.getText();
          MessageDialog.openError( getShell(), "Error", msg );
        }
      }
    } );
    Button selectAllButton = new Button( composite, SWT.PUSH );
    selectAllButton.setText( "Select all" );
    selectAllButton.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected( SelectionEvent e ) {
        text.selectAll();
        text.setFocus();
      }
    } );
  }

  private void createLimitText( Composite parent ) {
    Composite composite = new Composite( parent, SWT.NONE );
    composite.setLayout( new GridLayout( 4, false ) );
    Label label = new Label( composite, SWT.NONE );
    label.setText( "Text limit" );
    final Text limitText = new Text( composite, SWT.BORDER );
    limitText.setLayoutData( new GridData( 100, SWT.DEFAULT ) );
    Button setButton = new Button( composite, SWT.PUSH );
    setButton.setText( "Set" );
    Button resetButton = new Button( composite, SWT.PUSH );
    resetButton.setText( "Reset" );
    Listener changeListener = new Listener() {
      @Override
      public void handleEvent( Event event ) {
        try {
          text.setTextLimit( Integer.parseInt( limitText.getText() ) );
          limitText.setText( String.valueOf( text.getTextLimit() ) );
          limitText.setBackground( null );
        } catch( Exception e ) {
          limitText.setBackground( bgColors[ BG_COLOR_BROWN ] );
        }
      }
    };
    limitText.addListener( SWT.DefaultSelection, changeListener );
    setButton.addListener( SWT.Selection, changeListener  );
    resetButton.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected( SelectionEvent e ) {
        text.setTextLimit( Text.LIMIT );
        limitText.setText( "" );
        limitText.setBackground( null );
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
    setButton.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected( SelectionEvent e ) {
        text.setText( setText.getText() );
      }
    } );
    final Text appendText = new Text( group, SWT.BORDER );
    appendText.setLayoutData( new GridData( 200, SWT.DEFAULT ) );
    Button appendButton = new Button( group, SWT.PUSH );
    appendButton.setText( "Append" );
    appendButton.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected( SelectionEvent e ) {
        text.append( appendText.getText() );
      }
    } );
    final Text insertText = new Text( group, SWT.BORDER );
    insertText.setLayoutData( new GridData( 200, SWT.DEFAULT ) );
    Button insertButton = new Button( group, SWT.PUSH );
    insertButton.setText( "Insert" );
    insertButton.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected( SelectionEvent e ) {
        text.insert( insertText.getText() );
      }
    } );
  }

  private void createMessage( Composite parent ) {
    Composite composite = new Composite( parent, SWT.NONE );
    composite.setLayout( new GridLayout( 3, false ) );
    Label label = new Label( composite, SWT.NONE );
    label.setText( "Message" );
    final Text message = new Text( composite, SWT.BORDER );
    message.setLayoutData( new GridData( 185, SWT.DEFAULT ) );
    Button setButton = new Button( composite, SWT.PUSH );
    setButton.setText( "Set" );
    setButton.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected( SelectionEvent e ) {
        text.setMessage( message.getText() );
      }
    } );
  }

  private int parseInt( String text ) {
    int result;
    try {
      result = Integer.parseInt( text );
    } catch( NumberFormatException e ) {
      result = -1;
    }
    return result;
  }

  private void updateSelectionListener() {
    if( checkControl( btnSelectionListener ) ) {
      if( btnSelectionListener.getSelection() ) {
        text.addSelectionListener( selectionListener );
      } else {
        text.removeSelectionListener( selectionListener );
      }
    }
  }

  private void updateBlockingVerifyListener() {
    if( checkControl( btnBlockingVerifyListener ) ) {
      if( btnBlockingVerifyListener.getSelection() ) {
        text.addVerifyListener( blockingVerifyListener );
      } else {
        text.removeVerifyListener( blockingVerifyListener );
      }
    }
  }

  private void updateNumbersOnlyVerifyListener() {
    if( checkControl( btnNumbersOnlyVerifyListener ) ) {
      if( btnNumbersOnlyVerifyListener.getSelection() ) {
        text.addVerifyListener( numberOnlyVerifyListener );
      } else {
        text.removeVerifyListener( numberOnlyVerifyListener );
      }
    }
  }

  private void updateModifyListener() {
    if( checkControl( btnModifyListener ) ) {
      if( btnModifyListener.getSelection() ) {
        text.addModifyListener( modifyListener );
      } else {
        text.removeModifyListener( modifyListener );
      }
    }
  }

  private void updateKeyListener() {
    if( checkControl( btnKeyListener ) ) {
      if( btnKeyListener.getSelection() ) {
        text.addKeyListener( keyListener );
      } else {
        text.removeKeyListener( keyListener );
      }
    }
  }

  private void updateEditable() {
    if( checkControl( btnEditable ) ) {
      text.setEditable( btnEditable.getSelection() );
    }
  }

  private void updateEchoChar() {
    if( checkControl( btnEchoChar ) ) {
      text.setEchoChar( btnEchoChar.getSelection() ? '*' : 0);
    }
  }

  @Override
  protected void createNew() {
    updateSearchStyleButtons();
    super.createNew();
  }

  private void updateSearchStyleButtons() {
    boolean isSearch = searchStyleButton.getSelection();
    iconSearchStyleButton.setEnabled( isSearch );
    iconCancelStyleButton.setEnabled( isSearch );
    if( !isSearch ) {
      iconSearchStyleButton.setSelection( false );
      iconCancelStyleButton.setSelection( false );
    }
  }
}
