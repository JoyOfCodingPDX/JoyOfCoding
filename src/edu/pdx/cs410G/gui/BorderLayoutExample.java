package edu.pdx.cs410G.gui;

import java.awt.BorderLayout;
import java.awt.event.*;
import javax.swing.*;

/**
 * This program demonstrates the {@link BorderLayout} layout manager.
 */
public class BorderLayoutExample  {

  public static void main(String[] args) {
    JFrame f = new JFrame("BorderLayout Example");
    f.setLayout(new BorderLayout());
    f.add(new JLabel("North"), BorderLayout.NORTH);
    f.add(new JLabel("South"), BorderLayout.SOUTH);
    f.add(new JButton("East"), BorderLayout.EAST);
    f.add(new JButton("West"), BorderLayout.WEST);
    f.add(new JLabel("Center"), BorderLayout.CENTER);

    f.addWindowListener(new WindowAdapter() {
        public void windowClosing(WindowEvent e) {
          System.exit(1);
        }
      });

    f.pack();
    f.setVisible(true);
  }

}
