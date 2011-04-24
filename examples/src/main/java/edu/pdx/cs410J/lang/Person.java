package edu.pdx.cs410J.lang;

/**
 * This class demonstrates constructors, the toString method, and
 * instance fields.
 */
public class Person {
  private String name;     // A person's name
  private double shoeSize;    // A person's shoe size

  /** 
   * Creates a new person 
   */
  public Person(String name, double shoeSize) {
    this.name = name;
    this.shoeSize = shoeSize;
  }

  /**
   * Returns this <code>Person</code>'s shoe size.
   */
  public double shoeSize() {
    return this.shoeSize;
  }

  /** 
   * Returns a String represenation of this 
   * person 
   */
  public String toString() {
    return(name + " has size " + shoeSize + 
           " feet");
  }

  /** Program that creates and prints a Person */
  public static void main(String[] args) {
    Person dave = new Person("Dave", 10.5);
    System.out.println("Person " + dave);
  }
}
