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
package com.lvsrobot.webclient.demo.actions;

import org.eclipse.core.runtime.*;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

public class JobAction implements IWorkbenchWindowActionDelegate {

  private static final int TASK_AMOUNT = 100;

  public void dispose() {
  }

  public void init( final IWorkbenchWindow window ) {
  }

  public void run( final IAction action ) {
    Job job = new Job("Long Running Action:" ) {
      protected IStatus run( final IProgressMonitor monitor ) {
        monitor.beginTask( "Number counting", TASK_AMOUNT );
        for( int i = 0; i < TASK_AMOUNT; i++ ) {
          if( monitor.isCanceled() ) {
            monitor.done();
            return Status.CANCEL_STATUS;
          }
          int done = i % TASK_AMOUNT;
          monitor.subTask( "work done: (" + done + "%)" );
          monitor.worked( 1 );
          try {
            Thread.sleep( 200 );
          } catch( InterruptedException e ) {
            // TODO Auto-generated catch block
            e.printStackTrace();
          } // 0.5s.
        }
        monitor.done();
        return Status.OK_STATUS;
      }
    };
    job.setName( job.getName() + " " + job.hashCode() );
    job.schedule();
  }

  public void selectionChanged( final IAction action,
                                final ISelection selection )
  {
  }
}
