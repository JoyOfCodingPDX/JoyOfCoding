package edu.pdx.cs410J.examples;

import java.io.*;

/**
 * This class demonstrates serialization support for referential
 * integrity by deserializing a graph of <code>Node</code>s.
 */
public class ReadNodes {

  /**
   * Reads a graph of <code>Node</code>s from a file whose name is
   * specified on the command line.
   */ 
  public static void main(String[] args) {
    String fileName = args[0];

    try {
      FileInputStream fis = new FileInputStream(fileName);
      ObjectInputStream in = new ObjectInputStream(fis);
      Node root = (Node) in.readObject();
      System.out.println("Graph has " + root.traverse() + " nodes");

    } catch(ClassNotFoundException ex) {
      System.err.println("** No class: " + ex);
      System.exit(1);

    } catch(IOException ex) {
      System.err.println("** IOException: " + ex);
      System.exit(1);
    }
  }

}
