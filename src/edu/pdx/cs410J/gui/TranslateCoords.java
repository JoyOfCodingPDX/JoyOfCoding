package edu.pdx.cs410J.gui;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import javax.swing.*;

/**
 * This program demonstrate affine transformations by translating the
 * origin of a <code>DrawCoords</code> coordinate system to the
 * center.
 */
public class TranslateCoords extends DrawCoords {

  /**
   * Transform the <code>Graphics2D</code>'s coordinate system before
   * drawing.
   */
  public void paintComponent(Graphics g) {
    Graphics2D g2 = (Graphics2D) g;

    Dimension d = this.getSize();
    AffineTransform xform = new AffineTransform();
    xform.translate(d.width/2, d.height/2);

    if (this.point != null) {
      try {
        this.point = (Point) xform.inverseTransform(this.point, 
                                                    new Point());
      } catch (NoninvertibleTransformException ex) {
        // Don't change point, shouldn't happen with translate
      }
    }

    g2.setTransform(xform);
    super.paintComponent(g);

    // Draw axes
    int[] xs1 = {-d.width/2, d.width/2};
    int[] ys1 = {0, 0};
    g2.drawPolyline(xs1, ys1, 2);

    int[] xs2 = {0, 0};
    int[] ys2 = {-d.height/2, d.height/2};          
    g2.drawPolyline(xs2, ys2, 2);
  }

  /**
   * Make a <code>TranslateCoords</code> and display it in a frame.
   */
  public static void main(String[] args) {
    TranslateCoords tc = new TranslateCoords();
    JFrame frame = new JFrame("TranslateCoords");
    frame.getContentPane().add(tc);

    frame.addWindowListener(new WindowAdapter() {
        public void windowClosing(WindowEvent e) {
          System.exit(0);
        }
      });

    frame.pack();
    frame.setVisible(true);
  }
}
