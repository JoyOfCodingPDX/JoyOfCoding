package edu.pdx.cs410J.examples;

import java.io.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.tree.*;

/**
 * This program displays a browser that is used to browse the files
 * and directories in a file system.  It demonstrates the
 * <code>JTree</code> and <code>JTable</code> classes.
 */
public class FileSystemBrowser extends JPanel {

  /**
   * Set up a new <code>FileSystemBrowser</code> and add components to
   * it.
   */
  public FileSystemBrowser() {
    File[] fileRoots = File.listRoots();
    TreeNode[] rootNodes = new FileSystemNode[fileRoots.length];
    for(int i = 0; i < rootNodes.length; i++) {
      System.out.println("Root: " + fileRoots[i]);
      rootNodes[i] = new FileSystemNode(fileRoots[i]);
    }

    JTree tree = new JTree(rootNodes[0]);
    this.add(new JScrollPane(tree));
  }

  /**
   * Creates and displays a <code>FileSystemBrowser</code>
   */
  public static void main(String[] args) {
    JPanel browser = new FileSystemBrowser();
    JFrame frame = new JFrame("File System Browser");
    frame.getContentPane().add(browser);

    frame.addWindowListener(new WindowAdapter() {
        public void windowClosing(WindowEvent e) {
          System.exit(0);
        }
      });

    frame.pack();
    frame.setVisible(true);
  }

}
