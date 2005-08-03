package edu.pdx.cs399J.gui;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.*;
import javax.swing.*;

/**
 * This program uses the {@link Toolkit} to display a {@link Frame} in
 * the center of the screen.
 */
public class CenteredFrame extends JPanel {

  /**
   * A rather simple GUI
   */
  public CenteredFrame() {
    JLabel label = new JLabel("I'm in the center");
    this.add(label);
  }

  /**
   * The main program uses a {@link Toolkit} to determine the screen
   * size.  Then we use seventh grade math to compute the center of
   * the screen.
   */
  public static void main(String[] args) {
    JPanel p = new CenteredFrame();
    Dimension pSize = p.getSize();
    Toolkit tk = p.getToolkit();
    Dimension screenSize = tk.getScreenSize();
    
    int x = (screenSize.width / 2) - (pSize.width / 2);
    int y = (screenSize.height / 2) - (pSize.height / 2);

    JFrame f = new JFrame("Frame in the Center");
    f.add(p);
    f.setLocation(x, y);

    f.addWindowListener(new WindowAdapter() {
	public void windowClosing(WindowEvent e) {
	  System.exit(0);
	}
      });
    f.pack();
    f.setVisible(true);
  }
}
