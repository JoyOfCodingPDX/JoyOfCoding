package edu.pdx.cs399J.rmi;

import java.net.*;
import java.rmi.*;
import java.rmi.server.*;
import java.util.*;

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
    this.movies = new TreeMap<Long, Movie>(new Comparator<Long>() {
      public int compare(Long id1, Long id2) {
        return id1.compareTo(id2);
      }
    });

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
  public void noteCharacter(long movieId, String character, long actorId)
    throws RemoteException {

    // Note local call of remote method
    Movie movie = this.getMovie(movieId);
    if (movie == null) {
      String s = "There is no movie with id " + movieId;
      throw new IllegalArgumentException(s);
    }

    movie.addCharacter(character, actorId);
  }

  /**
   * Returns the movie in which a given actor acted.  The movies are
   * sorted by release date.
   */
  public SortedSet<Movie> getFilmography(final long actorId)
    throws RemoteException {

    Query query = new Query() {
        public boolean satisfies(Movie movie) {
          return movie.getActors().contains(actorId);
        }
      };

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
  public SortedSet<Movie> executeQuery(Query query, Comparator<Movie> sorter)
    throws RemoteException {

    SortedSet<Movie> result = new TreeSet<Movie>(sorter);
    Iterator<Movie> movies = this.movies.values().iterator();
    while (movies.hasNext()) {
      Movie movie = movies.next();
      if (query.satisfies(movie)) {
        result.add(movie);
      }
    }

    return result;
  }

  /**
   * Unregisters this <code>MovieDatabseImpl</code> with the RMI
   * registry.
   */
  public void shutdown() throws RemoteException {
    System.out.println("Shutting down Movie Database");
    UnicastRemoteObject.unexportObject(this, false /* force */);
    System.exit(0);
  }

  public Collection<Movie> getMovies() throws RemoteException {
    return this.movies.values();
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

    } catch (RemoteException ex) {
      ex.printStackTrace(System.err);

    } catch (MalformedURLException ex) {
      ex.printStackTrace(System.err);
    }

  }

}
