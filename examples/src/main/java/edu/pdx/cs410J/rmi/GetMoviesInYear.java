package edu.pdx.cs410J.rmi;

import java.net.*;
import java.rmi.*;
import java.util.*;

/**
 * This program queries the remote movie database an prints out the
 * movies that were released in a given year.  The interesting thing
 * about this program is that the query class is sent to the server
 * from the client.
 */
public class GetMoviesInYear {

  public static void main(String[] args) {
    String host = args[0];
    int port = Integer.parseInt(args[1]);
    final int year = Integer.parseInt(args[2]);

    // Install an RMISecurityManager, if there is not a
    // SecurityManager already installed
    if (System.getSecurityManager() == null) {
      System.setSecurityManager(new RMISecurityManager());
    }

    String name = "rmi://" + host + ":" + port + "/MovieDatabase";

    try {
      MovieDatabase db = 
        (MovieDatabase) Naming.lookup(name);

      Query query = movie -> movie.getYear() == year;

      Comparator<Movie> sorter = new SortMoviesByTitle();

      db.executeQuery(query, sorter).forEach(System.out::println);

    } catch (RemoteException | NotBoundException | MalformedURLException ex) {
      ex.printStackTrace(System.err);
    }

  }

  /**
   * An inner serializable comparator that sorts movies alphabetically
   * by their titles.
   */
  static class SortMoviesByTitle 
    implements Comparator<Movie>, java.io.Serializable {

    @Override
    public int compare(Movie m1, Movie m2) {
      return m1.getTitle().compareTo(m2.getTitle());
    }
  }

}
