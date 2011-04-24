package edu.pdx.cs410J.rmi;

import java.net.*;
import java.rmi.*;

/**
 * This program shutdowns the remote movie database.
 */
public class ShutdownMovieDatabase {

  public static void main(String[] args) {
    String host = args[0];
    int port = Integer.parseInt(args[1]);

    // Install an RMISecurityManager, if there is not a
    // SecurityManager already installed
    if (System.getSecurityManager() == null) {
      System.setSecurityManager(new RMISecurityManager());
    }

    String name = "rmi://" + host + ":" + port + "/MovieDatabase";

    try {
      MovieDatabase db = 
        (MovieDatabase) Naming.lookup(name);
      db.shutdown();

    } catch (RemoteException ex) {
      ex.printStackTrace(System.err);

    } catch (NotBoundException ex) {
      ex.printStackTrace(System.err);

    } catch (MalformedURLException ex) {
      ex.printStackTrace(System.err);
    }

  }

}
