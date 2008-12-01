package edu.pdx.cs399J.gwt.client;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import com.google.gwt.core.client.GWT;

/**
 * A GWT remote service that divides two <code>double</code>s
 */
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
        ServiceDefTarget endpoint = (ServiceDefTarget) instance;
        String url = GWT.getModuleBaseURL() + "division";
        endpoint.setServiceEntryPoint(url);
      }
      return instance;
    }
  }
}
