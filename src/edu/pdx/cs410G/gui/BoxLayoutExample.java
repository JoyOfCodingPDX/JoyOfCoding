package edu.pdx.cs399J.gui;

import javax.swing.*;

/**
 * This program demonstrates Swing's {@link BoxLayout} layout
 * manager.
 */
public class BoxLayoutExample extends JPanel {

  public BoxLayoutExample() {
    this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

    this.add(new JButton("One"));
    this.add(Box.createVerticalStrut(20));
    this.add(new JLabel("After strut"));
    this.add(Box.createVerticalGlue());
    this.add(new JButton("After glue"));
  }

  public static void main(String[] args) {
    JFrame frame = new JFrame("BoxLayout example");
    JPanel panel = new BoxLayoutExample();
    frame.getContentPane().add(panel);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.pack();
    frame.setVisible(true);
  }  

}
