package edu.pdx.cs410J.examples;

import java.io.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/**
 * This class demonstrates uses a <code>JTable</code> to display
 * information about the files in a directory.
 */
public class FileSystemTable extends JPanel {

  private JLabel dirNameLabel;
  private JScrollPane scrollPane;

  /**
   * Creates a new <code>FileSystemTable</code>
   */
  public FileSystemTable() {
    this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
    this.dirNameLabel = new JLabel();
    this.add(dirNameLabel);

    this.scrollPane = 
      new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                      JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
    this.add(this.scrollPane);
  }

  /**
   * Displays a given directory using a <code>JTable</code>
   */
  public void showDirectory(File dir) {
    this.dirNameLabel.setText(dir.getAbsolutePath());
    this.dirNameLabel.setHorizontalAlignment(SwingConstants.CENTER);

    // Build a JTable and display it in the scrollPane
    JTable table = new JTable(new FileTableModel(dir));
    table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
    this.scrollPane.setViewportView(table);
  }

  public Dimension getPreferredSize() {
    return new Dimension(600, 400);
  }

  /**
   * Take the name of a directory from the command line and display
   * its contents
   */
  public static void main(String[] args) {
    File file = new File(args[0]);
    if (!file.isDirectory() || !file.exists()) {
      System.err.println("** Not a directory: " + args[0]);
      System.exit(1);
    }

    FileSystemTable table = new FileSystemTable();
    table.showDirectory(file);

    JFrame frame = new JFrame("DrawCoords");
    frame.getContentPane().add(table);

    frame.addWindowListener(new WindowAdapter() {
        public void windowClosing(WindowEvent e) {
          System.exit(0);
        }
      });

    frame.pack();
    frame.setVisible(true);
  }

}
