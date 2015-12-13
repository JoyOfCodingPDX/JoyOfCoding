package edu.pdx.cs410J.original.client;

import com.google.gwt.junit.client.GWTTestCase;
import com.google.gwt.user.client.Timer;
import org.junit.Test;

/**
 * An integration test for the airline GWT UI.  Remember that GWTTestCase is JUnit 3 style.
 * So, test methods names must begin with "test".
 */
public class AirlineGwtIT extends GWTTestCase {
  @Override
  public String getModuleName() {
    return "edu.pdx.cs410J.original.AirlineIntegrationTests";
  }

  @Test
  public void testWeCanGetASimpleTestToPass() {
    Timer verify = new Timer() {
      @Override
      public void run() {
        finishTest();
      }
    };

    // Wait for the RPC call to return
    verify.schedule(500);

    delayTestFinish(1000);
  }
}
