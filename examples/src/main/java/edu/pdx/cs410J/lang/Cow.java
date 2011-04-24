package edu.pdx.cs410J.lang;

/**
 * This class represents a cow.
 */
public class Cow extends Mammal {
  public Cow(String name) {
    this.name = name;
  }

  public String says() { 
    return "Moo"; 
  }
}
