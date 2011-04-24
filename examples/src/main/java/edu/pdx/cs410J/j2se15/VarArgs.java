package edu.pdx.cs410J.j2se15;

/**
 * Demonstrates variable-length method arguments in J2SE 1.5.
 *
 * @author David Whitlock
 * @version $Revision: 1.1 $
 * @since Summer 2004
 */
public class VarArgs {

  /**
   * Prints the sum of a variable number of <code>int</code>s
   */
  private static void printSum(String header, int... ints) {
    int sum = 0;
    for (int i : ints) {
      sum += i;
    }

    System.out.print(header);
    System.out.println(sum);
  }

  /**
   * Prints the sum of a bunch of numbers
   */
  public static void main(String[] args) {
    printSum("1+2+3 = ", 1, 2, 3);
    printSum("1+2+3+4+5 = ", 1, 2, 3, 4, 5);
    printSum("2+4+6+8 = ", 2, 4, 6, 8);
  }

}
