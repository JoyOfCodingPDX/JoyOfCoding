package edu.pdx.cs410J.rmi;

import java.io.Serializable;

/**
 * This interface represents a query that can be performed on movies
 * in a movie database.  It is serializable so that it
 * <code>Query</code> objects can be sent between the client and the
 * server. 
 */
@FunctionalInterface
public interface Query extends Serializable {

  /**
   * Returns true if the given <code>Movie</code> satifies this
   * <code>Query</code>. 
   */
  public boolean satisfies(Movie movie);

}
