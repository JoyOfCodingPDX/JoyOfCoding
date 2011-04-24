package edu.pdx.cs410J.core;

import java.util.*;

/**
 * Compares two boxes of ceral by their price
 */
public class CerealComparator implements Comparator<Cereal> {
  public int compare(Cereal o1, Cereal o2) {
    double price1 = o1.getPrice();
    double price2 = o2.getPrice();

    if (price1 > price2) {
      return 1;
    } else if (price1 < price2) {
      return -1;
    } else {
      return 0;
    }
  }

  public static void main(String[] args) {
    Set<Cereal> set = new TreeSet<Cereal>(new CerealComparator());
    set.add(new Cereal("Cap'n Crunch", 2.59));
    set.add(new Cereal("Trix", 3.29));
    set.add(new Cereal("Count Chocula", 2.59));
    set.add(new Cereal("Froot Loops", 2.45));

    // Print out the cereals
    for (Cereal c : set) {
      System.out.println(c);
    }
  }
}
