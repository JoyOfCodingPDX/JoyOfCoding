package edu.pdx.cs410J.examples;

import java.io.*;
import java.util.*;
import javax.swing.tree.*;

/**
 * This class extends the <code>DefaultTreeModel</code> class and
 * describes a tree model for browsing the directories in a file
 * system.
 */
public class FileSystemNode implements TreeNode {

  private File dir;
  private File[] subdirs;  // Cache sorted list of subdirs

  /**
   * Creates a <code>FileSystemNode</code> for a given
   * <code>File</code>.
   */
  public FileSystemNode(File dir) {
    this.dir = dir;
  }

  /**
   * Returns the file associated with this node
   */
  public File getFile() {
    return this.dir;
  }

  /**
   * Returns an array of all of the subdirectories of the directory
   * represented by this node.
   */
  private File[] getSubdirs() {
    if (this.subdirs != null) {
      return subdirs;
    }

    File[] subdirs = 
    this.dir.listFiles(new FileFilter() {
        public boolean accept(File file) {
          if (file.isDirectory()) {
            return true;
          } else {
            return false;
          }
        }
      });

    if (subdirs == null) {
      return null;
    }

    SortedSet sorted = new TreeSet(new Comparator() {
        public int compare(Object o1, Object o2) {
          String s1 = ((File) o1).getName();
          String s2 = ((File) o2).getName();
          return s1.compareTo(s2);
        }
      });
    for (int i = 0; i < subdirs.length; i++) {
      sorted.add(subdirs[i]);
    }

    this.subdirs = (File[]) sorted.toArray(subdirs);
    return this.subdirs;
  }

  public TreeNode getChildAt(int childIndex) {
    File child = this.getSubdirs()[childIndex];
    return new FileSystemNode(child);
  }

  public int getChildCount() {
    return this.getSubdirs().length;
  }

  public TreeNode getParent() {
    File parent = dir.getParentFile();
    if (parent == null) {
      return null;

    } else {
      return new FileSystemNode(dir);
    } 
  }

  public int getIndex(TreeNode node) {
    File[] children = this.getSubdirs();
    for (int i = 0; i < children.length; i++) {
      if (children[i].equals(((FileSystemNode) node).dir)) {
        return i;
      }
    }

    return -1;
  }

  public boolean getAllowsChildren() {
    return this.getSubdirs() != null;
  }


  public boolean isLeaf() {
    return this.getSubdirs() == null;
  }

  public Enumeration children() {
    File[] children = this.getSubdirs();
    Vector v = new Vector();
    for (int i = 0; i < children.length; i++) {
      v.add(new FileSystemNode(children[i]));
    }

    return v.elements();
  }

  public boolean equals(Object o) {
    if (o instanceof FileSystemNode) {
      FileSystemNode other = (FileSystemNode) o;
      if (other.dir.equals(this.dir)) {
        return true;
      }
    }

    return false;
  }

  public String toString() {
    return this.dir.getName();
  }
}
