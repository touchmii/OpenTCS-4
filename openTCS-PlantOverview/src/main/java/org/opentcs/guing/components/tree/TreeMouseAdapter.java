/*
 * openTCS copyright information:
 * Copyright (c) 2013 Fraunhofer IML
 *
 * This program is free software and subject to the MIT license. (For details,
 * see the licensing information (LICENSE.txt) you should have received with
 * this copy of the software.)
 */
package org.opentcs.guing.components.tree;

import org.opentcs.guing.components.tree.elements.AbstractUserObject;
import org.opentcs.guing.components.tree.elements.UserObject;
import org.opentcs.guing.model.ModelComponent;
import org.opentcs.guing.util.I18nPlantOverview;
import org.opentcs.guing.util.ResourceBundleUtil;

import javax.inject.Inject;
import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Enumeration;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * A mouse adapter for the <code>TreeView</code> for components and blocks.
 *
 * @author Philipp Seifert (Philipp.Seifert@iml.fraunhofer.de)
 */
public class TreeMouseAdapter
    extends MouseAdapter {

  /**
   * The TreeView this mouse adapter belongs to.
   */
  protected final TreeView treeView;

  /**
   * Creates a new instance.
   *
   * @param treeView The tree view
   */
  @Inject
  public TreeMouseAdapter(TreeView treeView) {
    this.treeView = Objects.requireNonNull(treeView);
  }

  @Override
  public void mousePressed(MouseEvent e) {
    JTree objectTree = treeView.getTree();
    TreePath selPath = objectTree.getPathForLocation(e.getX(), e.getY());
    if (selPath == null) {
      if (e.getButton() == MouseEvent.BUTTON3) {
        showPopupMenu(e.getX(), e.getY());
      }
    }
    else {
      UserObject userObject = getUserObject(selPath);

      if (e.getButton() == MouseEvent.BUTTON3) {
        evaluateRightClick(e, userObject, treeView.getSelectedItems());
      }
      else if (e.getButton() == MouseEvent.BUTTON1) {

        //This Method tells the OpenTCSView what elements are currently selected
        ((AbstractUserObject) userObject).selectMultipleObjects();

        if (e.getClickCount() == 2) {
          userObject.doubleClicked();
        }

      }

    }

  }

  /**
   * Evaluates a right click the user made on an user object.
   *
   * @param e The MouseEvent.
   * @param userObject The user object that was right clicked.
   * @param currentSelection The user objects that were selected.
   */
  protected void evaluateRightClick(MouseEvent e,
                                    UserObject userObject,
                                    Set<UserObject> currentSelection) {

    if (!currentSelection.contains(userObject)) {
      currentSelection.clear();
      currentSelection.add(userObject);
    }

    Set<ModelComponent> dataObjects = currentSelection.stream()
        .map(userObj -> userObj.getModelComponent())
        .collect(Collectors.toSet());

    treeView.selectItems(dataObjects);

    userObject.rightClicked(treeView.getTree(), e.getX(), e.getY());
  }

  private UserObject getUserObject(TreePath path) {
    return (UserObject) ((DefaultMutableTreeNode) path.getLastPathComponent()).getUserObject();
  }

  /**
   * Shows a popup menu with options for the JTree.
   *
   * @param x x coordinate.
   * @param y y coordinate.
   */
  private void showPopupMenu(int x, int y) {
    JPopupMenu menu = new JPopupMenu();
    final JTree objectTree = treeView.getTree();
    ResourceBundleUtil labels = ResourceBundleUtil.getBundle(I18nPlantOverview.TREEVIEW_PATH);

    JMenuItem item = new JMenuItem(labels.getString("treeMouseAdapter.popupMenuItem_expandAllFolders.text"));
    item.setToolTipText(labels.getString("treeMouseAdapter.popupMenuItem_expandAllFolders.tooltipText"));

    item.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent event) {
        for (int i = 0; i < objectTree.getRowCount(); i++) {
          objectTree.expandRow(i);
        }
      }
    });

    menu.add(item);

    item = new JMenuItem(labels.getString("treeMouseAdapter.popupMenuItem_closeAllFolders.text"));
    item.setToolTipText(labels.getString("treeMouseAdapter.popupMenuItem_closeAllFolders.tooltipText"));

    item.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent event) {
        for (int i = 0; i < objectTree.getRowCount(); i++) {
          objectTree.collapseRow(i);
        }
      }
    });

    menu.add(item);

    menu.addSeparator();

    item = new JMenuItem(labels.getString("treeMouseAdapter.popupMenuItem_sortAllItems.text"));
    item.setToolTipText(labels.getString("treeMouseAdapter.popupMenuItem_sortAllItems.tooltipText"));

    item.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent event) {
        @SuppressWarnings("unchecked")
        Enumeration<TreeNode> eTreeNodes = (Enumeration<TreeNode>) ((TreeNode) objectTree.getModel().getRoot()).children();

        while (eTreeNodes.hasMoreElements()) {
          TreeNode node = eTreeNodes.nextElement();
          treeView.sortItems(node);
        }
      }
    });

    menu.add(item);

    menu.show(objectTree, x, y);
  }
}
