package edu.pdx.cs410J.examples;

import java.io.*;
import java.util.*;

/**
 * Demonstrates serialization by writing an instance of
 * <code>Date</code> to a file.
 *
 * <P align="center"><EM><A href =
 * "{@docRoot}/../src/edu/pdx/cs410J/examples/WriteDate.java">
 * View Source</A></EM></P>
 */
public class WriteDate {

  /**
   * Writes the <code>Date</code> object for the current day and time
   * to a file whose name is specified on the command line.
   */
  public static void main(String[] args) {
    String fileName = args[0];

    try {
      FileOutputStream fos = new FileOutputStream(fileName);
      ObjectOutputStream out = new ObjectOutputStream(fos);
      Date date = new Date();
      System.out.println("Writing " + date);
      out.writeObject(date);
      out.flush();
      out.close();

    } catch(IOException ex) {
      System.err.println("**IOException: " + ex);
      System.exit(1);
    }
  }

}
