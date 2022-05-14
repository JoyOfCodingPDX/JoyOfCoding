#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package};

import edu.pdx.cs410J.web.NewHttpRequestHelper;
import edu.pdx.cs410J.web.NewHttpRequestHelper.Response;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;

class IndexDotHtmlIT {
  private static final String HOSTNAME = "localhost";
  private static final String PORT = System.getProperty("http.port", "8080");

  @Test
  void indexDotHtmlExists() throws IOException {
    Response indexDotHtml = fetchIndexDotHtml();
    assertThat(indexDotHtml.getHttpStatusCode(), equalTo(200));
  }

  @Test
  void indexDotHtmlHasReasonableContent() throws IOException {
    Response indexDotHtml = fetchIndexDotHtml();
    assertThat(indexDotHtml.getContent(), containsString("<form"));
  }

  private Response fetchIndexDotHtml() throws IOException {
    int port = Integer.parseInt(PORT);
    return new IndexDotHtmlHelper(HOSTNAME, port).getIndexDotHtml();
  }

  static class IndexDotHtmlHelper {
    private static final String WEB_APP = "airline";
    private final NewHttpRequestHelper http;

    IndexDotHtmlHelper(String hostName, int port) {
      this.http = new NewHttpRequestHelper(String.format( "http://%s:%d/%s/%s", hostName, port, WEB_APP, "index.html" ));
    }

    Response getIndexDotHtml() throws IOException {
      return http.get(Map.of());
    }
  }
}
