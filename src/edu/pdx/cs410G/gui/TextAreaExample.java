package edu.pdx.cs410J.gui;

import java.awt.*;

/**
 * This class demonstrates an AWT text area widget
 */
public class TextAreaExample extends Panel {

  /**
   * Create a {@link java.awt.TextArea text area} and add it to this
   * panel 
   */
  public TextAreaExample() {
    TextArea area = 
      new TextArea("Initial Text", 5, 10, TextArea.SCROLLBARS_BOTH);
    area.append("Appended text");
    area.replaceRange("Words", 8, 12);
    this.add(area);
  }

  /**
   * Create a new {@link java.awt.Frame Frame} and add a TextAreaExample
   * to it 
   */
  public static void main(String[] args) {
    Frame frame = new Frame("TextArea example");
    frame.add(new TextAreaExample());
    frame.pack();
    frame.setVisible(true);
  }

}
