package edu.pdx.cs399J.gui;

import javax.swing.*;

/**
 * A very simple applet that just contains a <code>DrawCoords</code>
 */
public class DrawCoordsApplet extends JApplet {

  /**
   * When an applet is initialized, create a new
   * <code>DrawCoords</code> and add it
   */
  public void init() {
    System.out.println("Initializing Applet...");
    DrawCoords dc = new DrawCoords();
    this.getContentPane().add(dc);
  }

}
