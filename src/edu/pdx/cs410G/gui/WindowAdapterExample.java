package edu.pdx.cs410J.gui;

import java.awt.*;
import java.awt.event.*;

/**
 * This class demonstrates using a {@link java.awt.event.WindowAdapter
 * WindowAdapter} to gracefully close an application
 */
public class WindowAdapterExample extends Panel {

  /**
   * Create a {@link java.awt.Button button} so that there is
   * something in the panel
   */
  public WindowAdapterExample() {
    Label label = new Label("I don't do much");
    this.add(label);
  }

  /**
   * Create a new {@link java.awt.Frame Frame} and add a ActionEventExample
   * to it 
   */
  public static void main(String[] args) {
    Frame frame = new Frame("ActionEvent example with inner class");
    frame.addWindowListener(new WindowAdapter() {
        public void windowClosing(WindowEvent e) {
          // The frame is being closed, exit the JVM
          System.exit(0);
        }
      });
    frame.add(new WindowAdapterExample());
    frame.pack();
    frame.setVisible(true);
  }

}
