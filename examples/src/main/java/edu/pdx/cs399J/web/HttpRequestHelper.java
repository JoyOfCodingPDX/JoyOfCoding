package edu.pdx.cs399J.web;

import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.io.*;
import java.nio.charset.Charset;

/**
 * A helper class that provides methods for requesting resources via HTTP
 */
public class HttpRequestHelper {
  /**
   * Performs an HTTP GET on the given URL
   *
   * @param urlString The URL to get
   * @param parameters The key/value query parameters
   * @return A <code>Response</code> summarizing the result of the GET
   */
  protected Response get(String urlString, String... parameters) throws IOException {
    checkParameters(parameters);

    StringBuilder query = encodeParameters(parameters);
    if (query.length() > 0) {
      query.insert(0, '?');
    }

    URL url = new URL(urlString + query);
    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
    conn.setRequestMethod("GET");
    conn.setDoOutput(true);
    conn.setDoInput(true);

    return new Response(conn);

  }

  /**
   * Performs an HTTP POST to the given URL
   * @param urlString The URL to post to
   * @param parameters The key/value parameters
   * @return A <code>Response</code> summarizing the result of the POST
   */
  protected Response post(String urlString, String... parameters) throws IOException {
    checkParameters(parameters);

    StringBuilder data = encodeParameters(parameters);

    URL url = new URL(urlString);
    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
    conn.setRequestMethod("POST");
    conn.setDoOutput(true);

    OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream(), Charset.forName("UTF-8"));
    wr.write(data.toString());
    wr.flush();

    Response response = new Response(conn);
    wr.close();

    return response;

  }

  /**
   * Encodes parameters to be sent to the server via an HTTP GET, POST, or DELETE
   * @param parameters The parameter key/value pairs
   * @return The encoded parameters
   * @throws java.io.UnsupportedEncodingException If we can't encode UTF-8
   */
  private StringBuilder encodeParameters(String... parameters) throws UnsupportedEncodingException {
    StringBuilder query = new StringBuilder();
    for (int i = 0; i < parameters.length; i += 2) {
      String key = parameters[i];
      String value = parameters[i + 1];
      query.append(URLEncoder.encode(key, "UTF-8"));
      query.append("=");
      query.append(URLEncoder.encode(value, "UTF-8"));
      if (i < parameters.length - 2) {
        query.append("&");
      }
    }
    return query;
  }

  private void checkParameters(String... parameters) {
    if (parameters.length % 2 != 0) {
      String s = "You must specify an even number of parameters (key/value pairs)";
      throw new IllegalArgumentException(s);
    }
  }

  /**
   * Performs an HTTP PUT on the given URL
   *
   * @param urlString The URL to put to
   * @param parameters key/value parameters to the put
   * @return A <code>Reponse</code> summarizing the result of the PUT
   */
  protected Response put(String urlString, String... parameters) throws IOException {
    checkParameters(parameters);

    StringBuilder data = new StringBuilder();
    for (int i = 0; i < parameters.length; i += 2) {
      String key = parameters[i];
      String value = parameters[i + 1];
      data.append(key);
      data.append("=");
      data.append(value);
      data.append("\n");
    }

    URL url = new URL(urlString);
    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
    conn.setRequestMethod("PUT");
    conn.setDoOutput(true);
    conn.setDoInput(true);
    conn.setRequestProperty("Content-Type", "text/plain");
    conn.setRequestProperty("Context-Length", String.valueOf(data.length()));

    OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream(), Charset.forName("UTF-8"));
    wr.write(data.toString());
    wr.flush();

    Response response = new Response(conn);
    wr.close();

    return response;
  }

  /**
   * Encapsulates a response to an HTTP request
   */
  public static class Response {

    public int getCode() {
      return code;
    }

    public String getContent() {
      return content;
    }

    public int getContentLines() {
      return contentLines;
    }

    private final int code;

    private final String content;

    private int contentLines = 0;

    public Response(HttpURLConnection conn) throws IOException {
      this.code = conn.getResponseCode();
      if (this.code != java.net.HttpURLConnection.HTTP_OK) {
        this.content = conn.getResponseMessage();
        return;
      }

      StringBuilder content = new StringBuilder();
      BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
      String line;
      while ((line = rd.readLine()) != null) {
        content.append(line);
        content.append("\n");
        contentLines++;
      }
      rd.close();

      this.content = content.toString().trim();
    }

  }
}
