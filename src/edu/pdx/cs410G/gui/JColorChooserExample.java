package edu.pdx.cs399J.gui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/**
 * This program demonstrates Swing's {@link JColorChooser} container.
 */
public class JColorChooserExample extends JPanel {

  public JColorChooserExample(final JFrame parent) {
    this.setLayout(new FlowLayout());

    final JButton choose = new JButton("Choose color");
    choose.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          Color current = choose.getBackground();
          String title = "Choose button color";
          Color newColor = 
            JColorChooser.showDialog(parent, title, current);
          choose.setBackground(newColor);
        }
      });
    this.add(choose);
  }

  public static void main(String[] args) {
    JFrame frame = new JFrame("JColorChooser example");
    JPanel panel = new JColorChooserExample(frame);
    frame.getContentPane().add(panel);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.pack();
    frame.setVisible(true);
  }

}
