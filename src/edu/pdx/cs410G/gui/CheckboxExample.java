package edu.pdx.cs410G.gui;

import javax.swing.*;

/**
 * This class demonstrates Swing's {@link JCheckBox}
 */
public class CheckboxExample extends JPanel {

  /**
   * Create some {@link JCheckBox checkboxes} and add them to
   * this panel
   */
  public CheckboxExample() {
    JCheckBox cb1 = new JCheckBox("No");
    JCheckBox cb2 = new JCheckBox("Yes", true);
    JCheckBox cb3 = new JCheckBox("Maybe");
    cb3.setSelected(true);
    this.add(cb1);
    this.add(cb2);
    this.add(cb3);
  }

  /**
   * Create a new Frame and add a CheckboxExample to it
   */
  public static void main(String[] args) {
    JFrame frame = new JFrame("Checkbox example");
    frame.add(new CheckboxExample());
    frame.pack();
    frame.setVisible(true);
  }

}
