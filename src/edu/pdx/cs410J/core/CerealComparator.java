package edu.pdx.cs410J.examples;

import java.util.*;

/**
 * Compares two boxes of ceral by their price
 */
public class CerealComparator implements Comparator {
  public int compare(Object o1, Object o2) {
    double price1 = ((Cereal) o1).getPrice();
    double price2 = ((Cereal) o2).getPrice();

    if (price1 > price2) {
      return 1;
    } else if (price1 < price2) {
      return -1;
    } else {
      return 0;
    }
  }

  public static void main(String[] args) {
    Set set = new TreeSet(new CerealComparator());
    set.add(new Cereal("Cap'n Crunch", 2.59));
    set.add(new Cereal("Trix", 3.29));
    set.add(new Cereal("Count Chocula", 2.59));
    set.add(new Cereal("Froot Loops", 2.45));

    // Print out the cereals
    Iterator iter = set.iterator();
    while (iter.hasNext()) {
      Cereal c = (Cereal) iter.next();
      System.out.println(c);
    }
  }
}
