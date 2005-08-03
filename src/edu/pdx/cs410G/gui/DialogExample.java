package edu.pdx.cs410G.gui;

import java.awt.event.*;
import javax.swing.*;

/**
 * This class demonstrates using Swing dialog boxes
 */
public class DialogExample extends JFrame {

  /**
   * Create a button that pops up a modal {@link
   * edu.pdx.cs410G.gui.DialogBox DialogBox}
   */
  public DialogExample(String title) {
    super(title);

    JButton button = new JButton("Show dialog");
    button.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          (new DialogBox(DialogExample.this)).show();
        }
      });

    JPanel panel = new JPanel();
    panel.add(button);
    this.add(panel);
  }

  /**
   * Create a new {@link JFrame} and add a DialogExample
   * to it 
   */
  public static void main(String[] args) {
    JFrame frame = new DialogExample("Dialog Example");
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
class DialogBox extends JDialog {

  DialogBox(JFrame owner) {
    super(owner, "Example dialog box", true /*modal*/);

    JButton button = new JButton("Go away");
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
