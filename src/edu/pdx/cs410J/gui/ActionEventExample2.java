package edu.pdx.cs410J.gui;

import java.awt.*;
import java.awt.event.*;

/**
 * This class demonstrates handling an AWT action event using an
 * anonymous inner class
 */
public class ActionEventExample2 extends Panel {

  /**
   * Create a {@link java.awt.Button button}, register this as its
   * {@link java.awt.event.ActionListener ActionListener} and add it
   * to this panel
   */
  public ActionEventExample2() {
    final Label label = new Label("Not clicked");
    Button button = new Button("Click me");
    button.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          label.setText("Clicked");
        }
      });

    this.add(button);
    this.add(label);
  }

  /**
   * Create a new {@link java.awt.Frame Frame} and add a ActionEventExample
   * to it 
   */
  public static void main(String[] args) {
    Frame frame = new Frame("ActionEvent example with inner class");
    frame.add(new ActionEventExample2());
    frame.pack();
    frame.setVisible(true);
  }

}
