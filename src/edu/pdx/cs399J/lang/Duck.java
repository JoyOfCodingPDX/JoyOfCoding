package edu.pdx.cs410J.lang;

/**
 * This class represents a duck.  Ducks fly.
 */
public class Duck extends Bird implements Flies {

  public Duck(String name) {
    this.name = name;
  }

  public String says() {
    return "Quack";
  }

  public void fly() {
    System.out.println("I'm flying");
  }

}
