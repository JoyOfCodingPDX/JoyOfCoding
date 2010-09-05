package edu.pdx.cs399J.gwt.client;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * The client-side interface to the division service
 */
public interface DivisionServiceAsync {

  /**
   * Divides two numbers and returns their quotient
   */
  void divide(int dividend, int divisor, AsyncCallback<Integer> async);
}
