package edu.pdx.cs410J.examples;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/**
 * This class demonstrates Java's text and shape drawing
 * capabilities.  When you click the window, the coordinates of the
 * click are printed.
 * 
 * @author David Whitlock
 * @version $Revision: 1.3 $
 */
public class DrawCoords extends JPanel {

  protected Point point;  // Where the mouse was clicked

  /**
   * Create a new <code>DrawCoords</code>.  Set up a mouse listener
   * that keeps track of where the mouse was clicked.
   */
  public DrawCoords() {
    super();

    this.addMouseListener(new MouseAdapter() {
        public void mousePressed(MouseEvent e) {
          // Keep track of where the mouse was clicked

          int x = e.getX();
          int y = e.getY();
          if(point == null) {
            point = new Point(x, y);

          } else {
            point.x = x;
            point.y = y;
          }

          // Update on mouse click
          repaint();
        }
      });

    this.setPreferredSize(new Dimension(200, 200));
  }


  /**
   * Repaints this <code>DrawCoords</code> draw a small square where
   * the user clicked and draws the coordinates to the right of the
   * square.
   */
  public void paintComponent(Graphics g) {
    super.paintComponent(g);

    if(this.point == null) {
      // Nothing to do
      return;
    }

    g.fillRect(point.x - 2, point.y - 2, 4, 4);

    // Draw a String near the point.  Make sure it isn't written
    // outside of the panel
    String coords = "(" + point.x + ", " + point.y + ")";
    FontMetrics fm = g.getFontMetrics();
    int stringWidth = fm.stringWidth(coords);
    int stringHeight = fm.getHeight();

    Dimension panelSize = this.getSize();
    int panelWidth = panelSize.width;
    int panelHeight = panelSize.height;

    int x = point.x + 6;
    int y = point.y + 3;
    
    if(x + stringWidth > panelWidth) {
      x -= (stringWidth + 12);
    }

    if(y + stringHeight > panelHeight) {
      y -= (6);
    }

    g.drawString(coords, x, y);
  }

  

  /**
   * Make a <code>DrawCoords</code> and display it in a frame.
   */
  public static void main(String[] args) {
    DrawCoords dc = new DrawCoords();
    JFrame frame = new JFrame("DrawCoords");
    frame.getContentPane().add(dc);

    frame.addWindowListener(new WindowAdapter() {
        public void windowClosing(WindowEvent e) {
          System.exit(0);
        }
      });

    frame.pack();
    frame.setVisible(true);
  }
}
