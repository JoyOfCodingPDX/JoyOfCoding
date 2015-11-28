package edu.pdx.cs410J.rmi;

import org.junit.Test;

import java.rmi.RemoteException;
import java.util.Iterator;
import java.util.SortedSet;

import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Tests the behavior of the {@link MovieDatabase} class.
 */
public class MovieDatabaseTest {

  private MovieDatabase getMovieDatabase() throws RemoteException {
    return new MovieDatabaseImpl();
  }

  @Test
  public void testCreateMovie() throws RemoteException {
    MovieDatabase db = getMovieDatabase();
    String title = "Movie 1";
    int year = 2008;
    long id = db.createMovie(title, year);
    Movie movie = db.getMovie(id);
    assertNotNull(movie);
    assertEquals(title, movie.getTitle());
    assertEquals(year, movie.getYear());
    assertEquals(0, db.getFilmography(99L).size());
  }

  @Test
  public void testFilmography() throws RemoteException {
    long bobId = 56l;
    long billId = 57l;

    String title1 = "Bob's Movie 1";
    int year1 = 2007;
    String title2 = "Bob's Movie 2";
    int year2 = 2008;

    MovieDatabase db = getMovieDatabase();
    long id1 = db.createMovie(title1, year1);
    long id2 = db.createMovie(title2, year2);
    db.noteCharacter(id1, "Joe", bobId);
    db.noteCharacter(id2, "Frank", bobId);
    db.noteCharacter(id2, "Henry", billId);

    assertEquals(0, db.getFilmography(78L).size());

    SortedSet<Movie> bobMovies = db.getFilmography(bobId);
    assertEquals(2, bobMovies.size());
    Iterator<Movie> bobIter = bobMovies.iterator();
    Movie bobMovie1 = bobIter.next();
    assertEquals(title1, bobMovie1.getTitle());
    assertEquals(year1, bobMovie1.getYear());
    Movie bobMovie2 = bobIter.next();
    assertEquals(title2, bobMovie2.getTitle());
    assertEquals(year2, bobMovie2.getYear());

    SortedSet<Movie> billMovies = db.getFilmography(billId);
    assertEquals(1, billMovies.size());
    Movie billMovie = billMovies.iterator().next();
    assertEquals(title2, billMovie.getTitle());
    assertEquals(year2, billMovie.getYear());
  }

  @Test(expected = IllegalArgumentException.class)
  public void deletingAnNonExistentMovieThrowsIllegalArgumentException() throws RemoteException {
    MovieDatabase db = getMovieDatabase();
    db.deleteMovie(-1);
  }

  @Test
  public void testDeleteMovie() throws RemoteException {
    MovieDatabase db = getMovieDatabase();
    long movieId = db.createMovie("Movie 1", 2015);
    db.deleteMovie(movieId);
    assertThat(db.getMovie(movieId), nullValue());
  }
}
