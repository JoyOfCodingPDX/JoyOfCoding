#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package};

import edu.pdx.cs410J.web.HttpRequestHelper.Response;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.io.IOException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;

/**
 * Integration test that tests the REST calls made by {@link AirlineRestClient}
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class AirlineRestClientIT {
  private static final String HOSTNAME = "localhost";
  private static final String PORT = System.getProperty("http.port", "8080");

  private AirlineRestClient newAirlineRestClient() {
    int port = Integer.parseInt(PORT);
    return new AirlineRestClient(HOSTNAME, port);
  }

  @Test
  public void test0RemoveAllMappings() throws IOException {
    AirlineRestClient client = newAirlineRestClient();
    Response response = client.removeAllMappings();
    assertThat(response.getContent(), response.getCode(), equalTo(200));
  }

  @Test
  public void test1EmptyServerContainsNoMappings() throws IOException {
    AirlineRestClient client = newAirlineRestClient();
    Response response = client.getAllKeysAndValues();
    String content = response.getContent();
    assertThat(content, response.getCode(), equalTo(200));
    assertThat(content, containsString(Messages.getMappingCount(0)));
  }

  @Test
  public void test2AddOneKeyValuePair() throws IOException {
    AirlineRestClient client = newAirlineRestClient();
    String testKey = "TEST KEY";
    String testValue = "TEST VALUE";
    Response response = client.addKeyValuePair(testKey, testValue);
    String content = response.getContent();
    assertThat(content, response.getCode(), equalTo(200));
    assertThat(content, containsString(Messages.mappedKeyValue(testKey, testValue)));
  }

}