package edu.pdx.cs410J.lang;

/**
 * This class represents a human being (not a human doing).
 */
public class Human extends Mammal {
  public Human(String name) {
    this.name = name;
  }

  public String says() { 
    return "Hello"; 
  }
}
