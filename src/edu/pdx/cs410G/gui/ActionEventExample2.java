package edu.pdx.cs399J.gui;

import java.awt.event.*;
import javax.swing.*;

/**
 * This class demonstrates handling an action event using an
 * anonymous inner class
 */
public class ActionEventExample2 extends JPanel {

  /**
   * Create a {@link JButton button}, register this as its {@link
   * ActionListener} and add it to this panel
   */
  public ActionEventExample2() {
    final JLabel label = new JLabel("Not clicked");
    JButton button = new JButton("Click me");
    button.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          label.setText("Clicked");
        }
      });

    this.add(button);
    this.add(label);
  }

  /**
   * Create a new {@link java.awt.Frame Frame} and add a ActionEventExample
   * to it 
   */
  public static void main(String[] args) {
    JFrame frame = new JFrame("ActionEvent example with inner class");
    frame.add(new ActionEventExample2());
    frame.pack();
    frame.setVisible(true);
  }

}
