package edu.pdx.cs410G.swing;

import javax.swing.*;

/**
 * This program demonstrates Swing's support for {@link JToolTip tool
 * tips}.
 */
public class JToolTipExample extends JPanel {

  public JToolTipExample() {
    JButton button = new JButton("Mitchell");
    button.setToolTipText("Push the button, Frank");
    this.add(button);

    JTextField name = new JTextField(8);
    name.setToolTipText("Your name here");
    this.add(name);
  }

  public static void main(String[] args) {
    JFrame frame = new JFrame("JToolTip example");
    JPanel panel = new JToolTipExample();
    frame.getContentPane().add(panel);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.pack();
    frame.setVisible(true);
  }
  
}
