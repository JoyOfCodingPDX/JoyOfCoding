package edu.pdx.cs410J.core;

import java.io.*;

/**
 * This program takes a file whose name is given on the command line
 * and reads <code>double</code>s from it.  It demonstrates the
 * <code>FileInputStream</code> and <code>DataInputStream</code>
 * classes.
 */
public class ReadDoubles {
  static PrintStream out = System.out;
  static PrintStream err = System.err;

  /**
   * The one command line argument is the name of the file to read.
   */
  public static void main(String args[]) {
    FileInputStream fis = null;
    try {
      fis = new FileInputStream(args[0]);

    } catch (FileNotFoundException ex) {
      err.println("** No such file: " + args[0]);
    }

    DataInputStream dis = new DataInputStream(fis);
    while (true) {
      try {
	double d = dis.readDouble();
	out.print(d + " ");
	out.flush();

      } catch (EOFException ex) {
	// All done reading
	break;

      } catch (IOException ex) {
	err.println("** " + ex);
	System.exit(1);
      }
    }
    out.println("");
  }
}
