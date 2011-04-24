package edu.pdx.cs410J.rmi;

import java.rmi.*;

/**
 * This remote interface provides methods for solving a system of
 * equations on a remote machine.
 */
public interface EquationSolver extends Remote {

  /**
   * Solves a system of <code>n</code> equations of the form <code>Ax
   * = b</code> where <code>A</code> is an <code>n x n</code> matrix.
   *
   * @throws IllegalArgumentException
   *         The number of rows and columns in the matrix are not the
   *         same 
   * @throws RemoteException
   *         Something went wrong while communicating with the server
   */
  public double[] solve(double[][] matrix, double[] constants) 
    throws RemoteException;

}
