package edu.pdx.cs410J.rmi;

import java.net.*;
import java.rmi.*;
import java.rmi.server.*;

/**
 * Instances of this remote class reside in a server JVM and perform
 * the work of solving an equation of the form <code>Ax = b</code>.
 */
public class EquationSolverImpl extends UnicastRemoteObject
  implements EquationSolver {

  /**
   * Creates a new <code>EquationSolverImpl</code>.  Invokes
   * <code>UnicastRemoteObject's</code> constructor to register this
   * object with the RMI runtime.
   */
  public EquationSolverImpl() throws RemoteException {
    super();
  }
  
  /**
   * Use Cholesky's algorithm to solve a linear system of equations of
   * the <code>Ax = b</code>.
   *
   * @param matrix
   *        The <code>A</code> in <code>Ax = b</code>
   * @param constants
   *        The <code>b</code> in <code>Ax = b</code>
   *
   * @throws IllegalArgumentException
   *         The number of rows and columns in the matrix are not the
   *         same 
   * @throws RemoteException
   *         Something went wrong while communicating with the server
   */
  public double[] solve(double[][] matrix, double[] constants) 
    throws RemoteException {

      return GaussianElimination.solve(matrix, constants);
  }

  /**
   * Main program that creates a new <code>EquationSolverImpl</code>
   * and binds it into the RMI registry under a given name.
   */
  public static void main(String[] args) {
    String host = args[0];
    int port = Integer.parseInt(args[1]);

    // Install an RMISecurityManager, if there is not a
    // SecurityManager already installed
    if (System.getSecurityManager() == null) {
      System.setSecurityManager(new RMISecurityManager());
    }

    String name = "//" + host + ":" + port + "/EquationSolver";

    try {
      Remote solver = new EquationSolverImpl();
      Naming.rebind(name, solver);

    } catch (RemoteException ex) {
      ex.printStackTrace(System.err);

    } catch (MalformedURLException ex) {
      ex.printStackTrace(System.err);
    }
  }

}
