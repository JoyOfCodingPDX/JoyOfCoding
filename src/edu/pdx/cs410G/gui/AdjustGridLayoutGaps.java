package edu.pdx.cs399J.gui;

import java.awt.*;
import java.awt.event.*;

/**
 * This program demonstrates the effects of adjusting the
 * <code>hgap</code> and <code>vgap</code> values of a {@link
 * GridLayout}.
 */
public class AdjustGridLayoutGaps  {

  public static void main(String[] args) {
    int gap = -1;
    
    if (args.length > 0) {
      gap = Integer.parseInt(args[0]);
    }

    final Panel grid = new Panel();
    int rows = 4;
    int columns = 3;
    GridLayout layout;

    if (gap >= 0) {
      layout = new GridLayout(rows, columns, gap, gap);

    } else {
      layout= new GridLayout(rows, columns);
    }
    grid.setLayout(layout);

    for (int i = 0; i < rows; i++) {
      for (int j = 0; j < columns; j++) {
        grid.add(new Button("(" + i + ", " + j + ")"));
      }
    }

    Frame f = new Frame("Adjusting GridLayout Gaps");
    f.setLayout(new BorderLayout());
    f.add(grid, BorderLayout.CENTER);

    f.addWindowListener(new WindowAdapter() {
        public void windowClosing(WindowEvent e) {
          System.exit(1);
        }
      });

    f.pack();
    f.setVisible(true);
  }

}
