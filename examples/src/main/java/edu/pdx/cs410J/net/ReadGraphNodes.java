package edu.pdx.cs410J.net;

import java.io.*;

/**
 * This class demonstrates serialization support for referential
 * integrity by deserializing a graph of <code>GraphNode</code>s.
 */
public class ReadGraphNodes {

  /**
   * Reads a graph of <code>GraphNode</code>s from a file whose name is
   * specified on the command line.
   */ 
  public static void main(String[] args) {
    String fileName = args[0];

    try {
      FileInputStream fis = new FileInputStream(fileName);
      ObjectInputStream in = new ObjectInputStream(fis);
      GraphNode root = (GraphNode) in.readObject();
      System.out.println("Graph has " + root.traverse() + " nodes");

    } catch (ClassNotFoundException ex) {
      System.err.println("** No class: " + ex);
      System.exit(1);

    } catch (IOException ex) {
      System.err.println("** IOException: " + ex);
      System.exit(1);
    }
  }

}
