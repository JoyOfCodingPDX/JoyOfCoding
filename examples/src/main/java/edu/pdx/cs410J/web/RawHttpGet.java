package edu.pdx.cs410J.web;

import java.net.Socket;
import java.io.*;

/**
 * Sends an HTTP request to a server via a socket.  This demonstrates HTTP at a very low level.
 *
 * @author David Whitlock
 * @since Summer 2008
 */
public class RawHttpGet {

  /**
   * Contacts the HTTP server on the given host and port and GETs the given file
   *
   * @param host The name/address of the host machine
   * @param port The HTTP port
   * @param file The file to GET
   * @throws java.io.IOException If we can't communicate with the server
   */
  private static void getHttp(String host, int port, String file) throws IOException {
    Socket socket = new Socket(host, port);
    Writer output = new OutputStreamWriter(socket.getOutputStream());
    InputStreamReader input = new InputStreamReader(socket.getInputStream());

    output.write("GET ");
    output.write(file);
    output.write(" HTTP/1.1");
    output.write("\r\n");
    output.write("Host: ");
    output.write(host);
    output.write("\r\n");
    output.write("\r\n");
    output.flush();

    BufferedReader br = new BufferedReader(input);
    do {
      System.out.println(br.readLine());
    } while (br.ready());

    br.close();
    output.close();
  }

  public static void main(String[] args) throws IOException {
    String host = null;
    String file = null;
    int port = 80;

    for (String arg : args) {
      if (arg.equals("-port")) {
        port = Integer.parseInt(arg);

      } else if (host == null) {
        host = arg;

      } else if (file == null) {
        file = arg;

      } else {
        usage("Extraneous command line argument: " + arg);
      }
    }

    if (host == null) {
      usage("Missing host");

    } else if (file == null) {
      usage("Missing file");
    }

    getHttp(host, port, file);
  }

  private static void usage(String message) {
    PrintStream err = System.err;
    err.println("** " + message);
    err.println("usage: java RawHttpGet [-port port] host file");
    System.exit(1);
  }
}
