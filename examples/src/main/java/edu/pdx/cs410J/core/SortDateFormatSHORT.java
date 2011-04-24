package edu.pdx.cs410J.core;

import java.io.*;
import java.text.*;
import java.util.*;

/**
 * This program reads in {@link Date}s from standard in using {@link
 * DateFormat#SHORT}.  Then it adds them to a {@link SortedSet} to see
 * how their sorted.  One of my students claimed that {@link
 * DateFormat#SHORT} didn't parse 4-digit dates correct.  This test
 * will see if she was right.
 *
 * @author David Whitlock
 * @version $Revision: 1.2 $
 */
public class SortDateFormatSHORT {

  public static void main(String[] args) throws IOException {
    int f = DateFormat.SHORT;
    DateFormat df = DateFormat.getDateTimeInstance(f, f);
    df.setLenient(false);

    System.out.println("An example of DateFormat.SHORT: " +
                       df.format(new Date()));
    System.out.println("\nEnter some dates to sort:");

    SortedSet<Date> sorted = new TreeSet<Date>();
    BufferedReader br =
      new BufferedReader(new InputStreamReader(System.in));
    while (true) {
      String line = br.readLine();
      try {
        sorted.add(df.parse(line));

      } catch (ParseException ex) {
        break;
      }
    }

    System.out.println(sorted.size() + " sorted dates:");
    for (Iterator iter = sorted.iterator(); iter.hasNext(); ) {
      System.out.print("  ");
      System.out.println(iter.next());
    }
  }

}
