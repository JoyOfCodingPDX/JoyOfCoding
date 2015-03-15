package edu.pdx.cs410J.rmi;

import java.io.Serializable;
import java.util.*;

/**
 * This class represents a remote <code>Movie</code> object.  It is
 * {@link Serializable} because instances of <code>Movie</code> are
 * sent from the server to client.  Because it does not implement the
 * {@link java.rmi.Remote} interface, the client JVM receives copies
 * of the <code>Movie</code> object. That is, changes to a
 * <code>Movie</code> made on by the client are not reflected in the
 * server.
 */
public class Movie implements Serializable {
  
  /** The next movie id */
  private static long nextId = 0;

  /** A unique number that identifies this movie */
  private long id;

  /** The title of the movie */
  private String title;

  /** The year the movie was released */
  private int year;

  /** A map of the characters in a movie to the id of the actor who played
   * them. */
  private Map<String, Long> characters;

  private int numberOfAwards;

  /////////////////////////  Constructors  ////////////////////////

  /**
   * Creates a new <code>Movie</code> with the given title and release
   * year.  Movies should only be created on the server side.
   */
  Movie(String title, int year) {
    this.id = Movie.nextId++;
    this.title = title;
    this.year = year;
    this.characters = new HashMap<>();
  }

  //////////////////////  Accessor Methods  ///////////////////////

  /**
   * Returns this <code>Movie</code>'s id
   */
  public long getId() {
    return this.id;
  }

  /**
   * Package-protected method for setting the id
   * @param id The id for this movie
   */
  void setId(long id) {
    this.id = id;
  }

  /**
   * Returns the title of this <code>Movie</code>
   */
  public String getTitle() {
    return this.title;
  }

  /**
   * Sets the title of this <code>Movie</code>
   */
  public void setTitle(String title) {
    this.title = title;
  }

  /**
   * Returns the year in which this movie was released
   */
  public int getYear() {
    return this.year;
  }

  /**
   * Sets the year in which this movie was released
   */
  public void setYear(int year) {
    this.year = year;
  }

  /**
   * Returns a map of character names to the actor that played
   * them.
   */
  public Map<String, Long> getCharacters() {
    return this.characters;
  }

  /**
   * Returns the ids of the actors that are in this movie
   */
  public Set<Long> getActors() {
    return new HashSet<>(this.characters.values());
  }

  /**
   * Makes note of a character in the movie played by a given actor.
   * This behavior is intended to be server-side only.
   *
   * @throws IllegalArgumentException
   *         There is a character by that name that is played by a
   *         different actor.
   */
  void addCharacter(String character, long actor) {
    Long a = this.characters.get(character);
    if (a != null && !a.equals(actor)) {
      String s = "The character " + character +
        " is already played by " + a;
      throw new IllegalArgumentException(s);
    }

    this.characters.put(character, actor);
  }

  //////////////////////  Utility Methods  /////////////////////////

  /**
   * Returns a brief textual representation of this <code>Movie</code>
   */
  public String toString() {
    return "Movie " + this.getId() + " \"" + this.getTitle() +
      "\" (" + this.getYear() + ")";
  }

  /**
   * Two <code>Movie</code>s are equal if they have the same id
   */
  public boolean equals(Object o) {
    if (o instanceof Movie) {
      Movie other = (Movie) o;
      return this.getId() == other.getId();
    }

    return false;
  }

  public int getNumberOfAwards() {
    return numberOfAwards;
  }
}
