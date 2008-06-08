package edu.pdx.cs410G.gui;

import javax.swing.*;

/**
 * This class demonstrates a text area widget
 */
public class TextAreaExample extends JPanel {

  /**
   * Create a {@link JTextArea text area} and add it to this
   * panel 
   */
  public TextAreaExample() {
    JTextArea area = 
      new JTextArea("Initial Text", 5, 10);
    area.append("Appended text");
    area.replaceRange("Words", 8, 12);
    this.add(area);
  }

  /**
   * Create a new {@link JFrame} and add a TextAreaExample
   * to it 
   */
  public static void main(String[] args) {
    JFrame frame = new JFrame("JTextArea example");
    frame.add(new TextAreaExample());
    frame.pack();
    frame.setVisible(true);
  }

}
