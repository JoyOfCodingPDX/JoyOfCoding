package edu.pdx.cs410J.phonebillgwt.client;

import com.google.gwt.junit.tools.GWTTestSuite;
import junit.framework.Test;

public class PhoneBillGwtTestSuite {
  public static Test suite() {
    GWTTestSuite suite = new GWTTestSuite("Phone Bill GWT Integration Tests");

    suite.addTestSuite(PhoneBillGwtIT.class);

    return suite;
  }

}
