package edu.pdx.cs410E.rmi;

import java.io.PrintStream;
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

    String name = "//" + host + ":" + port + "/MovieDatabase";

    try {
      MovieDatabase db = 
        (MovieDatabase) Naming.lookup(name);

      Query query = new Query() {
          public boolean satisfies(Movie movie) {
            return movie.getYear() == year;
          }
        };

      Comparator sorter = new SortMoviesByTitle();

      Iterator movies = db.executeQuery(query, sorter).iterator();
      while (movies.hasNext()) {
        Movie movie = (Movie) movies.next();
        System.out.println(movie);
      }

    } catch (RemoteException ex) {
      ex.printStackTrace(System.err);

    } catch (NotBoundException ex) {
      ex.printStackTrace(System.err);

    } catch (MalformedURLException ex) {
      ex.printStackTrace(System.err);
    }

  }

  /**
   * An inner serializable comparator that sorts movies alphabetically
   * by their titles.
   */
  static class SortMoviesByTitle 
    implements Comparator, java.io.Serializable {

    public int compare(Object o1, Object o2) {
      Movie m1 = (Movie) o1;
      Movie m2 = (Movie) o2;
      return m1.getTitle().compareTo(m2.getTitle());
    }
  }

}
