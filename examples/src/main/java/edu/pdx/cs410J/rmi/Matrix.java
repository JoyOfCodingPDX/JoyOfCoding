package edu.pdx.cs410J.rmi;

import java.io.*;
import java.text.*;

/**
 * Represents a matrix of <tt>double</tt>s.  Contains methods to
 * create special matrices for this assignment.  It also contains
 * general methods to multiply matrices.
 *
 * @author David Whitlock
 */
public class Matrix {
  private static NumberFormat format;

  static {
    format = NumberFormat.getNumberInstance();
  }

  /**
   * Sets the number of decimal places to be displayed.
   */
  public static void setPrecision(int n) {
    format.setMinimumFractionDigits(n);
  }

  /**
   * Returns the number of rows in a matrix
   */
  public static int countRows(double[][] m) {
    return m.length;
  }

  /**
   * Returns the number of columns in a matrix
   *
   * @throws IllegalArgumentException
   *         The columns are not length-consistent
   */
  public static int countColumns(double[][] m) {
    int count = m[0].length;
    for (int i = 1; i < m.length; i++) {
      if (m[i].length != count) {
        String s = "The matrix is not length-consistent (" +
          m[i].length + " != " + count + ")";
        throw new IllegalArgumentException(s);
      }
    }
    return count;
  }

  /**
   * Multiplies two matrices and returns their product.
   */
  public static double[][] multiply(double[][] a, double[][] b) {
    // Make sure the matrices can be multiplied
    int aRows = countRows(a);
    int aColumns = countColumns(a);
    int bRows = countRows(b);
    int bColumns = countColumns(b);

    if(aColumns != bRows) {
      throw new
	IllegalArgumentException("Matrices cannot be multiplied");
    }

    int n = aColumns;

    // a x b = c
    double[][] c = new double[aRows][bColumns];
    for(int i = 0; i < aRows; i++) {
      for(int j = 0; j < bColumns; j++) {
	double sum = 0.0;
	for(int k = 0; k < n; k++) {
	  sum += a[i][k] * b[k][j];
	}
	c[i][j] = sum;
      }
    }

    return(c);
  }

  /**
   * Prints this a matrix
   */
  public static void print(String name, double[][] m, PrintWriter out) {
    out.println("Matrix " + name + ":");
    for(int i = 0; i < countRows(m); i++) {
      out.print("  ");
      for(int j = 0; j < countColumns(m); j++) {
	out.print(format.format(m[i][j]) + " ");
      }
      out.println("");
    }
  }

  /**
   * Prints a vector in column form
   */
  public static void print(String name, double[] v, PrintWriter out) {
    out.println("Vector " + name + ":");
    for (int i = 0; i < v.length; i++) {
      out.println("  " + format.format(v[i]) + " ");
    }
  }

}
