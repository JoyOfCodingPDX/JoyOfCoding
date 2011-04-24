package edu.pdx.cs410J.lang;

/**
 * This class represents a Sheep.
 */
public class Sheep extends Mammal {

  public Sheep(String name) {
    this.name = name;
  }

  public String says() {
    return "Baa";
  }

}
