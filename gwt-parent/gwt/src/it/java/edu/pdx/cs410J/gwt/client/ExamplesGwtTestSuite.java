package edu.pdx.cs410J.gwt.client;

import com.google.gwt.junit.tools.GWTTestSuite;
import junit.framework.Test;
import junit.framework.TestCase;

/**
 * A suite of GWT tests that can all be run inside the same development mode shell instance.
 * Making this a <code>TestCase</code> makes the GWT Maven plugin happy
 */
public class ExamplesGwtTestSuite extends TestCase {

  public static Test suite() {
    GWTTestSuite suite = new GWTTestSuite("GWT Examples");

    suite.addTestSuite(DivisionServiceGwtIT.class);
    suite.addTestSuite(DivisionServiceExampleGwtIT.class);
    suite.addTestSuite(MovieServiceGwtIT.class);

    return suite;
  }

}
