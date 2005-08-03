package edu.pdx.cs410G.gui;

import java.awt.event.*;
import javax.swing.*;

/**
 * This program demonstrates the Swing {@link JButton} widget
 */
public class JButtonExample extends JPanel {

  public JButtonExample() {
    JButton b1 = new JButton("Press me!");
    b1.setMnemonic(KeyEvent.VK_P);
    b1.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          System.out.println("I was pressed!");
        }
      });
    this.add(b1);

    JButton b2 = new JButton("<html>No, press <i>me</i>!</html>");
    b2.setMnemonic(KeyEvent.VK_N);
    b2.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          System.out.println("No, I was pressed!");
        }
      });
    this.add(b2);

    JToggleButton b3 = new JToggleButton("Toggle Button");
    this.add(b3);
  }

  public static void main(String[] args) {
    JFrame frame = new JFrame("JButton example");
    JPanel panel = new JButtonExample();
    frame.getContentPane().add(panel);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.pack();
    frame.setVisible(true);
  }

}
