package edu.pdx.cs410J.gui;

import java.awt.BorderLayout;
import javax.swing.*;

/**
 * This program demonstrates Swing's {@link JSplitPane} container.
 */
public class JSplitPaneExample extends JPanel {

  public JSplitPaneExample() {
    this.setLayout(new BorderLayout());
    JSplitPane pane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);

    JPanel left = new JPanel();
    left.add(new JButton("Left"));
    pane.setLeftComponent(left);

    JPanel right = new JPanel();
    right.add(new JButton("Right"));
    pane.setRightComponent(right);

    pane.setOneTouchExpandable(true);
    pane.setDividerLocation(0.75);

    this.add(pane, BorderLayout.CENTER);
  }

  public static void main(String[] args) {
    JFrame frame = new JFrame("JSplitPane example");
    JPanel panel = new JSplitPaneExample();
    frame.getContentPane().add(panel);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.pack();
    frame.setVisible(true);
  }

}
