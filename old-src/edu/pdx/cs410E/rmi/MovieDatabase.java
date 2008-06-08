package edu.pdx.cs410E.rmi;

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
  public long createMovie(String title, int year) 
    throws RemoteException; 
  
  /**
   * Returns the <code>Movie</code> with the given id.
   */
  public Movie getMovie(long id) throws RemoteException;

  /**
   * Makes note of a character in a given movie played by a given
   * actor.
   *
   * @throws IllegalArgumentException
   *         The character is already played by someone else
   */
  public void noteCharacter(long movieId, String character, String actor) 
    throws RemoteException;

  /**
   * Returns the movie in which a given actor acted.  The movies are
   * sorted by release date.
   */
  public SortedSet getFilmography(String actor) 
    throws RemoteException;

  /**
   * Performs a query on the database.  The movies that match the
   * query are sorted using the given comparator.
   */
  public SortedSet executeQuery(Query query, Comparator sorter) 
    throws RemoteException;

  /**
   * Unregisters this <code>MovieDatabase</code> object with the RMI
   * registry.  Once it is unregistered, this object will no longer be
   * accessible.
   */
  public void shutdown() throws RemoteException;

}
