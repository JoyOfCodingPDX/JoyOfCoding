package edu.pdx.cs410G.gui;

import java.awt.event.KeyEvent;
import javax.swing.*;

/**
 * This program simply pops up a {@link JPanel} with a {@link JButton}
 * in it.
 */
public class ButtonExample extends JPanel {
  public ButtonExample() {
    JButton ok = new JButton("OK");
    ok.setMnemonic(KeyEvent.VK_O);
    JButton cancel =
      new JButton("<html><i>Cancel</i></html>");
    this.add(ok);
    this.add(cancel);
  }

  public static void main(String[] args) {
    JFrame frame = new JFrame("Button example");
    frame.add(new ButtonExample());
    frame.pack();
    frame.setVisible(true);
  }

}
