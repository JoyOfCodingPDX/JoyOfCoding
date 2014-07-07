package edu.pdx.cs410J.core;

import java.util.*;

/**
 * This program demonstrates what happens when you modify a collection
 * while iterating over it.
 */
public class ModifyWhileIterating {

  public static void main(String[] args) {
    List<String> list = new ArrayList<>();
    list.add("one");
    list.add("two");

    Iterator<String> iter = list.iterator();
    while (iter.hasNext()) {
      String s = iter.next();
      if (s.equals("one")) {
        list.add(0, "start");
      }
    }
  }

}
