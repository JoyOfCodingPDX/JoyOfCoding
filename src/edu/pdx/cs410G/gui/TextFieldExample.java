package edu.pdx.cs399J.gui;

import javax.swing.*;

/**
 * This class demonstrates a text field widget
 */
public class TextFieldExample extends JPanel {

  /**
   * Create a {@link JTextField text field} and add it to this panel
   */
  public TextFieldExample() {
    JTextField field = new JTextField(20);  // 20 columns
    field.setText("Initial contents");
    this.add(field);
  }

  /**
   * Create a new {@link JFrame JFrame} and add a TextFieldExample
   * to it 
   */
  public static void main(String[] args) {
    JFrame frame = new JFrame("JTextField example");
    frame.add(new TextFieldExample());
    frame.pack();
    frame.setVisible(true);
  }

}
