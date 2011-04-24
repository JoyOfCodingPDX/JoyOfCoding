package edu.pdx.cs410J.tips;

/**
 * This class demonstrates some interesting things about static
 * initializers. 
 */
public class Truth {
  public static void main(String args[]) {
    new Foo();
  }
}

class Foo {
  static Bar b = new Bar();

  static boolean truth() { return true; }
  static final boolean TRUTH = truth();

  Foo() {
    System.out.println("The truth is: " + TRUTH);
  }
}

class Bar extends Foo { } 
