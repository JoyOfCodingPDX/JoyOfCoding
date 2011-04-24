package edu.pdx.cs410J.lang;

/**
 * This program uses Java's reflection mechanism to print the name of
 * a given <code>Object</code>'s class.
 */
public class WhatAmI {

  /**
   * Prints out the name of a given <code>Object</code>'s class.
   */
  private static void whatAmI(Object o) {
    Class c = o.getClass();
    System.out.println("I (" + o + ") am a " + c.getName());
  }

  public static void main(String args[]) {
    whatAmI("Hello");
    whatAmI(new Integer(4));
    whatAmI(new Double(2.7));
    whatAmI(new Cow("Tootie"));
  }

}
