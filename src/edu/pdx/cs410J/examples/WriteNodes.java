package edu.pdx.cs410J.examples;

import java.io.*;

/**
 * This program constructs a graph of <code>Node</code>s and
 * serializes them to a file.
 */
public class WriteNodes {

  /**
   * Creates a graph of <code>Node</code>s and serializes them to a
   * file whose name is given on the command line.
   */
  public static void main(String[] args) {
    String fileName = args[0];

    // Make a graph of nodes
    Node a = new Node();
    Node b = new Node();
    Node c = new Node();
    Node d = new Node();
    Node e = new Node();

    a.addChild(b);
    a.addChild(c);
    a.addChild(d);
    b.addChild(e);
    c.addChild(e);
    d.addChild(e);

    System.out.println("Graph has " + a.traverse() + " nodes");

    try {
      FileOutputStream fos = new FileOutputStream(fileName);
      ObjectOutputStream out = new ObjectOutputStream(fos);
      out.writeObject(a);
      out.flush();
      out.close();

    } catch(IOException ex) {
      System.err.println("** IOException: " + ex);
      System.exit(1);
    }
  }
}
