package edu.pdx.cs410J.servlets;

import edu.pdx.cs410J.rmi.Movie;
import edu.pdx.cs410J.rmi.MovieDatabase;
import edu.pdx.cs410J.rmi.MovieDatabaseImpl;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.rmi.RemoteException;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import static javax.servlet.http.HttpServletResponse.SC_BAD_REQUEST;
import static javax.servlet.http.HttpServletResponse.SC_NOT_FOUND;

/**
 * A servlet that provides REST access to a movie database
 */
public class MovieDatabaseServlet extends HttpServlet {
  private MovieDatabase database;

  enum DataType {
    MOVIE, ACTOR, CHARACTER, UNKNOWN
  }

  @Override
  public void init() throws ServletException {
    try {
      this.database = new MovieDatabaseImpl();

    } catch (RemoteException ex) {
      throw new ServletException(ex);
    }
  }

  /**
   * Returns the type of data requested
   */
  private DataType getDataType(HttpServletRequest request) {
    String uri = request.getRequestURI();
    if (uri.contains("actors")) {
      return DataType.ACTOR;

    } else if (uri.contains("characters")) {
      return DataType.CHARACTER;

    } else if (uri.contains("movies")) {
      return DataType.MOVIE;

    } else {
      return DataType.UNKNOWN;
    }
  }

  /**
   * Updates information in the database
   *
   * @param request The request from the client.  Note that unlike POST, data
   *                arrives in the body (conent) of the request.  {#link getParameter} returns
   *                <code>null</code>
   */
  @Override
  protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    Map<String, String> parameters = readParametersFromRequest(request);

    response.setContentType("text/plain");
    DataType dataType = getDataType(request);
    switch (dataType) {
      case MOVIE:
        updateMovie(parameters, response);
        break;
      case UNKNOWN:
        response.sendError(SC_BAD_REQUEST, "Unknown dataType: " + dataType);
        break;
    }
  }

  private Map<String, String> readParametersFromRequest(HttpServletRequest request) throws IOException {
    Map<String, String> parameters = new HashMap<>();
    BufferedReader br = request.getReader(); // new BufferedReader(new InputStreamReader(request.getInputStream(), Charset.forName("UTF-8")));
    for (String line = br.readLine(); line != null; line = br.readLine()) {
      String[] strings = line.split("=");
      parameters.put(strings[0], strings[1]);
    }
    return parameters;
  }

  /**
   * Creates a new movie in the database
   */
  private void createMovie(HttpServletRequest parameters, HttpServletResponse response) throws IOException {
    String title = parameters.getParameter("title");
    if (notExists(title)) {
      response.sendError(SC_BAD_REQUEST, "Missing title");
      return;
    }

    String yearString = parameters.getParameter("year");
    if (notExists(yearString)) {
      response.sendError(SC_BAD_REQUEST, "Missing year");
      return;
    }

    int year;
    try {
      year = Integer.parseInt(yearString);

    } catch (NumberFormatException ex) {
      response.sendError(SC_BAD_REQUEST, "Malformed year: " + yearString);
      return;
    }

    long movieId = this.database.createMovie(title, year);
    PrintWriter pw = response.getWriter();
    pw.println(movieId);
  }

  /**
   * Returns whether or not the given parameter value was specified
   * @return <code>true</code> if <code>value</code> is <code>null</code> or
   * the empty string
   */
  private boolean notExists(String value) {
    return value == null || value.equals("");
  }

  /**
   * Searches for information in the database
   */
  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    response.setContentType("text/plain");

    switch (getDataType(request)) {
      case MOVIE:
        dumpMovies(request, response);
    }
  }

  /**
   * Creates information in the database
   */
  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    response.setContentType("text/plain");

    switch (getDataType(request)) {
      case MOVIE:
        createMovie(request, response);
    }
  }

  private void updateMovie(Map<String, String> request, HttpServletResponse response) throws IOException {
//    System.out.println(request.getParameterMap().size() + " parameters");
//    for (Object key : request.getParameterMap().keySet()) {
//      String value = request.getParameter((String) key);
//      System.out.println("  " + key + " -> " + value);
//    }

    String idString = request.get("id");
    if (notExists(idString)) {
      response.sendError(SC_BAD_REQUEST, "Missing id");
      return;
    }

    Movie movie;
    try {
      movie = this.database.getMovie(Long.parseLong(idString));

    } catch (NumberFormatException ex) {
      response.sendError(SC_BAD_REQUEST, "Malformed id: " + idString);
      return;
    }

    if (movie == null) {
      response.sendError(SC_BAD_REQUEST, "Unknown movie: " + idString);
      return;
    }

    String title = request.get("title");
    if (title != null) {
      movie.setTitle(title);
    }

    String yearString = request.get("year");
    if (yearString != null) {
      try {
        movie.setYear(Integer.parseInt(yearString));
        response.getWriter().println(idString);

      } catch (NumberFormatException ex) {
        response.sendError(SC_BAD_REQUEST, "Malformed year: " + yearString);
      }
    }
  }

  /**
   * Writes a description of one or more movies back to the client
   */
  private void dumpMovies(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    Comparator<Movie> sorter = (m1, m2) -> {
      long id1 = m1.getId();
      long id2 = m2.getId();
      return id1 > id2 ? 1 : id1 < id2 ? -1 : 0;
    };

    String idString = request.getParameter("id");
    try {
    if (notExists(idString)) {
      PrintWriter pw = response.getWriter();
      final String title = request.getParameter("title");
      final String year = request.getParameter("year");

      if (notExists(title) && notExists(year)) {
        // Dump all movies
        for (Movie movie : this.database.getMovies()) {
          dumpMovie(movie, pw);
        }

      } else {
        for (Movie movie: this.database.executeQuery(movie1 -> {
          if (notExists(title)) {
            return movie1.getYear() == Integer.parseInt(year);

          } else if (notExists(year)) {
            return movie1.getTitle().contains(title);

          } else {
            return movie1.getYear() == Integer.parseInt(year) && movie1.getTitle().contains(title);
          }
        }, sorter)) {
          dumpMovie(movie, pw);
        }
      }


    } else {
      try {
        Movie movie = this.database.getMovie(Long.parseLong(idString));
        if (movie == null) {
          response.sendError(SC_NOT_FOUND, "There is no movie with id " + idString);

        } else {
          dumpMovie(movie, response.getWriter());
        }
      } catch (NumberFormatException ex) {
        response.sendError(SC_BAD_REQUEST, "Invalid movie id: " + idString);
      }
    }
    } catch (RemoteException ex) {
      throw new ServletException(ex);
    }
  }

  /**
   * Writes a description of a movie back to the client
   */
  private void dumpMovie(Movie movie, PrintWriter pw) {
    pw.println(movie.getId() + " \"" + movie.getTitle() + "\" (" + movie.getYear() + ")");
  }

  /**
   * Removes information from the database
   */
  @Override
  protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    Map<String, String> parameters = readParametersFromRequest(request);

    response.setContentType("text/plain");
    DataType dataType = getDataType(request);
    switch (dataType) {
      case MOVIE:
        deleteMovie(parameters, response);
        break;
      case UNKNOWN:
        response.sendError(SC_BAD_REQUEST, "Unknown dataType: " + dataType);
        break;
    }
  }

  private void deleteMovie(Map<String, String> request, HttpServletResponse response) throws IOException {
    String idString = request.get("id");
    if (notExists(idString)) {
      response.sendError(SC_BAD_REQUEST, "Missing id");
      return;
    }

    Movie movie;
    try {
      movie = this.database.getMovie(Long.parseLong(idString));

    } catch (NumberFormatException ex) {
      response.sendError(SC_BAD_REQUEST, "Malformed id: " + idString);
      return;
    }

    if (movie == null) {
      response.sendError(SC_NOT_FOUND, "Unknown movie: " + idString);
      return;
    }

    this.database.deleteMovie(movie.getId());
  }
}
