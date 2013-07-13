package edu.pdx.cs410J.j2se15;

import java.io.PrintStream;
import java.util.Calendar;

/**
 * Demonstrated J2SE 1.5's facilities for <code>printf</code>-style
 * formatting. 
 *
 * @see java.util.Formatter
 *
 * @author David Whitlock
 * @version $Revision: 1.2 $
 * @since Summer 2004
 */
public class Formatting {

  /**
   * Formats a number of different kinds of data using J2SE 1.5's
   * formatting facilities.
   */
  public static void main(String[] args) {
    PrintStream out = System.out;
    out.printf("%s%n", "Hello World");

    Calendar today = Calendar.getInstance();

    out.printf("Today's date is: %tm/%td/%tY%n", today, today, today);
    out.printf("The current time is: %tl:%tM %tp%n", today, today, today);

    out.printf("%f/%.2f = %f%n", 2.0, 3.0, (2.0/3.0));

    for (int i = 0; i < 3; i++) {
      out.printf("%5s%5s%5s%n", i, i+1, i+2);
    }

    out.printf("%-10s%s%n", "left", "right");
  }

}
