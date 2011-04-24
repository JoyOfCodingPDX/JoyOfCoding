package edu.pdx.cs410J.core;

import java.util.*;

/**
 * Prints out information about today's date.  Demonstrates the
 * <code>Date</code> and <code>Calendar</code> classes.
 */
public class Today {

  public static void main(String[] args) {
    Date today = new Date();
    Calendar cal = Calendar.getInstance();
    cal.setTime(today);

    int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
    int dayOfYear = cal.get(Calendar.DAY_OF_YEAR);
    int weekOfMonth = cal.get(Calendar.WEEK_OF_MONTH);

    StringBuffer sb = new StringBuffer();
    sb.append("Today is " + today + "\n");
    sb.append("It's been " + today.getTime() +
	      "ms since the epoch.");
    sb.append("\nIt is the " + dayOfWeek + 
	      "th day of the week \nand the " +
	      dayOfYear + "th day of the year.  ");
    sb.append("\nWe are in the " + weekOfMonth +
	      "th week of the month.");
    System.out.println(sb.toString());

  }

}
