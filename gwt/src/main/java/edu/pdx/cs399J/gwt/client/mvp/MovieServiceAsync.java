package edu.pdx.cs399J.gwt.client.mvp;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import edu.pdx.cs399J.rmi.Movie;

import java.util.ArrayList;

/**
 * Provides remote access to the movie database
 */
@RemoteServiceRelativePath("movies")
public interface MovieServiceAsync {

  /**
   * Returns all of the movies in the database
   * @param callback Gets the result of the remote call
   */
  public void getAllMovies(AsyncCallback<ArrayList<Movie>> callback);

  void addCharacter(long movieId, String character, long actorId, AsyncCallback<Movie> callback);
}
