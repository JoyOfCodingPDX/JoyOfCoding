package edu.pdx.cs410G.awt;

import java.awt.*;
import java.awt.event.*;

/**
 * This program demonstrates the {@link BorderLayout} layout manager.
 */
public class BorderLayoutExample  {

  public static void main(String[] args) {
    Frame f = new Frame("BorderLayout Example");
    f.setLayout(new BorderLayout());
    f.add(new Label("North"), BorderLayout.NORTH);
    f.add(new Label("South"), BorderLayout.SOUTH);
    f.add(new Button("East"), BorderLayout.EAST);
    f.add(new Button("West"), BorderLayout.WEST);
    f.add(new Label("Center"), BorderLayout.CENTER);

    f.addWindowListener(new WindowAdapter() {
        public void windowClosing(WindowEvent e) {
          System.exit(1);
        }
      });

    f.pack();
    f.setVisible(true);
  }

}
