package edu.pdx.cs410J.servlets;

import edu.pdx.cs410J.web.NewHttpRequestHelper;
import edu.pdx.cs410J.web.NewHttpRequestHelper.Response;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Map;

import static java.net.HttpURLConnection.*;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests the functionality of the <code>MovieDatabaseRestServlet</code>
 */
public class MovieDatabaseServletIT {

  /**
   * The URL for accessing the Movie database application
   */
  private static final String MOVIES = "http://localhost:8080/web/movies";

  private NewHttpRequestHelper http() {
    return new NewHttpRequestHelper(MOVIES);
  }

  @Test
  public void testMissingTitle() throws IOException {
    assertEquals(HTTP_BAD_REQUEST, http().post(Map.of()).getHttpStatusCode());
  }

  @Test
  public void testMissingYear() throws IOException {
    assertEquals(HTTP_BAD_REQUEST, http().post(Map.of("title", "Title")).getHttpStatusCode());
  }

  @Test
  public void testMalformedYear() throws IOException {
    assertEquals(HTTP_BAD_REQUEST, http().post(Map.of("title", "Title", "year", "asdfa")).getHttpStatusCode());
  }

  @Test
  public void testCreateMovie() throws IOException {
    createMovie("Title", "2008");
  }

  private long createMovie(String title, String year) throws IOException {
    Response response = http().post(Map.of("title", title, "year", year));
    assertEquals(HTTP_OK, response.getHttpStatusCode(), response.getContent());
    assertNotNull(response.getContent());
    return Long.parseLong(response.getContent().trim());
  }

  @Test
  public void testGetAllMovies() throws IOException {
    String title1 = "testGetAllMovies1";
    createMovie(title1, "2007");
    String title2 = "testGetAllMovies2";
    createMovie(title2, "2013");
    Response response = http().get(Map.of());
    assertNotNull(response);
    String content = response.getContent();
    assertTrue(content.contains(title1), content);
    assertTrue(content.contains(title2), content);
    int lines = response.getContentLines();
    assertTrue(lines > 1, () -> "Expected more than 1 line, only got " + lines + "\n" +
      "In content: " + content);
  }

  @Test
  public void testGetOneMovie() throws IOException {
    String title = "testGetOneMovie" + "-title";
    String year = "1997";
    long id = createMovie(title, year);
    Response response = http().get(Map.of("id", String.valueOf(id)));
    assertNotNull(response);
    assertTrue(response.getContent().contains(title), response.getContent());
    assertTrue(response.getContent().contains(year), response.getContent());
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

    Response response = http().get(Map.of("title", query));
    assertTrue(response.getContent().contains(title1), response.getContent());
    assertFalse(response.getContent().contains(title2), response.getContent());
    assertTrue(response.getContent().contains(title3), response.getContent());
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

    Response response = http().get(Map.of("year", query));
    assertFalse(response.getContent().contains(title1), response.getContent());
    assertTrue(response.getContent().contains(title2), response.getContent());
    assertTrue(response.getContent().contains(title3), response.getContent());
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

    Response response = http().get(Map.of("title", titleQuery));
    assertTrue(response.getContent().contains(title1), response.getContent());
    assertFalse(response.getContent().contains(title2), response.getContent());
    assertTrue(response.getContent().contains(title3), response.getContent());
  }

  @Test
  public void testUpdateMovie() throws IOException {
    long id = createMovie("Old Title", "1998");

    String newTitle = "New Title";
    String newYear = "2008";
    Response response =
      http().put(Map.of("id", String.valueOf(id), "title", newTitle, "year", newYear));
    assertEquals(HTTP_OK, response.getHttpStatusCode(), response.getContent());
    assertEquals(1, response.getContentLines());
    assertEquals(String.valueOf(id), response.getContent());

    response = http().get(Map.of("id", String.valueOf(id)));
    assertEquals(HTTP_OK, response.getHttpStatusCode());
    assertEquals(1, response.getContentLines());
    assertTrue(response.getContent().contains(newTitle));
    assertTrue(response.getContent().contains(newYear));
  }

  @Test
  public void canDeleteMovieFromServer() throws IOException {
    long id = createMovie("Movie to be deleted", "2015");

    Response response = http().delete(Map.of("id", String.valueOf(id)));
    assertThat(response.getContent(), response.getHttpStatusCode(), equalTo(HTTP_OK));

    response = http().get(Map.of("id", String.valueOf(id)));
    assertThat(response.getContent(), response.getHttpStatusCode(), equalTo(HTTP_NOT_FOUND));
  }

}
