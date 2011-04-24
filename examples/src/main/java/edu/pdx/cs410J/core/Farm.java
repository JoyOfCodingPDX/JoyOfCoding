package edu.pdx.cs410J.core;

import edu.pdx.cs410J.lang.*;
import java.util.*;

/**
 * This classes demonstrates <code>Map</code>s in the collection
 * classes.
 */
public class Farm {

  /**
   * Prints the contents of a <code>Map</code>.
   */
  private static void print(Map<String, Animal> map) {
    for (String key : map.keySet()) {
      Object value = map.get(key);

      String s = key + " -> " + value;
      System.out.println(s);
    }
  }

  /**
   * Create a <code>Map</code> and print it.
   */
  public static void main(String[] args) {
    Map<String, Animal> farm = new HashMap<String, Animal>();
    farm.put("Old MacDonald", new Human("Old MacDonald"));
    farm.put("Bossie", new Cow("Bossie"));
    farm.put("Clyde", new Sheep("Clyde"));
    farm.put("Louise", new Duck("Louise"));

    print(farm);
  }

}
