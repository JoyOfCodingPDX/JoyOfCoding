package edu.pdx.cs410J.rmi;

import java.rmi.*;
import java.util.*;

/**
 * This remote interface allows a client to interact with a database
 * of {@link Movie} objects.
 */
public interface MovieDatabase extends Remote {

  /**
   * Creates a new <code>Movie</code> object on the server.  It
   * returns the id of the movie that was created.
   *
   * @param title
   *        The title of the movie
   * @param year
   *        The year in which the movie was released
   */
  long createMovie(String title, int year)
    throws RemoteException; 
  
  /**
   * Returns the <code>Movie</code> with the given id.
   */
  Movie getMovie(long id) throws RemoteException;

  /**
   * Makes note of a character in a given movie played by a given
   * actor.
   *
   * @throws IllegalArgumentException
   *         The character is already played by someone else
   */
  void noteCharacter(long movieId, String character, long actorId)
    throws RemoteException;

  /**
   * Returns the movie in which a given actor acted.  The movies are
   * sorted by release date.
   */
  SortedSet<Movie> getFilmography(long actorId)
    throws RemoteException;

  /**
   * Performs a query on the database.  The movies that match the
   * query are sorted using the given comparator.
   */
  SortedSet<Movie> executeQuery(Query query, Comparator<Movie> sorter)
    throws RemoteException;

  /**
   * Unregisters this <code>MovieDatabase</code> object with the RMI
   * registry.  Once it is unregistered, this object will no longer be
   * accessible.
   */
  void shutdown() throws RemoteException;

  /**
   * Returns all of the movies in the database
   */
  Collection<Movie> getMovies() throws RemoteException;

  void deleteMovie(long movieId) throws RemoteException;
}
