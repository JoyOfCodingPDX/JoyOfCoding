package edu.pdx.cs399J.gui;

import java.awt.*;

/**
 * This class demonstrates an AWT text field widget
 */
public class TextFieldExample extends Panel {

  /**
   * Create a {@link java.awt.TextField text field} and add it to this
   * panel 
   */
  public TextFieldExample() {
    TextField field = new TextField(20);  // 20 columns
    field.setText("Initial contents");
    this.add(field);
  }

  /**
   * Create a new {@link java.awt.Frame Frame} and add a TextFieldExample
   * to it 
   */
  public static void main(String[] args) {
    Frame frame = new Frame("TextField example");
    frame.add(new TextFieldExample());
    frame.pack();
    frame.setVisible(true);
  }

}
