package edu.pdx.cs410J.examples;

import java.io.*;
import java.util.*;
import javax.swing.tree.*;

/**
 * This class extends the <code>DefaultTreeModel</code> class and
 * describes a tree model for browsing a file system.
 */
public class FileSystemNode implements TreeNode {

  private File file;

  /**
   * Creates a <code>FileSystemNode</code> for a given
   * <code>File</code>.
   */
  public FileSystemNode(File file) {
    this.file = file;
  }

  public TreeNode getChildAt(int childIndex) {
    File child = this.file.listFiles()[childIndex];
    return(new FileSystemNode(child));
  }

  public int getChildCount() {
    if(!this.file.isDirectory()) {
      return(0);
    } else {
      return(this.file.listFiles().length);
    }
  }

  public TreeNode getParent() {
    File parent = file.getParentFile();
    if(parent == null) {
      return(null);

    } else {
      return(new FileSystemNode(file));
    } 
  }

  public int getIndex(TreeNode node) {
    File[] children = this.file.listFiles();
    for(int i = 0; i < children.length; i++) {
      if(children[i].equals(((FileSystemNode) node).file)) {
        return(i);
      }
    }

    return(-1);
  }

  public boolean getAllowsChildren() {
    return(!this.isLeaf());
  }


  public boolean isLeaf() {
    return(!this.file.isDirectory());
  }

  public Enumeration children() {
    File[] children = this.file.listFiles();
    Vector v = new Vector();
    for(int i = 0; i < children.length; i++) {
      v.add(new FileSystemNode(children[i]));
    }

    return(v.elements());
  }

  public boolean equals(Object o) {
    if(o instanceof FileSystemNode) {
      FileSystemNode other = (FileSystemNode) o;
      if(other.file.equals(this.file)) {
        return(true);
      }
    }

    return(false);
  }

  public String toString() {
    return(this.file.getName());
  }
}
