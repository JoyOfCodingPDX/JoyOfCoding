package edu.pdx.cs410J.gui;

import java.awt.*;
import java.awt.event.*;

/**
 * This program demonstrates the interaction between a {@link
 * FlowLayout} and a {@link BorderLayout}.
 */
public class FlowLayoutInCenter  {

  public static void main(String[] args) {
    Panel p = new Panel();
    p.add(new Label("One"));
    p.add(new Label("Two"));
    p.add(new Label("Three"));
    p.add(new Label("Four"));
    p.add(new Label("Five"));
    p.add(new Label("Six"));

    Frame f = new Frame("FlowLayout in the CENTER");
    f.setLayout(new BorderLayout());
    f.add(new Label("North"), BorderLayout.NORTH);
    f.add(new Label("South"), BorderLayout.SOUTH);
    f.add(new Button("East"), BorderLayout.EAST);
    f.add(new Button("West"), BorderLayout.WEST);
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
