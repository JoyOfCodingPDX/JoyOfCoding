package edu.pdx.cs399J.gui;

import java.awt.BorderLayout;
import java.awt.event.*;
import javax.swing.*;

/**
 * This program demonstrates Swing's {@link JTextField}, {@link
 * JPasswordField}, and {@link JTextArea}.
 */
public class JTextExample extends JPanel {

  public JTextExample() {
    this.setLayout(new BorderLayout());
    JPanel p = new JPanel();

    p.add(new JLabel("User name:"));
    final JTextField name = new JTextField(10);
    p.add(name);

    p.add(new JLabel("Password:"));
    final JPasswordField password = new JPasswordField(10);
    p.add(password);

    this.add(p, BorderLayout.NORTH);

    final JTextArea text = new JTextArea(10, 30);
    this.add(text, BorderLayout.CENTER);

    JButton submit = new JButton("Submit");
    submit.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          String s = "User name: " + name.getText() + " (" +
            name.getSelectedText() + " selected)" + "\n" +
            "Password: " + new String(password.getPassword());
          text.setText(s);
        }
      });
    this.add(submit, BorderLayout.SOUTH);
  }

  public static void main(String[] args) {
    JFrame frame = new JFrame("Swing text example");
    JPanel panel = new JTextExample();
    frame.getContentPane().add(panel);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.pack();
    frame.setVisible(true);
  }
 

}
