package edu.pdx.cs410G.jfcunit;

import javax.swing.*;

/**
 * A very simple class that displays a {@link JTextField}
 *
 * @see ShowTextFieldTest
 */
public class ShowTextField extends JPanel {

  /** The title of the window */
  static final String TITLE = "A simple GUI to test";

  /** The text to show */
  static final String TEXT = "This is some text";

  public ShowTextField() {
    JTextField text = new JTextField(TEXT);
    text.setEditable(false);
    this.add(text);
  }

  public static void main(String[] args) {
    JFrame frame = new JFrame(TITLE);
    JPanel panel = new ShowTextField();
    frame.getContentPane().add(panel);
    frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    frame.pack();
    frame.setVisible(true);
  }

}
