package edu.pdx.cs410G.gui;

import java.awt.FlowLayout;
import java.awt.event.*;
import javax.swing.*;

/**
 * This program demonstrates the {@link FlowLayout} layout manager
 */
public class FlowLayoutExample  {

  public static void main(String[] args) {
    JFrame f = new JFrame("FlowLayout Example");
    f.setLayout(new FlowLayout());
    f.add(new JLabel("One"));
    f.add(new JLabel("Two"));
    f.add(new JLabel("Three"));
    f.add(new JLabel("Four"));
    f.add(new JLabel("Five"));
    f.add(new JLabel("Six"));

    f.addWindowListener(new WindowAdapter() {
        public void windowClosing(WindowEvent e) {
          System.exit(1);
        }
      });

    f.pack();
    f.setVisible(true);
  }

}
