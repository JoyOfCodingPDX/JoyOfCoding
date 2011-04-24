package edu.pdx.cs410J.examples;

import edu.pdx.cs410J.lang.*;
import java.util.*;

/**
 * This class is a <code>Comparator</code> that compares
 * <code>Object</code>s based on the name of their classes.
 */
public class ClassComparator implements Comparator<Object> {
  public int compare(Object o1, Object o2) {
    String name1 = o1.getClass().getName();
    String name2 = o2.getClass().getName();
    
    // Take advantage of String's compartTo method
    return name1.compareTo(name2);
  }

  /**
   * Create a bunch of object and store them in a set using a
   * <code>ClassComparator</code>. 
   */
  public static void main(String[] args) {
    Set<Object> set = new TreeSet<Object>(new ClassComparator());
    set.add("Hello");
    set.add(new Cow("Betty"));
    set.add(new Vector());
    set.add(new Ant("Richard"));

    // Print out the set
    Iterator iter = set.iterator();
    while (iter.hasNext()) {
      Object o = iter.next();
      String s = o + " (" + o.getClass().getName() + ")";
      System.out.println(s);
    }
  }
}
