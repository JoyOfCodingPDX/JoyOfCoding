package edu.pdx.cs410J.core;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;

/**
 * This program does the same thing as <code>WriteToFile</code> but uses the new
 * "try with resource" language feature of Java 7.
 */
public class WriteToFileTryWithResource {

  /**
   * The first argument is the file to write to.
   */
  public static void main(String[] args) {
    // Wrap a PrintWriter around System.err
    PrintWriter err = new PrintWriter(System.err, true);

    try (Writer writer = new FileWriter(args[0])) {
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
