package edu.pdx.cs410J.examples;

import java.util.*;

/**
 * This class is a <code>Comparator</code> that compares
 * <code>Person</code>s based on their shoe size.
 */
public class PersonComparator implements Comparator {

  public int compare(Object o1, Object o2) {
    double size1 = ((Person) o1).shoeSize();
    double size2 = ((Person) o2).shoeSize();

    if(size1 > size2) {
      return(1);

    } else if(size1 < size2) {
      return(-1);

    } else {
      return(0);
    }
  }

  /**
   * Creates a set of <code>Person</code>s and prints out the
   * contents.
   */
  public static void main(String[] args) {
    Set set = new TreeSet(new PersonComparator());
    set.add(new Person("Quan", 10.5));
    set.add(new Person("Jerome", 11.0));
    set.add(new Person("Dave", 10.5));
    set.add(new Person("Nandini", 8.0));

    // Print out the people
    Iterator iter = set.iterator();
    while(iter.hasNext()) {
      Person p = (Person) iter.next();
      System.out.println(p);
    }
  }

}
