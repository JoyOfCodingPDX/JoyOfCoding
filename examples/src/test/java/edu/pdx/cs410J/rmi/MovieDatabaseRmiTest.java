package edu.pdx.cs410J.rmi;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.net.MalformedURLException;
import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

public class MovieDatabaseRmiTest {

  static final int RMI_PORT = 7777;
  static final String RMI_HOST = "localhost";

  private MovieDatabase database;

  @BeforeClass
  public static void bindMovieDatabaseIntoRmiRegistry() throws RemoteException, AlreadyBoundException, MalformedURLException {
    String name = "/MovieDatabase";
    MovieDatabase database = (MovieDatabase) UnicastRemoteObject.exportObject(new MovieDatabaseImpl(), RMI_PORT);
    Registry rmiRegistry = LocateRegistry.createRegistry(RMI_PORT);
    rmiRegistry.bind(name, database);
  }

  @Before
  public void lookupMovieDatabase() throws RemoteException, NotBoundException, MalformedURLException {
    Registry registry = LocateRegistry.getRegistry(RMI_HOST, RMI_PORT);
    this.database = (MovieDatabase) registry.lookup("/MovieDatabase");
  }

  @AfterClass
  public static void unbindMovieDatabaseFromRmiRegistry() throws RemoteException, MalformedURLException, AlreadyBoundException, NotBoundException {
    Registry registry = LocateRegistry.getRegistry(RMI_HOST, RMI_PORT);
    registry.unbind("/MovieDatabase");
  }

  @Test
  public void createMovieInRemoteDatabase() throws RemoteException {
    String title = "Avengers: Infinity War";
    int year = 2018;
    long movieId = this.database.createMovie(title, year);
    Movie movie = this.database.getMovie(movieId);
    assertThat(movie.getId(), equalTo(movieId));
    assertThat(movie.getTitle(), equalTo(title));
    assertThat(movie.getYear(), equalTo(year));
  }
}
