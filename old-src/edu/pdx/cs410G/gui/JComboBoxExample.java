package edu.pdx.cs410G.gui;

import javax.swing.*;

/**
 * This program demonstrates Swing's {@link JComboBox} widget.
 */
public class JComboBoxExample extends JPanel {

  public JComboBoxExample() {
    String[] flavors = { "Vanilla", "Strawberry", "Chocolate", 
                         "Fudge Swirl", "Pistachio" };
    JComboBox combo = new JComboBox(flavors);
    combo.addItem("Rocky Road");
    combo.setEditable(true);
    combo.setMaximumRowCount(3);

    this.add(combo);
  }

  public static void main(String[] args) {
    JFrame frame = new JFrame("JComboBox example");
    JPanel panel = new JComboBoxExample();
    frame.getContentPane().add(panel);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.pack();
    frame.setVisible(true);
  }
 

}
