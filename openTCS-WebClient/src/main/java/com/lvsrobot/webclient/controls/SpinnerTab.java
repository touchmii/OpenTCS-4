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

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;


public class SpinnerTab extends ExampleTab {

  private Spinner spinner;
  private Spinner modifySpinner;
  private int minimum;
  private int maximum;
  private int digits;
  private int increment;
  private int pageIncrement;
  private int selection;

  public SpinnerTab() {
    super( "Spinner" );
    minimum = 0;
    maximum = 100;
    digits = 0;
    increment = 1;
    pageIncrement = 10;
    selection = 0;
  }

  @Override
  protected void createStyleControls( final Composite parent ) {
    createStyleButton( "BORDER", SWT.BORDER );
    createStyleButton( "READ_ONLY", SWT.READ_ONLY );
    createStyleButton( "WRAP", SWT.WRAP );
    createOrientationButton();
    createVisibilityButton();
    createEnablementButton();
    createRangeControls( parent );
    createFgColorButton();
    createBgColorButton();
    createFontChooser();
    createCursorCombo();
  }

  @Override
  protected void createExampleControls( final Composite parent ) {
    GridLayout gridLayout = new GridLayout( 2, false );
    parent.setLayout( gridLayout );
    Label label = new Label( parent, SWT.NONE );
    label.setText( "Simple Spinner" );
    spinner = new Spinner( parent, getStyle() );
    label = new Label( parent, SWT.NONE );
    label.setText( "Spinner with ModifyListener" );
    modifySpinner = new Spinner( parent, getStyle() );
    label = new Label( parent, SWT.NONE );
    label.setText( "Current value" );
    final Label lblSpinnerValue = new Label( parent, SWT.NONE );
    lblSpinnerValue.setLayoutData( new GridData( SWT.FILL, SWT.FILL, true, false ) );
    lblSpinnerValue.setText( String.valueOf( modifySpinner.getSelection() ) );
    modifySpinner.addModifyListener( new ModifyListener() {

      @Override
      public void modifyText( final ModifyEvent event ) {
        //String value = String.valueOf( modifySpinner.getSelection() );
        lblSpinnerValue.setText( modifySpinner.getText() );
      }
    } );
    updateSpinners();
    registerControl( spinner );
    registerControl( modifySpinner );
  }

  private void createRangeControls( final Composite parent ) {
    Group group = new Group( parent, SWT.NONE );
    group.setText( "Settings" );
    group.setLayout( new GridLayout( 2, false ) );

    final Text txtMin
      = createLabeledText( group, "Minimum", String.valueOf( minimum ) );
    final Text txtMax
      = createLabeledText( group, "Maximum", String.valueOf( maximum ) );
    final Spinner spnDigits
      = createLabeledSpinner( group, "Digits", digits );
    final Text txtInc
      = createLabeledText( group, "Increment", String.valueOf( increment ) );
    final Text txtPageInc = createLabeledText( group,
                                               "PageIncrement",
                                               String.valueOf( pageIncrement ) );
    final Text txtSelection
      = createLabeledText( group, "Selection", String.valueOf( selection ) );
    Button btnApply = new Button( group, SWT.PUSH );
    btnApply.setText( "Apply" );
    GridData btnApplyData = new GridData( GridData.HORIZONTAL_ALIGN_END );
    btnApplyData.horizontalSpan = 2;
    btnApply.setLayoutData( btnApplyData );
    btnApply.addSelectionListener( new SelectionAdapter() {

      @Override
      public void widgetSelected( final SelectionEvent event ) {
        minimum = parseInt( txtMin, spinner.getMinimum() );
        maximum = parseInt( txtMax, spinner.getMaximum() );
        digits = spnDigits.getSelection();
        increment = parseInt( txtInc, spinner.getIncrement() );
        pageIncrement = parseInt( txtPageInc, spinner.getPageIncrement() );
        selection = parseInt( txtSelection, spinner.getSelection() );
        updateSpinners();
      }
    } );
  }

  private Text createLabeledText( final Composite parent,
                                  final String text,
                                  final String value )
  {
    final Label label = new Label( parent, SWT.NONE );
    label.setText( text );
    final Text result = new Text( parent, SWT.BORDER );
    result.setText( value );
    result.setLayoutData( new GridData( GridData.FILL_HORIZONTAL ) );
    return result;
  }

  private Spinner createLabeledSpinner( final Composite parent,
                                        final String text,
                                        final int value )
  {
    final Label label = new Label( parent, SWT.NONE );
    label.setText( text );
    final Spinner result = new Spinner( parent, SWT.BORDER );
    result.setSelection( value );
    result.setLayoutData( new GridData( GridData.FILL_HORIZONTAL ) );
    return result;
  }

  private void updateSpinners() {
    spinner.setValues( selection,
                       minimum,
                       maximum,
                       digits,
                       increment,
                       pageIncrement );
    modifySpinner.setValues( selection,
                             minimum,
                             maximum,
                             digits,
                             increment,
                             pageIncrement );
  }

  private int parseInt( final Text text, final int oldValue ) {
    int result;
    try {
      result = Integer.parseInt( text.getText() );
    } catch( NumberFormatException e ) {
      text.setText( String.valueOf( oldValue ) );
      result = oldValue;
    }
    return result;
  }
}
