package edu.pdx.cs410J.net;

import java.io.*;
import java.net.*;

/**
 * A client that connects to a {@link DateServer} and reads the {@link
 * java.util.Date} sent to it as a <code>String</code>.  The important
 * things to note are the ports that are used to communicate.
 *
 * @author David Whitlock
 * @since Fall 2005
 */
public class DateClient {

  private static final PrintStream out = System.out;
  private static final PrintStream err = System.err;

  /**
   * Connects to a server and receives the date.
   */
  public static void main(String[] args) {
    String host = args[0];
    int port = Integer.parseInt(args[1]);

    try {
      out.println("Client connecting to " + host + ":" + port);
      Socket socket = new Socket(host, port);

      out.println("Client running on " + socket.getLocalAddress() +
                  ":" + socket.getLocalPort());
      out.println("Client communicating with " + socket.getInetAddress() 
                  + ":" + socket.getPort());

      InputStream is = socket.getInputStream();
      InputStreamReader isr = new InputStreamReader(is);
      BufferedReader br = new BufferedReader(isr);

      out.println("Client read " + br.readLine());
      out.close();

    } catch (IOException ex) {
      err.println("** IOException: " + ex);
      System.exit(1);
    }
  }

}
