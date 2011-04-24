package edu.pdx.cs410J.j2se15;

import static java.lang.Integer.*;
import static java.lang.System.*;

/**
 * Demonstrates J2SE's "static import" facility.
 *
 * @author David Whitlock
 * @version $Revision: 1.1 $
 * @since Summer 2004
 */
public class StaticImports {

  /**
   * Prints the sum of the integers entered on the command line
   */
  public static void main(String[] args) {
    int sum = 0;
    for (int i = 0; i < args.length; i++) {
      sum += parseInt(args[i]); // Integer.parseInt()
    }

    out.println("Sum is " + sum); // System.out
    out.println("MAX_INT is " + MAX_VALUE); // Integer.MAX_VALUE
  }

}
