package edu.pdx.cs410J.servlets;

import edu.pdx.cs410J.rmi.MovieDatabase;
import edu.pdx.cs410J.rmi.MovieDatabaseImpl;
import edu.pdx.cs410J.rmi.Movie;
import edu.pdx.cs410J.rmi.Query;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import static javax.servlet.http.HttpServletResponse.*;
import javax.servlet.ServletException;
import java.rmi.RemoteException;
import java.io.*;
import java.util.Map;
import java.util.HashMap;
import java.util.Comparator;

/**
 * A servlet that provides REST access to a movie database
 */
public class MovieDatabaseServlet extends HttpServlet {
  private MovieDatabase database;

  enum DataType {
    MOVIE, ACTOR, CHARACTER, UNKNOWN
  }

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
  protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    Map<String, String> parameters = new HashMap<String, String>();
    BufferedReader br = request.getReader(); // new BufferedReader(new InputStreamReader(request.getInputStream(), Charset.forName("UTF-8")));
    for (String line = br.readLine(); line != null; line = br.readLine()) {
      String[] strings = line.split("=");
      parameters.put(strings[0], strings[1]);
    }

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

  /**
   * Creates a new movie in the database
   *
   * @return Whether or not the movie was successfully created
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
    Comparator<Movie> sorter = new Comparator<Movie>() {

      public int compare(Movie m1, Movie m2) {
        long id1 = m1.getId();
        long id2 = m2.getId();
        return id1 > id2 ? 1 : id1 < id2 ? -1 : 0;
      }
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
        for (Movie movie: this.database.executeQuery(new Query() {

          public boolean satisfies(Movie movie) {
            if (notExists(title)) {
              return movie.getYear() == Integer.parseInt(year);

            } else if (notExists(year)) {
              return movie.getTitle().contains(title);

            } else {
              return movie.getYear() == Integer.parseInt(year) && movie.getTitle().contains(title);  
            }
          }
        }, sorter)) {
          dumpMovie(movie, pw);
        }
      }


    } else {
      try {
        Movie movie = this.database.getMovie(Long.parseLong(idString));
        if (movie == null) {
          response.sendError(SC_BAD_REQUEST, "There is no movie with id " + idString);

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
  protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

  }
}
