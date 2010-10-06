package edu.pdx.cs399J.gwt.client.mvp;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import edu.pdx.cs399J.rmi.Movie;

import java.util.ArrayList;

/**
 * Provides access to Movies in the database
 */
@RemoteServiceRelativePath("movies")
public interface MovieService extends RemoteService {
  public ArrayList<Movie> getAllMovies();

  public Movie addCharacter(long movieId, String character, long actorId);

}
