package edu.pdx.cs410J.examples;

import java.io.*;

/**
 * Demonstrates anonymous inner classes by creating anonymous
 * subclasses of <code>FileFilter</code> and
 * <code>FilenameFilter</code> that recursively search subdirectories
 * for <code>.class</code> files.
 *
 * <P align="center"><EM><A href =
 * "{@docRoot}/../src/edu/pdx/cs410J/examples/FindClassFiles.java">
 * View Source</A></EM></P>
 */
public class FindClassFiles {

  private static void findFiles(File dir) {
    final String suffix = ".class";

    File[] classFiles = dir.listFiles(new FilenameFilter() {
	public boolean accept(File dir, String name) {
	  return name.endsWith(suffix);
	}
      });

    // Print out the names of class files
    for (int i = 0; i < classFiles.length; i++) {
      System.out.println(classFiles[i]);
    }

    File[] subdirs = dir.listFiles(new FileFilter() {
	public boolean accept(File file) {
	  return file.isDirectory();
	}
      });

    // Recurse over subdirectories
    for (int i = 0; i < subdirs.length; i++) {
      findFiles(subdirs[i]);
    }
  }

  public static void main(String[] args) {
    findFiles(new File(args[0]));
  }
}
