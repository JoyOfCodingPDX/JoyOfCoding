package edu.pdx.cs410J.examples;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/**
 * This program displays a simple GUI that consists of a
 * <code>JButton</code>, a <code>JTextField</code>, and a
 * <code>JLabel</code>.  When the button is clicked, the text from the
 * field is placed into the label.
 *
 * <P align="center"><EM><A href =
 * "{@docRoot}/../src/edu/pdx/cs410J/examples/SimpleGUI.java">
 * View Source</A></EM></P>
 */
public class SimpleGUI extends JFrame {

  /**
   * Constructs the GUI.
   */
  public SimpleGUI(String title) {
    super(title);

    // In swing, we add components to the "content pane"
    Container pane = this.getContentPane();

    // Set up the layout
    pane.setLayout(new BorderLayout());

    // Set up the text field
    final JTextField text = new JTextField("Type text here");
    pane.add(text, BorderLayout.NORTH);

    // Set up the label
    final JLabel label = new JLabel("You text here");
    pane.add(label, BorderLayout.SOUTH);

    // Set up the button
    JButton button = new JButton("Push the button");
    button.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          // The button was pushed, get the text from the text field,
          // clear the text field, display it in the label.
          String string = text.getText();
          text.setText("");
          label.setText(string);
        }
      });
    pane.add(button, BorderLayout.CENTER);

    // Set up a WindowListener that will exit the program when we
    // close the window
    this.addWindowListener(new WindowAdapter() {
        public void windowClosing(WindowEvent e) {
          System.exit(0);
        }
      });
  }

  /**
   * Main program that creates a <code>SimpleGUI</code> and displays
   * it.
   */
  public static void main(String[] args) {
    JFrame frame = new SimpleGUI("A simple GUI");
    frame.pack();
    frame.setVisible(true);
  }

}
