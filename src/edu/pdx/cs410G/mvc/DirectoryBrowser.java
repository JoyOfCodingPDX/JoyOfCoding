package edu.pdx.cs410G.mvc;

import java.awt.BorderLayout;
import java.io.File;
import javax.swing.*;

/**
 * This program demonstrates the {@link JTree} widget by browsing
 * through the directories of a file system.
 *
 * @see FileNode
 */
public class DirectoryBrowser extends JPanel {

  /**
   * Creates a new <code>DirectoryBrowser</code> that browses a given
   * directory
   */
  public DirectoryBrowser(File dir) {
    this.setLayout(new BorderLayout());
    JTree tree = new JTree(new FileNode(dir));
    tree.setCellRenderer(new FileNodeRenderer());

    this.add(new JScrollPane(tree), BorderLayout.CENTER);
  }

  public static void main(String[] args) {
    File dir = new File(args[0]);
    if (!dir.exists()) {
      System.err.println("Does not exist: " + dir);
      System.exit(1);

    } else if (!dir.isDirectory()) {
      System.err.println("Not a directory: " + dir);
      System.exit(1);
    }

    JFrame frame = new JFrame("JTree Example");
    frame.getContentPane().add(new DirectoryBrowser(dir));
    frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    frame.pack();
    frame.setVisible(true);
  }
}
