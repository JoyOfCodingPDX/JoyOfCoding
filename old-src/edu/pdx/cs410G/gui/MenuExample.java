package edu.pdx.cs410G.gui;

import java.awt.Color;
import java.awt.event.*;
import javax.swing.*;

/**
 * This class demonstrates Swing menus
 */
public class MenuExample extends JFrame {

  /**
   * Create a label whose color is selected using a {@link JMenu}
   */
  public MenuExample(String title) {
    super(title);

    final JLabel label = new JLabel("Your text here");
    label.setOpaque(true);      // Swing needs opaque
    
    JMenu menu = new JMenu("Colors");

    JMenuItem item = new JMenuItem("Blue");
    menu.add(item);
    item.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          label.setBackground(Color.blue);
        }
      });

    item = new JMenuItem("Red");
    menu.add(item);
    item.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          label.setBackground(Color.red);
        }
      });

    item = new JMenuItem("Yellow");
    menu.add(item);
    item.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          label.setBackground(Color.yellow);
        }
      });

    JMenuBar menuBar = new JMenuBar();
    menuBar.add(menu);
    this.setJMenuBar(menuBar);

    JPanel panel = new JPanel();
    panel.add(label);
    this.add(panel);
  }

  /**
   * Create a new {@link JFrame} and add a MenuExample
   * to it 
   */
  public static void main(String[] args) {
    JFrame frame = new MenuExample("Menu Example");
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
