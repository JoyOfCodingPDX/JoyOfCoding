package edu.pdx.cs410J.examples;

import java.util.*;

/**
 * This class is used to demonstrate object serialization support for
 * referential integrity.
 */
public class Node implements java.io.Serializable {
  private Collection children = new ArrayList();
  private transient boolean beenVisited = false;

  /**
   * Adds a child node to this node
   */
  public void addChild(Node child) {
    this.children.add(child);
  }

  /**
   * Returns this node's number of unvisited descendents
   */
  public int traverse() {
    int total = 1;
    this.beenVisited = true;

    Iterator iter = children.iterator();
    while(iter.hasNext()) {
      Node child = (Node) iter.next();
      if(!child.beenVisited) {
	total += child.traverse();
      }
    }

    return(total);
  }
}
