package edu.pdx.cs410J.gui;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import javax.swing.*;

/**
 * This program demonstrates Swing's {@link JTabbedPane} container.
 */
public class JTabbedPaneExample extends JPanel {

  public JTabbedPaneExample() {
    this.setLayout(new BorderLayout());
    
    JTabbedPane pane = new JTabbedPane(JTabbedPane.TOP);
    pane.addTab("Shutdown", new JButton("Shutdown"));

    JPanel panel = new JPanel();
    panel.setLayout(new GridLayout(3, 1));
    panel.add(new JCheckBox("Take shower"));
    panel.add(new JCheckBox("Make coffee"));
    panel.add(new JCheckBox("Get the paper"));
    pane.addTab("Options", panel);

    this.add(pane, BorderLayout.CENTER);
  }

  public static void main(String[] args) {
    JFrame frame = new JFrame("JTabbedPane example");
    JPanel panel = new JTabbedPaneExample();
    frame.getContentPane().add(panel);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.pack();
    frame.setVisible(true);
  }

}
