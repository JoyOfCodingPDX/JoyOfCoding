package edu.pdx.cs410J.rmi;

import java.net.*;
import java.rmi.*;

/**
 * This class uses RMI to solve the below system of equations:
 *
 * <PRE>
 * 4x1 + 3x2 + x3 = 17
 * 2x1 - 6x2 + 4x3 = 8
 * 7x1 + 5x2 + 3x3 = 32
 * </PRE>
 *
 * The server will compute the values of <code>x1</code>,
 * <code>x2</code>, and <code>x3</code>. 
 */
public class EquationClient {

  public static void main(String[] args) {
    String host = args[0];
    int port = Integer.parseInt(args[1]);

    if (System.getSecurityManager() == null) {
      System.setSecurityManager(new RMISecurityManager());
    }

    String name = "//" + host + ":" + port + "/EquationSolver";

    double[][] A = { { 4.0, 3.0, 1.0 }, 
                     { 2.0, -6.0, 4.0 },
                     { 7.0, 5.0, 3.0 } };
    double[] b = { 17.0, 8.0, 32.0 };


    try {
      EquationSolver solver = 
        (EquationSolver) Naming.lookup(name);
      double[] x = solver.solve(A, b);

      StringBuffer sb = new StringBuffer();
      for (int i = 0; i < x.length; i++) {
        sb.append(x[i]);
        sb.append(' ');
      }
      System.out.println(sb);

    } catch (RemoteException ex) {
      ex.printStackTrace(System.err);

    } catch (NotBoundException ex) {
      ex.printStackTrace(System.err);

    } catch (MalformedURLException ex) {
      ex.printStackTrace(System.err);
    }

  }

}
