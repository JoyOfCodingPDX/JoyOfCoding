package edu.pdx.cs410J.net;

import java.io.*;
import java.net.*;

/**
 * A <code>Speaker</code> sends strings over a <code>Socket</code> to
 * a <code>Listener</code>.
 */
public class Speaker {
  private static PrintStream err = System.err;

  /**
   * Reads the host name and port number, as well as the strings to
   * send, from the command line.
   */
  public static void main(String[] args) {
    String host = args[0];
    int port = 0;

    try {
      port = Integer.parseInt(args[1]);

    } catch (NumberFormatException ex) {
      err.println("** Bad port number: " + args[1]);
      System.exit(1);
    }

    try {
      Socket socket = new Socket(host, port);
      
      // Send some strings over the socket
      OutputStreamWriter osw = 
	new OutputStreamWriter(socket.getOutputStream());
      PrintWriter speaker = new PrintWriter(osw);

      for (int i = 2; i < args.length; i++) {
	speaker.println(args[i]);
      }

      speaker.close();

    } catch (UnknownHostException ex) {
      err.println("** Could not connect to host: " + host);

    } catch (IOException ex) {
      err.println("** IOException: " + ex);
      System.exit(1);
    }
  }

}
