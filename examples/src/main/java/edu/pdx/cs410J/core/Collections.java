package edu.pdx.cs410J.core;

// All classes not in java.lang must be imported
import java.util.*;

/**
 * This class demonstrates several of the collection classes.
 */
public class Collections {

  /**
   * Prints the contents of a <code>Collection</code>
   */
  private static void print(Collection c) {
    Iterator iter = c.iterator();
    while (iter.hasNext()) {
      Object o = iter.next();
      System.out.println(o);
    }
  }

  public static void main(String[] args) {
    Collection<String> c = new ArrayList<>();
    c.add("One");
    c.add("Two");
    c.add("Three");
    print(c);
    System.out.println("");

    Set<String> set = new HashSet<>(c);
    set.add("Four");
    set.add("Two");
    print(set);
  }

}
