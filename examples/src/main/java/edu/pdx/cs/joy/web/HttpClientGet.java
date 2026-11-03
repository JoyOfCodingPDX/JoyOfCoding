package edu.pdx.cs.joy.web;

import java.io.IOException;
import java.io.PrintStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.List;
import java.util.Map;

/**
 * Demonstrates making an HTTP GET request using the {@link java.net.http.HttpClient} class
 *
 * @author David Whitlock
 * @since Winter 2027
 */
public class HttpClientGet {

  /**
   * Fetches the content of a URL using the {@link java.net.http.HttpClient}
   *
   * @param urlString The URL to GET
   * @throws java.io.IOException If a problem occurs while reading URL
   */
  private static void getURL(String urlString) throws IOException, InterruptedException {
    PrintStream out = System.out;
    out.println("Headers");

    try (HttpClient client = HttpClient.newBuilder().followRedirects(HttpClient.Redirect.NORMAL).build()) {
      HttpRequest request = HttpRequest.newBuilder()
        .uri(URI.create(urlString))
        .build();
      HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
      Map<String, List<String>> headers = response.headers().map();
      for (String key : headers.keySet()) {
        out.print("  ");
        if (key != null) {
          out.print(key + ": ");
        }
        for (String value : headers.get(key)) {
          out.print(value);
        }
        out.println();
      }

      out.println("\nContent");
      out.flush();

      out.println(response.body());
    }
  }

  public static void main(String[] args) throws IOException, InterruptedException {
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
    err.println("usage: java HttpClientGet url");
    System.exit(1);
  }
}
