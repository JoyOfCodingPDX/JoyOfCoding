package edu.pdx.cs410G.gui;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.*;
import javax.swing.*;

/**
 * This program demonstrates {@link GridLayout}.
 */
public class GridLayoutExample  {

  public static void main(String[] args) {
    JPanel p = new JPanel();
    int rows = 4;
    int columns = 3;
    p.setLayout(new GridLayout(rows, columns));

    for (int i = 0; i < rows; i++) {
      for (int j = 0; j < columns; j++) {
        p.add(new JLabel("(" + i + ", " + j + ")"));
      }
    }

    JFrame f = new JFrame("GridLayout Example");
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
