package edu.pdx.cs410J.gwt.client.mvp;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import edu.pdx.cs410J.rmi.Movie;

import java.util.ArrayList;

/**
 * Provides access to Movies in the database
 */
@RemoteServiceRelativePath("GWT.rpc")
public interface MovieService extends RemoteService {
  public ArrayList<Movie> getAllMovies();

  public Movie addCharacter(long movieId, String character, long actorId);

}
