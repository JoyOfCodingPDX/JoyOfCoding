package edu.pdx.cs410J.gui;

import javax.swing.*;

/**
 * This program demonstrates some of Swing's menu classes.
 */
public class JMenuExample extends JApplet {

  public JMenuExample() {
    JMenuBar menuBar = new JMenuBar();
    
    JMenu file = new JMenu("File");
    file.add(new JMenuItem("Open..."));
    file.add(new JMenuItem("Open in other window..."));
    file.addSeparator();
    file.add(new JMenuItem("Save"));
    file.add(new JMenuItem("Save As..."));
    menuBar.add(file);

    JMenu levels = new JMenu("Levels");
    ButtonGroup group = new ButtonGroup();
    JCheckBoxMenuItem item = new JCheckBoxMenuItem("Easy");
    group.add(item);
    levels.add(item);
    item = new JCheckBoxMenuItem("Medium");
    group.add(item);
    levels.add(item);
    item = new JCheckBoxMenuItem("Hard");
    group.add(item);
    levels.add(item);
    menuBar.add(levels);    

    this.setJMenuBar(menuBar);
  }

  public static void main(String[] args) {
    JFrame frame = new JFrame("JMenu example");
    java.awt.Panel panel = new JMenuExample();
    frame.getContentPane().add(panel);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.pack();
    frame.setVisible(true);
  }
}
