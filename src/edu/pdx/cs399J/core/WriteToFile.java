package edu.pdx.cs410J.examples;

import java.io.*;

/**
 * This program writes the arguments from the command line to a text
 * file.  It demonstrates the <code>FileWriter</code> class.
 *
 * <P align="center"><EM><A href =
 * "{@docRoot}/../src/edu/pdx/cs410J/examples/WriteToFile.java">
 * View Source</A></EM></P>
 */
public class WriteToFile {
  private static PrintWriter err;

  /**
   * The first argument is the file to write to.
   */
  public static void main(String[] args) {
    // Wrap a PrintWriter around System.err
    err = new PrintWriter(System.err, true);

    // Make a new FileWriter
    Writer writer;
    try {
      writer = new FileWriter(args[0]);

      // Write the command line arguments to the file
      for (int i = 1; i < args.length; i++) {
	writer.write(args[i]);
	writer.write('\n');
      }
    
      // All done
      writer.flush();
      writer.close();

    } catch (IOException ex) {
      err.println("** " + ex);
    }
  }
}
