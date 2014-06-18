package edu.pdx.cs410J.gwt.server.mvp;

import com.google.common.collect.Lists;
import com.google.inject.Inject;
import edu.pdx.cs410J.gwt.client.mvp.MovieService;
import edu.pdx.cs410J.rmi.Movie;
import edu.pdx.cs410J.rmi.MovieDatabase;

import java.rmi.RemoteException;
import java.util.ArrayList;

public class MovieServiceImpl implements MovieService {
  private final MovieDatabase db;

  @Inject
  public MovieServiceImpl(MovieDatabase db) {
    this.db = db;
  }

  @Override
  public ArrayList<Movie> getAllMovies() {
    try {
      return Lists.newArrayList(db.getMovies());

    } catch (RemoteException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public Movie addCharacter(long movieId, String character, long actorId) {
    try {
      db.noteCharacter(movieId, character, actorId);
      return db.getMovie(movieId);

    } catch (RemoteException e) {
      throw new RuntimeException(e);
    }
  }
}
