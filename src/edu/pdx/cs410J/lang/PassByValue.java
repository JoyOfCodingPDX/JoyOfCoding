package edu.pdx.cs410J.lang;

/**
 * Demonstrates Java's pass by value mechanism
 */
public class PassByValue {
  private static void doubled(int i) {
    i = i * 2;
    System.out.println("Doubled: " + i);
  }

  public static void main(String[] args) {
    int i = 27;
    System.out.println("Before: " + i);
    doubled(i);
    System.out.println("After: " + i);
  }
}
