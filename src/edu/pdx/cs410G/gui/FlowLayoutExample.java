package edu.pdx.cs410J.gui;

import java.awt.*;
import java.awt.event.*;

/**
 * This program demonstrates the {@link FlowLayout} layout manager
 */
public class FlowLayoutExample  {

  public static void main(String[] args) {
    Frame f = new Frame("FlowLayout Example");
    f.setLayout(new FlowLayout());
    f.add(new Label("One"));
    f.add(new Label("Two"));
    f.add(new Label("Three"));
    f.add(new Label("Four"));
    f.add(new Label("Five"));
    f.add(new Label("Six"));

    f.addWindowListener(new WindowAdapter() {
        public void windowClosing(WindowEvent e) {
          System.exit(1);
        }
      });

    f.pack();
    f.setVisible(true);
  }

}
