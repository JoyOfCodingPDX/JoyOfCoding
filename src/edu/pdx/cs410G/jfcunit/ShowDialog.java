package edu.pdx.cs410G.jfcunit;

import java.awt.BorderLayout;
import java.awt.event.*;
import javax.swing.*;

/**
 * This program displays a {@link JTextField} containing the days of
 * the week and a {@link JButton}.  When the button is clicked a
 * dialog box ({@link JOptionPane}) is popped up.
 */
public class ShowDialog extends JPanel {

  /** The title of this Window */
  static final String TITLE = "A more complex GUI";

  /** The days of the week */
  static final String[] DAYS = 
  { "Monday", "Tuesday", "Wednesday", "Thursday", "Friday",
    "Saturday", "Sunday" };

  public ShowDialog() {
    this.setLayout(new BorderLayout());
    final JList list = new JList(DAYS);
    this.add(BorderLayout.CENTER, list);
    JButton button = new JButton("Press Me");
    button.addActionListener(new ActionListener() {
	public void actionPerformed(ActionEvent e) {
	  Object value = list.getSelectedValue();
	  JOptionPane.showMessageDialog(ShowDialog.this, value);
	}
      });
    this.add(BorderLayout.SOUTH, button);
  }

  public static void main(String[] args) {
    JFrame frame = new JFrame(TITLE);
    JPanel panel = new ShowDialog();
    frame.getContentPane().add(panel);
    frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    frame.pack();
    frame.setVisible(true);
  }

}
