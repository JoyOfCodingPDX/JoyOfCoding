package edu.pdx.cs410J.gwt;

import edu.pdx.cs410J.web.HttpRequestHelper;
import org.junit.Test;

import java.io.IOException;

import static junit.framework.TestCase.assertEquals;

/**
 * Uses GWT sync proxy to invoke the <code>DivisionService</code>.
 * This verifies that the GWT web application is up and running in Jetty.
 */
public class DivisionServiceSyncProxyIT extends HttpRequestHelper {

  @Test
  public void gwtWebApplicationIsRunning() throws IOException {
    Response response = get("http://localhost:8080/gwt");
    assertEquals(200, response.getCode());
  }
}
