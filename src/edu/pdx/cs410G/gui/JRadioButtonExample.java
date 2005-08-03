package edu.pdx.cs399J.gui;

import javax.swing.*;

/**
 * This class demonstrates using a {@link ButtonGroup} to implement
 * "radio buttons".
 *
 * @author David Whitlock
 * @since Summer 2005
 */
public class JRadioButtonExample extends JPanel {

  /**
   * Create some {@link JRadioButton radio buttons} in a {@link
   * ButtonGroup group} and add them to this panel.
   */
  public JRadioButtonExample() {
    JRadioButton rb1 = new JRadioButton("UNIX", true);
    JRadioButton rb2 = new JRadioButton("Windows", false);
    JRadioButton rb3 = new JRadioButton("Macintosh");
    rb3.setSelected(false);

    ButtonGroup group = new ButtonGroup();
    group.add(rb1);
    group.add(rb2);
    group.add(rb3);

    this.add(rb1);
    this.add(rb2);
    this.add(rb3);
  }

  /**
   * Create a new Frame and add a JRadioButtonExample to it
   */
  public static void main(String[] args) {
    JFrame frame = new JFrame("JRadioButton example");
    frame.add(new JRadioButtonExample());
    frame.pack();
    frame.setVisible(true);
  }

}
