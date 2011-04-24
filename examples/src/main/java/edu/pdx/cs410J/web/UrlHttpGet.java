package edu.pdx.cs410J.web;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;

/**
 * Demonstrates making an HTTP GET request using the {@link java.net.URLConnection} class
 *
 * @author David Whitlock
 * @since Summer 2008
 */
public class UrlHttpGet {

  /**
   * Fetches the content of a URL using an HTTP GET
   *
   * @param urlString The URL to GET
   * @throws java.io.IOException If a problem occurs while reading URL
   */
  private static void getURL(String urlString) throws IOException {
    URL url = new URL(urlString);
    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
    conn.setRequestMethod("GET");
    conn.connect();

    PrintStream out = System.out;
    out.println("Headers");

    Map<String,List<String>> headers = conn.getHeaderFields();
    for (String key : headers.keySet() ) {
      out.print("  ");
      if (key != null) {
        out.print(key + ": ");
      }
      for (String value : headers.get(key)) {
        out.print(value);
      }
      out.println("\n");
    }

    out.println("\nContent");
    out.flush();

    BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
    do {
      out.println(br.readLine());
    } while (br.ready());

    br.close();
  }

  public static void main(String[] args) throws IOException {
    String url = null;

    for (String arg : args) {
      if (url == null) {
        url = arg;

      } else {
        usage("Extraneous command line argument: " + arg);
      }
    }

    if (url == null) {
      usage("Missing URL");
    }

    getURL(url);
  }

  private static void usage(String message) {
    PrintStream err = System.err;
    err.println("** " + message);
    err.println("usage: java UrlHttpGet url");
    System.exit(1);
  }
}
