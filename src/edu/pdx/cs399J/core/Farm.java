package edu.pdx.cs410J.core;

import edu.pdx.cs410J.java.*;
import java.util.*;

/**
 * This classes demonstrates <code>Map</code>s in the collection
 * classes.
 */
public class Farm {

  /**
   * Prints the contents of a <code>Map</code>.
   */
  private static void print(Map map) {
    Iterator keys = map.keySet().iterator();
    while (keys.hasNext()) {
      Object key = keys.next();
      Object value = map.get(key);

      String s = key + " -> " + value;
      System.out.println(s);
    }
  }

  /**
   * Create a <code>Map</code> and print it.
   */
  public static void main(String[] args) {
    Map farm = new HashMap();
    farm.put("Old MacDonald", new Human("Old MacDonald"));
    farm.put("Bossie", new Cow("Bossie"));
    farm.put("Clyde", new Sheep("Clyde"));
    farm.put("Louise", new Duck("Louise"));

    print(farm);
  }

}
