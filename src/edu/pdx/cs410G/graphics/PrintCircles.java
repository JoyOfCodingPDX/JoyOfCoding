package edu.pdx.cs410G.graphics;

import java.awt.*;
import java.awt.event.*;
import java.awt.print.*;
import java.util.*;
import javax.swing.*;

/**
 * A program that demonstrates Java's {@linkplain java.awt.print
 * printing} capabilities by letting you draw a bunch of circles on
 * the screen and then printing them out.
 *
 * @author David Whitlock
 * @version $Revision: 1.1 $
 * @since Fall 2003
 */
public class PrintCircles extends JPanel implements Printable {

  /** The radius of the circles */
  private static final int RADIUS = 15;

  /** The centers of the circles */
  private Collection circles;

  /////////////////////  Constructors  ///////////////////////

  /**
   * Creates a new <code>PrintCircles</code> and does some
   * initialization. 
   */
  public PrintCircles() {
    this.setBackground(Color.WHITE);
    this.setForeground(Color.BLACK);
    this.circles = new ArrayList();
    this.addMouseListener(new MouseAdapter() {
        public void mouseClicked(MouseEvent e) {
          circles.add(new Point(e.getX(), e.getY()));
          PrintCircles.this.repaint();
        }
      });
    this.setPreferredSize(new Dimension(400, 200));
  }

  ////////////////////  Instance Methods  ////////////////////

  /**
   * Draws the circles on the screen
   */
  public void paintComponent(Graphics g) {
    super.paintComponent(g);
    this.drawCircles(g);
  }

  /**
   * Draws a bunch of circles on a <code>Graphics</code>
   */
  private void drawCircles(Graphics g) {
    for (Iterator iter = this.circles.iterator(); iter.hasNext(); ) {
      Point p = (Point) iter.next();
      g.drawOval(p.x - RADIUS, p.y - RADIUS, 2 * RADIUS, 2 * RADIUS);
    }
  }

  /**
   * Invoked when the <code>PrintCircles</code> is to be printed
   */
  public int print(Graphics g, PageFormat pageFormat, int pageIndex)
    throws PrinterException {

    if (pageIndex >= 1) {
      return Printable.NO_SUCH_PAGE;
    }

    this.drawCircles(g);
    return Printable.PAGE_EXISTS;
  }

  //////////////////////  Main Program  //////////////////////

  /**
   * Displays a frame that contains a <code>PrintCircles</code>
   */
  public static void main(String[] args) {
    final PrintCircles circles = new PrintCircles();

    JFrame frame = new JFrame("Print Circles");
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    
    frame.getContentPane().add(circles, BorderLayout.CENTER);
    JButton print = new JButton("Print");
    print.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          PrinterJob job = PrinterJob.getPrinterJob();
          job.setPrintable(circles);
          if (job.printDialog()) {
            try {
              job.print();
              
            } catch (PrinterException ex) {
              ex.printStackTrace(System.err);
            }
          }
        }
      });
    frame.getContentPane().add(print, BorderLayout.SOUTH);

    frame.pack();
    frame.setVisible(true);
  }

}
