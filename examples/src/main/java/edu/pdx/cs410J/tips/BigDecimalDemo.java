package edu.pdx.cs410J.tips;

import java.math.BigDecimal;

/**
 * This program demonstrates that {@link BigDecimal}s provide
 * arbitrary-precision decimal arithmetic.
 *
 * @see DoubleTrouble
 *
 * @author David Whitlock
 * @version $Revision: 1.1 $
 */
public class BigDecimalDemo {

  public static void main(String[] args) {
    BigDecimal increment = new BigDecimal("0.10");
    BigDecimal total = new BigDecimal("0.0");
    for (int i = 0; i < 10; i++) {
      System.out.println(total);
      total = total.add(increment);
    }
    BigDecimal one = new BigDecimal("1.00");
    System.out.println(total +
                       (total.equals(one) ? "\nYes" : "\nNo?"));
  }

}
