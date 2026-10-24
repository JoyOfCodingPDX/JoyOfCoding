package edu.pdx.cs.joy.web;

import com.google.common.annotations.VisibleForTesting;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * A helper class that provides methods for requesting resources via HTTP
 */
public class HttpRequestHelper {

  private static final HttpClient CLIENT = HttpClient.newBuilder()
    .followRedirects(HttpClient.Redirect.NORMAL)
    .build();

  private final String urlString;

  public HttpRequestHelper(String urlString) {
    this.urlString = urlString;
  }

  /**
   * Performs an HTTP GET
   *
   * @param parameters The key/value query parameters
   * @return A <code>Response</code> summarizing the result of the GET
   */
  public Response get(Map<String, String> parameters) throws IOException {
    StringBuilder query = encodeParameters(parameters);
    if (!query.isEmpty()) {
      query.insert(0, '?');
    }

    HttpRequest request = HttpRequest.newBuilder(URI.create(urlString + query))
      .GET()
      .build();
    return send(request);
  }

  /**
   * Performs an HTTP POST
   *
   * @param parameters The key/value parameters
   * @return A <code>Response</code> summarizing the result of the POST
   */
  public Response post(Map<String, String> parameters) throws IOException {
    return sendEncodedRequest("POST", parameters);
  }

  /**
   * Performs an HTTP DELETE
   *
   * @param parameters The key/value parameters
   * @return A <code>Response</code> summarizing the result of the POST
   */
  public Response delete(Map<String, String> parameters) throws IOException {
    return sendEncodedRequest("DELETE", parameters);
  }

  private Response sendEncodedRequest(String requestMethod, Map<String, String> parameters) throws IOException {
    String data = encodeParameters(parameters).toString();

    HttpRequest request = HttpRequest.newBuilder(URI.create(urlString))
      .header("Content-Type", "application/x-www-form-urlencoded")
      .method(requestMethod, HttpRequest.BodyPublishers.ofString(data, StandardCharsets.UTF_8))
      .build();
    return send(request);
  }

  private Response send(HttpRequest request) throws IOException {
    try {
      HttpResponse<String> response = CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
      return new Response(response);

    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new IOException("Interrupted while sending HTTP request", e);
    }
  }

  /**
   * Encodes parameters to be sent to the server via an HTTP GET, POST, or DELETE
   * @param parameters The parameter key/value pairs
   * @return The encoded parameters
   */
  private StringBuilder encodeParameters(Map<String, String> parameters) {
    StringBuilder query = new StringBuilder();
    for (Iterator<Map.Entry<String, String>> iter = parameters.entrySet().iterator(); iter.hasNext(); ) {
      Map.Entry<String, String> pair = iter.next();
      String key = pair.getKey();
      String value = pair.getValue();
      query.append(URLEncoder.encode(key, StandardCharsets.UTF_8));
      query.append("=");
      query.append(URLEncoder.encode(value, StandardCharsets.UTF_8));
      if (iter.hasNext()) {
        query.append("&");
      }
    }
    return query;
  }

  /**
   * Performs an HTTP PUT on the given URL
   *
   * @param parameters key/value parameters to the put
   * @return A <code>Response</code> summarizing the result of the PUT
   */
  public Response put(Map<String, String> parameters) throws IOException {
    StringBuilder data = new StringBuilder();
        parameters.forEach((key, value) -> {
          data.append(key);
          data.append("=");
          data.append(value);
          data.append("\n");
        });

    HttpRequest request = HttpRequest.newBuilder(URI.create(urlString))
      .header("Content-Type", "text/plain")
      .PUT(HttpRequest.BodyPublishers.ofString(data.toString(), StandardCharsets.UTF_8))
      .build();
    return send(request);
  }

  /**
   * Encapsulates a response to an HTTP request
   */
  public static class Response {

    private final int httpStatusCode;

    private final String content;

    private int contentLines = 0;

    private Response(HttpResponse<String> response) {
      this.httpStatusCode = response.statusCode();

      StringBuilder content = new StringBuilder();
      String body = response.body();
      if (body != null) {
        try (BufferedReader rd = new BufferedReader(new StringReader(body))) {
          String line;
          while ((line = rd.readLine()) != null) {
            content.append(line);
            content.append("\n");
            contentLines++;
          }
        } catch (IOException e) {
          throw new UncheckedIOException(e);
        }
      }

      this.content = content.toString().trim();
    }

    @VisibleForTesting
    public Response(String content) {
      this.content = content;
      this.httpStatusCode = HttpURLConnection.HTTP_OK;
    }

    /**
     * Returns the HTTP status code of the response
     * @see java.net.http.HttpResponse#statusCode()
     */
    public int getHttpStatusCode() {
      return httpStatusCode;
    }

    /**
     * Returns the (presumably textual) response from the URL
     */
    public String getContent() {
      return content;
    }

    /**
     * Returns the number of lines in the response's content
     */
    public int getContentLines() {
      return contentLines;
    }
  }


  private static Map<String, String> arrayToMap(String[] parameters) {
    Map<String, String> params = new HashMap<>();
    for (int i = 0; i < parameters.length; i++) {
      String key = parameters[i];
      i++;
      String value = parameters[i];
      params.put(key, value);
    }
    return params;
  }

  /**
   * A main method that requests a resource from a URL using a given HTTP method
   */
  public static void main(String[] args) throws IOException {
    String method = args[0];
    String url = args[1];
    String[] parameters = new String[args.length - 2];
    System.arraycopy(args, 2, parameters, 0, parameters.length);
    Map<String, String> map = arrayToMap(parameters);

    HttpRequestHelper helper = new HttpRequestHelper(url);

    Response response;
    if (method.equalsIgnoreCase("PUT")) {
      response = helper.put(map);

    } else if (method.equalsIgnoreCase("GET")) {
      response = helper.get(map);

    } else if (method.equalsIgnoreCase("POST")) {
      response = helper.post(map);

    } else {
      System.err.println("** Unknown method: " + method);
      return;
    }

    System.out.println("Returned code " + response.getHttpStatusCode() + " and " +
      response.getContentLines() + " lines of content\n");
    System.out.println(response.getContent());

  }

  public static class RestException extends RuntimeException {

    private final int httpStatusCode;

    public RestException(int httpStatusCode, String message) {
      super("HTTP Status Code " + httpStatusCode + ": " + message);

      this.httpStatusCode = httpStatusCode;
    }

    public int getHttpStatusCode() {
      return this.httpStatusCode;
    }
  }
}
