package edu.pdx.cs410J.gwt;

import com.gdevelop.gwt.syncrpc.SyncProxy;
import edu.pdx.cs410J.gwt.client.DivisionService;
import edu.pdx.cs410J.web.HttpRequestHelper;
import org.junit.Test;

import java.io.IOException;

import static junit.framework.TestCase.assertEquals;

/**
 * Uses GWT sync proxy to invoke the <code>DivisionService</code>.
 * This verifies that the GWT web application is up and running in Jetty.
 */
public class DivisionServiceSyncProxyIT extends HttpRequestHelper {

  private final int httpPort = Integer.getInteger("http.port", 8080);
  private String webAppUrl = "http://localhost:" + httpPort + "/gwt";

  @Test
  public void gwtWebApplicationIsRunning() throws IOException {
    Response response = get(this.webAppUrl);
    assertEquals(200, response.getCode());
  }

  @Test
  public void canInvokeDivisionServiceWithGwtSyncProxy() {
    String moduleName = "examples";
    SyncProxy.setBaseURL(this.webAppUrl + "/" + moduleName + "/");

    DivisionService sync = SyncProxy.createSync(DivisionService.class);
    int quotient = sync.divide(6, 2);
    assertEquals(3, quotient);
  }
}
