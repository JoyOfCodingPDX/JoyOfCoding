package edu.pdx.cs410J.rmi;

import java.io.PrintStream;
import java.net.*;
import java.rmi.*;
import java.util.*;

/**
 * This program contacts the remote movie database and makes note of a
 * character in a movie that is played by a given actor.  After the
 * character is noted, the <code>Movie</code> is fetched from the
 * database and is printed out.
 */
public class NoteCharacter {
  private static PrintStream out = System.out;

  public static void main(String[] args) {
    String host = args[0];
    int port = Integer.parseInt(args[1]);
    long id = Long.parseLong(args[2]);
    String character = args[3];
    long actorId = Long.parseLong(args[4]);

    // Install an RMISecurityManager, if there is not a
    // SecurityManager already installed
    if (System.getSecurityManager() == null) {
      System.setSecurityManager(new RMISecurityManager());
    }

    String name = "rmi://" + host + ":" + port + "/MovieDatabase";

    try {
      MovieDatabase db = 
        (MovieDatabase) Naming.lookup(name);
      db.noteCharacter(id, character, actorId);

      Movie movie = db.getMovie(id);
      out.println(movie.getTitle());
      for (Map.Entry entry : movie.getCharacters().entrySet()) {
        out.println("  " + entry.getKey() + "\t" + entry.getValue());
      }

    } catch (RemoteException | NotBoundException | MalformedURLException ex) {
      ex.printStackTrace(System.err);
    }

  }

}
