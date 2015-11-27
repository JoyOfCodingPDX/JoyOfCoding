package edu.pdx.cs410J.rmi;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RMISecurityManager;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;
import java.util.stream.Collectors;

/**
 * This class provides an implementation of the remote {@link
 * MovieDatabase} interface.  Note that this class does not extened
 * {@link UnicastRemoteObject}.  Therefore, we have to invoke {@link
 * UnicastRemoteObject#exportObject} in the constructor.
 */
public class MovieDatabaseImpl implements MovieDatabase {

  /** A map that maps id's to their movies.  This allows for a 
   * fast lookup of movies by their id. */
  private Map<Long, Movie> movies;

  ////////////////////////  Constructors  /////////////////////////

  /**
   * Creates a new <code>MovieDatabaseImpl</code>.
   * @throws java.rmi.RemoteException
   */
  public MovieDatabaseImpl() throws RemoteException {
    // Sort movies by their id, so the lookup is O(lg n)
    this.movies = new TreeMap<>(Long::compareTo);

    System.out.println("Starting Movie Database");
    UnicastRemoteObject.exportObject(this);
  }

  ///////////////////////  Remote Methods  ////////////////////////

  /**
   * Creates a new <code>Movie</code> object on the server.  It
   * returns the id of the movie that was created.
   *
   * @param title
   *        The title of the movie
   * @param year
   *        The year in which the movie was released
   */
  @Override
  public long createMovie(String title, int year)
    throws RemoteException {
    Movie movie = new Movie(title, year);
    long id = movie.getId();
    this.movies.put(id, movie);
    System.out.println("Created a new movie " + movie);
    return id;
  }
  
  /**
   * Returns the <code>Movie</code> with the given id.
   */
  @Override
  public Movie getMovie(long id) throws RemoteException {
    return this.movies.get(id);
  }

  /**
   * Makes note of a character in a given movie played by a given
   * actor.
   *
   * @throws IllegalArgumentException
   *         There is no movie with <code>movieId</code> or the
   *         character is already played by someone else
   */
  @Override
  public void noteCharacter(long movieId, String character, long actorId)
    throws RemoteException {
    Movie movie = getExistingMovie(movieId);
    movie.addCharacter(character, actorId);
  }

  private Movie getExistingMovie(long movieId) throws RemoteException {
    // Note local call of remote method
    Movie movie = this.getMovie(movieId);
    if (movie == null) {
      String s = "There is no movie with id " + movieId;
      throw new IllegalArgumentException(s);
    }
    return movie;
  }

  /**
   * Returns the movie in which a given actor acted.  The movies are
   * sorted by release date.
   */
  @Override
  public SortedSet<Movie> getFilmography(final long actorId)
    throws RemoteException {

    Query query = movie -> movie.getActors().contains(actorId);

    Comparator<Movie> sorter = new SortMoviesByReleaseDate();

    return executeQuery(query, sorter);
  }

  /**
   * A comparator that sorts movies based on the year in which they
   * were released.  It must be serializable so that it may be sent to
   * the client.  It must be static so that it doesn't have a
   * reference to its outer class.
   */
  static class SortMoviesByReleaseDate 
    implements Comparator<Movie>, java.io.Serializable {

    @Override
    public int compare(Movie movie1, Movie movie2) {
      int year1 = movie1.getYear();
      int year2 = movie2.getYear();
      return year1 > year2 ? 1 : year1 < year2 ? -1 : 0;
    }
  }

  /**
   * Performs a query on the database.  The movies that match the
   * query are sorted using the given comparator.
   */
  @Override
  public SortedSet<Movie> executeQuery(Query query, Comparator<Movie> sorter)
    throws RemoteException {

    return this.movies.values().stream()
      .filter(query::satisfies)
      .collect(Collectors.toCollection(() -> new TreeSet<>(sorter)));
  }

  /**
   * Unregisters this <code>MovieDatabaseImpl</code> with the RMI
   * registry.
   */
  @Override
  public void shutdown() throws RemoteException {
    System.out.println("Shutting down Movie Database");
    UnicastRemoteObject.unexportObject(this, false /* force */);
    System.exit(0);
  }

  @Override
  public Collection<Movie> getMovies() throws RemoteException {
    return this.movies.values();
  }

  @Override
  public void deleteMovie(long movieId) throws RemoteException {
    Movie movie = getExistingMovie(movieId);
    this.movies.remove(movie.getId());
  }

  ///////////////////////  Main Program  /////////////////////////

  /**
   * This main program registers an instance of MovieDatabaseImpl in
   * an RMI registry.
   */
  public static void main(String[] args) {
    String host = args[0];
    int port = Integer.parseInt(args[1]);

    // Install an RMISecurityManager, if there is not a
    // SecurityManager already installed
    if (System.getSecurityManager() == null) {
      System.setSecurityManager(new RMISecurityManager());
    }

    String name = "rmi://" + host + ":" + port + "/MovieDatabase";

    try {
      MovieDatabase db  = new MovieDatabaseImpl();
      Naming.rebind(name, db);

    } catch (RemoteException | MalformedURLException ex) {
      ex.printStackTrace(System.err);
    }

  }

}
