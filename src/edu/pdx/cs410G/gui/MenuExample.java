package edu.pdx.cs399J.gui;

import java.awt.*;
import java.awt.event.*;

/**
 * This class demonstrates using AWT menus
 */
public class MenuExample extends Frame {

  /**
   * Create a label whose color is selected using a {@link
   * java.awt.Menu Menu}
   */
  public MenuExample(String title) {
    super(title);

    final Label label = new Label("Your text here");
    
    Menu menu = new Menu("Colors");

    MenuItem item = new MenuItem("Blue");
    menu.add(item);
    item.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          label.setBackground(Color.blue);
        }
      });

    item = new MenuItem("Red");
    menu.add(item);
    item.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          label.setBackground(Color.red);
        }
      });

    item = new MenuItem("Yellow");
    menu.add(item);
    item.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          label.setBackground(Color.yellow);
        }
      });

    MenuBar menuBar = new MenuBar();
    menuBar.add(menu);
    this.setMenuBar(menuBar);

    Panel panel = new Panel();
    panel.add(label);
    this.add(panel);
  }

  /**
   * Create a new {@link java.awt.Frame Frame} and add a MenuExample
   * to it 
   */
  public static void main(String[] args) {
    Frame frame = new MenuExample("Menu Example");
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
