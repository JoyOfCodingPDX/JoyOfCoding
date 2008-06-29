package edu.pdx.cs399J.servlets;

import static java.net.HttpURLConnection.*;
import java.io.*;

import org.junit.Test;
import static org.junit.Assert.*;
import edu.pdx.cs399J.web.HttpRequestHelper;

/**
 * Tests the functionality of the <code>MovieDatabaseRestServlet</code>
 */
public class MovieDatabaseRestServletTest extends HttpRequestHelper {

  /**
   * The URL for accessing the Movie database application
   */
  private static final String DATABASE_URL = "http://localhost:8080/web/movies/rest";

  private final String MOVIES = "";

  @Test
  public void testMissingTitle() throws IOException {
    assertEquals(HTTP_BAD_REQUEST, put(getResourceURL(MOVIES)).getCode());
  }

  @Test
  public void testMissingYear() throws IOException {
    assertEquals(HTTP_BAD_REQUEST, put(getResourceURL(MOVIES), "title", "Title").getCode());
  }

  @Test
  public void testMalformedYear() throws IOException {
    assertEquals(HTTP_BAD_REQUEST, put(getResourceURL(MOVIES), "title", "Title", "year", "asdfa").getCode());
  }

  @Test
  public void testCreateMovie() throws IOException {
    Response response = put(getResourceURL(MOVIES), "title", "Title", "year", "2008");
    assertEquals(response.getContent(), HTTP_OK, response.getCode());
    assertNotNull(response.getContent());
    Integer.parseInt(response.getContent().trim());
  }

  @Test
  public void testGetAllMovies() throws IOException {
    String title = "testGetAllMovies";
    Response response = put(getResourceURL(MOVIES), "title", title, "year", "2007");
    assertEquals(HTTP_OK, response.getCode());
    response = get(getResourceURL(MOVIES));
    assertNotNull(response);
    assertTrue(response.getContent(), response.getContent().contains(title));
    assertTrue(response.getContentLines()> 1);
  }

  @Test
  public void testGetOneMovie() throws IOException {
    String title = "testGetOneMovie" + "-title";
    String year = "1997";
    Response response = put(getResourceURL(MOVIES), "title", title, "year", year);
    assertEquals(HTTP_OK, response.getCode());
    long id = Long.parseLong(response.getContent());
    response = get(getResourceURL(MOVIES), "id", String.valueOf(id));
    assertNotNull(response);
    assertTrue(response.getContent(), response.getContent().contains(title));
    assertTrue(response.getContent(), response.getContent().contains(year));
    assertEquals(1, response.getContentLines());
  }

  @Test
  public void testUpdateMovie() throws IOException {
    Response response = put(getResourceURL(MOVIES), "title", "Old Title", "year", "1998");
    assertEquals(HTTP_OK, response.getCode());
    long id = Long.parseLong(response.getContent());

    String newTitle = "New Title";
    String newYear = "2008";
    response = post(getResourceURL(MOVIES), "id", String.valueOf(id), "title", newTitle, "year", newYear);
    assertEquals(response.getContent(), HTTP_OK, response.getCode());
    assertEquals(1, response.getContentLines());
    assertEquals(String.valueOf(id), response.getContent());

    response = get(getResourceURL(MOVIES), "id", String.valueOf(id));
    assertEquals(HTTP_OK, response.getCode());
    assertEquals(1, response.getContentLines());
    assertTrue(response.getContent().contains(newTitle));
    assertTrue(response.getContent().contains(newYear));
  }

  private String getResourceURL(String resource) {
    return DATABASE_URL + resource;
  }

}
