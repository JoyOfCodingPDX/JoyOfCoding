package edu.pdx.cs410J.core;

import java.io.*;

/**
 * This class is a <code>FilenameFilter</code> that only accepts files
 * that end in .java.
 */
public class JavaFilenameFilter implements FilenameFilter {

  /**
   * Returns <code>true</code> if a file's name ends with ".java"
   */
  public boolean accept(File dir, String fileName) {
    if (fileName.endsWith(".java")) {
      return true;

    } else {
      return false;
    }
  }

}
