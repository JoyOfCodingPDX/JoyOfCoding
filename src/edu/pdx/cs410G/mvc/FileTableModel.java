package edu.pdx.cs410G.mvc;

import java.io.*;
import java.util.*;
import javax.swing.table.*;

/**
 * This class implements a <code>TableModel</code> interface for
 * display the contents of directories.
 */
public class FileTableModel extends AbstractTableModel {

  private static String[] columnNames = {"Name", "Size", 
                                         "Last Modified", "Readable", 
                                         "Writable", "Hidden"};
  private Object[][] data;

  /**
   * Creates a <code>FileTableModel</code> that displays the contents
   * of a directory.
   */
  public FileTableModel(File dir) {
    // Gather information about the files in the directory
    File[] files = dir.listFiles(new FileFilter() {
        public boolean accept(File file) {
          if (file.isDirectory()) {
            return false;
          } else {
            return true;
          }
        }
      });

    SortedSet sorted = new TreeSet(new Comparator() {
        public int compare(Object o1, Object o2) {
          String s1 = ((File) o1).getName();
          String s2 = ((File) o2).getName();
          return s1.compareTo(s2);
        }
      });
    for (int i = 0; i < files.length; i++) {
      sorted.add(files[i]);
    }
    files = (File[]) sorted.toArray(files);

    this.data = new Object[files.length][];
    for (int i = 0; i < files.length; i++) {
      File file = files[i];
      data[i] = new Object[this.getColumnCount()];
      data[i][0] = file.getName();
      data[i][1] = new Long(file.length());
      data[i][2] = new Date(file.lastModified());
      data[i][3] = new Boolean(file.canRead());
      data[i][4] = new Boolean(file.canWrite());
      data[i][5] = new Boolean(file.isHidden());
    }
  }

  public int findColumn(String columnName) {
    for (int i = 0; i < columnNames.length; i++) {
      if (columnNames.equals(columnName)) {
        return i;
      }
    }

    return -1;
  }

  public Class getColumnClass(int columnIndex) {
    return this.data[0][columnIndex].getClass();
  }

  public String getColumnName(int column) {
    return columnNames[column];
  }

  public int getRowCount() {
    return data.length;
  }

  public int getColumnCount() {
    return columnNames.length;
  }

  public Object getValueAt(int row, int column) {
    return data[row][column];
  }
}
