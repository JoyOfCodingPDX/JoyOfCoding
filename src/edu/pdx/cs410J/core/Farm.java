package edu.pdx.cs410J.examples;

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
    while(keys.hasNext()) {
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
    farm.put("Old MacDonald", new Human());
    farm.put("Bossie", new Cow());
    farm.put("Clyde", new Sheep());
    farm.put("Louise", new Duck());

    print(farm);
  }

}
