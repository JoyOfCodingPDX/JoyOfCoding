package edu.pdx.cs410J.rmi;

import static org.junit.Assert.assertTrue;
import org.junit.Test;

import java.util.Arrays;

/**
 * This is a JUnit test that tests solving a system of equations using
 * {@link GaussianElimination}.
 */
public class GaussianEliminationTest {

  @Test
  public void testTwoEquations() {
    double[][] a = { { 4.0, 3.0 }, { 3.0, 2.0 } };
    double[] b = { 31.0, 22.0 };
    double[] expected = { 4.0, 5.0 };

    double[] actual = GaussianElimination.solve(a, b);
    assertTrue(Arrays.equals(expected, actual));
  }

  @Test
  public void testThreeEquations() {
    double[][] a = { { 4.0, 3.0, 1.0 }, 
                     { 2.0, -6.0, 4.0 },
                     { 7.0, 5.0, 3.0 } };
    double[] b = { 17.0, 8.0, 32.0 };
    double[] expected = { 3.0, 1.0, 2.0 };

    double[] actual = GaussianElimination.solve(a, b);
    assertTrue(Arrays.equals(expected, actual));
  }

}
