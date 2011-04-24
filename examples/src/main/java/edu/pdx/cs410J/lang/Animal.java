package edu.pdx.cs410J.lang;

/**
 * This class is the base class in our animal hierarchy.  Each animal
 * has a name and it makes a sound.
 */
public abstract class Animal {
  protected String name;

  /**
   * Returns the name of this animal.
   */
  public final String getName() {
    return this.name;
  }

  /**
   * Returns the sound that this animal makes.
   */
  public abstract String says();

  public String toString() {
    return getName() + " says " + says();
  }
}
