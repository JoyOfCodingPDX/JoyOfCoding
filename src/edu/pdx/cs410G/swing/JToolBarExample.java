package edu.pdx.cs410G.swing;

import javax.swing.*;

/**
 * This program demonstrates Swing's {@link JToolBar} container.
 */
public class JToolBarExample extends JPanel {

  public JToolBarExample() {
    JToolBar bar = new JToolBar("Tools", JToolBar.HORIZONTAL);
    bar.add(new JButton("A button"));
    bar.add(new JLabel("A label"));
    bar.setFloatable(true);
    this.add(bar);

    bar = new JToolBar();
    bar.setOrientation(JToolBar.VERTICAL);
    bar.setFloatable(false);
    bar.add(new JButton("Another Button"));
    this.add(bar);
  }

  public static void main(String[] args) {
    JFrame frame = new JFrame("JToolBar example");
    JPanel panel = new JToolBarExample();
    frame.getContentPane().add(panel);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.pack();
    frame.setVisible(true);
  }

}
