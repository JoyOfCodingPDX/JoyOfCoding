package edu.pdx.cs399J.core;

import java.util.*;

/**
 * This class demonstrates the <code>Properties</code> class and shows
 * how to use the JVM's system properties.
 */
public class SystemProperties {

  /**
   * Print out a couple of the system properties and check to see if
   * the "edu.pdx.cs399J.Debug" property has been set on the command
   * line.
   */
  public static void main(String[] args) {
    // Print out some properties
    Properties props = System.getProperties();
    props.list(System.out);

    // Is the "edu.pdx.cs399J.Debug" property set?
    String name = "edu.pdx.cs399J.Debug";
    boolean debug = Boolean.getBoolean(name);
    System.out.print("\nAre we debugging? ");
    System.out.println((debug ? "Yes." : "No."));
  }

}
