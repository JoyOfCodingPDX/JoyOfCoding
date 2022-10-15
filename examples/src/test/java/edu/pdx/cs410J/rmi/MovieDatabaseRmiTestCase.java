package edu.pdx.cs410J.rmi;

import edu.pdx.cs410J.InvokeMainTestCase;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;

import java.net.MalformedURLException;
import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class MovieDatabaseRmiTestCase extends InvokeMainTestCase {
  static int rmiPort;

  private static int getRandomPort() {
    return (int) (1_000 + Math.random() * (9_000 - 1));
  }

  static final String RMI_HOST = "localhost";
  private static MovieDatabaseImpl movieDatabaseImpl;
  private static Registry rmiRegistry;

  @BeforeAll
  public static void bindMovieDatabaseIntoRmiRegistry() throws RemoteException, AlreadyBoundException, MalformedURLException {
    movieDatabaseImpl = new MovieDatabaseImpl();
    rmiPort = getRandomPort();
    MovieDatabase database = (MovieDatabase) UnicastRemoteObject.exportObject(movieDatabaseImpl, rmiPort);
    rmiRegistry = LocateRegistry.createRegistry(rmiPort);
    rmiRegistry.bind(MovieDatabase.RMI_OBJECT_NAME, database);
  }

  @AfterAll
  public static void unbindMovieDatabaseFromRmiRegistry() throws RemoteException, NotBoundException {
    Registry registry = rmiRegistry;
    UnicastRemoteObject.unexportObject(movieDatabaseImpl, true);
    registry.unbind(MovieDatabase.RMI_OBJECT_NAME);
    UnicastRemoteObject.unexportObject(rmiRegistry, true);
    movieDatabaseImpl = null;
  }

  private static Registry getRmiRegistry() throws RemoteException {
    return LocateRegistry.getRegistry(RMI_HOST, rmiPort);
  }

  static MovieDatabase getMovieDatabase() throws RemoteException, NotBoundException {
    return (MovieDatabase) getRmiRegistry().lookup(MovieDatabase.RMI_OBJECT_NAME);
  }
}
