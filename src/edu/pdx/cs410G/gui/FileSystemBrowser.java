package edu.pdx.cs399J.gui;

import java.io.*;
import java.awt.*;
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
  public FileSystemBrowser(File dir) {
    File[] fileRoots = null;

    if (dir == null) {
      fileRoots = File.listRoots();
    } else {
      fileRoots = new File[1];
      fileRoots[0] = dir;
    }

    TreeNode[] rootNodes = new FileSystemNode[fileRoots.length];
    for (int i = 0; i < fileRoots.length; i++) {
      rootNodes[i] = new FileSystemNode(fileRoots[i]);
    }

    JSplitPane splitPane = new JSplitPane();

    final FileSystemTable table = new FileSystemTable();
    splitPane.setRightComponent(table);

    JTree tree = new JTree(rootNodes[0]);
    tree.addTreeSelectionListener(new TreeSelectionListener() {
        public void valueChanged(TreeSelectionEvent e) {
          TreePath path = e.getPath();
          FileSystemNode node = 
            (FileSystemNode) path.getLastPathComponent();
          File file = node.getFile();
          table.showDirectory(file);
        }
      });

    JScrollPane scroll = 
      new JScrollPane(tree,
                      JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, 
                      JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
    splitPane.setLeftComponent(scroll);

    this.setLayout(new BorderLayout());
    this.add(splitPane, BorderLayout.CENTER);
  }

  /**
   * Creates and displays a <code>FileSystemBrowser</code>
   */
  public static void main(String[] args) {
    File dir = null;

    if (args.length > 0) {
      dir = new File(args[0]);
      if (!dir.exists() || !dir.isDirectory()) {
        System.err.println("** Not a directory: " + dir);
        System.exit(1);
      }
    }

    JPanel browser = new FileSystemBrowser(dir);


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
