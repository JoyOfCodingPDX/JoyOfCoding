package edu.pdx.cs410J.lang;

/**
 * This class demonstrate the <code>instanceof</code> operator and the
 * concept of interfaces using the animal hierarchy.  It uses the
 * <code>instanceof</code> operator to determine whether or not an
 * animal implements the <code>Flies</code> interface.
 */
public class DoIFly {

  /**
   * Prints out whether or not an animal can fly.  It checks the
   * run-time type of the <code>Animal</code>.  If the animal
   * implements the <code>Flies</code> interface, then the animal can
   * fly.
   */
  private static void doIFly(Animal animal) {
    boolean iFly = (animal instanceof Flies);
    System.out.print("Does " + animal.getName() + " fly?  ");
    System.out.println((iFly ? "Yes." : "No."));
  }

  /**
   * This main program creates several animals and then prints out
   * whether or not they can fly.
   */
  public static void main(String[] args) {
    doIFly(new Cow("Bessy"));
    doIFly(new Bee("Buzz"));
    doIFly(new Turkey("Tom"));
  }

}
