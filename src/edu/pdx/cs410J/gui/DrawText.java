package edu.pdx.cs410J.gui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/**
 * This program displays a String read from the command line in the
 * center of the panel.
 */
public class DrawText extends JPanel {
  String text;

  /**
   * Create a new <code>DrawText</code> for drawing text
   */
  public DrawText(String text) {
    this.text = text;
  }

  /**
   * Draw the text centered on this panel
   */
  public void paint(Graphics g) {
    Graphics2D g2d = (Graphics2D) g;
    
    // Print out some info about what we're drawing
    Font font = g2d.getFont();
    System.out.println("Drawing with " + font);

    FontMetrics fm = g2d.getFontMetrics();
    Rectangle2D bounds = fm.getStringBounds(this.text, g2d);
    
    
  }
}
