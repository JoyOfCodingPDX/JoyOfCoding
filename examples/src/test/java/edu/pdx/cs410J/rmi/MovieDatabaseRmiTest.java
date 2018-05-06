package edu.pdx.cs410J.rmi;

import org.junit.Test;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

public class MovieDatabaseRmiTest extends MovieDatabaseRmiTestCase {

  @Test
  public void createMovieInRemoteDatabase() throws RemoteException, NotBoundException {
    String title = "Avengers: Infinity War";
    int year = 2018;
    MovieDatabase database = getMovieDatabase();
    long movieId = database.createMovie(title, year);
    Movie movie = database.getMovie(movieId);
    assertThat(movie.getId(), equalTo(movieId));
    assertThat(movie.getTitle(), equalTo(title));
    assertThat(movie.getYear(), equalTo(year));
  }
}
