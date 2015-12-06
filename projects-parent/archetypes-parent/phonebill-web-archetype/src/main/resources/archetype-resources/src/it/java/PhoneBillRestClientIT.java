#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package};

import edu.pdx.cs410J.web.HttpRequestHelper;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.io.IOException;
import java.net.HttpURLConnection;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;

/**
 * Integration test that tests the REST calls made by {@link PhoneBillRestClient}
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class PhoneBillRestClientIT {
  private static final String HOSTNAME = "localhost";
  private static final String PORT = System.getProperty("http.port", "8080");

  private PhoneBillRestClient newPhoneBillRestClient() {
    int port = Integer.parseInt(PORT);
    return new PhoneBillRestClient(HOSTNAME, port);
  }

  @Test
  public void test0RemoveAllMappings() throws IOException {
    PhoneBillRestClient client = newPhoneBillRestClient();
    HttpRequestHelper.Response response = client.removeAllMappings();
    assertThat(response.getContent(), response.getCode(), equalTo(200));
  }

  @Test
  public void test1EmptyServerContainsNoMappings() throws IOException {
    PhoneBillRestClient client = newPhoneBillRestClient();
    HttpRequestHelper.Response response = client.getAllKeysAndValues();
    String content = response.getContent();
    assertThat(content, response.getCode(), equalTo(200));
    assertThat(content, containsString(Messages.getMappingCount(0)));
  }

  @Test
  public void test2AddOneKeyValuePair() throws IOException {
    PhoneBillRestClient client = newPhoneBillRestClient();
    String testKey = "TEST KEY";
    String testValue = "TEST VALUE";
    HttpRequestHelper.Response response = client.addKeyValuePair(testKey, testValue);
    String content = response.getContent();
    assertThat(content, response.getCode(), equalTo(200));
    assertThat(content, containsString(Messages.mappedKeyValue(testKey, testValue)));
  }

  @Test
  public void missingRequiredParameterReturnsPreconditionFailed() throws IOException {
    PhoneBillRestClient client = newPhoneBillRestClient();
    HttpRequestHelper.Response response = client.postToMyURL();
    assertThat(response.getContent(), containsString(Messages.missingRequiredParameter("key")));
    assertThat(response.getCode(), equalTo(HttpURLConnection.HTTP_PRECON_FAILED));
  }

}
