package edu.pdx.cs410J.net;

import java.io.*;
import java.net.*;

/**
 * This program listens to a socket.  It expects strings from a
 * <code>Speaker</code>.  The conversation terminates with the word
 * "bye".
 */
public class Listener {
  private static PrintStream out = System.out;
  private static PrintStream err = System.err;

  /**
   * Main program that reads the port number to listen to from the
   * command line.
   */
  public static void main(String[] args) {
    int port = 0;

    try {
      port = Integer.parseInt(args[0]);
    } catch (NumberFormatException ex) {
      err.println("** Bad port number: " + args[0]);
      System.exit(1);
    }

    try {
      // Backlog of 5 messages
      ServerSocket server = new ServerSocket(port, 5);

      // Wait for the Speaker to connect
      Socket socket = server.accept();

      // Read from the Socket
      InputStreamReader isr = 
	new InputStreamReader(socket.getInputStream());
      BufferedReader listener = new BufferedReader(isr);

      String line = "";
      while (!line.equals("bye")) {
	line = listener.readLine();
	out.println(line);
      }

      listener.close();

    } catch (IOException ex) {
      err.println("** IOException: " + ex);
      System.exit(1);
    }
  }
}
