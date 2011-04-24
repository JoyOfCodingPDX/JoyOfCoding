package edu.pdx.cs410J.tips;

/**
 * This program demonstrates that <code>double</code>s only provide
 * <I>approximations</I> of negative powers of 10.
 *
 * @see BigDecimalDemo
 *
 * @author David Whitlock
 * @version $Revision: 1.1 $
 */
public class DoubleTrouble {

  public static void main(String[] args) {
    double increment = 0.10;  // Not REALLY 0.10
    double total = 0.0;
    for (int i = 0; i < 10; i++) {
      System.out.println(total);
      total += increment;
    }
    System.out.println(total + (total == 1.00 ? "\nYes" : "\nNo?"));
  }

}
