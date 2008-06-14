package edu.pdx.cs399J.rmi;

import junit.framework.TestCase;

import java.rmi.RemoteException;
import java.util.SortedSet;
import java.util.Iterator;

/**
 * Tests the behavior of the {@link MovieDatabase} class.
 */
public class MoveDatabaseTest extends TestCase {

  private MovieDatabase getMovieDatabase() throws RemoteException {
    return new MovieDatabaseImpl();
  }

  public void testCreateMovie() throws RemoteException {
    MovieDatabase db = getMovieDatabase();
    String title = "Moive 1";
    int year = 2008;
    long id = db.createMovie(title, year);
    Movie movie = db.getMovie(id);
    assertNotNull(movie);
    assertEquals(title, movie.getTitle());
    assertEquals(year, movie.getYear());
    assertEquals(0, db.getFilmography("Bob").size());
  }

  public void testFilmography() throws RemoteException {
    String bob = "Bob";
    String bill = "Bill";

    String title1 = "Bob's Movie 1";
    int year1 = 2007;
    String title2 = "Bob's Movie 2";
    int year2 = 2008;

    MovieDatabase db = getMovieDatabase();
    long id1 = db.createMovie(title1, year1);
    long id2 = db.createMovie(title2, year2);
    db.noteCharacter(id1, "Joe", bob);
    db.noteCharacter(id2, "Frank", bob);
    db.noteCharacter(id2, "Henry", bill);

    assertEquals(0, db.getFilmography("Stan").size());

    SortedSet<Movie> bobMovies = db.getFilmography(bob);
    assertEquals(2, bobMovies.size());
    Iterator<Movie> bobIter = bobMovies.iterator();
    Movie bobMovie1 = bobIter.next();
    assertEquals(title1, bobMovie1.getTitle());
    assertEquals(year1, bobMovie1.getYear());
    Movie bobMovie2 = bobIter.next();
    assertEquals(title2, bobMovie2.getTitle());
    assertEquals(year2, bobMovie2.getYear());

    SortedSet<Movie> billMovies = db.getFilmography(bill);
    assertEquals(1, billMovies.size());
    Movie billMovie = billMovies.iterator().next();
    assertEquals(title2, billMovie.getTitle());
    assertEquals(year2, billMovie.getYear());
  }
}
