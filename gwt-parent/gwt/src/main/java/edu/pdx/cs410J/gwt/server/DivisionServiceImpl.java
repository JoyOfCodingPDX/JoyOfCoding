package edu.pdx.cs410J.gwt.server;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import edu.pdx.cs410J.gwt.client.DivisionService;

/**
 * The server-side implementation of the division service
 */
public class DivisionServiceImpl extends RemoteServiceServlet implements DivisionService {
  public int divide(int dividend, int divisor) {
    return dividend / divisor;
  }
}
