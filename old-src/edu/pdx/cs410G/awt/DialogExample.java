package edu.pdx.cs410G.awt;

import java.awt.*;
import java.awt.event.*;

/**
 * This class demonstrates using AWT dialog boxes
 */
public class DialogExample extends Frame {

  /**
   * Create a button that pops up a modal {@link
   * edu.pdx.cs410G.awt.DialogBox DialogBox}
   */
  public DialogExample(String title) {
    super(title);

    Button button = new Button("Show dialog");
    button.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          (new DialogBox(DialogExample.this)).show();
        }
      });

    Panel panel = new Panel();
    panel.add(button);
    this.add(panel);
  }

  /**
   * Create a new {@link java.awt.Frame Frame} and add a MouseAndKeyEvents
   * to it 
   */
  public static void main(String[] args) {
    Frame frame = new DialogExample("Dialog Example");
    frame.addWindowListener(new WindowAdapter() {
        public void windowClosing(WindowEvent e) {
          // The frame is being closed, exit the JVM
          System.exit(0);
        }
      });
    frame.pack();
    frame.setVisible(true);
  }

}

/**
 * An example modal dialog box
 */
class DialogBox extends Dialog {

  DialogBox(Frame owner) {
    super(owner, "Example dialog box", true /*modal*/);

    Button button = new Button("Go away");
    button.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          DialogBox.this.dispose();
        }
      });
    this.add(button);

    this.addWindowListener(new WindowAdapter() {
        public void windowClosing(WindowEvent e) {
          DialogBox.this.dispose();
        }
      });

    this.pack();
  }

}
