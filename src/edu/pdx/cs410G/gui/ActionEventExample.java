package edu.pdx.cs399J.gui;

import java.awt.*;
import java.awt.event.*;

/**
 * This class demonstrates handling an AWT action event
 */
public class ActionEventExample extends Panel 
  implements ActionListener {

  private Button button;
  private Label label;

  /**
   * Create a {@link java.awt.Button button}, register this as its
   * {@link java.awt.event.ActionListener ActionListener} and add it
   * to this panel
   */
  public ActionEventExample() {
    this.button = new Button("Click me");
    this.button.addActionListener(this);
    this.label = new Label("Not clicked");
    this.add(this.button);
    this.add(this.label);
  }

  /**
   * Handles ActionEvents
   */
  public void actionPerformed(ActionEvent e) {
    if (e.getSource() == this.button) {
      this.label.setText("Clicked");
    }
  }

  /**
   * Create a new {@link java.awt.Frame Frame} and add a ActionEventExample
   * to it 
   */
  public static void main(String[] args) {
    Frame frame = new Frame("ActionEvent example");
    frame.add(new ActionEventExample());
    frame.pack();
    frame.setVisible(true);
  }

}
