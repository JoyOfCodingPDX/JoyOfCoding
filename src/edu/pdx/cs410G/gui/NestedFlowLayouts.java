package edu.pdx.cs399J.gui;

import java.awt.*;
import java.awt.event.*;

/**
 * This program demonstrates what happens when you have one {@link
 * FlowLayout} nested inside another.
 */
public class NestedFlowLayouts  {

  public static void main(String[] args) {
    Frame f = new Frame("Nested FlowLayouts");
    f.setLayout(new FlowLayout());

    Panel p = new Panel();
    p.add(new Label("One"));
    p.add(new Label("Two"));
    p.add(new Label("Three"));
    p.add(new Label("Four"));
    p.add(new Label("Five"));
    p.add(new Label("Six"));

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
