/**
 * Copyright (c) The openTCS Authors.
 *
 * This program is free software and subject to the MIT license. (For details,
 * see the licensing information (LICENSE.txt) you should have received with
 * this copy of the software.)
 */
package org.opentcs.guing.application.action.synchronize;

import com.google.inject.Inject;
import java.awt.event.ActionEvent;
import static java.util.Objects.requireNonNull;

import javax.swing.*;

import static javax.swing.Action.ACCELERATOR_KEY;
import static javax.swing.Action.MNEMONIC_KEY;
import javax.swing.KeyStroke;
import org.opentcs.guing.application.OpenTCSView;
import org.opentcs.guing.application.OperationMode;
import static org.opentcs.guing.util.I18nPlantOverview.MENU_PATH;
import org.opentcs.guing.util.ResourceBundleUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An action to switch to modelling mode.
 *
 * @author Mustafa Yalciner (Fraunhofer IML)
 */
public class SwitchToModellingAction
    extends AbstractAction {

  /**
   * The id of this action. (Often used to map a key combination in the ressources.)
   */
  public static final String ID = "file.mode.switchToModelling";
  
  private static final ResourceBundleUtil BUNDLE = ResourceBundleUtil.getBundle(MENU_PATH);

  private static final Logger LOG = LoggerFactory.getLogger(SwitchToModellingAction.class);

  /**
   * The view.
   */
  private final OpenTCSView view;

  @Inject
  public SwitchToModellingAction(OpenTCSView view) {
    this.view = requireNonNull(view, "view");
    
    putValue(NAME, BUNDLE.getString("switchToModellingAction.name"));
    putValue(SHORT_DESCRIPTION, BUNDLE.getString("switchToModellingAction.shortDescription"));
    putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke("alt M"));
    putValue(MNEMONIC_KEY, Integer.valueOf('M'));
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    LOG.info("Switch to ModellingMode你好是aaaModeaaaaa");
//    System.out.println("Switch to ModellingMode");
    //JOptionPane.showConfirmDialog(null, "\u8BF7\u8F93\u5165\u5BC6\u7801:", "\u8BF7\u8F93\u5165\u5BC6\u7801XXX", JOptionPane.ERROR_MESSAGE);

    view.switchPlantOverviewState(OperationMode.MODELLING);

  }


}
