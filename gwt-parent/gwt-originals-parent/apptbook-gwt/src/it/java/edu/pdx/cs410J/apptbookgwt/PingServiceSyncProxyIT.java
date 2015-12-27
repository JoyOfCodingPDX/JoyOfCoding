package edu.pdx.cs410J.apptbookgwt;

import com.gdevelop.gwt.syncrpc.SyncProxy;
import edu.pdx.cs410J.apptbookgwt.client.AppointmentBook;
import edu.pdx.cs410J.apptbookgwt.client.PingService;
import edu.pdx.cs410J.web.HttpRequestHelper;
import org.junit.Test;

import java.io.IOException;

import static junit.framework.TestCase.assertEquals;

public class PingServiceSyncProxyIT extends HttpRequestHelper {

  private final int httpPort = Integer.getInteger("http.port", 8080);
  private String webAppUrl = "http://localhost:" + httpPort + "/apptbook";

  @Test
  public void gwtWebApplicationIsRunning() throws IOException {
    Response response = get(this.webAppUrl);
    assertEquals(200, response.getCode());
  }

  @Test
  public void canInvokePingServiceWithGwtSyncProxy() {
    String moduleName = "apptbook";
    SyncProxy.setBaseURL(this.webAppUrl + "/" + moduleName + "/");

    PingService ping = SyncProxy.createSync(PingService.class);
    AppointmentBook apptbook = ping.ping();
    assertEquals("My Owner", apptbook.getOwnerName());
    assertEquals(1, apptbook.getAppointments().size());
  }

}
