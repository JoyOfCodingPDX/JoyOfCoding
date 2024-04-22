package edu.pdx.cs.joy.rmi;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;

/**
 * This program contacts the remote movie database and creates a new
 * movie in it.  The id of the movie is printed out.
 */
public class CreateMovie {

  public static void main(String[] args) {
    String host = args[0];
    int port = Integer.parseInt(args[1]);
    String title = args[2];
    int year = Integer.parseInt(args[3]);

    try {
      MovieDatabase db = (MovieDatabase) LocateRegistry.getRegistry(host, port).lookup(MovieDatabase.RMI_OBJECT_NAME);
      long id = db.createMovie(title, year);
      System.out.println("Created movie " + id);

    } catch (RemoteException | NotBoundException ex) {
      ex.printStackTrace(System.err);
    }

  }

}
