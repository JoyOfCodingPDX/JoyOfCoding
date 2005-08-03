package edu.pdx.cs399J.gui;

import java.awt.event.*;
import javax.swing.*;

/**
 * This class demonstrates handling an action event
 */
public class ActionEventExample extends JPanel 
  implements ActionListener {

  private JButton button;
  private JLabel label;

  /**
   * Create a {@link java.awt.JButton button}, register this as its
   * {@link java.awt.event.ActionListener ActionListener} and add it
   * to this panel
   */
  public ActionEventExample() {
    this.button = new JButton("Click me");
    this.button.addActionListener(this);
    this.label = new JLabel("Not clicked");
    this.add(this.button);
    this.add(this.label);
  }

  /**
   * Handles ActionEvents
   */
  public void actionPerformed(ActionEvent e) {
    if (e.getSource() == this.button) {
      this.label.setText("Clicked");
    }
  }

  /**
   * Create a new {@link java.awt.JFrame JFrame} and add a
   * ActionEventExample to it
   */
  public static void main(String[] args) {
    JFrame frame = new JFrame("ActionEvent example");
    frame.add(new ActionEventExample());
    frame.pack();
    frame.setVisible(true);
  }

}
