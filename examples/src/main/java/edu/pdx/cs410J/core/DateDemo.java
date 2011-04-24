package edu.pdx.cs410J.core;

import java.text.*;
import java.util.*;

/**
 * This class demonstrates how to use the Java's day and time
 * facilities. 
 */
public class DateDemo {

  private static DateFormat dfShort;
  private static DateFormat dfMedium;
  private static DateFormat dfLong;
  private static DateFormat dfFull;

  /**
   * This main method works with dates.  If there are any arguments on
   * the command line, they are interpreted as a date to be parsed.
   * Otherwise, the current day/time are used.
   */
  public static void main(String[] args) {

    // Set up the DateFormats
    dfShort = DateFormat.getDateTimeInstance(DateFormat.SHORT,
					     DateFormat.SHORT );
    dfMedium = DateFormat.getDateTimeInstance(DateFormat.MEDIUM,
					      DateFormat.MEDIUM);
    dfLong = DateFormat.getDateTimeInstance(DateFormat.LONG,
					    DateFormat.LONG);
    dfFull = DateFormat.getDateTimeInstance(DateFormat.FULL,
					    DateFormat.FULL);
    
    Date day = null;

    if (args.length == 0) {
      // Use the current day/time
      day = new Date();

    } else {
      // Parse the command line as if it were a date
      StringBuffer sb = new StringBuffer();
      for (int i = 0; i < args.length; i++) {
	sb.append(args[i]);
	sb.append(' ');
      }

      try {
	day = dfShort.parse(sb.toString());

      } catch (ParseException ex) {
	System.err.println("** Malformatted date: " + sb);
	System.exit(1);
      }
    }

    // Print out the date in several formats
    System.out.println("Short: " + dfShort.format(day));
    System.out.println("Medium: " + dfMedium.format(day));
    System.out.println("Long: " + dfLong.format(day));
    System.out.println("Full: " + dfFull.format(day));

  }

}
