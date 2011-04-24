package edu.pdx.cs410J.core;

import java.util.*;

/**
 * This class represents a box of cereal.  It has a name and a price.
 */
public class Cereal implements Comparable<Cereal> {
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

  /**
   * Compares two <code>Cereal</code>s based on their name
   */
  public int compareTo(Cereal c2) {
    return this.getName().compareTo(c2.getName());
  }

  /**
   * Two be consistent with the natural ordering, two
   * <code>Cereal</code>s that have the same name, are themselves the
   * same.
   */
  public boolean equals(Object o) {
    if (o instanceof Cereal) {
      Cereal other = (Cereal) o;
      return this.getName().equals(other.getName());
    }
    return false;
  }

  /**
   * Two cereals that are equal must have the same hash code.
   */
  public int hashCode() {
    return this.getName().hashCode();
  }

  /**
   * Demonstrates the natural ordering of <code>Cereals</code> by
   * adding a bunch of cereals to a {@link SortedSet}
   */
  public static void main(String[] args) {
    SortedSet<Cereal> set = new TreeSet<Cereal>();
    set.add(new Cereal("Total", 3.56));
    set.add(new Cereal("Raisin Bran", 2.65));
    set.add(new Cereal("Sugar Crisps", 2.38));

    for (Cereal c : set ) {
      System.out.println(c);
    }
  }

}
