/*******************************************************************************
 * Copyright (c) 2002, 2016 Innoopract Informationssysteme GmbH and others.
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

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.window.Window;
import org.eclipse.rap.rwt.widgets.DialogCallback;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.ColorDialog;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FontDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;


public class DialogsTab extends ExampleTab {

  private Label inputDlgResLabel;
  private Label loginDlgResLabel;
  private Label messageDlgResLabel;
  private Label errorDlgResLabel;
  private Label messageBoxDlgResLabel;
  private Button okButton;
  private Button cancelButton;
  private Button yesButton;
  private Button noButton;
  private Button retryButton;
  private Button abortButton;
  private Button ignoreButton;
  private Button iconErrorButton;
  private Button iconInformationButton;
  private Button iconQuestionButton;
  private Button iconWarningButton;
  private Button iconWorkingButton;
  private Button noIconButton;
  private Button showMessageBoxDlgButton;
  private Button showColorDialogButton;
  private Button showFontDialogButton;
  private boolean useDialogCallback;
  private boolean useMarkup;

  public DialogsTab() {
    super( "Dialogs" );
  }

  @Override
  protected void createStyleControls( final Composite parent ) {
    parent.setLayout( new GridLayout( 1, true ) );
    createMessageBoxStyleControls( parent );
  }

  @Override
  protected void createExampleControls( final Composite parent ) {
    parent.setLayout( new GridLayout() );
    Group group1 = new Group( parent, SWT.NONE );
    group1.setLayoutData( new GridData( GridData.FILL_HORIZONTAL ) );
    group1.setText( "JFace Dialogs" );
    group1.setLayout( new GridLayout( 3, true ) );

    // JFace input dialog
    Button showInputDlgButton = new Button( group1, SWT.PUSH );
    showInputDlgButton.setText( "Input Dialog" );
    showInputDlgButton.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected( final SelectionEvent event ) {
        showInputDialog();
      }
    } );
    showInputDlgButton.setLayoutData( createGridDataFillBoth() );
    Button showProgressDlgButton = new Button( group1, SWT.PUSH );
    showProgressDlgButton.setText( "ProgressDialog" );
    showProgressDlgButton.setLayoutData( createGridDataFillBoth() );
    showProgressDlgButton.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected(final SelectionEvent e) {
        showProgressDialog();
      }
    } );
    insertSpaceLabels( group1, 1 );

    inputDlgResLabel = new Label( group1, SWT.WRAP );
    inputDlgResLabel.setText( "Result:" );
    GridData gdInputDlgResLabel = new GridData();
    gdInputDlgResLabel.horizontalSpan = 3;
    inputDlgResLabel.setLayoutData( gdInputDlgResLabel );

    Button showMessageInfoDlgButton = new Button( group1, SWT.PUSH );
    showMessageInfoDlgButton.setLayoutData( createGridDataFillBoth() );
    showMessageInfoDlgButton.setText( "Info Message" );
    showMessageInfoDlgButton.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected( final SelectionEvent event ) {
        showMessageDialogInfo();
      }
    } );

    Button showMessageWarningDlgButton = new Button( group1, SWT.PUSH );
    showMessageWarningDlgButton.setLayoutData( createGridDataFillBoth() );
    showMessageWarningDlgButton.setText( "Warning Dialog" );
    showMessageWarningDlgButton.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected( final SelectionEvent event ) {
        showMessageDialogWarning();
      }
    } );
    Button showMessageErrorDlgButton = new Button( group1, SWT.PUSH );
    showMessageErrorDlgButton.setLayoutData( createGridDataFillBoth() );
    showMessageErrorDlgButton.setText( "Error Message" );
    showMessageErrorDlgButton.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected( final SelectionEvent event ) {
        showMessageDialogError();
      }
    } );

    Button showMessageQuestionDlgButton = new Button( group1, SWT.PUSH );
    showMessageQuestionDlgButton.setLayoutData( createGridDataFillBoth() );
    showMessageQuestionDlgButton.setText( "Question Dialog" );
    showMessageQuestionDlgButton.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected( final SelectionEvent event ) {
        showMessageDialogQuestion();
      }
    } );

    Button showMessageConfirmDlgButton = new Button( group1, SWT.PUSH );
    showMessageConfirmDlgButton.setLayoutData( createGridDataFillBoth() );
    showMessageConfirmDlgButton.setText( "Confirm Message" );
    showMessageConfirmDlgButton.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected( final SelectionEvent event ) {
        showMessageDialogConfirm();
      }
    } );
    insertSpaceLabels( group1, 1 );

    messageDlgResLabel = new Label( group1, SWT.WRAP );
    messageDlgResLabel.setText( "Result:" );
    insertSpaceLabels( group1, 2 );

    Button showErrorDlgButton = new Button( group1, SWT.PUSH );
    showErrorDlgButton.setLayoutData( createGridDataFillBoth() );
    showErrorDlgButton.setText( "Error Dialog" );
    showErrorDlgButton.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected( final SelectionEvent event ) {
        showErrorDialog();
      }
    } );
    insertSpaceLabels( group1, 2 );

    errorDlgResLabel = new Label( group1, SWT.WRAP );
    errorDlgResLabel.setText( "Result:" );
    insertSpaceLabels( group1, 2 );


    Group group2 = new Group( parent, SWT.NONE );
    group2.setLayoutData( new GridData( GridData.FILL_HORIZONTAL ) );
    group2.setText( "Custom Dialogs" );
    group2.setLayout( new GridLayout( 3, true ) );

    Button showLoginDlgButton = new Button( group2, SWT.PUSH );
    showLoginDlgButton.setText( "Login Dialog" );
    showLoginDlgButton.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected( final SelectionEvent event ) {
        showLoginDialog();
      }
    } );
    showLoginDlgButton.setLayoutData( createGridDataFillBoth() );
    insertSpaceLabels( group2, 2 );

    loginDlgResLabel = new Label( group2, SWT.WRAP );
    loginDlgResLabel.setText( "Result:" );

    Group swtDialogsGroup = new Group( parent, SWT.NONE );
    swtDialogsGroup.setLayoutData( new GridData( GridData.FILL_HORIZONTAL ) );
    swtDialogsGroup.setText( "SWT Dialogs" );
    swtDialogsGroup.setLayout( new GridLayout( 3, true ) );

    final Button cbUseDialogCallback = new Button( swtDialogsGroup, SWT.CHECK );
    cbUseDialogCallback.setText( "Use DialogCallback" );
    cbUseDialogCallback.setSelection( useDialogCallback );
    cbUseDialogCallback.setLayoutData( new GridData( SWT.LEFT, SWT.CENTER, false, false, 1, 1 ) );
    cbUseDialogCallback.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected( SelectionEvent event ) {
        useDialogCallback = cbUseDialogCallback.getSelection();
      }
    } );

    final Button cbUseMarkup = new Button( swtDialogsGroup, SWT.CHECK );
    cbUseMarkup.setText( "Use markup" );
    cbUseMarkup.setSelection( useMarkup );
    cbUseMarkup.setLayoutData( new GridData( SWT.LEFT, SWT.CENTER, false, false, 2, 1 ) );
    cbUseMarkup.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected( SelectionEvent event ) {
        useMarkup = cbUseMarkup.getSelection();
      }
    } );

    showMessageBoxDlgButton = new Button( swtDialogsGroup, SWT.PUSH );
    showMessageBoxDlgButton.setText( "MessageBox Dialog" );
    showMessageBoxDlgButton.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected( final SelectionEvent event ) {
        showMessageBoxDialog();
      }
    } );
    showMessageBoxDlgButton.setLayoutData( createGridDataFillBoth() );

    showColorDialogButton = new Button( swtDialogsGroup, SWT.PUSH );
    showColorDialogButton.setText( "ColorDialog" );
    showColorDialogButton.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected( final SelectionEvent e ) {
        showColorDialog();
      }
    });
    showColorDialogButton.setLayoutData( createGridDataFillBoth() );
    showFontDialogButton = new Button( swtDialogsGroup, SWT.PUSH );
    showFontDialogButton.setText( "FontDialog" );
    showFontDialogButton.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected( final SelectionEvent e ) {
        showFontDialog();
      }
    });
    showFontDialogButton.setLayoutData( createGridDataFillBoth() );

    messageBoxDlgResLabel = new Label( swtDialogsGroup, SWT.WRAP );
    messageBoxDlgResLabel.setText( "Result:" );
    GridData gdMessageBoxDlgResLabel = new GridData();
    gdMessageBoxDlgResLabel.horizontalSpan = 3;
    messageBoxDlgResLabel.setLayoutData( gdMessageBoxDlgResLabel );
  }

  private GridData createGridDataFillBoth() {
    return new GridData( GridData.FILL_BOTH );
  }

  private void insertSpaceLabels( final Group group, final int count ) {
    for( int i = 0; i < count; i++ ) {
      new Label( group, SWT.NONE );
    }
  }

  private void showInputDialog() {
    final IInputValidator val = new IInputValidator() {
      @Override
      public String isValid( final String newText ) {
        String result = null;
        if( newText.length() < 5 ) {
          result = "Input text too short!";
        }
        return result;
      }
    };
    String title = "Input Dialog";
    String mesg = "Enter at least five characters";
    String def = "default text";
    final InputDialog dlg;
    dlg = new InputDialog( getShell(), title, mesg, def, val );
    int returnCode = dlg.open();
    String resultText = "Result: " + getReturnCodeText( returnCode );
    if( returnCode == Window.OK ) {
      resultText += ", value: " + dlg.getValue();
    }
    inputDlgResLabel.setText( resultText  );
    inputDlgResLabel.pack();
  }

  private void showProgressDialog() {
    ProgressMonitorDialog dialog = new ProgressMonitorDialog( getShell() );
    try {
      dialog.run( true, true, new IRunnableWithProgress() {
        @Override
        public void run( final IProgressMonitor monitor )
          throws InvocationTargetException, InterruptedException
        {
          monitor.beginTask( "Counting from one to 20...", 20 );
          for( int i = 1; !monitor.isCanceled() && i <= 20; i++ ) {
            monitor.worked( 1 );
            Thread.sleep( 1000 );
          }
          monitor.done();
        }
      } );
    } catch( Exception e ) {
      MessageDialog.openError( getShell(), "Error", e.getMessage() );
    }
  }

  private void showMessageDialogInfo() {
    String title = "Information";
    String mesg = "RAP rocks!";
    MessageDialog.openInformation( getShell(), title, mesg );
    messageDlgResLabel.setText( "Result: none" );
    messageDlgResLabel.pack();
  }

  private void showMessageDialogError() {
    String title = "Error";
    String mesg = "An everyday error occured.\n " + "Nothing to get worried.";
    MessageDialog.openError( getShell(), title, mesg );
    messageDlgResLabel.setText( "Result: none" );
    messageDlgResLabel.pack();
  }

  private void showMessageDialogQuestion() {
    String title = "Question";
    String mesg = "Do you like the RAP technology?\n\n"
                  + "Note that you can also press <Return> here. "
                  + "The correct answer is automatically selected.";
    boolean result = MessageDialog.openQuestion( getShell(), title, mesg );
    messageDlgResLabel.setText( "Result: " + result );
    messageDlgResLabel.pack();
  }

  private void showMessageDialogConfirm() {
    String title = "Confirmation";
    String mesg = "Nothing will be done. Ok?";
    boolean result = MessageDialog.openConfirm( getShell(), title, mesg );
    messageDlgResLabel.setText( "Result: " + result );
    messageDlgResLabel.pack();
  }

  private void showMessageDialogWarning() {
    String title = "Warning";
    String mesg = "You have been warned.";
    MessageDialog.openWarning( getShell(), title, mesg );
    messageDlgResLabel.setText( "Result: none" );
    messageDlgResLabel.pack();
  }

  private void showErrorDialog() {
    String title = "Error";
    int code = 23;
    String mesg = "An absolutetly weird error occured";
    String reason = "Don't know, it just happened ...";
    Exception exception = new IndexOutOfBoundsException( "negative index: -1" );
    exception = new RuntimeException( exception );
    IStatus status = new Status( IStatus.ERROR,
                                 "org.eclipse.rap.demo",
                                 code,
                                 reason,
                                 exception );
    int returnCode = ErrorDialog.openError( getShell(), title, mesg, status );
    errorDlgResLabel.setText( "Result: " + getReturnCodeText( returnCode ) );
    errorDlgResLabel.pack();
  }

  private void showLoginDialog() {
    String message = "Please sign in with your username and password:";
    final LoginDialog loginDialog
      = new LoginDialog( getShell(), "Login", message );
    loginDialog.setUsername( "john" );
    int returnCode = loginDialog.open();
    String resultText = "Result: " + getReturnCodeText( returnCode );
    if( returnCode == Window.OK ) {
      String username = loginDialog.getUsername();
      String password = loginDialog.getPassword();
      String pwInfo = password == null ? "n/a" : password.length() + " chars";
      resultText += ", user: " + username + ", password: " + pwInfo;
    }
    loginDlgResLabel.setText( resultText );
    loginDlgResLabel.pack();
  }

  private String getReturnCodeText( final int code ) {
    String result;
    if( code == Window.OK ) {
      result = "OK";
    } else if( code == Window.CANCEL ) {
        result = "CANCEL";
    } else {
      result = String.valueOf( code );
    }
    return result ;
  }

  private void showMessageBoxDialog() {
    int style = getStyle();
    if( okButton.getEnabled() && okButton.getSelection() ) {
      style |= SWT.OK;
    }
    if( cancelButton.getEnabled() && cancelButton.getSelection() ) {
      style |= SWT.CANCEL;
    }
    if( yesButton.getEnabled() && yesButton.getSelection() ) {
      style |= SWT.YES;
    }
    if( noButton.getEnabled() && noButton.getSelection() ) {
      style |= SWT.NO;
    }
    if( retryButton.getEnabled() && retryButton.getSelection() ) {
      style |= SWT.RETRY;
    }
    if( abortButton.getEnabled() && abortButton.getSelection() ) {
      style |= SWT.ABORT;
    }
    if( ignoreButton.getEnabled() && ignoreButton.getSelection() ) {
      style |= SWT.IGNORE;
    }
    if( iconErrorButton.getEnabled() && iconErrorButton.getSelection() ) {
      style |= SWT.ICON_ERROR;
    }
    if( iconInformationButton.getEnabled()
        && iconInformationButton.getSelection() ) {
      style |= SWT.ICON_INFORMATION;
    }
    if( iconQuestionButton.getEnabled() && iconQuestionButton.getSelection() ) {
      style |= SWT.ICON_QUESTION;
    }
    if( iconWarningButton.getEnabled() && iconWarningButton.getSelection() ) {
      style |= SWT.ICON_WARNING;
    }
    if( iconWorkingButton.getEnabled() && iconWorkingButton.getSelection() ) {
      style |= SWT.ICON_WORKING;
    }
    String title = "MessageBox Title";
    String mesg = "Lorem <b>ipsum</b> dolor sit amet consectetuer adipiscing elit.";
    MessageBox mb = new MessageBox( getShell(), style );
    mb.setMarkupEnabled( useMarkup );
    mb.setText( title );
    mb.setMessage( mesg );
    if( useDialogCallback ) {
      mb.open( new DialogCallback() {
        @Override
        public void dialogClosed( int returnCode ) {
          String returnCodeText = returnCodeText( returnCode );
          messageBoxDlgResLabel.setText( "Result: " + returnCodeText );
          messageBoxDlgResLabel.pack();
        }
      } );
    } else {
      int result = mb.open();
      String returnCodeText = returnCodeText( result );
      messageBoxDlgResLabel.setText( "Result: " + returnCodeText );
      messageBoxDlgResLabel.pack();
    }
  }

  private void showColorDialog() {
    final ColorDialog dialog = new ColorDialog( getShell() );
    if( useDialogCallback ) {
      dialog.open( new DialogCallback() {
        @Override
        public void dialogClosed( int returnCode ) {
          RGB result = dialog.getRGB();
          messageBoxDlgResLabel.setText( "Result: " + result );
          messageBoxDlgResLabel.pack();
        }
      } );
    } else {
      RGB result = dialog.open();
      messageBoxDlgResLabel.setText( "Result: " + result );
      messageBoxDlgResLabel.pack();
    }
  }

  protected void showFontDialog() {
    final FontDialog dialog = new FontDialog( getShell(), SWT.SHELL_TRIM );
    if( useDialogCallback ) {
      dialog.open( new DialogCallback() {
        @Override
        public void dialogClosed( int returnCode ) {
          FontData fontData = returnCode == SWT.OK ? dialog.getFontList()[ 0 ] : null;
          RGB rgb = dialog.getRGB();
          messageBoxDlgResLabel.setText( "Result: " + fontData+ " / " + rgb );
          messageBoxDlgResLabel.pack();
        }
      } );
    } else {
      FontData result = dialog.open();
      messageBoxDlgResLabel.setText( "Result: " + result + " / " + dialog.getRGB() );
      messageBoxDlgResLabel.pack();
    }
  }

  private void createMessageBoxStyleControls( final Composite parent ) {
    createButtonStyleGroup( parent );
    createIconStyleGroup( parent );
  }

  private void createButtonStyleGroup( final Composite parent ) {
    Group group = new Group( parent, SWT.NONE );
    group.setLayout( new GridLayout() );
    GridData layoutData = new GridData( SWT.FILL, SWT.FILL, false, false );
    group.setLayoutData( layoutData );
    group.setText( "SWT MessageBox Styles" );
    okButton = new Button( group, SWT.CHECK );
    okButton.setText( "SWT.OK" );
    cancelButton = new Button( group, SWT.CHECK );
    cancelButton.setText( "SWT.CANCEL" );
    yesButton = new Button( group, SWT.CHECK );
    yesButton.setText( "SWT.YES" );
    noButton = new Button( group, SWT.CHECK );
    noButton.setText( "SWT.NO" );
    retryButton = new Button( group, SWT.CHECK );
    retryButton.setText( "SWT.RETRY" );
    abortButton = new Button( group, SWT.CHECK );
    abortButton.setText( "SWT.ABORT" );
    ignoreButton = new Button( group, SWT.CHECK );
    ignoreButton.setText( "SWT.IGNORE" );
  }

  private void createIconStyleGroup( final Composite parent ) {
    Group group = new Group( parent, SWT.NONE );
    group.setLayout( new GridLayout() );
    GridData iconGroupData = new GridData( SWT.FILL, SWT.FILL, false, false );
    group.setLayoutData( iconGroupData );
    group.setText( "SWT MessageBox Icon Styles" );
    iconErrorButton = new Button( group, SWT.RADIO );
    iconErrorButton.setText( "SWT.ICON_ERROR" );
    iconInformationButton = new Button( group, SWT.RADIO );
    iconInformationButton.setText( "SWT.ICON_INFORMATION" );
    iconQuestionButton = new Button( group, SWT.RADIO );
    iconQuestionButton.setText( "SWT.ICON_QUESTION" );
    iconWarningButton = new Button( group, SWT.RADIO );
    iconWarningButton.setText( "SWT.ICON_WARNING" );
    iconWorkingButton = new Button( group, SWT.RADIO );
    iconWorkingButton.setText( "SWT.ICON_WORKING" );
    noIconButton = new Button( group, SWT.RADIO );
    noIconButton.setText( "No Icon" );
    noIconButton.setSelection( true );
  }

  private static String returnCodeText( int result ) {
    String strResult = "";
    switch( result ) {
      case SWT.OK:
        strResult = "SWT.OK";
      break;
      case SWT.YES:
        strResult = "SWT.YES";
      break;
      case SWT.NO:
        strResult = "SWT.NO";
      break;
      case SWT.CANCEL:
        strResult = "SWT.CANCEL";
      break;
      case SWT.ABORT:
        strResult = "SWT.ABORT";
      break;
      case SWT.RETRY:
        strResult = "SWT.RETRY";
      break;
      case SWT.IGNORE:
        strResult = "SWT.IGNORE";
      break;
      default:
        strResult = "" + result;
      break;
    }
    return strResult;
  }

}
