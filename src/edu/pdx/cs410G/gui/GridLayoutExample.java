package edu.pdx.cs399J.gui;

import java.awt.*;
import java.awt.event.*;

/**
 * This program demonstrates {@link GridLayout}.
 */
public class GridLayoutExample  {

  public static void main(String[] args) {
    Panel p = new Panel();
    int rows = 4;
    int columns = 3;
    p.setLayout(new GridLayout(rows, columns));

    for (int i = 0; i < rows; i++) {
      for (int j = 0; j < columns; j++) {
        p.add(new Label("(" + i + ", " + j + ")"));
      }
    }

    Frame f = new Frame("GridLayout Example");
    f.setLayout(new BorderLayout());
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
