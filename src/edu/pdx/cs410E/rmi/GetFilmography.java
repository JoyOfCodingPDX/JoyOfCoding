package edu.pdx.cs410E.rmi;

import java.io.PrintStream;
import java.net.*;
import java.rmi.*;
import java.util.*;

/**
 * This program searches the remote movie database for all of the
 * movies that a given actor has acted in.
 */
public class GetFilmography {

  public static void main(String[] args) {
    String host = args[0];
    int port = Integer.parseInt(args[1]);
    String actor = args[2];

    // Install an RMISecurityManager, if there is not a
    // SecurityManager already installed
    if (System.getSecurityManager() == null) {
      System.setSecurityManager(new RMISecurityManager());
    }

    String name = "//" + host + ":" + port + "/MovieDatabase";

    try {
      MovieDatabase db = 
        (MovieDatabase) Naming.lookup(name);
      Iterator movies = db.getFilmography(actor).iterator();
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

}
