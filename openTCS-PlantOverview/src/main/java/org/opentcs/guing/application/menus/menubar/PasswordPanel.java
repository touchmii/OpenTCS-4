package org.opentcs.guing.application.menus.menubar;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * Class that creates a panel with a password field. Extension of Adamski's class
 *
 * @author adamski https://stackoverflow.com/users/127479/adamski
 * @author agi-hammerthief https://stackoverflow.com/users/2225787/agi-hammerthief
 * @see https://stackoverflow.com/a/8881370/2225787
 */
public class PasswordPanel extends JPanel {

  private final JPasswordField JFieldPass;
  private JLabel JLblPass;
  private boolean gainedFocusBefore;

  /**
   * "Hook" method that causes the JPasswordField to request focus when method is
   * first  called.
   */
  public void gainedFocus () {
    if (!gainedFocusBefore) {
      gainedFocusBefore = true;
      JFieldPass.requestFocusInWindow();
    }
  }

  public PasswordPanel(int length) {
    super(new FlowLayout());
    gainedFocusBefore = false;
    JFieldPass = new JPasswordField(length);
    Dimension d = new Dimension();
    d.setSize(30, 22);
    JFieldPass.setMinimumSize(d);
    JFieldPass.setColumns(10);
    JLblPass = new JLabel("\u5BC6\u7801: ");
    add(JLblPass);
    add(JFieldPass);
  }

  public PasswordPanel() {
    super(new FlowLayout());
    gainedFocusBefore = false;
    JFieldPass = new JPasswordField();
    Dimension d = new Dimension();
    d.setSize(30, 22);
    JFieldPass.setMinimumSize(d);
    JFieldPass.setColumns(10);
    JLblPass = new JLabel("\u5BC6\u7801: ");
    add(JLblPass);
    add(JFieldPass);
  }

  public char[] getPassword() {
    return JFieldPass.getPassword();
  }

  public String getPasswordString() {
    StringBuilder passBuilder = new StringBuilder();

    char[] pwd = this.getPassword();
    if (pwd.length > 0) {
      for (char c : pwd) {
        passBuilder.append(c);
      }
    }

    return passBuilder.toString();
  }

  private static String displayDialog (
    Component parent, final PasswordPanel panel, String title
  ) {
    String password = null;
    /* For some reason, using `JOptionPane(panel, JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE)`
    does not give the same results as setting values after creation, which is weird */
    JOptionPane op = new JOptionPane(panel);
    op.setMessageType(JOptionPane.QUESTION_MESSAGE);
    op.setOptionType(JOptionPane.OK_CANCEL_OPTION);
    JDialog dlg = op.createDialog(parent, title);
    // Ensure the JPasswordField is able to request focus when the dialog is first shown.
    dlg.addWindowFocusListener (new WindowAdapter () {
      @Override
      public void windowGainedFocus (WindowEvent e) {
        panel.gainedFocus ();
      }
    });
    dlg.setDefaultCloseOperation (JOptionPane.OK_OPTION); // necessary?

    dlg.setVisible (true);

    Object val = op.getValue ();
    if (null != val && val.equals (JOptionPane.OK_OPTION)) {
      password = panel.getPasswordString();
    }

    return password;
  }

  public static String showDialog (Component parent, String title) {
    final PasswordPanel pPnl = new PasswordPanel();
    return displayDialog(parent, pPnl, title);
  }

  public static String showDialog (
    Component parent, String title, int passwordLength
  ) {
    final PasswordPanel pPnl = new PasswordPanel(passwordLength);

    return displayDialog (parent, pPnl, title);
  }

  public static String showDialog (String title) {
    return showDialog(null, title);
  }

  public static String showDialog (String title, int passwordLength) {
    return showDialog(null, title, passwordLength);
  }

  /**
   * Show a test dialog
   */
//  public static void main(String[] args) {
//    String test = PasswordPanel.showDialog ("Enter Your Password");
//    System.out.println ("Entered Password: " + test);
//    System.exit(0);
//  }

}