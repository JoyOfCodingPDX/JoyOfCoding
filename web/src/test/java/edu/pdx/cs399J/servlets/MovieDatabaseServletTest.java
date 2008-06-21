package edu.pdx.cs399J.servlets;

import junit.framework.TestCase;

import java.net.*;
import static java.net.HttpURLConnection.*;
import java.io.*;
import java.nio.charset.Charset;

/**
 * Tests the REST functionality of the <code>MovieDatabaseServlet</code>
 */
public class MovieDatabaseServletTest extends TestCase {

  /**
   * The URL for accessing the Movie database application
   */
  private static final String DATABASE_URL = "http://localhost:8080/web/movies";

  private final String MOVIES = "";

  public void testMissingTitle() throws IOException {
    assertEquals(HTTP_BAD_REQUEST, put(MOVIES).code);
  }

  public void testMissingYear() throws IOException {
    assertEquals(HTTP_BAD_REQUEST, put(MOVIES, "title", "Title").code);
  }

  public void testMalformedYear() throws IOException {
    assertEquals(HTTP_BAD_REQUEST, put(MOVIES, "title", "Title", "year", "asdfa").code);
  }

  public void testCreateMovie() throws IOException {
    Response response = put(MOVIES, "title", "Title", "year", "2008");
    assertEquals(response.content, HTTP_OK, response.code);
    assertNotNull(response.content);
    Integer.parseInt(response.content.trim());
  }

  public void testGetAllMovies() throws IOException {
    String title = this.getName();
    Response response = put(MOVIES, "title", title, "year", "2007");
    assertEquals(HTTP_OK, response.code);
    response = get(MOVIES);
    assertNotNull(response);
    assertTrue(response.content, response.content.contains(title));
    assertTrue(response.contentLines > 1);
  }

  public void testGetOneMovie() throws IOException {
    String title = this.getName() + "-title";
    String year = "1997";
    Response response = put(MOVIES, "title", title, "year", year);
    assertEquals(HTTP_OK, response.code);
    long id = Long.parseLong(response.content);
    response = get(MOVIES, "id", String.valueOf(id));
    assertNotNull(response);
    assertTrue(response.content, response.content.contains(title));
    assertTrue(response.content, response.content.contains(year));
    assertEquals(1, response.contentLines);
  }

  public void testUpdateMovie() throws IOException {
    Response response = put(MOVIES, "title", "Old Title", "year", "1998");
    assertEquals(HTTP_OK, response.code);
    long id = Long.parseLong(response.content);

    String newTitle = "New Title";
    String newYear = "2008";
    response = post(MOVIES, "id", String.valueOf(id), "title", newTitle, "year", newYear);
    assertEquals(response.content, HTTP_OK, response.code);
    assertEquals(1, response.contentLines);
    assertEquals(String.valueOf(id), response.content);

    response = get(MOVIES, "id", String.valueOf(id));
    assertEquals(HTTP_OK, response.code);
    assertEquals(1, response.contentLines);
    assertTrue(response.content.contains(newTitle));
    assertTrue(response.content.contains(newYear));
  }

  /**
   * Encapsulates a response to an HTTP request
   */
  static class Response {

    private final int code;

    private final String content;

    private int contentLines = 0;

    public Response(HttpURLConnection conn) throws IOException {
      this.code = conn.getResponseCode();
      if (this.code != HTTP_OK) {
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

  /**
   * Performs an HTTP GET for the given resource
   *
   * @param resource   The resource to get
   * @param parameters The key/value query parameters
   * @return A <code>Response</code> summarizing the result of the GET
   */
  private Response get(String resource, String... parameters) throws IOException {
    checkParameters(parameters);

    StringBuilder query = encodeParameters(parameters);
    if (query.length() > 0) {
      query.insert(0, '?');
    }

    URL url = new URL(DATABASE_URL + resource + query);
    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
    conn.setRequestMethod("GET");
    conn.setDoOutput(true);
    conn.setDoInput(true);

    return new Response(conn);

  }

  /**
   * Performs an HTTP POST for the given resource
   * @param resource The resource to POST to
   * @param parameters The key/value parameters
   * @return A <code>Response</code> summarizing the result of the POST
   */
  private Response post(String resource, String... parameters) throws IOException {
    checkParameters(parameters);

    StringBuilder data = encodeParameters(parameters);

    URL url = new URL(DATABASE_URL + resource);
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
   * @throws UnsupportedEncodingException If we can't encode UTF-8
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
   * Performs an HTTP PUT for the given resource
   *
   * @param resource   The name of the resource
   * @param parameters key/value parameters to the put
   * @return A <code>Reponse</code> summarizing the result of the PUT
   */
  private Response put(String resource, String... parameters) throws IOException {
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

    URL url = new URL(DATABASE_URL + resource);
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

}
