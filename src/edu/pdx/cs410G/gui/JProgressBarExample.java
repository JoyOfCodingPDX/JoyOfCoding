package edu.pdx.cs399J.gui;

import java.awt.BorderLayout;
import java.awt.event.*;
import javax.swing.*;

/**
 * This program demonstrates Swing's {@link JProgressBar} widget.
 */
public class JProgressBarExample extends JPanel {
  public JProgressBarExample() {
    this.setLayout(new BorderLayout());

    final JProgressBar progress = new JProgressBar(0, 25);
    progress.setBorderPainted(true);
    progress.setStringPainted(true);
    this.add(progress, BorderLayout.NORTH);

    JButton button = new JButton("Click me 25 times");
    button.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          progress.setValue(progress.getValue() + 1);
        }
      });
    this.add(button, BorderLayout.SOUTH);
  }

  public static void main(String[] args) {
    JFrame frame = new JFrame("JProgressBar Example");
    JPanel panel = new JProgressBarExample();
    frame.getContentPane().add(panel);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.pack();
    frame.setVisible(true);
  }
 
}
