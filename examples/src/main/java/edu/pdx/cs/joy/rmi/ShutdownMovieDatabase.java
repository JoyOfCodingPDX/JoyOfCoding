package edu.pdx.cs.joy.rmi;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;

/**
 * This program shutdowns the remote movie database.
 */
public class ShutdownMovieDatabase {

  public static void main(String[] args) {
    String host = args[0];
    int port = Integer.parseInt(args[1]);

    try {
      MovieDatabase db = (MovieDatabase) LocateRegistry.getRegistry(host, port).lookup(MovieDatabase.RMI_OBJECT_NAME);
      db.shutdown();
      System.exit(0);

    } catch (RemoteException | NotBoundException ex) {
      ex.printStackTrace(System.err);
      System.exit(1);
    }

  }

}
