package edu.pdx.cs.joy.rmi;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;

/**
 * This program searches the remote movie database for all of the
 * movies that a given actor has acted in.
 */
public class GetFilmography {

  public static void main(String[] args) {
    String host = args[0];
    int port = Integer.parseInt(args[1]);
    Long actor = Long.parseLong(args[2]);

    try {
      MovieDatabase db = (MovieDatabase) LocateRegistry.getRegistry(host, port).lookup(MovieDatabase.RMI_OBJECT_NAME);
      db.getFilmography(actor).forEach(System.out::println);

    } catch (RemoteException | NotBoundException ex) {
      ex.printStackTrace(System.err);
    }

  }

}
