package edu.pdx.cs399J.gui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/**
 * This programs draws a polygon based on where the user clicks.
 */
public class DrawPolygon extends JPanel {

  Polygon polygon;

  /**
   * Create a new <code>DrawPolygon</code> and initialize the mouse
   * listener.
   */
  public DrawPolygon() {
    this.polygon = new Polygon();
    this.addMouseListener(new MouseAdapter() {
        public void mouseClicked(MouseEvent e) {
          polygon.addPoint(e.getX(), e.getY());
          DrawPolygon.this.repaint();
        }
      });

    this.setPreferredSize(new Dimension(200, 200));
  }

  /**
   * Draw the polygon
   */
  public void paintComponent(Graphics g) {
    super.paintComponent(g);

    Graphics2D g2d = (Graphics2D) g;
    g2d.fill(this.polygon);
  }

  /**
   * Create a new DrawPolygon and put a frame around it
   */
  public static void main(String[] args) {
    JFrame frame = new JFrame("DrawPolygon");
    frame.getContentPane().add(new DrawPolygon());

    frame.addWindowListener(new WindowAdapter() {
        public void windowClosing(WindowEvent e) {
          System.exit(0);
        }
      });

    frame.pack();
    frame.setVisible(true);
  }

}
