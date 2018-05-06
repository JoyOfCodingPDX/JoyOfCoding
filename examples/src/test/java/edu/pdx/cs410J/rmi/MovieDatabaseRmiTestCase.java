package edu.pdx.cs410J.rmi;

import edu.pdx.cs410J.InvokeMainTestCase;
import org.junit.AfterClass;
import org.junit.BeforeClass;

import java.net.MalformedURLException;
import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class MovieDatabaseRmiTestCase extends InvokeMainTestCase {
  static final int RMI_PORT = 7777;
  static final String RMI_HOST = "localhost";
  private static MovieDatabaseImpl movieDatabaseImpl;
  private static Registry rmiRegistry;

  @BeforeClass
  public static void bindMovieDatabaseIntoRmiRegistry() throws RemoteException, AlreadyBoundException, MalformedURLException {
    movieDatabaseImpl = new MovieDatabaseImpl();
    MovieDatabase database = (MovieDatabase) UnicastRemoteObject.exportObject(movieDatabaseImpl, RMI_PORT);
    rmiRegistry = LocateRegistry.createRegistry(RMI_PORT);
    rmiRegistry.bind(MovieDatabase.RMI_OBJECT_NAME, database);
  }

  @AfterClass
  public static void unbindMovieDatabaseFromRmiRegistry() throws RemoteException, MalformedURLException, AlreadyBoundException, NotBoundException {
    Registry registry = rmiRegistry;
    UnicastRemoteObject.unexportObject(movieDatabaseImpl, true);
    registry.unbind(MovieDatabase.RMI_OBJECT_NAME);
    UnicastRemoteObject.unexportObject(rmiRegistry, true);
    movieDatabaseImpl = null;
  }

  private static Registry getRmiRegistry() throws RemoteException {
    return LocateRegistry.getRegistry(RMI_HOST, RMI_PORT);
  }

  static MovieDatabase getMovieDatabase() throws RemoteException, NotBoundException {
    return (MovieDatabase) getRmiRegistry().lookup(MovieDatabase.RMI_OBJECT_NAME);
  }
}
