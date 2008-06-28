package edu.pdx.cs399J.servlets;

import edu.pdx.cs399J.rmi.MovieDatabase;
import edu.pdx.cs399J.rmi.MovieDatabaseImpl;
import edu.pdx.cs399J.rmi.Movie;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import static javax.servlet.http.HttpServletResponse.*;
import javax.servlet.ServletException;
import java.rmi.RemoteException;
import java.io.*;
import java.util.Map;
import java.util.Enumeration;
import java.util.HashMap;
import java.nio.charset.Charset;

/**
 * A servlet that provides REST access to a movie database
 */
public class MovieDatabaseRestServlet extends HttpServlet {
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
    if (uri.endsWith("movies")) {
      return DataType.MOVIE;

    } else if (uri.endsWith("actors")) {
      return DataType.ACTOR;

    } else if (uri.endsWith("characters")) {
      return DataType.CHARACTER;

    } else {
      return DataType.UNKNOWN;
    }
  }

  /**
   * Creates information in the database
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
        createMovie(parameters, response);
    }
  }

  /**
   * Creates a new movie in the database
   *
   * @return Whether or not the movie was successfully created
   */
  private void createMovie(Map<String, String> parameters, HttpServletResponse response) throws IOException {
    String title = parameters.get("title");
    if (title == null) {
      response.sendError(SC_BAD_REQUEST, "Missing title");
      return;
    }

    String yearString = parameters.get("year");
    if (yearString == null) {
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
   * Updates information in the database
   */
  protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    response.setContentType("text/plain");

    switch (getDataType(request)) {
      case MOVIE:
        updateMovie(request, response);
    }
  }

  private void updateMovie(HttpServletRequest request, HttpServletResponse response) throws IOException {
//    System.out.println(request.getParameterMap().size() + " parameters");
//    for (Object key : request.getParameterMap().keySet()) {
//      String value = request.getParameter((String) key);
//      System.out.println("  " + key + " -> " + value);
//    }

    String idString = request.getParameter("id");
    if (idString == null) {
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

    String title = request.getParameter("title");
    if (title != null) {
      movie.setTitle(title);
    }

    String yearString = request.getParameter("year");
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
    String idString = request.getParameter("id");
    try {
    if (idString == null) {
      PrintWriter pw = response.getWriter();
      for (Movie movie : this.database.getMovies()) {
        dumpMovie(movie, pw);
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
