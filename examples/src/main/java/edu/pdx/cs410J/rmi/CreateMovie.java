package edu.pdx.cs410J.rmi;

import java.net.*;
import java.rmi.*;

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

    // Install an RMISecurityManager, if there is not a
    // SecurityManager already installed
    if (System.getSecurityManager() == null) {
      System.setSecurityManager(new RMISecurityManager());
    }

    String name = "//" + host + ":" + port + "/MovieDatabase";

    try {
      MovieDatabase db = 
        (MovieDatabase) Naming.lookup(name);
      long id = db.createMovie(title, year);
      System.out.println("Created movie " + id);

    } catch (RemoteException ex) {
      ex.printStackTrace(System.err);

    } catch (NotBoundException ex) {
      ex.printStackTrace(System.err);

    } catch (MalformedURLException ex) {
      ex.printStackTrace(System.err);
    }

  }

}
