package edu.pdx.cs.joy.airlineweb;

import edu.pdx.cs.joy.ParserException;
import edu.pdx.cs.joy.web.HttpRequestHelper;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * A unit test for the REST client that demonstrates using mocks and
 * dependency injection
 */
public class AirlineRestClientTest {

  @Test
  void getAllDictionaryEntriesPerformsHttpGetWithNoParameters() throws ParserException, IOException {
    String dictionaryName = "TEST DICTIONARY";

    Dictionary<String, String> dictionary = new Hashtable<>();
    dictionary.put("One", "1");
    dictionary.put("Two", "2");

    HttpRequestHelper http = mock(HttpRequestHelper.class);
    when(http.get(eq(Map.of()))).thenReturn(dictionaryAsText(dictionary));
    
    AirlineRestClient client = new AirlineRestClient(http);

    assertThat(client.getAllDictionaryEntries(dictionaryName), equalTo(dictionary));
  }

  private HttpRequestHelper.Response dictionaryAsText(Dictionary<String, String> dictionary) {
    StringWriter writer = new StringWriter();
    new TextDumper(writer).dump(dictionary);

    return new HttpRequestHelper.Response(writer.toString());
  }
}
