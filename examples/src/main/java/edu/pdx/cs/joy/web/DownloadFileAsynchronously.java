package edu.pdx.cs.joy.web;

import java.io.PrintStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;

public class DownloadFileAsynchronously {

  private static void getURL(String url, Path path) {
    try (HttpClient client = HttpClient.newBuilder().followRedirects(HttpClient.Redirect.NORMAL).build()) {
      HttpRequest request = HttpRequest.newBuilder()
        .uri(URI.create(url))
        .build();
      CompletableFuture<HttpResponse<Path>> download =
        client.sendAsync(request, HttpResponse.BodyHandlers.ofFile(path));
      download.thenApply(HttpResponse::body);

      download.join();
    }
  }

  public static void main(String[] args) {
    String url = null;
    String fileName = null;

    for (String arg : args) {
      if (url == null) {
        url = arg;

      } else  if (fileName == null) {
        fileName = arg;

      } else {
        usage("Extraneous command line argument: " + arg);
      }
    }

    if (url == null) {
      usage("Missing URL");

    } else if (fileName == null) {
      usage("Missing file name");
    }

    getURL(url, Path.of(fileName));
  }

  private static void usage(String message) {
    PrintStream err = System.err;
    err.println("** " + message);
    err.println("usage: java DownloadFileAsynchronously url file");
    System.exit(1);
  }
}
