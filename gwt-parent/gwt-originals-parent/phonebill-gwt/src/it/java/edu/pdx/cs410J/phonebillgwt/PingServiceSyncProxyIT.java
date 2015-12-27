package edu.pdx.cs410J.phonebillgwt;

import com.gdevelop.gwt.syncrpc.SyncProxy;
import edu.pdx.cs410J.phonebillgwt.client.PhoneBill;
import edu.pdx.cs410J.phonebillgwt.client.PingService;
import edu.pdx.cs410J.web.HttpRequestHelper;
import org.junit.Test;

import java.io.IOException;

import static junit.framework.TestCase.assertEquals;

public class PingServiceSyncProxyIT extends HttpRequestHelper {

  private final int httpPort = Integer.getInteger("http.port", 8080);
  private String webAppUrl = "http://localhost:" + httpPort + "/phonebill";

  @Test
  public void gwtWebApplicationIsRunning() throws IOException {
    Response response = get(this.webAppUrl);
    assertEquals(200, response.getCode());
  }

  @Test
  public void canInvokePingServiceWithGwtSyncProxy() {
    String moduleName = "phonebill";
    SyncProxy.setBaseURL(this.webAppUrl + "/" + moduleName + "/");

    PingService ping = SyncProxy.createSync(PingService.class);
    PhoneBill airline = ping.ping();
    assertEquals("CS410J", airline.getCustomer());
    assertEquals(1, airline.getPhoneCalls().size());
  }

}
