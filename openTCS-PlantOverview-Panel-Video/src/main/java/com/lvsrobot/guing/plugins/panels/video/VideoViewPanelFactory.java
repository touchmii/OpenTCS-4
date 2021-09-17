/**
 * Copyright (c) The openTCS Authors.
 *
 * This program is free software and subject to the MIT license. (For details,
 * see the licensing information (LICENSE.txt) you should have received with
 * this copy of the software.)
 */
package com.lvsrobot.guing.plugins.panels.video;

import static java.util.Objects.requireNonNull;
import java.util.ResourceBundle;
import javax.inject.Inject;
import javax.inject.Provider;
import org.opentcs.access.Kernel;
import org.opentcs.access.SharedKernelServicePortalProvider;
import org.opentcs.components.plantoverview.PluggablePanel;
import org.opentcs.components.plantoverview.PluggablePanelFactory;
import static com.lvsrobot.guing.plugins.panels.video.I18nPlantOverviewPanelVideoView.BUNDLE_PATH;

/**
 * Provides a {@link ResourceAllocationPanel} for the plant overview if the kernel is in operating
 * state.
 *
 * @author Mats Wilhelm (Fraunhofer IML)
 * @author Mustafa Yalciner (Fraunhofer IML)
 */
public class VideoViewPanelFactory
    implements PluggablePanelFactory {

  /**
   * This classe's bundle.
   */
  private final ResourceBundle bundle = ResourceBundle.getBundle(BUNDLE_PATH);
  /**
   * The provider for the portal.
   */
  private final SharedKernelServicePortalProvider portalProvider;

  /**
   * The provider for the panel this factory wants to create.
   */
  private final Provider<VideoViewPanel> panelProvider;

  /**
   * Creates a new instance.
   *
   * @param portalProvider the provider for access to the kernel
   * @param panelProvider the provider for the panel
   */
  @Inject
  public VideoViewPanelFactory(SharedKernelServicePortalProvider portalProvider,
                               Provider<VideoViewPanel> panelProvider) {
    this.portalProvider = requireNonNull(portalProvider, "portalProvider");
    this.panelProvider = requireNonNull(panelProvider, "panelProvider");
  }

  @Override
  public boolean providesPanel(Kernel.State state) {
    return (state == Kernel.State.OPERATING);

  }

  @Override
  public String getPanelDescription() {
    return bundle.getString("resourceAllocationPanelFactory.panelDescription");
  }

  @Override
  public PluggablePanel createPanel(Kernel.State state) {
    if (state != Kernel.State.OPERATING) {
      return null;
    }
    if (portalProvider == null || !portalProvider.portalShared()) {
      return null;
    }
    return panelProvider.get();
  }

}
