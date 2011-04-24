package edu.pdx.cs410J.net;

import java.io.*;

/**
 * This program constructs a graph of <code>Node</code>s and
 * serializes them to a file.
 */
public class WriteGraphNodes {

  /**
   * Creates a graph of <code>GraphNode</code>s and serializes them to a
   * file whose name is given on the command line.
   */
  public static void main(String[] args) {
    String fileName = args[0];

    // Make a graph of nodes
    GraphNode a = new GraphNode();
    GraphNode b = new GraphNode();
    GraphNode c = new GraphNode();
    GraphNode d = new GraphNode();
    GraphNode e = new GraphNode();

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

    } catch (IOException ex) {
      System.err.println("** IOException: " + ex);
      System.exit(1);
    }
  }
}
