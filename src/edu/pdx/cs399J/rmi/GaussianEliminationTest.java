package edu.pdx.cs399J.rmi;

import java.util.Arrays;
import junit.framework.*;

/**
 * This is a JUnit test that tests solving a system of equations using
 * {@link GaussianElmination}.
 */
public class GaussianEliminationTest extends TestCase {

  public static Test suite() {
    return(new TestSuite(GaussianEliminationTest.class));
  }

  public GaussianEliminationTest(String name) {
    super(name);
  }

  //////// main program

  /**
   * Run one test method from this class
   */
  public static void main(String[] args) {
    TestSuite suite = new TestSuite();

    if (args.length == 0) {
      suite.addTest(suite());

    } else {
      for (int i = 0; i < args.length; i++) {
        suite.addTest(new GaussianEliminationTest(args[i]));
      }
    }

    junit.textui.TestRunner.run(suite);
  }


  ////////  Test cases
 
  public void testTwoEquations() {
    double[][] a = { { 4.0, 3.0 }, { 3.0, 2.0 } };
    double[] b = { 31.0, 22.0 };
    double[] expected = { 4.0, 5.0 };

    double[] actual = GaussianElimination.solve(a, b);
    assertTrue(Arrays.equals(expected, actual));
  }

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
