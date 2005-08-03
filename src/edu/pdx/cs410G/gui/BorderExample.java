package edu.pdx.cs410G.gui;

import java.awt.Color;
import javax.swing.*;

/**
 * This program demonstrates Swing's (@link Border} facility. 
 */
public class BorderExample extends JPanel {

  public BorderExample() {
    JButton button = new JButton("Click me");
    button.setBorder(BorderFactory.createMatteBorder(2, 3, 4, 5,
                                                     Color.RED));
    this.add(button);

    String[] entries = { "Twenty-seven", "Thirty-nine", "Two-hundred",
                         "Four", "Five" };
    JList list = new JList(entries);
    JScrollPane scroll = new JScrollPane(list);
    scroll.setBorder(BorderFactory.createTitledBorder("Numbers"));
    this.add(scroll);
  }

  public static void main(String[] args) {
    JFrame frame = new JFrame("Border example");
    JPanel panel = new BorderExample();
    frame.getContentPane().add(panel);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.pack();
    frame.setVisible(true);
  }
}
