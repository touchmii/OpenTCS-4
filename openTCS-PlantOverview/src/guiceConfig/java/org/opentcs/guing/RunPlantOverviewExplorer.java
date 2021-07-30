/*
 * openTCS copyright information:
 * Copyright (c) 2013 Fraunhofer IML
 *
 * This program is free software and subject to the MIT license. (For details,
 * see the licensing information (LICENSE.txt) you should have received with
 * this copy of the software.)
 */
package org.opentcs.guing;

import org.swingexplorer.Launcher;

/**
 * The plant overview process's default entry point.
 *
 * @author Stefan Walter (Fraunhofer IML)
 */
public class RunPlantOverviewExplorer {
  public static void main(final String[] args) {
    Launcher.main(new String[] {"org.opentcs.guing.RunPlantOverview"});
  }

}