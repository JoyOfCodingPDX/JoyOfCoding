package edu.pdx.cs410J.net;

import java.io.*;
import java.util.*;

/**
 * This class demonstrates object serialization by reading an instance
 * of <code>Date</code> from a file.
 */
public class ReadDate {

  /**
   * Read a <code>Date</code> instance from th file whose name is
   * specified on the command line.
   */
  public static void main(String[] args) {
    String fileName = args[0];

    Date date = null;
    try {
      FileInputStream fis = new FileInputStream(fileName);
      ObjectInputStream in = new ObjectInputStream(fis);
      date = (Date) in.readObject();
      in.close();
      System.out.println("Read " + date);

    } catch (ClassNotFoundException ex) {
      System.err.println("** No class " + ex);
      System.exit(1);

    } catch (IOException ex) {
      System.err.println("**IOException: " + ex);
      System.exit(1);
    }
  }

}
