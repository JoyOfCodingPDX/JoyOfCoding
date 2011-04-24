package edu.pdx.cs410J.lang;

/**
 * This program demonstrates exception handling by reading two
 * integers from the command line and prints their sum.
 */
public class AddTwo {

  /**
   * Prints the sum of two numbers from the command line.
   */
  public static void main(String[] args) {
    int anInt = 0;      // Must initialize these guys
    int anotherInt = 0;

    try {
      anInt = Integer.parseInt(args[0]);

    } catch (NumberFormatException ex) {
      System.err.println("Invalid integer: " + args[0]);
      System.exit(1);
    }

    try {
      anotherInt = Integer.parseInt(args[1]);

    } catch (NumberFormatException ex) {
      System.err.println("Invalid integer: " + args[1]);
      System.exit(1);
    }

    System.out.println(anInt + " + " + anotherInt + " = " + 
		       (anInt + anotherInt));
  }
}
