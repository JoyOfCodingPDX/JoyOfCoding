package edu.pdx.cs410J.core;

/**
 * This class represents a box of cereal.  It has a name and a price.
 */
public class Cereal {
  private String name;
  private double price;

  /**
   * Creates a new box of cereal
   */
  public Cereal(String name, double price) {
    this.name = name;
    this.price = price;
  }

  /**
   * Returns the name of this cereal
   */
  public String getName() {
    return this.name;
  }

  /**
   * Returns the price of this cereal
   */
  public double getPrice() {
    return this.price;
  }

  public String toString() {
    return this.name + " $" + this.price;
  }
}
