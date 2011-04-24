package edu.pdx.cs410J.net;

import java.io.*;
import java.net.*;
import java.util.Date;

/**
 * A server that waits for a {@link DateClient} to connect to it.
 * When a client connects, it waits for 5 seconds before returning the
 * current date and time.  This program demonstrates the ports that
 * are actually used when a socket connection is made.  The server
 * will exit after five clients have connected to it.
 *
 * @author David Whitlock
 * @since Fall 2005
 */
public class DateServer {

  private static final PrintStream out = System.out;
  private static final PrintStream err = System.err;

  /**
   * Listens for 5 clients to attach.  The client's request is
   * handled in its own {@link DateServer.Worker} thread.
   */
  public static void main(String[] args) {
    int port = Integer.parseInt(args[0]);

    try {
      ServerSocket server = new ServerSocket(port, 5);
      for (int i = 0; i < 5; i++) {
        out.println("Server listening on " + server.getInetAddress() + 
                    ":" + server.getLocalPort());
        Socket socket = server.accept();
        out.println("Server accepted client " + i);

        // Fire off a thread to write the date
        Worker worker = new Worker(socket);
        worker.start();
      }
      
      out.println("Server exiting");

    } catch (IOException ex) {
      err.println("** IOException: " + ex);
      System.exit(1);
    }
  }

  /**
   * A worker thread that waits five seconds before returning the
   * current date.
   */
  static class Worker extends Thread {
    private final Socket socket;


    Worker(Socket socket) {
      this.socket = socket;
    }

    public void run() {
      try {
        out.println("Server running on " + socket.getLocalAddress() +
                    ":" + socket.getLocalPort());
        out.println("Server communicating with " + socket.getInetAddress() 
                    + ":" + socket.getPort());

        Thread.sleep(5 * 1000);

        OutputStream os = socket.getOutputStream();
        PrintWriter pw = new PrintWriter(os, true);
        Date now = new Date();
        pw.println(now.toString());
        pw.flush();
        pw.close();

      } catch (InterruptedException ex) {
        err.println("** InterruptedException: " + ex);
        System.exit(1);

      } catch (IOException ex) {
        err.println("** IOException: " + ex);
        System.exit(1);
      }
    }
  }

}
