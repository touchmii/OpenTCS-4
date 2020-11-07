/*******************************************************************************
 * Copyright (c) 2007, 2020 Innoopract Informationssysteme GmbH and others.
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

import java.util.HashMap;

import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.application.AbstractEntryPoint;
import org.eclipse.rap.rwt.client.service.BrowserNavigation;
import org.eclipse.rap.rwt.client.service.BrowserNavigationEvent;
import org.eclipse.rap.rwt.client.service.BrowserNavigationListener;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;


public class ControlsDemo {

  private Composite header;
  private Tree tree;
  private Composite exampleParent;
  private Color backgroundColor;


  public ControlsDemo( Composite parent ) {
    parent.setLayout( new FormLayout() );
    backgroundColor = new Color( parent.getDisplay(), 0x31, 0x61, 0x9C );
    header = new Composite( parent, SWT.NONE );
    header.setBackground( backgroundColor );
    header.setBackgroundMode( SWT.INHERIT_DEFAULT );
    header.setLayoutData( createLayoutDataForHeader() );
    Label label = new Label( header, SWT.NONE );
    label.setText( "RAP Controls Demo" );
    label.setForeground( parent.getDisplay().getSystemColor( SWT.COLOR_WHITE ) );
    label.setBounds( 40, 30, 250, 30 );
    tree = new Tree( parent, SWT.FULL_SELECTION );
    tree.setLayoutData( createLayoutDataForTree() );
    exampleParent = new Composite( parent, SWT.NONE );
    exampleParent.setLayout( new FillLayout() );
    exampleParent.setLayoutData( createLayoutDataForExampleParent() );
    fillTree( parent );
  }

  private void fillTree( Composite parent ) {
    final HashMap<String, ExampleTab> exampleMap = new HashMap< String, ExampleTab >();
    final BrowserNavigation navigation = RWT.getClient().getService( BrowserNavigation.class );
    for( ExampleTab tab : createExampleTabs() ) {
      TreeItem item = new TreeItem( tree, SWT.NONE );
      item.setText( tab.getName() );
      item.setData( tab );
      tab.setData( item );
      exampleMap.put( tab.getId(), tab );
    }
    tree.addListener( SWT.Selection, new Listener() {
      @Override
      public void handleEvent( Event event ) {
        ExampleTab tab = ( ExampleTab )event.item.getData();
        selectTab( tab );
        navigation.pushState( tab.getId(), null );
      }
    } );
    navigation.addBrowserNavigationListener( new BrowserNavigationListener() {
      @Override
      public void navigated( BrowserNavigationEvent event ) {
        ExampleTab tab = exampleMap.get( event.getState() );
        if( tab != null ) {
          tree.select( ( TreeItem )tab.getData() );
          tree.showSelection();
          selectTab( tab );
        }
      }
    } );
    selectTab( ( ExampleTab )tree.getItem( 0 ).getData() );
  }

  private void selectTab( ExampleTab exampleTab ) {
    Control[] children = exampleParent.getChildren();
    for( Control control : children ) {
      control.dispose();
    }
    if( exampleTab != null ) {
      exampleTab.createContents( exampleParent );
    }
    exampleParent.layout();
  }

  private FormData createLayoutDataForHeader() {
    FormData layoutData = new FormData();
    layoutData.left = new FormAttachment( 0, 0 );
    layoutData.right = new FormAttachment( 100, 0 );
    layoutData.top = new FormAttachment( 0, 0 );
    layoutData.height = 80;
    return layoutData;
  }

  private FormData createLayoutDataForTree() {
    FormData layoutData = new FormData();
    layoutData.top = new FormAttachment( header, 0 );
    layoutData.left = new FormAttachment( 0, 0 );
    layoutData.bottom = new FormAttachment( 100, 0 );
    layoutData.width = 190;
    return layoutData;
  }

  private FormData createLayoutDataForExampleParent() {
    FormData layoutData = new FormData();
    layoutData.top = new FormAttachment( header, 0 );
    layoutData.left = new FormAttachment( tree, 10 );
    layoutData.right = new FormAttachment( 100, 0 );
    layoutData.bottom = new FormAttachment( 100, 0 );
    return layoutData;
  }

  private static ExampleTab[] createExampleTabs() {
    return new ExampleTab[] {
      new ButtonTab(),
      new MyTab(),
      new BrowserTab(),
      new CanvasTab(),
      new CBannerTab(),
      new CLabelTab(),
      new ComboTab(),
      new CompositeTab(),
      new CoolBarTab(),
      new CTabFolderTab(),
      new DateTimeTab(),
      new DialogsTab(),
      new DropDownTab(),
      new ExpandBarTab(),
      new FocusTab(),
      new GroupTab(),
      new LabelTab(),
      new ListTab(),
      new LinkTab(),
      new NebulaGridTab(),
      new NebulaRichTextTab(),
      new ProgressBarTab(),
//      new RequestTab(),
      new SashTab(),
      new SashFormTab(),
      new ScaleTab(),
      new ScrolledCompositeTab(),
      new ShellTab(),
      new SliderTab(),
      new SpinnerTab(),
      new TabFolderTab(),
      new TableTab(),
      new TableViewerTab(),
      new TextTab(),
      new TextSizeTab(),
      new ToolBarTab(),
      new ToolTipTab(),
      new TreeTab(),
      new ScriptingTab(),
      new DNDExampleTab(),
      new ContainmentTab(),
      new ZOrderTab(),
      new VariantsTab(),
      new ControlDecorationTab(),
      new ClipboardTab(),
      new ErrorHandlingTab(),
      new ClientServicesTab(),
      new NLSTab(),
      new MnemonicsTab(),
    };
  }

}
