package edu.pdx.cs.joy.rmi;

import java.io.PrintStream;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.util.Map;

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
    long movieId = Long.parseLong(args[2]);
    String character = args[3];
    long actorId = Long.parseLong(args[4]);

    try {
      MovieDatabase db = (MovieDatabase) LocateRegistry.getRegistry(host, port).lookup(MovieDatabase.RMI_OBJECT_NAME);
      db.noteCharacter(movieId, character, actorId);

      Movie movie = db.getMovie(movieId);
      out.println(movie.getTitle());
      for (Map.Entry entry : movie.getCharacters().entrySet()) {
        out.println("  " + entry.getKey() + "\t" + entry.getValue());
      }
      System.exit(0);

    } catch (RemoteException | NotBoundException ex) {
      ex.printStackTrace(System.err);
      System.exit(1);
    }

  }

}
