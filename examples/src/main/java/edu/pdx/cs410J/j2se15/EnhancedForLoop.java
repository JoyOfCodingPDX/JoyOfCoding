package edu.pdx.cs410J.j2se15;

import java.util.*;

/**
 * Demonstrates J2SE's "enhanced for loop" functionality.
 *
 * @author David Whitlock
 * @version $Revision: 1.1 $
 * @since Summer 2004
 */
public class EnhancedForLoop {

  /**
   * Sorts the command line arguments and prints their sum.
   */
  public static void main(String[] args) {
    int sum = 0;
    for (String arg : args) {
      sum += Integer.parseInt(arg);
    }

    System.out.println("Sum is " + sum);
    System.out.print("Sorted arguments: ");

    SortedSet set = new TreeSet(Arrays.asList(args));
    for (Object o : set) {
      System.out.print(o + " ");
    }

    System.out.println("");
  }

}
