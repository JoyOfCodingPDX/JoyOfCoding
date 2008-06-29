package edu.pdx.cs399J.servlets;

import static java.net.HttpURLConnection.*;
import java.io.*;

import org.junit.Test;
import static org.junit.Assert.*;
import edu.pdx.cs399J.web.HttpRequestHelper;

/**
 * Tests the functionality of the <code>MovieDatabaseRestServlet</code>
 */
public class MovieDatabaseServletTest extends HttpRequestHelper {

  /**
   * The URL for accessing the Movie database application
   */
  private static final String DATABASE_URL = "http://localhost:8080/web/movies";

  private final String MOVIES = "";

  @Test
  public void testMissingTitle() throws IOException {
    assertEquals(HTTP_BAD_REQUEST, post(getResourceURL(MOVIES)).getCode());
  }

  @Test
  public void testMissingYear() throws IOException {
    assertEquals(HTTP_BAD_REQUEST, post(getResourceURL(MOVIES), "title", "Title").getCode());
  }

  @Test
  public void testMalformedYear() throws IOException {
    assertEquals(HTTP_BAD_REQUEST, post(getResourceURL(MOVIES), "title", "Title", "year", "asdfa").getCode());
  }

  @Test
  public void testCreateMovie() throws IOException {
    createMovie("Title", "2008");
  }

  private long createMovie(String title, String year) throws IOException {
    Response response = post(getResourceURL(MOVIES), "title", title, "year", year);
    assertEquals(response.getContent(), HTTP_OK, response.getCode());
    assertNotNull(response.getContent());
    return Long.parseLong(response.getContent().trim());
  }

  @Test
  public void testGetAllMovies() throws IOException {
    String title = "testGetAllMovies";
    createMovie(title, "2007");
    Response response = get(getResourceURL(MOVIES));
    assertNotNull(response);
    assertTrue(response.getContent(), response.getContent().contains(title));
    assertTrue(response.getContentLines()> 1);
  }

  @Test
  public void testGetOneMovie() throws IOException {
    String title = "testGetOneMovie" + "-title";
    String year = "1997";
    long id = createMovie(title, year);
    Response response = get(getResourceURL(MOVIES), "id", String.valueOf(id));
    assertNotNull(response);
    assertTrue(response.getContent(), response.getContent().contains(title));
    assertTrue(response.getContent(), response.getContent().contains(year));
    assertEquals(1, response.getContentLines());
  }

  @Test
  public void testGetMoviesByTitle() throws IOException {
    String query = "testGetMoviesByTitle";
    String title1 = query + 1;
    String year1 = "2001";
    String title2 = "NotGetMoviesByTitle" + 2;
    String year2 = "2002";
    String title3 = query + 3;
    String year3 = "2003";

    createMovie(title1, year1);
    createMovie(title2, year2);
    createMovie(title3, year3);

    Response response = get(getResourceURL(MOVIES), "title", query);
    assertTrue(response.getContent(), response.getContent().contains(title1));
    assertFalse(response.getContent(), response.getContent().contains(title2));
    assertTrue(response.getContent(), response.getContent().contains(title3));
  }

  @Test
  public void testGetMoviesByYear() throws IOException {
    String query = "2008";
    String title1 = "testGetMoviesByYear" + 1;
    String year1 = "2001";
    String title2 = "testGetMoviesByTear" + 2;
    String year2 = query;
    String title3 = "testGetMoviesByYear" + 3;
    String year3 = query;

    createMovie(title1, year1);
    createMovie(title2, year2);
    createMovie(title3, year3);

    Response response = get(getResourceURL(MOVIES), "year", query);
    assertFalse(response.getContent(), response.getContent().contains(title1));
    assertTrue(response.getContent(), response.getContent().contains(title2));
    assertTrue(response.getContent(), response.getContent().contains(title3));
  }

  @Test
  public void testGetMoviesByTitleAndYear() throws IOException {
    String titleQuery = "testGetMoviesByTitleAndYear";
    String yearQuery = "2001";

    String title1 = titleQuery + 1;
    String year1 = yearQuery;
    String title2 = "NotGetMoviesByTitleAndYear" + 2;
    String year2 = "2002";
    String title3 = titleQuery + 3;
    String year3 = yearQuery;

    createMovie(title1, year1);
    createMovie(title2, year2);
    createMovie(title3, year3);

    Response response = get(getResourceURL(MOVIES), "title", titleQuery);
    assertTrue(response.getContent(), response.getContent().contains(title1));
    assertFalse(response.getContent(), response.getContent().contains(title2));
    assertTrue(response.getContent(), response.getContent().contains(title3));
  }

  @Test
  public void testUpdateMovie() throws IOException {
    long id = createMovie("Old Title", "1998");

    String newTitle = "New Title";
    String newYear = "2008";
    Response response = put(getResourceURL(MOVIES), "id", String.valueOf(id), "title", newTitle, "year", newYear);
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
