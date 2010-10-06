package edu.pdx.cs399J.gwt.server.mvp;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import edu.pdx.cs399J.gwt.client.mvp.MovieService;
import edu.pdx.cs399J.rmi.Movie;

import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: dwhitlock
 * Date: Oct 6, 2010
 * Time: 1:10:26 AM
 * To change this template use File | Settings | File Templates.
 */
public class MovieServiceImpl extends RemoteServiceServlet implements MovieService {
  @Override
  public ArrayList<Movie> getAllMovies() {
    return null;  //To change body of implemented methods use File | Settings | File Templates.
  }

  @Override
  public Movie addCharacter(long movieId, String character, long actorId) {
    return null;  //To change body of implemented methods use File | Settings | File Templates.
  }
}
