package edu.pdx.cs410J.net;

import java.util.*;

/**
 * This class is used to demonstrate object serialization support for
 * referential integrity.
 */
public class GraphNode implements java.io.Serializable {
  private Collection children = new ArrayList();
  private transient boolean beenVisited = false;

  /**
   * Adds a child node to this node
   */
  public void addChild(GraphNode child) {
    this.children.add(child);
  }

  /**
   * Returns this node's number of unvisited descendents
   */
  public int traverse() {
    int total = 1;
    this.beenVisited = true;

    Iterator iter = children.iterator();
    while (iter.hasNext()) {
      GraphNode child = (GraphNode) iter.next();
      if (!child.beenVisited) {
	total += child.traverse();
      }
    }

    return total;
  }
}
