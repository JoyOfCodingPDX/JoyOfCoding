package edu.pdx.cs410G.awt;

import java.awt.*;
import java.awt.event.*;

/**
 * This program uses the {@link Toolkit} to display a {@link Frame} in
 * the center of the screen.
 */
public class CenteredFrame extends Panel {

  /**
   * A rather simple GUI
   */
  public CenteredFrame() {
    Label label = new Label("I'm in the center");
    this.add(label);
  }

  /**
   * The main program uses a {@link Toolkit} to determine the screen
   * size.  Then we use seventh grade math to compute the center of
   * the screen.
   */
  public static void main(String[] args) {
    Panel p = new CenteredFrame();
    Dimension pSize = p.getSize();
    Toolkit tk = p.getToolkit();
    Dimension screenSize = tk.getScreenSize();
    
    int x = (pSize.width / 2) + (screenSize.width / 2);
    int y = (pSize.height / 2) + (screenSize.height / 2);

    Frame f = new Frame("Frame in the Center");
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
