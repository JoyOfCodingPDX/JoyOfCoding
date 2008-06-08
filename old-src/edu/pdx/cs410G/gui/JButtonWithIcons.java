package edu.pdx.cs410G.gui;

import java.net.URL;
import javax.swing.*;

/**
 * This program demonstrates how icons can be associated with a {@link
 * JButton}.  The icon graphics are reside in JPEG files that are
 * loaded by this program as "resources".
 *
 * @author David Whitlock
 * @since Summer 2005
 */
public class JButtonWithIcons extends JPanel {

  /**
   * Contains a <code>JButton</code> that has an icon or two.
   */
  JButtonWithIcons() {
    JButton button = new JButton("I have an icon");

    URL sad = this.getClass().getResource("sad.jpg");
    button.setIcon(new ImageIcon(sad));

    URL happy = this.getClass().getResource("happy.jpg");
    button.setRolloverIcon(new ImageIcon(happy));

    this.add(button);
  }

  public static void main(String[] args) {
    JFrame frame = new JFrame("Icon button example");
    frame.add(new JButtonWithIcons());
    frame.pack();
    frame.setVisible(true);
  }

}
