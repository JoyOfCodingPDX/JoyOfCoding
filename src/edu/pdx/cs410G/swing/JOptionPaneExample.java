package edu.pdx.cs410G.swing;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/**
 * This program demonstrates Swing's {@link JOptionPane} container.
 */
public class JOptionPaneExample extends JPanel {

  public JOptionPaneExample(final JFrame parent) {
    this.setLayout(new FlowLayout());

    JButton error = new JButton("Error dialog");
    error.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          String message = "File is malformatted";
          String title = "Error during parsing";
          int type = JOptionPane.ERROR_MESSAGE;
          JOptionPane.showMessageDialog(parent, message, title, type); 
        }
      });
    this.add(error);

    JButton confirm = new JButton("Confirm");
    confirm.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          String message = "Are you sure you want to delete this file?";
          String title = "Confirmation";
          int type = JOptionPane.YES_NO_OPTION;
          int answer = 
            JOptionPane.showConfirmDialog(parent, message, title,
                                          type); 
          System.out.println((answer == JOptionPane.YES_OPTION ? "Yes"
                              : "No"));
        }
      });
    this.add(confirm);

    JButton name = new JButton("Name");
    name.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          String message = "Please enter your name:";
          String title = "Enter Name";
          int type = JOptionPane.PLAIN_MESSAGE;
          String answer = 
            JOptionPane.showInputDialog(parent, message, title, type);
          System.out.println("Your name is \"" + answer + "\"");
        }
      });
    this.add(name);
  }

  public static void main(String[] args) {
    JFrame frame = new JFrame("JOptionPane example");
    JPanel panel = new JOptionPaneExample(frame);
    frame.getContentPane().add(panel);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.pack();
    frame.setVisible(true);
  }

}
