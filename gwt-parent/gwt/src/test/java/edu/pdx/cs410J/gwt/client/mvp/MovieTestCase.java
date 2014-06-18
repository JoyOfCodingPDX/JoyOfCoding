package edu.pdx.cs410J.gwt.client.mvp;

import edu.pdx.cs410J.rmi.Movie;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import static org.junit.Assert.fail;

public abstract class MovieTestCase {

  /**
   * Uses reflection to create a new movie because the constructor is package-private
   *
   * @param title The title of the movie
   * @param year  The year the movie was released
   * @return A new movie
   */
  Movie newMovie(String title, int year) {
    try {
      Constructor<Movie> init = Movie.class.getDeclaredConstructor(String.class, int.class);
      init.setAccessible(true);
      return init.newInstance(title, year);

    } catch (Exception e) {
      fail(e.toString());
      return null;
    }
  }

  /**
   * Uses reflection to invoke the addCharacter method of Movie
   * @param movie
   * @param character
   * @param actorId
   */
  protected void addCharacter(Movie movie, String character, long actorId) {
    try {
      Method m = Movie.class.getDeclaredMethod("addCharacter", String.class, long.class);
      m.setAccessible(true);
      m.invoke(movie, character, actorId);

    } catch (Exception e) {
      fail(e.toString());
    }
  }
}