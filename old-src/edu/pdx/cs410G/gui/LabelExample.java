package edu.pdx.cs410G.gui;

import javax.swing.*;

/**
 * This class demonstrates a Swing {@link JLabel} widget
 */
public class LabelExample extends JPanel {

  /**
   * Create some {@link JLabel labels} and adds them to this panel
   */
  public LabelExample() {
    JLabel l1 = new JLabel("A label");
    JLabel l2 = new JLabel("Left justified", JLabel.LEFT);
    JLabel l3 = new JLabel("Right", JLabel.RIGHT);
    JLabel l4 = new JLabel("Centered");
    l4.setAlignmentX(JLabel.CENTER);

    this.setLayout(new java.awt.GridLayout(0, 1));

    this.add(l1);
    this.add(l2);
    this.add(l3);
    this.add(l4);
  }

  /**
   * Create a new {@link JFrame JFrame} and add a LabelExample
   * to it 
   */
  public static void main(String[] args) {
    JFrame frame = new JFrame("JLabel example");
    frame.add(new LabelExample());
    frame.pack();
    frame.setVisible(true);
  }

}
