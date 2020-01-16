/**
 * Copyright (c) The openTCS Authors.
 * <p>
 * This program is free software and subject to the MIT license. (For details,
 * see the licensing information (LICENSE.txt) you should have received with
 * this copy of the software.)
 */
package org.opentcs.kernel.extensions.controlcenter;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import static java.util.Objects.requireNonNull;

import java.util.ResourceBundle;
import java.util.Set;
import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Provider;
import javax.swing.*;

import org.opentcs.access.Kernel;
import org.opentcs.access.KernelStateTransitionEvent;
import org.opentcs.access.LocalKernel;
import org.opentcs.access.ModelTransitionEvent;
import org.opentcs.access.to.model.PlantModelCreationTO;
import org.opentcs.components.kernel.KernelExtension;
import org.opentcs.components.kernel.services.InternalPlantModelService;
import org.opentcs.customizations.ApplicationEventBus;
import org.opentcs.customizations.kernel.ActiveInModellingMode;
import org.opentcs.customizations.kernel.ActiveInOperatingMode;
import org.opentcs.util.event.EventHandler;
import org.opentcs.util.event.EventSource;
import org.opentcs.util.gui.Icons;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A GUI frontend for basic control over the kernel.
 *
 * @author Stefan Walter (Fraunhofer IML)
 */
@SuppressWarnings("deprecation")
public class KernelControlCenter extends JFrame implements KernelExtension, EventHandler {

    /**
     * This class's logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger(KernelControlCenter.class);
    /**
     * The key of the bundle string that's always in this frame's title.
     */
    private static final String TITLE_BASE = "KernelControlCenter.titleBase";
    /**
     * This class's resource bundle.
     */
    private static final ResourceBundle BUNDLE = ResourceBundle.getBundle("org/opentcs/kernel/controlcenter/Bundle");
    /**
     * The kernel.
     */
    private final LocalKernel kernel;
    /**
     * The plant model service.
     */
    private final InternalPlantModelService plantModelService;
    /**
     * Where we get application events from.
     */
    private final EventSource eventHub;
    /**
     * The factory providing a ControlCenterInfoHandler
     */
    private final ControlCenterInfoHandlerFactory controlCenterInfoHandlerFactory;
    /**
     * Providers for panels shown in modelling mode.
     */
    private final Collection<Provider<org.opentcs.components.kernel.ControlCenterPanel>> panelProvidersModelling;
    /**
     * Providers for panels shown in operating mode.
     */
    private final Collection<Provider<org.opentcs.components.kernel.ControlCenterPanel>> panelProvidersOperating;
    /**
     * An about dialog.
     */
    private final AboutDialog aboutDialog;
    /**
     * Panels currently active/shown.
     */
    private final Set<org.opentcs.components.kernel.ControlCenterPanel> activePanels = Collections.synchronizedSet(new HashSet<>());
    /**
     * Indicates whether this component is initialized.
     */
    private boolean initialized;
    /**
     * The kernel's current state.
     */
    private Kernel.State kernelState;
    /**
     * The ControlCenterInfoHandler.
     */
    private ControlCenterInfoHandler infoHandler;
    /**
     * The current Model Name.
     */
    private String currentModel = "";

    /**
     * Creates a new instance.
     *
     * @param kernel                          The kernel.
     * @param plantModelService               The plant model service.
     * @param eventSource                     Where this instance receives application events from.
     * @param controlCenterInfoHandlerFactory The factory providing a ControlCenterInfoHandler.
     * @param panelProvidersModelling         Providers for panels in modelling mode.
     * @param panelProvidersOperating         Providers for panels in operating mode.
     */
    @Inject
    public KernelControlCenter(@Nonnull LocalKernel kernel, @Nonnull InternalPlantModelService plantModelService, @ApplicationEventBus EventSource eventSource, @Nonnull ControlCenterInfoHandlerFactory controlCenterInfoHandlerFactory, @Nonnull @ActiveInModellingMode Collection<Provider<org.opentcs.components.kernel.ControlCenterPanel>> panelProvidersModelling, @Nonnull @ActiveInOperatingMode Collection<Provider<org.opentcs.components.kernel.ControlCenterPanel>> panelProvidersOperating) {
        this.kernel = requireNonNull(kernel, "kernel");
        this.plantModelService = requireNonNull(plantModelService, "plantModelService");
        this.eventHub = requireNonNull(eventSource, "eventSource");
        this.controlCenterInfoHandlerFactory = requireNonNull(controlCenterInfoHandlerFactory, "controlCenterInfoHandlerFactory");
        this.panelProvidersModelling = requireNonNull(panelProvidersModelling, "panelProvidersModelling");
        this.panelProvidersOperating = requireNonNull(panelProvidersOperating, "panelProvidersOperating");

        initComponents();
        setIconImages(Icons.getOpenTCSIcons());
        aboutDialog = new AboutDialog(this, false);
        aboutDialog.setAlwaysOnTop(true);
        registerControlCenterInfoHandler();
    }

    @Override
    public boolean isInitialized() {
        return initialized;
    }

    @Override
    public void initialize() {
        if (initialized) {
            LOG.debug("Already initialized.");
            return;
        }

        eventHub.subscribe(this);

        try {
            EventQueue.invokeAndWait(() -> setVisible(true));
        } catch (InterruptedException | InvocationTargetException exc) {
            throw new IllegalStateException("Unexpected exception initializing", exc);
        }

        initialized = true;
    }

    @Override
    public void terminate() {
        if (!initialized) {
            LOG.debug("Not initialized");
            return;
        }

        // Hide the window.
        setVisible(false);
        dispose();

        initialized = false;
    }

    @Override
    public void onEvent(Object event) {
        if (event instanceof KernelStateTransitionEvent) {
            KernelStateTransitionEvent stateEvent = (KernelStateTransitionEvent) event;
            if (!stateEvent.isTransitionFinished()) {
                leavingKernelState(stateEvent.getLeftState());
            } else {
                enteringKernelState(stateEvent.getEnteredState());
            }
        } else if (event instanceof ModelTransitionEvent) {
            ModelTransitionEvent modelEvent = (ModelTransitionEvent) event;
            updateModelName(modelEvent.getNewModelName());
        }
    }

    /**
     * Perfoms some tasks when a state is being leaved.
     *
     * @param oldState The state we're leaving
     */
    private void leavingKernelState(Kernel.State oldState) {
        requireNonNull(oldState, "oldState");

        removePanels(activePanels);
        activePanels.clear();
    }

    /**
     * Notifies this control center that the kernel has entered a different state.
     *
     * @param newState
     */
    private void enteringKernelState(Kernel.State newState) {
        requireNonNull(newState, "newState");

        switch (newState) {
            case OPERATING:
                addPanels(panelProvidersOperating);

                menuButtonOperating.setSelected(true);
                newModelMenuItem.setEnabled(false);
                break;
            case MODELLING:
                addPanels(panelProvidersModelling);

                menuButtonModelling.setSelected(true);
                newModelMenuItem.setEnabled(true);
                break;
            default:
                // Do nada.
        }
        // Remember the new state.
        kernelState = newState;
        // Updating the window title
        setWindowTitle();
    }

    @SuppressWarnings("deprecation")
    private void addPanels(Collection<Provider<org.opentcs.components.kernel.ControlCenterPanel>> providers) {
        for (Provider<org.opentcs.components.kernel.ControlCenterPanel> provider : providers) {
            SwingUtilities.invokeLater(() -> addPanel(provider.get()));
        }
    }

    @SuppressWarnings("deprecation")
    private void addPanel(org.opentcs.components.kernel.ControlCenterPanel panel) {
        panel.initialize();
        activePanels.add(panel);
        tabbedPaneMain.add(panel.getTitle(), panel);
    }

    @SuppressWarnings("deprecation")
    private void removePanels(Collection<org.opentcs.components.kernel.ControlCenterPanel> panels) {
        List<org.opentcs.components.kernel.ControlCenterPanel> panelsCopy = new ArrayList<>(panels);
        SwingUtilities.invokeLater(() -> {
            for (org.opentcs.components.kernel.ControlCenterPanel panel : panelsCopy) {
                tabbedPaneMain.remove(panel);
                panel.terminate();
            }
        });
    }

    /**
     * Updates the model name to the current one.
     *
     * @param newModelName The new/updated model name.
     */
    private void updateModelName(String newModelName) {
        this.currentModel = newModelName;
        setWindowTitle();
    }

    /**
     * Shows a message dialog to confirm the user wants to shut down the kernel.
     *
     * @return true for yes, false otherwise.
     */
    private boolean confirmExit() {
        int n = JOptionPane.showConfirmDialog(this, BUNDLE.getString("EXIT_MESSAGE"), BUNDLE.getString("EXIT_TITLE"), JOptionPane.YES_NO_OPTION);
        return n == JOptionPane.YES_OPTION;
    }

    /**
     * Adds the ControlCenterInfoHandler to the root logger.
     */
    private void registerControlCenterInfoHandler() {
        infoHandler = controlCenterInfoHandlerFactory.createHandler(loggingTextArea);
        eventHub.subscribe(infoHandler);
    }

    /**
     * Updates the window's title.
     */
    private void setWindowTitle() {
        if ("".equals(currentModel)) {
            setTitle(BUNDLE.getString(TITLE_BASE) + " - " + kernelStateString(kernelState));
        } else {
            setTitle(BUNDLE.getString(TITLE_BASE) + " - " + kernelStateString(kernelState) + " - \"" + currentModel + "\"");
        }
    }

    /**
     * Returns a user friendly string for the given kernel state.
     *
     * @param state The kernel state to be converted.
     * @return A user friendly string for the given kernel state.
     */
    private String kernelStateString(Kernel.State state) {
        return BUNDLE.getString("KernelControlCenter.kernelState." + state.name());
    }

    // CHECKSTYLE:OFF
    // Generated code starts here.

    /**
     * This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        buttonGroupKernelMode = new ButtonGroup();
        tabbedPaneMain = new JTabbedPane();
        loggingPanel = new JPanel();
        loggingScrollPane = new JScrollPane();
        loggingTextArea = new JTextArea();
        loggingPropertyPanel = new JPanel();
        autoScrollCheckBox = new JCheckBox();
        menuBarMain = new JMenuBar();
        menuKernel = new JMenu();
        menuKernelMode = new JMenu();
        menuButtonModelling = new JRadioButtonMenuItem();
        menuButtonOperating = new JRadioButtonMenuItem();
        newModelMenuItem = new JMenuItem();
        jSeparator1 = new JPopupMenu.Separator();
        menuButtonExit = new JMenuItem();
        menuHelp = new JMenu();
        menuAbout = new JMenuItem();

        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle(BUNDLE.getString(TITLE_BASE));
        setMinimumSize(new java.awt.Dimension(1200, 750));
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        loggingPanel.setLayout(new BorderLayout());

        loggingTextArea.setEditable(false);
        loggingScrollPane.setViewportView(loggingTextArea);

        loggingPanel.add(loggingScrollPane, BorderLayout.CENTER);

        loggingPropertyPanel.setLayout(new GridBagLayout());

        autoScrollCheckBox.setSelected(true);
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/opentcs/kernel/controlcenter/Bundle"); // NOI18N
        autoScrollCheckBox.setText(bundle.getString("AutoScroll")); // NOI18N
        autoScrollCheckBox.addActionListener(evt -> autoScrollCheckBoxActionPerformed(evt));
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        loggingPropertyPanel.add(autoScrollCheckBox, gridBagConstraints);

        loggingPanel.add(loggingPropertyPanel, java.awt.BorderLayout.PAGE_START);

        tabbedPaneMain.addTab(bundle.getString("Logging"), loggingPanel); // NOI18N

        getContentPane().add(tabbedPaneMain, java.awt.BorderLayout.CENTER);

        menuKernel.setText(BUNDLE.getString(TITLE_BASE));

        menuKernelMode.setText(bundle.getString("Mode")); // NOI18N

        buttonGroupKernelMode.add(menuButtonModelling);
        menuButtonModelling.setSelected(true);
        menuButtonModelling.setText(bundle.getString("KernelModeModelling")); // NOI18N
        menuButtonModelling.addActionListener(evt -> menuButtonModellingActionPerformed(evt));
        menuKernelMode.add(menuButtonModelling);

        buttonGroupKernelMode.add(menuButtonOperating);
        menuButtonOperating.setText(bundle.getString("KernelModeOperating")); // NOI18N
        menuButtonOperating.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuButtonOperatingActionPerformed(evt);
            }
        });
        menuKernelMode.add(menuButtonOperating);

        menuKernel.add(menuKernelMode);

        newModelMenuItem.setText(bundle.getString("NewModel")); // NOI18N
        newModelMenuItem.addActionListener(evt -> newModelMenuItemActionPerformed(evt));
        menuKernel.add(newModelMenuItem);
        menuKernel.add(jSeparator1);

        menuButtonExit.setText(bundle.getString("Exit")); // NOI18N
        menuButtonExit.addActionListener(evt -> menuButtonExitActionPerformed(evt));
        menuKernel.add(menuButtonExit);

        menuBarMain.add(menuKernel);

        menuHelp.setText(bundle.getString("Help")); // NOI18N

        menuAbout.setText(bundle.getString("AboutOpenTCS")); // NOI18N
        menuAbout.addActionListener(evt -> menuAboutActionPerformed(evt));
        menuHelp.add(menuAbout);

        menuBarMain.add(menuHelp);

        setJMenuBar(menuBarMain);

        setSize(new java.awt.Dimension(1208, 782));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void menuButtonExitActionPerformed(ActionEvent evt) {//GEN-FIRST:event_menuButtonExitActionPerformed
        if (confirmExit()) {
            kernel.setState(Kernel.State.SHUTDOWN);
        }
    }//GEN-LAST:event_menuButtonExitActionPerformed

    private void autoScrollCheckBoxActionPerformed(ActionEvent evt) {//GEN-FIRST:event_autoScrollCheckBoxActionPerformed
        if (autoScrollCheckBox.isSelected()) {
            infoHandler.setAutoScroll(true);
        } else {
            infoHandler.setAutoScroll(false);
        }
    }//GEN-LAST:event_autoScrollCheckBoxActionPerformed

    private void menuButtonModellingActionPerformed(ActionEvent evt) {//GEN-FIRST:event_menuButtonModellingActionPerformed
        kernel.setState(Kernel.State.MODELLING);
    }//GEN-LAST:event_menuButtonModellingActionPerformed

    private void menuButtonOperatingActionPerformed(ActionEvent evt) {//GEN-FIRST:event_menuButtonOperatingActionPerformed
        kernel.setState(Kernel.State.OPERATING);
    }//GEN-LAST:event_menuButtonOperatingActionPerformed

    private void menuAboutActionPerformed(ActionEvent evt) {//GEN-FIRST:event_menuAboutActionPerformed
        aboutDialog.setLocationRelativeTo(null);
        aboutDialog.setVisible(true);
    }//GEN-LAST:event_menuAboutActionPerformed

    private void formWindowClosing(WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        if (confirmExit()) {
            kernel.setState(Kernel.State.SHUTDOWN);
            dispose();
        }
    }//GEN-LAST:event_formWindowClosing

    private void newModelMenuItemActionPerformed(ActionEvent evt) {//GEN-FIRST:event_newModelMenuItemActionPerformed
        if (kernelState == Kernel.State.MODELLING) {
            String message = BUNDLE.getString("ConfirmNewModelMsg");
            String dialogTitle = BUNDLE.getString("ConfirmNewModelTitle");
            // display the JOptionPane showConfirmDialog
            int reply = JOptionPane.showConfirmDialog(null, message, dialogTitle, JOptionPane.YES_NO_OPTION);
            if (reply == JOptionPane.YES_OPTION) {
                plantModelService.createPlantModel(new PlantModelCreationTO(Kernel.DEFAULT_MODEL_NAME));
                setWindowTitle();
            }
        }
    }//GEN-LAST:event_newModelMenuItemActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private JCheckBox autoScrollCheckBox;
    private ButtonGroup buttonGroupKernelMode;
    private JPopupMenu.Separator jSeparator1;
    private JPanel loggingPanel;
    private JPanel loggingPropertyPanel;
    private JScrollPane loggingScrollPane;
    private JTextArea loggingTextArea;
    private JMenuItem menuAbout;
    private JMenuBar menuBarMain;
    private JMenuItem menuButtonExit;
    private JRadioButtonMenuItem menuButtonModelling;
    private JRadioButtonMenuItem menuButtonOperating;
    private JMenu menuHelp;
    private JMenu menuKernel;
    private JMenu menuKernelMode;
    private JMenuItem newModelMenuItem;
    private JTabbedPane tabbedPaneMain;
    // End of variables declaration//GEN-END:variables
    // CHECKSTYLE:ON
}
