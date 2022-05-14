#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package};

import edu.pdx.cs410J.ParserException;
import edu.pdx.cs410J.web.NewHttpRequestHelper;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class PhoneBillRestClientTest {

  @Test
  void getAllDictionaryEntriesPerformsHttpGetWithNoParameters() throws ParserException, IOException {
    Map<String, String> dictionary = Map.of("One", "1", "Two", "2");

    NewHttpRequestHelper http = mock(NewHttpRequestHelper.class);
    when(http.get(eq(Map.of()))).thenReturn(dictionaryAsText(dictionary));

    PhoneBillRestClient client = new PhoneBillRestClient(http);

    assertThat(client.getAllDictionaryEntries(), equalTo(dictionary));
  }

  private NewHttpRequestHelper.Response dictionaryAsText(Map<String, String> dictionary) {
    StringWriter writer = new StringWriter();
    new TextDumper(writer).dump(dictionary);

    return new NewHttpRequestHelper.Response(writer.toString());
  }
}
