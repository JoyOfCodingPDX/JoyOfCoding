package edu.pdx.cs399J.gwt.client;

import com.google.gwt.junit.client.GWTTestCase;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * Unit tests for the {@link DivisionService}
 */
public class DivisionServiceGwtTest extends GWTTestCase {
  

  public String getModuleName() {
    return "edu.pdx.cs399J.gwt.Examples";
  }

  public void testSimpleDivision() {
    DivisionServiceAsync service = DivisionService.Helper.getAsync();
    service.divide(4, 2, new AsyncCallback<Integer>() {

      public void onFailure(Throwable throwable) {
        fail("Got exception: " + throwable);
        finishTest();
      }

      public void onSuccess(Integer quotient) {
        assertEquals(new Integer(2), quotient);
        finishTest();
      }
    });
    delayTestFinish(1000);
  }
}
