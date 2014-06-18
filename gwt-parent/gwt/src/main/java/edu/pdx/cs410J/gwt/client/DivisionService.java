package edu.pdx.cs410J.gwt.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * A GWT remote service that divides two <code>double</code>s
 */
@RemoteServiceRelativePath("division")
public interface DivisionService extends RemoteService {

  /**
   * Divides two numbers and returns their quotient
   */
  public int divide(int dividend, int divisor);

  /**
   * Inner class for accessing the async service
   */
  public static class Helper {
    private static DivisionServiceAsync instance;

    /**
     * Returns the async interface for the <code>DivisionService</code>
     */
    public static DivisionServiceAsync getAsync() {
      if (instance == null) {
        instance = GWT.create(DivisionService.class);
      }
      return instance;
    }
  }
}
