package edu.pdx.cs410G.gui;

import java.awt.FlowLayout;
import java.awt.event.*;
import javax.swing.*;

/**
 * This program demonstrates what happens when you have one {@link
 * FlowLayout} nested inside another.
 */
public class NestedFlowLayouts  {

  public static void main(String[] args) {
    JFrame f = new JFrame("Nested FlowLayouts");
    f.setLayout(new FlowLayout());

    JPanel p = new JPanel();
    p.add(new JLabel("One"));
    p.add(new JLabel("Two"));
    p.add(new JLabel("Three"));
    p.add(new JLabel("Four"));
    p.add(new JLabel("Five"));
    p.add(new JLabel("Six"));

    f.add(p);

    f.addWindowListener(new WindowAdapter() {
        public void windowClosing(WindowEvent e) {
          System.exit(1);
        }
      });

    f.pack();
    f.setVisible(true);
  }

}
