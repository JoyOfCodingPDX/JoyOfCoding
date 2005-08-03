package edu.pdx.cs399J.gui;

import java.awt.event.*;
import javax.swing.*;

/**
 * This class demonstrates using a {@link java.awt.event.WindowAdapter
 * WindowAdapter} to gracefully close an application
 */
public class WindowAdapterExample extends JPanel {

  /**
   * Create a {@link JButton button} so that there is
   * something in the panel
   */
  public WindowAdapterExample() {
    JLabel label = new JLabel("I don't do much");
    this.add(label);
  }

  /**
   * Create a new {@link JFrame} and add a ActionEventExample to it
   */
  public static void main(String[] args) {
    JFrame frame = new JFrame("WindowAdapter example");
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
