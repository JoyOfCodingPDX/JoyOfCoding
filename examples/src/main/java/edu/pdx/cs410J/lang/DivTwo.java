package edu.pdx.cs410J.lang;

/**
 * This class demonstrates throwing exceptions.  It reads two
 * <code>double</code>s from the command line and divides the second
 * by the first.  If the second is zero, an
 * <code>IllegalArgumentException</code> is thrown.
 */
public class DivTwo {

  /**
   * Reads two <code>double</code>s from the command line and divides
   * the first by the second.
   */
  public static void main(String[] args) {
    double d1 = 0.0;
    double d2 = 0.0;

    if (args.length < 2) {
      System.err.println("Not enough arguments");
      System.exit(1);
    }

    try {
      d1 = Double.parseDouble(args[0]);
      d2 = Double.parseDouble(args[1]);

    } catch (NumberFormatException ex) {
      System.err.println("Not a double: " + ex);
      System.exit(1);
    }

    if (d2 == 0.0) {
      String m = "Denominator can't be zero!";
      throw new IllegalArgumentException(m);
    }

    System.out.println(d1 + " / " + d2 + " = " + (d1/d2));
  }

}
