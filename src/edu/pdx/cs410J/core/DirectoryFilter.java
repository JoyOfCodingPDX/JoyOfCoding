package edu.pdx.cs410J.core;

import java.io.*;

/**
 * This class is a <code>FileFilter</code> that only accepts files
 * that are directories.
 */
public class DirectoryFilter implements FileFilter {

  /**
   * Returns <code>true</code> if a file is a directory
   */
  public boolean accept(File file) {
    if (file.isDirectory()) {
      return true;

    } else {
      return false;
    }
  }

}
