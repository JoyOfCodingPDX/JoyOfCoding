package edu.pdx.cs410G.gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.*;
import javax.swing.*;

/**
 * This program demonstrates the interaction between a {@link
 * FlowLayout} and a {@link BorderLayout}.
 */
public class FlowLayoutInCenter  {

  public static void main(String[] args) {
    JPanel p = new JPanel();
    p.add(new JLabel("One"));
    p.add(new JLabel("Two"));
    p.add(new JLabel("Three"));
    p.add(new JLabel("Four"));
    p.add(new JLabel("Five"));
    p.add(new JLabel("Six"));

    JFrame f = new JFrame("FlowLayout in the CENTER");
    f.setLayout(new BorderLayout());
    f.add(new JLabel("North"), BorderLayout.NORTH);
    f.add(new JLabel("South"), BorderLayout.SOUTH);
    f.add(new JButton("East"), BorderLayout.EAST);
    f.add(new JButton("West"), BorderLayout.WEST);
    f.add(p, BorderLayout.CENTER);

    f.addWindowListener(new WindowAdapter() {
        public void windowClosing(WindowEvent e) {
          System.exit(1);
        }
      });

    f.pack();
    f.setVisible(true);
  }

}
