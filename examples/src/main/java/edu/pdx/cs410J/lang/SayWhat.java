package edu.pdx.cs410J.lang;

/**
 * This class has a main method that demonstrates the effects of
 * inheritance and virtual method dispatches using the animal class
 * hierarchy.
 */
public class SayWhat {

  private static Human human;
  private static Cow cow;
  private static Ant ant;

  /**
   * Prints an animal's name and what it says.
   */
  private static void saysWhat(Animal animal) {
    System.out.println(animal.getName() + " says \"" +
		       animal.says() + "\"");
  }

  /**
   * This main method creates a number of animals and prints out their
   * names and what they say.
   */
  public static void main(String[] args) {
    human = new Human("Dave");
    cow = new Cow("Bessy");
    ant = new Ant("Arthur");

    saysWhat(human);
    saysWhat(cow);
    saysWhat(ant);
  }

}
