package edu.pdx.cs410J.examples;

import java.io.*;
import java.net.*;

/**
 * This program dumps the contents of a URL to standard out.
 *
 * <P align="center"><EM><A href =
 * "{@docRoot}/../src/edu/pdx/cs410J/examples/DumpURL.java">
 * View Source</A></EM></P>
 */
public class DumpURL {
  private static PrintStream err = System.err;

  /**
   * Read the URL from the command line.
   */
  public static void main(String[] args) {
    URL url = null;
    try {
      url = new URL(args[0]);

    } catch (MalformedURLException ex) {
      err.println("** Bad URL: " + args[0]);
      System.exit(1);
    }

    try {
      InputStream urlStream = url.openStream();
      InputStreamReader isr = new InputStreamReader(urlStream);
      BufferedReader br = new BufferedReader(isr);

      while (br.ready()) {
	String line = br.readLine();
	System.out.println(line);
      }

      br.close();

    } catch (IOException ex) {
      err.println("** IOException: " + ex);
      System.exit(1);
    }
  }

}
