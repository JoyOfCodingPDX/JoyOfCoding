package edu.pdx.cs410G.graphics;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.Border;

/**
 * Demonstrates custom graphics that take a component's {@linkplain
 * Border border} into account.  
 *
 * @author David Whitlock
 * @version $Revision: 1.1 $
 * @since Fall 2003
 *
 * @see Insets
 */
public class RoundRectangleWithBorder extends JPanel {

  /**
   * Draws a green rounded retangle, taking this component's border
   * into account.
   */
  public void paintComponent(Graphics g) {
    super.paintComponent(g);    // Paint borders

    Insets insets = this.getInsets();
    Dimension size = this.getSize();
    int top = insets.top;
    int left = insets.left;
    int height = size.height - insets.top - insets.bottom;
    int width = size.width - insets.left - insets.right;
    
    g.setColor(Color.GREEN);
    g.fillRoundRect(top, left, width, height, 40, 40);
  }

  /**
   * Displays a frame with a rounded rectangle with a border
   */
  public static void main(String[] args) {
    RoundRectangleWithBorder rect = new RoundRectangleWithBorder();
    rect.setPreferredSize(new Dimension(100, 100));
    Border border =
      BorderFactory.createMatteBorder(10, 10, 10, 10, Color.RED);
    rect.setBorder(border);
    rect.setBackground(Color.BLACK);

    JFrame frame = new JFrame("Round rectangle with border");
    frame.getContentPane().add(rect);
    frame.pack();
    frame.setVisible(true);
  }

}
