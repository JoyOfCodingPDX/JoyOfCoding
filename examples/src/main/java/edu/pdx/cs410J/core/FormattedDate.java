package edu.pdx.cs410J.core;

import java.text.*;
import java.util.*;

/**
 * This program reads a date and time from the command in
 * <code>DateFormat.MEDIUM</code> format and prints it back out in all
 * four formats.
 */
public class FormattedDate {

  /**
   * The command line contains a date to be formatted
   */
  public static void main(String[] args) {
    StringBuffer sb = new StringBuffer();
    for (int i = 0; i < args.length; i++) {
      sb.append(args[i] + " ");
    }
    Date date = null;
    DateFormat df = 
      DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM);
    try {
      date = df.parse(sb.toString().trim());

    } catch (ParseException ex) {
      String s = "Bad date: " + sb;
      System.err.println("** " + s);
      System.exit(1);
    }

    df = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT);
    System.out.println("SHORT: " + df.format(date));

    df = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM);
    System.out.println("MEDIUM: " + df.format(date));

    df = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG);
    System.out.println("LONG: " + df.format(date));

    df = DateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.FULL);
    System.out.println("FULL: " + df.format(date));
  }

}
