package edu.pdx.cs410J.gwt.server.di;

import com.google.inject.Inject;
import edu.pdx.cs410J.rmi.MovieDatabase;

import java.rmi.RemoteException;

/**
 * Initializes the contents of the movie database to contain interesting data
 */
public class MovieDatabaseInitializer {

  @Inject
  public MovieDatabaseInitializer(MovieDatabase db) throws RemoteException {
     db.createMovie("Forrest Gump", 1994);
     db.createMovie("Apollo 13", 1995);
     db.createMovie("Pulp Fiction", 1994);
  }
}
