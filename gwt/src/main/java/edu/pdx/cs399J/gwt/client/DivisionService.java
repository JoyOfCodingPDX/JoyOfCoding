package edu.pdx.cs399J.gwt.client;

import com.google.gwt.user.client.rpc.RemoteService;

/**
 * A GWT remote service that divides two <code>double</code>s
 */
public interface DivisionService extends RemoteService {

  /**
   * Divides two numbers and returns their quotient
   */
  public int divide(int dividend, int divisor);
}
