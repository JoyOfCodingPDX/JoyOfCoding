package edu.pdx.cs410J.examples;

import java.applet.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.text.*;
import java.util.*;

/**
 * This class uses the AWT to display a clock.
 *
 * <P align="center"><EM><A href =
 * "{@docRoot}/../src/edu/pdx/cs410J/examples/Clock.java">
 * View Source</A></EM></P>
 *
 * @author David Whitlock
 */
public class Clock extends Canvas {

  private static PrintWriter out = new PrintWriter(System.out, true);
  private static PrintWriter err = new PrintWriter(System.err, true);

  private int hourHandLength;     // Length of hour hand
  private int minuteHandLength;   // Length of minute hand
  private int secondHandLength;   // Length of second hand
  private int numberDistance;     // Where the numbers are placed
  private int circleRadius;       // Radius of the clock face

  private Date now;               // Time currently being displayed
//   private FontMetrics fm;         // Used to draw characters

  /**
   * Creates a new <code>Clock</code> of a given size.
   */
  public Clock(int size) {
    this.now = new Date();
    this.setSize(size, size);

    // Calculate various lengths based on size
    reSize();

    // When the window is resized, update the clock
    this.addComponentListener(new ComponentAdapter() {
	public void componentResized(ComponentEvent e) {
	  Clock.this.reSize();
	  Clock.this.repaint();
	}
      });

    this.repaint();
  }

  /**
   * Sets various length values based on the size of the clock.
   */
  private void reSize() {
    Dimension d = this.getSize();
    int size = (d.height > d.width ? d.width : d.height);

    this.circleRadius = 95 * size / 200;
    this.numberDistance = 90 * size / 200;
    this.minuteHandLength = 85 * size / 200;
    this.secondHandLength = 80 * size / 200;
    this.hourHandLength = 50 * size / 200;
  }

  /**
   * Draw the clock.
   */
  public void paint(Graphics g) {
    Dimension d = this.getSize();
    int size = (d.height > d.width ? d.width : d.height);

    Point center = new Point(size / 2, size / 2);

    // Draw the clock face, a circle centered in the middle of the
    // clock with radius
    g.drawOval(center.x - this.circleRadius,   // upper left X
	       center.y - this.circleRadius,   // upper left Y
	       this.circleRadius * 2,          // width
	       this.circleRadius * 2);         // height

    // Draw the numbers on the clock face
    for(int i = 1; i <= 12; i++) {
      Point end = this.pointAtTime(center, 5 * i, this.numberDistance);

      // Attempt to center the string nicely
      FontMetrics fm = g.getFontMetrics();
      int x = end.x - (fm.stringWidth("" + i) / 2);
      int y = end.y + (fm.getHeight() / 2);
      g.drawString("" + i, x, y);
    }

    Calendar calendar = Calendar.getInstance();
    this.now = new Date();
    calendar.setTime(this.now);

    // Draw the minute hand
    int minute = calendar.get(Calendar.MINUTE);
    Point minuteEnd = this.pointAtTime(center, minute,
				       this.minuteHandLength);
    g.drawLine(center.x, center.y, minuteEnd.x, minuteEnd.y);

    // Draw the hour hand
    int hour = calendar.get(Calendar.HOUR);
    Point hourEnd = this.pointAtTime(center, 5 * hour + (minute / 12),
				     this.hourHandLength);
    g.drawLine(center.x, center.y, hourEnd.x, hourEnd.y);

    // Draw the second hand in red
    int second = calendar.get(Calendar.SECOND);
    Point secondEnd = this.pointAtTime(center, second,
				       this.secondHandLength); 
    g.setColor(Color.red);
    g.drawLine(center.x, center.y, secondEnd.x, secondEnd.y);
    
  }

  /**
   * Returns the point along the clock's edge corresponding to a given
   * minute and radius.
   *
   * @param center
   *        The center of the clock
   * @param minutes
   *        The number of minutes past the hour
   * @param radius
   *        How far away from the center should the point be?
   *
   * @return The point radius distance from the center point towards a
   *         given minute.
   */
  private Point pointAtTime(Point center, int minutes, int radius) {
    // Each minute counts for 6 degrees
    double angle = -1.0 * Math.toRadians(minutes * 6.0 + 180.0);
    double x = radius * Math.sin(angle) + center.x;
    double y = radius * Math.cos(angle) + center.y;

    return(new Point((int) x, (int) y));
  }

  /**
   * Returns a text description of the date being displayed by this
   * <code>Clock</code>.
   */
  public String getDateString() {
    DateFormat df = DateFormat.getDateInstance(DateFormat.LONG);
    return(df.format(this.now));
  }

  public Dimension getPreferredSize() {
    // As I recall, it is a good idea to override this method
    return(this.getSize());
  }

  public Dimension getMinimumSize() {
    return(this.getPreferredSize());
  }

  /**
   * Create a <code>Clock</code> and put it inside a <code>Frame</code> so it
   * can be displayed.
   */
  public static void main(String[] args) {
    Clock clock = new Clock(400);

    Frame frame = new Frame(clock.getDateString());
    frame.add(clock);

    // Add a WindowListener to handle closing the Frame
    frame.addWindowListener(new WindowAdapter() {
	public void windowClosing(WindowEvent e) {
	  System.exit(0);
	}
      });

    // Pack in the contents of the frame
    frame.pack();

    // Center the Frame in the screen
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    Dimension frameSize = frame.getSize();
    frame.setLocation((screenSize.width - frameSize.width) / 2,
		      (screenSize.height - frameSize.height) / 2);

    // Show the frame
    frame.show();
  }

}
