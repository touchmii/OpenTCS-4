/**
 * Copyright (c) The openTCS Authors.
 *
 * This program is free software and subject to the MIT license. (For details,
 * see the licensing information (LICENSE.txt) you should have received with
 * this copy of the software.)
 */
package org.opentcs.kernel.extensions.controlcenter.vehicles;

import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.*;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import javax.swing.*;
import javax.swing.table.*;
import static java.util.Objects.requireNonNull;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.opentcs.data.model.Vehicle;
import org.opentcs.data.notification.UserNotification;
import org.opentcs.drivers.vehicle.VehicleCommAdapter;
import org.opentcs.drivers.vehicle.VehicleProcessModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Displays information about a vehicle (VehicleModel) graphically.
 *
 * @author Iryna Felko (Fraunhofer IML)
 * @author Stefan Walter (Fraunhofer IML)
 */
final class DetailPanel
    extends JPanel
    implements PropertyChangeListener {

  /**
   * This class's logger.
   */
  private static final Logger LOG = LoggerFactory.getLogger(DetailPanel.class);
  /**
   * A panel's default border title.
   */
  private static final String DEFAULT_BORDER_TITLE = "";
  /**
   * A <code>DateFormat</code> instance for formatting message's time stamps.
   */
  private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter
      .ofLocalizedDateTime(FormatStyle.SHORT)
      .withLocale(Locale.getDefault())
      .withZone(ZoneId.systemDefault());
  /**
   * The adapter specific list of JPanels.
   */
  @SuppressWarnings("deprecation")
  private final List<org.opentcs.drivers.vehicle.VehicleCommAdapterPanel> customPanelList
      = new LinkedList<>();
  /**
   * The logging table model to use.
   */
  private final LogTableModel loggingTableModel = new LogTableModel();
  /**
   * The vehicle model of the vehicle current associated with this window.
   */
  private VehicleEntry vehicleEntry;
  /**
   * The comm adapter currently attached to the vehicle (model).
   */
  private VehicleCommAdapter commAdapter;

  private static final ResourceBundle BUNDLE
          = ResourceBundle.getBundle("org/opentcs/kernel/controlcenter/vehicles/Bundle");

  /**
   * Creates a new instance.
   */
  DetailPanel() {
    initComponents();

    loggingTable.setModel(loggingTableModel);
    loggingTable.getColumnModel().getColumn(0).setPreferredWidth(40);
    loggingTable.getColumnModel().getColumn(1).setPreferredWidth(110);
    loggingTable.getSelectionModel().addListSelectionListener(new RowListener());
    // Make sure we start with an empty panel.
    detachFromVehicle();
  }

  @Override
  public void propertyChange(PropertyChangeEvent evt) {
    if (evt.getSource() instanceof VehicleEntry) {
      updateFromVehicleEntry(evt);
    }
    else if (evt.getSource() instanceof VehicleProcessModel) {
      updateFromVehicleProcessModel(evt);
    }
  }

  // Methods not declared in any interface start here.
  /**
   * Attaches this panel to a vehicle.
   *
   * @param newVehicleEntry The vehicle entry to attach to.
   */
  void attachToVehicle(VehicleEntry newVehicleEntry) {
    requireNonNull(newVehicleEntry, "newVehicleEntry");

    // Clean up first - but only if we're not reattaching the vehicle model
    // which is already attached to this panel.
    if (vehicleEntry != null && vehicleEntry != newVehicleEntry) {
      detachFromVehicle();
    }
    vehicleEntry = newVehicleEntry;
    vehicleEntry.addPropertyChangeListener(this);
    vehicleEntry.getProcessModel().addPropertyChangeListener(this);
    setBorderTitle(vehicleEntry.getProcessModel().getName());
    // Ensure the tabbed pane containing vehicle information is shown.
    removeAll();
    add(tabbedPane);

    // Init vehicle status.
    loggingTableModel.clear();
    for (UserNotification curMessage : vehicleEntry.getProcessModel().getNotifications()) {
      loggingTableModel.addRow(curMessage);
    }
    initVehicleEntryAttributes();
    // Update panel contents.
    validate();
//    int newIndex = vehicleEntry.getSelectedTabIndex();
    int newIndex = 0;
    LOG.debug("Setting tabbedPane.selectedTabIndex to {}", newIndex);
    tabbedPane.setSelectedIndex(newIndex);
  }

  /**
   * Detaches this panel from a vehicle (if it is currently attached to any).
   */
  private void detachFromVehicle() {
    if (vehicleEntry != null) {
      vehicleEntry.removePropertyChangeListener(this);
      vehicleEntry.getProcessModel().removePropertyChangeListener(this);
      // Remove all custom panels and let the comm adapter know we don't need
      // them any more.
      removeCustomPanels();
      if (commAdapter != null) {
        commAdapter = null;
      }
      customPanelList.clear();
      vehicleEntry = null;
    }
    // Clear the log message table.
    loggingTableModel.clear();
    setBorderTitle(DEFAULT_BORDER_TITLE);
    // Remove the contents of this panel.
    removeAll();
    add(noVehiclePanel);
    // Update panel contents.
    validate();
  }

  private void initVehicleEntryAttributes() {
    updateCommAdapter(vehicleEntry.getCommAdapter());

    updateCommAdapterEnabled(vehicleEntry.getProcessModel().isCommAdapterEnabled());
    updateVehiclePosition(vehicleEntry.getProcessModel().getVehiclePosition());
    updateVehicleState(vehicleEntry.getProcessModel().getVehicleState());
  }

  private void updateFromVehicleEntry(PropertyChangeEvent evt) {
    if (Objects.equals(evt.getPropertyName(), VehicleEntry.Attribute.COMM_ADAPTER.name())) {
      updateCommAdapter((VehicleCommAdapter) evt.getNewValue());
    }
  }

  private void updateFromVehicleProcessModel(PropertyChangeEvent evt) {
    if (Objects.equals(evt.getPropertyName(), VehicleProcessModel.Attribute.COMM_ADAPTER_ENABLED)) {
      updateCommAdapterEnabled((Boolean) evt.getNewValue());
    }
    else if (Objects.equals(evt.getPropertyName(), VehicleProcessModel.Attribute.POSITION.name())) {
      updateVehiclePosition((String) evt.getNewValue());
    }
    else if (Objects.equals(evt.getPropertyName(), VehicleProcessModel.Attribute.STATE.name())) {
      updateVehicleState((Vehicle.State) evt.getNewValue());
    }
    else if (Objects.equals(evt.getPropertyName(),
                            VehicleProcessModel.Attribute.USER_NOTIFICATION.name())) {
      updateUserNotification((UserNotification) evt.getNewValue());
    }
  }

  private void updateCommAdapter(VehicleCommAdapter newCommAdapter) {
    SwingUtilities.invokeLater(() -> {
      // If there was a comm adapter and it changed, we need to clean up a few
      // things first.
      if (commAdapter != null) {
        // Detach all custom panels of the old comm adapter.
        removeCustomPanels();
        customPanelList.clear();
      }
      // Update the comm adapter reference.
      commAdapter = newCommAdapter;
      // If we have a new comm adapter, set up a few things.
      if (commAdapter != null) {
        // Update the custom panels displayed.
        updateCustomPanels();
      }
      chkBoxEnable.setEnabled(commAdapter != null);
      chkBoxEnable.setSelected(commAdapter != null && commAdapter.isEnabled());
    });
  }

  private void updateCommAdapterEnabled(boolean enabled) {
    SwingUtilities.invokeLater(() -> chkBoxEnable.setSelected(enabled));
  }

  private void updateVehiclePosition(String position) {
    SwingUtilities.invokeLater(() -> curPosTxt.setText(position));
  }

  private void updateVehicleState(Vehicle.State state) {
//    SwingUtilities.invokeLater(() -> curStateTxt.setText(state.toString()));
    SwingUtilities.invokeLater(() -> curStateTxt.setText(BUNDLE.getString(String.format("Status_%s", state.toString()))));

  }

  private void updateUserNotification(UserNotification notification) {
    SwingUtilities.invokeLater(() -> loggingTableModel.addRow(notification));
  }

  /**
   * Update the list of custom panels in the tabbed pane.
   */
  @SuppressWarnings("deprecation")
  private void updateCustomPanels() {
    for (org.opentcs.drivers.vehicle.VehicleCommAdapterPanel curPanel : customPanelList) {
      LOG.debug("Removing {} from tabbedPane.", curPanel);
      tabbedPane.remove(curPanel);
    }
    customPanelList.clear();
    if (commAdapter != null) {
      customPanelList.addAll(commAdapter.getAdapterPanels());
      for (org.opentcs.drivers.vehicle.VehicleCommAdapterPanel curPanel : customPanelList) {
        LOG.debug("Adding {} with title {} to tabbedPane.", curPanel, curPanel.getTitle());
        tabbedPane.addTab(curPanel.getTitle(), curPanel);
      }
    }
  }

  /**
   * Removes the custom panels from this panel's tabbed pane.
   */
  @SuppressWarnings("deprecation")
  private void removeCustomPanels() {
    LOG.debug("Setting selected component of tabbedPane to overviewTabPanel.");
    tabbedPane.setSelectedComponent(overviewTabPanel);
    for (org.opentcs.drivers.vehicle.VehicleCommAdapterPanel panel : customPanelList) {
      LOG.debug("Removing {} from tabbedPane.", panel);
      tabbedPane.remove(panel);
    }
  }

  /**
   * Sets this panel's border title.
   *
   * @param newTitle This panel's new border title.
   */
  private void setBorderTitle(String newTitle) {
    requireNonNull(newTitle, "newTitle");
    ((TitledBorder) getBorder()).setTitle(newTitle);
    // Trigger a repaint - the title sometimes looks strange otherwise.
    repaint();
  }

  /**
   * This method appends the selected notification to the text area in the log tab.
   *
   * @param row The selected row in the table.
   */
  private void outputLogNotification(int row) {
    UserNotification message = loggingTableModel.getRow(row);
    String timestamp = DATE_FORMAT.format(message.getTimestamp());
    String output = timestamp + " (" + message.getLevel() + "):\n" + message.getText();
    loggingTextArea.setText(output);
  }

  // CHECKSTYLE:OFF
  /**
   * This method is called from within the constructor to
   * initialize the form.
   * WARNING: Do NOT modify this code. The content of this method is
   * always regenerated by the Form Editor.
   */
  // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
  private void initComponents() {
      ResourceBundle bundle = ResourceBundle.getBundle("org.opentcs.kernel.controlcenter.vehicles.Bundle");
      tabbedPane = new JTabbedPane();
      overviewTabPanel = new JPanel();
      headPanel = new JPanel();
      statusPanel = new JPanel();
      adapterStatusPanel = new JPanel();
      chkBoxEnable = new JCheckBox();
      statusFiguresPanel = new JPanel();
      curPosLbl = new JLabel();
      curPosTxt = new JTextField();
      curStateLbl = new JLabel();
      curStateTxt = new JTextField();
      fillingLbl = new JLabel();
      logoPanel = new JPanel();
      logoLbl = new JLabel();
      logPanel = new JPanel();
      logTableScrollPane = new JScrollPane();
      loggingTable = new JTable();
      logTextScrollPane = new JScrollPane();
      loggingTextArea = new JTextArea();
      logPopupMenu = new JPopupMenu();
      clearMenuItem = new JMenuItem();
      loggingTablePopupMenu = new JPopupMenu();
      filterMenu = new JMenu();
      everythingCheckBoxMenuItem = new JCheckBoxMenuItem();
      warningsCheckBoxMenuItem = new JCheckBoxMenuItem();
      errorsCheckBoxMenuItem = new JCheckBoxMenuItem();
      noVehiclePanel = new JPanel();
      noVehicleLabel = new JLabel();

      //======== this ========
      setBorder(new TitledBorder("<User Code>"));
      setLayout(new BorderLayout());

      //======== tabbedPane ========
      {
          tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);

          //======== overviewTabPanel ========
          {
              overviewTabPanel.setLayout(new GridBagLayout());

              //======== headPanel ========
              {
                  headPanel.setLayout(new BorderLayout());

                  //======== statusPanel ========
                  {
                      statusPanel.setLayout(new BoxLayout(statusPanel, BoxLayout.X_AXIS));

                      //======== adapterStatusPanel ========
                      {
                          adapterStatusPanel.setBorder(new TitledBorder("Adapter status"));
                          adapterStatusPanel.setLayout(new BorderLayout());

                          //---- chkBoxEnable ----
                          chkBoxEnable.setText(bundle.getString("EnableAdapter"));
                          chkBoxEnable.setEnabled(false);
                          chkBoxEnable.addActionListener(e -> chkBoxEnableActionPerformed(e));
                          adapterStatusPanel.add(chkBoxEnable, BorderLayout.CENTER);
                      }
                      statusPanel.add(adapterStatusPanel);

                      //======== statusFiguresPanel ========
                      {
                          statusFiguresPanel.setBorder(new TitledBorder("Vehicle status"));
                          statusFiguresPanel.setLayout(new GridBagLayout());

                          //---- curPosLbl ----
                          curPosLbl.setText(bundle.getString("CurrentPosition"));
                          statusFiguresPanel.add(curPosLbl, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                              GridBagConstraints.EAST, GridBagConstraints.NONE,
                              new Insets(0, 3, 0, 0), 0, 0));

                          //---- curPosTxt ----
                          curPosTxt.setEditable(false);
                          curPosTxt.setColumns(9);
                          curPosTxt.setHorizontalAlignment(SwingConstants.RIGHT);
                          curPosTxt.setText("Point-0001");
                          statusFiguresPanel.add(curPosTxt, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
                              GridBagConstraints.CENTER, GridBagConstraints.NONE,
                              new Insets(0, 3, 0, 0), 0, 0));

                          //---- curStateLbl ----
                          curStateLbl.setText(bundle.getString("CurrentState"));
                          statusFiguresPanel.add(curStateLbl, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
                              GridBagConstraints.EAST, GridBagConstraints.NONE,
                              new Insets(3, 3, 0, 0), 0, 0));

                          //---- curStateTxt ----
                          curStateTxt.setText(Vehicle.State.UNKNOWN.name());
                          curStateTxt.setEditable(false);
                          curStateTxt.setColumns(9);
                          curStateTxt.setHorizontalAlignment(SwingConstants.RIGHT);
                          statusFiguresPanel.add(curStateTxt, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0,
                              GridBagConstraints.CENTER, GridBagConstraints.NONE,
                              new Insets(3, 3, 0, 0), 0, 0));
                          statusFiguresPanel.add(fillingLbl, new GridBagConstraints(2, 0, 1, 1, 1.0, 0.0,
                              GridBagConstraints.CENTER, GridBagConstraints.NONE,
                              new Insets(0, 0, 0, 0), 0, 0));
                      }
                      statusPanel.add(statusFiguresPanel);
                  }
                  headPanel.add(statusPanel, BorderLayout.WEST);

                  //======== logoPanel ========
                  {
                      logoPanel.setBackground(Color.white);
                      logoPanel.setLayout(new BorderLayout());

                      //---- logoLbl ----
                      logoLbl.setHorizontalAlignment(SwingConstants.CENTER);
                      logoLbl.setIcon(new ImageIcon(getClass().getResource("/org/opentcs/kernel/controlcenter/res/logos/opentcs_logo.gif")));
                      logoPanel.add(logoLbl, BorderLayout.CENTER);
                  }
                  headPanel.add(logoPanel, BorderLayout.CENTER);
              }
              overviewTabPanel.add(headPanel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                  GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                  new Insets(0, 0, 0, 0), 0, 0));

              //======== logPanel ========
              {
                  logPanel.setBorder(new TitledBorder("Messages"));
                  logPanel.setPreferredSize(new Dimension(468, 200));
                  logPanel.setLayout(new BorderLayout());

                  //======== logTableScrollPane ========
                  {
                      logTableScrollPane.setComponentPopupMenu(loggingTablePopupMenu);

                      //---- loggingTable ----
                      loggingTable.setModel(new DefaultTableModel(
                          new Object[][] {
                          },
                          new String[] {
                              "Time stamp", "Message"
                          }
                      ) {
                          boolean[] columnEditable = new boolean[] {
                              false, false
                          };
                          @Override
                          public boolean isCellEditable(int rowIndex, int columnIndex) {
                              return columnEditable[columnIndex];
                          }
                      });
                      loggingTable.setComponentPopupMenu(loggingTablePopupMenu);
                      loggingTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
                      logTableScrollPane.setViewportView(loggingTable);
                  }
                  logPanel.add(logTableScrollPane, BorderLayout.CENTER);

                  //======== logTextScrollPane ========
                  {

                      //---- loggingTextArea ----
                      loggingTextArea.setEditable(false);
                      loggingTextArea.setColumns(20);
                      loggingTextArea.setFont(new Font("Courier New", Font.PLAIN, 12));
                      loggingTextArea.setLineWrap(true);
                      loggingTextArea.setRows(3);
                      loggingTextArea.setComponentPopupMenu(logPopupMenu);
                      logTextScrollPane.setViewportView(loggingTextArea);
                  }
                  logPanel.add(logTextScrollPane, BorderLayout.SOUTH);
              }
              overviewTabPanel.add(logPanel, new GridBagConstraints(0, 1, 1, 1, 1.0, 1.0,
                  GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                  new Insets(0, 0, 0, 0), 0, 0));
          }
          tabbedPane.addTab(bundle.getString("GeneralStatus"), overviewTabPanel);
      }
      add(tabbedPane, BorderLayout.CENTER);

      //======== logPopupMenu ========
      {

          //---- clearMenuItem ----
          clearMenuItem.setText(bundle.getString("ClearLogMessageText"));
          clearMenuItem.addActionListener(e -> clearMenuItemActionPerformed(e));
          logPopupMenu.add(clearMenuItem);
      }

      //======== loggingTablePopupMenu ========
      {

          //======== filterMenu ========
          {
              filterMenu.setText(bundle.getString("FilterMessages"));
              filterMenu.setActionCommand(" message filtering");

              //---- everythingCheckBoxMenuItem ----
              everythingCheckBoxMenuItem.setText(bundle.getString("FilterMessagesShowAll"));
              everythingCheckBoxMenuItem.addActionListener(e -> everythingCheckBoxMenuItemActionPerformed(e));
              filterMenu.add(everythingCheckBoxMenuItem);

              //---- warningsCheckBoxMenuItem ----
              warningsCheckBoxMenuItem.setText(bundle.getString("FilterMessagesShowErrorsAndWarnings"));
              warningsCheckBoxMenuItem.addActionListener(e -> warningsCheckBoxMenuItemActionPerformed(e));
              filterMenu.add(warningsCheckBoxMenuItem);

              //---- errorsCheckBoxMenuItem ----
              errorsCheckBoxMenuItem.setText(bundle.getString("FilterMessagesShowErrors"));
              errorsCheckBoxMenuItem.addActionListener(e -> errorsCheckBoxMenuItemActionPerformed(e));
              filterMenu.add(errorsCheckBoxMenuItem);
          }
          loggingTablePopupMenu.add(filterMenu);
      }

      //======== noVehiclePanel ========
      {
          noVehiclePanel.setLayout(new BorderLayout());

          //---- noVehicleLabel ----
          noVehicleLabel.setFont(noVehicleLabel.getFont().deriveFont(noVehicleLabel.getFont().getSize() + 3f));
          noVehicleLabel.setHorizontalAlignment(SwingConstants.CENTER);
          noVehicleLabel.setText(bundle.getString("NoVehicleAttached"));
          noVehiclePanel.add(noVehicleLabel, BorderLayout.CENTER);
      }
  }// </editor-fold>//GEN-END:initComponents

  private void warningsCheckBoxMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_warningsCheckBoxMenuItemActionPerformed
    loggingTableModel.filterMessages((notification)
        -> notification.getLevel().equals(UserNotification.Level.IMPORTANT)
        || notification.getLevel().equals(UserNotification.Level.NOTEWORTHY));
    warningsCheckBoxMenuItem.setSelected(true);
    errorsCheckBoxMenuItem.setSelected(false);
    everythingCheckBoxMenuItem.setSelected(false);
  }//GEN-LAST:event_warningsCheckBoxMenuItemActionPerformed

  private void everythingCheckBoxMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_everythingCheckBoxMenuItemActionPerformed
    loggingTableModel.filterMessages((notification) -> true);
    everythingCheckBoxMenuItem.setSelected(true);
    errorsCheckBoxMenuItem.setSelected(false);
    warningsCheckBoxMenuItem.setSelected(false);
  }//GEN-LAST:event_everythingCheckBoxMenuItemActionPerformed

  private void errorsCheckBoxMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_errorsCheckBoxMenuItemActionPerformed
    loggingTableModel.filterMessages(
        (notification) -> notification.getLevel().equals(UserNotification.Level.IMPORTANT));
    errorsCheckBoxMenuItem.setSelected(true);
    everythingCheckBoxMenuItem.setSelected(false);
    warningsCheckBoxMenuItem.setSelected(false);
  }//GEN-LAST:event_errorsCheckBoxMenuItemActionPerformed

  private void clearMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clearMenuItemActionPerformed
    loggingTextArea.setText("");
  }//GEN-LAST:event_clearMenuItemActionPerformed

  private void chkBoxEnableActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkBoxEnableActionPerformed
    if (chkBoxEnable.isSelected()) {
      commAdapter.enable();
    }
    else {
      commAdapter.disable();
    }
  }//GEN-LAST:event_chkBoxEnableActionPerformed
  // Variables declaration - do not modify//GEN-BEGIN:variables
  private JTabbedPane tabbedPane;
  private JPanel overviewTabPanel;
  private JPanel headPanel;
  private JPanel statusPanel;
  private JPanel adapterStatusPanel;
  private JCheckBox chkBoxEnable;
  private JPanel statusFiguresPanel;
  private JLabel curPosLbl;
  private JTextField curPosTxt;
  private JLabel curStateLbl;
  private JTextField curStateTxt;
  private JLabel fillingLbl;
  private JPanel logoPanel;
  private JLabel logoLbl;
  private JPanel logPanel;
  private JScrollPane logTableScrollPane;
  private JTable loggingTable;
  private JScrollPane logTextScrollPane;
  private JTextArea loggingTextArea;
  private JPopupMenu logPopupMenu;
  private JMenuItem clearMenuItem;
  private JPopupMenu loggingTablePopupMenu;
  private JMenu filterMenu;
  private JCheckBoxMenuItem everythingCheckBoxMenuItem;
  private JCheckBoxMenuItem warningsCheckBoxMenuItem;
  private JCheckBoxMenuItem errorsCheckBoxMenuItem;
  private JPanel noVehiclePanel;
  private JLabel noVehicleLabel;
  // End of variables declaration//GEN-END:variables
  // CHECKSTYLE:ON

  /**
   * A <code>ListSelectionListener</code> for handling the logging table
   * selection events.
   */
  private final class RowListener
      implements ListSelectionListener {

    /**
     * Creates a new instance.
     */
    private RowListener() {
    }

    @Override
    public void valueChanged(ListSelectionEvent event) {
      if (event.getValueIsAdjusting()) {
        return;
      }
      if (loggingTable.getSelectedRow() >= 0) {
        outputLogNotification(loggingTable.getSelectedRow());
      }
      else {
        loggingTextArea.setText("");
      }
    }
  }
}
