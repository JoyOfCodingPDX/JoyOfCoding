package edu.pdx.cs.joy.airlineweb;

import com.google.common.annotations.VisibleForTesting;
import edu.pdx.cs.joy.ParserException;
import edu.pdx.cs.joy.web.HttpRequestHelper;
import edu.pdx.cs.joy.web.HttpRequestHelper.Response;

import java.io.IOException;
import java.io.StringReader;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.function.Function;

import static edu.pdx.cs.joy.web.HttpRequestHelper.*;
import static java.net.HttpURLConnection.HTTP_OK;

/**
 * A helper class for accessing the rest client.  Note that this class provides
 * an example of how to make gets and posts to a URL.  You'll need to change it
 * to do something other than just send dictionary entries.
 */
public class AirlineRestClient
{
    private static final String WEB_APP = "airline";
    private static final String SERVLET = "flights";

    private final Function<String, HttpRequestHelper> httpRequestHelperCreator;


  /**
     * Creates a client to the airline REST service running on the given host and port
     * @param hostName The name of the host
     * @param port The port
     */
    public AirlineRestClient( String hostName, int port )
    {
        this.httpRequestHelperCreator =
          dictionaryName -> new HttpRequestHelper(String.format("http://%s:%d/%s/%s/%s", hostName, port, WEB_APP, SERVLET, urlEncode(dictionaryName)));
    }

  private String urlEncode(String dictionaryName) {
    return URLEncoder.encode(dictionaryName, Charset.defaultCharset());
  }

  @VisibleForTesting
    AirlineRestClient(HttpRequestHelper http) {
      this.httpRequestHelperCreator = (dictionaryName) -> http;
    }

  /**
   * Returns all dictionary entries from the server
   */
  public Map<String, String> getAllDictionaryEntries(String dictionaryName) throws IOException, ParserException {
    Response response = getHttpRequestHelper(dictionaryName).get(Map.of());
    throwExceptionIfNotOkayHttpStatus(response);

    TextParser parser = new TextParser(new StringReader(response.getContent()));
    return parser.parse();
  }

  private HttpRequestHelper getHttpRequestHelper(String dictionaryName) {
    return httpRequestHelperCreator.apply(dictionaryName);
  }

  /**
   * Returns the definition for the given word
   */
  public String getDefinition(String dictionaryName, String word) throws IOException, ParserException {
    Response response = getHttpRequestHelper(dictionaryName).get(Map.of(AirlineServlet.WORD_PARAMETER, word));
    throwExceptionIfNotOkayHttpStatus(response);
    String content = response.getContent();

    TextParser parser = new TextParser(new StringReader(content));
    return parser.parse().get(word);
  }

  public void addDictionaryEntry(String dictionaryName, String word, String definition) throws IOException {
    Response response = getHttpRequestHelper(dictionaryName).post(Map.of(AirlineServlet.WORD_PARAMETER, word, AirlineServlet.DEFINITION_PARAMETER, definition));
    throwExceptionIfNotOkayHttpStatus(response);
  }

  public void removeAllDictionaryEntries() throws IOException {
    Response response = getHttpRequestHelper("ANY DICTIONARY").delete(Map.of());
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
