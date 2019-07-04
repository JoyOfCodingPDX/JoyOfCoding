import edu.pdx.cs410J.web.HttpRequestHelper;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;

#set($symbol_pound='#')
  #set($symbol_dollar='$')
  #set($symbol_escape='\' )
  package ${package};

/**
 * Integration test that tests the REST calls made by {@link AppointmentBookRestClient}
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class AppointmentBookRestClientIT {
  private static final String HOSTNAME = "localhost";
  private static final String PORT = System.getProperty("http.port", "8080");

  private AppointmentBookRestClient newAppointmentBookRestClient() {
    int port = Integer.parseInt(PORT);
    return new AppointmentBookRestClient(HOSTNAME, port);
  }

  @Test
  public void test0RemoveAllDictionaryEntries() throws IOException {
    AppointmentBookRestClient client = newAppointmentBookRestClient();
    client.removeAllDictionaryEntries();
  }

  @Test
  public void test1EmptyServerContainsNoDictionaryEntries() throws IOException {
    AppointmentBookRestClient client = newAppointmentBookRestClient();
    Map<String, String> dictionary = client.getAllDictionaryEntries();
    assertThat(dictionary.size(), equalTo(0));
  }

  @Test
  public void test2DefineOneWord() throws IOException {
    AppointmentBookRestClient client = newAppointmentBookRestClient();
    String testWord = "TEST WORD";
    String testDefinition = "TEST DEFINITION";
    client.addDictionaryEntry(testWord, testDefinition);

    String definition = client.getDefinition(testWord);
    assertThat(definition, equalTo(testDefinition));
  }

  @Test
  public void test4MissingRequiredParameterReturnsPreconditionFailed() throws IOException {
    AppointmentBookRestClient client = newAppointmentBookRestClient();
    HttpRequestHelper.Response response = client.postToMyURL(Map.of());
    assertThat(response.getContent(), containsString(Messages.missingRequiredParameter("word")));
    assertThat(response.getCode(), equalTo(HttpURLConnection.HTTP_PRECON_FAILED));
  }

}
