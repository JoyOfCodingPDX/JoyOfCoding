package edu.pdx.cs410G.mvc;

import java.io.*;
import java.util.*;
import javax.swing.tree.*;

/**
 * This class provides a model (to be viewed by a {@link JTree}) of a
 * {@link File} on a file system.
 */
public class FileNode implements TreeNode {

  private File file;
  private File[] contents;  // Cache sorted list of contents

  /**
   * Creates a <code>FileNode</code> for a given directory.
   */
  public FileNode(File dir) {
    assert dir.isDirectory();
    this.file = dir;
  }

  /**
   * Returns the file associated with this node
   */
  public File getFile() {
    return this.file;
  }

  /**
   * Returns an array of all of the subdirectories of the directory
   * represented by this node.
   */
  private File[] getContents() {
    if (this.contents != null) {
      return contents;
    }

    assert this.file.isDirectory();

    File[] contents = this.file.listFiles();
    if (contents == null) {
      return null;
    }

    SortedSet sorted = new TreeSet(new Comparator() {
        public int compare(Object o1, Object o2) {
          String s1 = ((File) o1).getName();
          String s2 = ((File) o2).getName();
          return s1.compareTo(s2);
        }
      });

    for (int i = 0; i < contents.length; i++) {
      sorted.add(contents[i]);
    }

    this.contents = (File[]) sorted.toArray(contents);
    return this.contents;
  }

  public TreeNode getChildAt(int childIndex) {
    File child = this.getContents()[childIndex];
    return new FileNode(child);
  }

  public int getChildCount() {
    return this.getContents().length;
  }

  public TreeNode getParent() {
    File parent = file.getParentFile();
    if (parent == null) {
      return null;

    } else {
      return new FileNode(file);
    } 
  }

  public int getIndex(TreeNode node) {
    File[] children = this.getContents();
    for (int i = 0; i < children.length; i++) {
      if (children[i].equals(((FileNode) node).file)) {
        return i;
      }
    }

    return -1;
  }

  public boolean getAllowsChildren() {
    return !this.isLeaf();
  }


  public boolean isLeaf() {
    return !this.file.isDirectory();
  }

  public Enumeration children() {
    File[] children = this.getContents();
    Vector v = new Vector();
    for (int i = 0; i < children.length; i++) {
      v.add(new FileNode(children[i]));
    }

    return v.elements();
  }

  public boolean equals(Object o) {
    if (o instanceof FileNode) {
      FileNode other = (FileNode) o;
      if (other.file.equals(this.file)) {
        return true;
      }
    }

    return false;
  }

  public String toString() {
    return this.file.getName();
  }
}
