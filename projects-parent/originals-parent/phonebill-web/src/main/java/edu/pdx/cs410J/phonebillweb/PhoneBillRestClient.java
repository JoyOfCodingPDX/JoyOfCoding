package edu.pdx.cs410J.phonebillweb;

import com.google.common.annotations.VisibleForTesting;
import edu.pdx.cs410J.ParserException;
import edu.pdx.cs410J.web.NewHttpRequestHelper;

import java.io.IOException;
import java.io.StringReader;
import java.util.Map;

import static edu.pdx.cs410J.web.NewHttpRequestHelper.Response;
import static edu.pdx.cs410J.web.NewHttpRequestHelper.RestException;
import static java.net.HttpURLConnection.HTTP_OK;

/**
 * A helper class for accessing the rest client.  Note that this class provides
 * an example of how to make gets and posts to a URL.  You'll need to change it
 * to do something other than just send dictionary entries.
 */
public class PhoneBillRestClient {

    private static final String WEB_APP = "phonebill";
    private static final String SERVLET = "calls";

  private final NewHttpRequestHelper http;


    /**
     * Creates a client to the Phone Bil REST service running on the given host and port
     * @param hostName The name of the host
     * @param port The port
     */
    public PhoneBillRestClient( String hostName, int port )
    {
      this(new NewHttpRequestHelper(String.format("http://%s:%d/%s/%s", hostName, port, WEB_APP, SERVLET)));
    }

  @VisibleForTesting
  PhoneBillRestClient(NewHttpRequestHelper http) {
    this.http = http;
  }

  /**
   * Returns all dictionary entries from the server
   */
  public Map<String, String> getAllDictionaryEntries() throws IOException, ParserException {
    Response response = http.get(Map.of());

    TextParser parser = new TextParser(new StringReader(response.getContent()));
    return parser.parse();
  }

  /**
   * Returns the definition for the given word
   */
  public String getDefinition(String word) throws IOException, ParserException {
    Response response = http.get(Map.of("word", word));
    throwExceptionIfNotOkayHttpStatus(response);
    String content = response.getContent();

    TextParser parser = new TextParser(new StringReader(content));
    return parser.parse().get(word);
  }

    public void addDictionaryEntry(String word, String definition) throws IOException {
      Response response = http.post(Map.of("word", word, "definition", definition));
      throwExceptionIfNotOkayHttpStatus(response);
    }

  public void removeAllDictionaryEntries() throws IOException {
      Response response = http.delete(Map.of());
      throwExceptionIfNotOkayHttpStatus(response);
    }

    private void throwExceptionIfNotOkayHttpStatus(Response response) {
      int code = response.getHttpStatusCode();
      if (code != HTTP_OK) {
        String message = response.getContent();
        throw new RestException(code, message);
      }
    }

}
