package edu.pdx.cs410J.core;

import java.io.*;

/**
 * This program demonstrates <code>File</code>,
 * <code>FileFilter</code>, and <code>FilenameFilter</code> by
 * searching recursively through directories and printing out the
 * names of Java source files.
 */
public class FindJavaFiles {

  private static FileFilter      dirFilter;
  private static FilenameFilter  javaFilter;

  /**
   * Prints out the names all of the Java source files in a directory
   * and then recurses over subdirectories.
   */
  private static void findJavaFiles(File dir) {
    File[] javaFiles = dir.listFiles(javaFilter);
    for (int i = 0; i < javaFiles.length; i++) {
      System.out.println(javaFiles[i].toString());
    }

    File[] dirs = dir.listFiles(dirFilter);
    for (int i = 0; i < dirs.length; i++) {
      findJavaFiles(dirs[i]);
    }
  }

  /**
   * The one command line parameter is the directory in which to start
   * the search.
   */
  public static void main(String[] args) {
    File file = new File(args[0]);
    if (file.isDirectory()) {
      dirFilter = new DirectoryFilter();
      javaFilter = new JavaFilenameFilter();

      // Look for Java files
      findJavaFiles(file);

    } else {
      System.err.println(file + " is not a directory");
    }
  }

}
