package edu.pdx.cs410G.graphics;

import java.awt.*;
import javax.swing.*;

/**
 * Demonstrates drawing custom graphics by simply drawing a red ball.
 *
 * @author David Whitlock
 * @version $Revision: 1.1 $
 * @since Fall 2003
 */
public class RedBall extends JPanel {

  /**
   * Override <code>paintComponent</code> to draw a big, red ball
   */
  public void paintComponent(Graphics g) {
    super.paintComponent(g);

    g.setColor(Color.RED);
    Dimension size = this.getSize();
    g.fillOval(0, 0, size.width, size.height);
  }

  /**
   * Pops up a frame that contains a big, red ball
   */
  public static void main(String[] args) {
    RedBall ball = new RedBall();
    ball.setPreferredSize(new Dimension(100, 100));

    JFrame frame = new JFrame("A big, red ball");
    frame.getContentPane().add(ball);

    frame.pack();
    frame.setVisible(true);
  }

}
