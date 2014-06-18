package edu.pdx.cs410J.gwt.client.mvp;

import com.google.gwt.user.client.rpc.AsyncCallback;
import edu.pdx.cs410J.rmi.Movie;

import java.util.ArrayList;

/**
 * Provides remote access to the movie database
 */
public interface MovieServiceAsync {

  /**
   * Returns all of the movies in the database
   * @param callback Gets the result of the remote call
   */
  public void getAllMovies(AsyncCallback<ArrayList<Movie>> callback);

  void addCharacter(long movieId, String character, long actorId, AsyncCallback<Movie> callback);
}
