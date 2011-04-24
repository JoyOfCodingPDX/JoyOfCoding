package edu.pdx.cs410J.j2se15;

import java.util.*;

/**
 * Demonstrates J2SE primitive data "autoboxing" features.
 *
 * @author David Whitlock
 * @version $Revision: 1.1 $
 * @since Summer 2004
 */
public class Autoboxing {

  /**
   * Performs a bunch of operations that demonstrate autoboxing
   */
  public static void main(String[] args) {
    // Recall that Integer.valueOf returns an Integer
    int i = Integer.valueOf("123");

    List list = new ArrayList();
    list.add(i);
    
    int j = (Integer) list.get(0);
  }

}
