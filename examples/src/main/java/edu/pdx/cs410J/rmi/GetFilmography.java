package edu.pdx.cs410J.rmi;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RMISecurityManager;
import java.rmi.RemoteException;

/**
 * This program searches the remote movie database for all of the
 * movies that a given actor has acted in.
 */
public class GetFilmography {

  public static void main(String[] args) {
    String host = args[0];
    int port = Integer.parseInt(args[1]);
    Long actor = Long.parseLong(args[2]);

    // Install an RMISecurityManager, if there is not a
    // SecurityManager already installed
    if (System.getSecurityManager() == null) {
      System.setSecurityManager(new RMISecurityManager());
    }

    String name = "rmi://" + host + ":" + port + "/MovieDatabase";

    try {
      MovieDatabase db = 
        (MovieDatabase) Naming.lookup(name);
      db.getFilmography(actor).forEach(System.out::println);

    } catch (RemoteException | NotBoundException | MalformedURLException ex) {
      ex.printStackTrace(System.err);
    }

  }

}
