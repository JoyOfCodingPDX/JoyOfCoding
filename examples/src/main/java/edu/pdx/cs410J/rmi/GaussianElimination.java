package edu.pdx.cs410J.rmi;

/**
 * This class performs Gaussian Elimination to solve a system of
 * linear equations of the form <code>Ax = b</code>.
 */
public class GaussianElimination {

  /**
   * Solve a system <code>Ax = b</code> using Gaussian elimination
   *
   * @throws IllegalArgumentException
   *         <code>A</code> is not an <code>n x n</code> matrix,
   *         <code>b</code> does not have length <code>n</code>
   */
  public static double[] solve(double[][] a, double[] b) {
    // First validate that 

    int rows = Matrix.countRows(a);
    int columns = Matrix.countColumns(a);
    if (rows != columns) {
      String s = "Matrix a must have the same number of rows (" + rows
        + ") and columns (" + columns + ")";
      throw new IllegalArgumentException(s);
    }

    if (b.length != rows) {
      String s = "Vector b must have " + rows + " rows (not " +
      b.length + ")";
      throw new IllegalArgumentException(s);
    }

    java.io.PrintWriter pw = new java.io.PrintWriter(System.out, true);
    Matrix.print("Matrix A", a, pw);
    Matrix.print("Vector b", b, pw);

    int n = rows;
    double[] x = new double[n];

    // Transform "a" into a upper-diagnol matrix

    // Divide each element in the row by a[0][0]
    double d = a[0][0];
    for(int h = 0; h < n; h++) {
      a[0][h] = a[0][h] / d;
    }
    b[0] = b[0] / d;
    
    for (int k = 0; k < n; k++) {
      for (int i = k + 1; i < n; i++) {
        double multiplier = a[i][k] / a[k][k];
        for (int j = k; j < n; j++) {
          a[i][j] = a[i][j] - (multiplier * a[k][j]);
        }
        b[i] = b[i] - (multiplier * b[k]);
      }
    }

    // Now solve for x from the bottom up
    for (int i = n-1; i >= 0; i--) {
      double v = b[i];
      for (int j = i+1; j < n; j++) {
        v -= (a[i][j] * x[j]);
      }
      x[i] = v / a[i][i];
    }

    Matrix.print("Vector x", x, pw);
    
    return x;
  }

}
