package edu.pdx.cs.joy.web;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for the <code>HttpRequestHelper</code> class
 */
public class HttpRequestHelperTest {

  @Test
  public void testGet() throws IOException {
    HttpRequestHelper helper = new HttpRequestHelper("http://www.google.com");
    HttpRequestHelper.Response response = helper.get(Map.of());
    assertEquals(HttpURLConnection.HTTP_OK, response.getHttpStatusCode());
    assertTrue(response.getContent().contains("Google"));
  }

  @Test
  public void testGetWithParameters() throws IOException {
    HttpRequestHelper helper = new HttpRequestHelper("https://www.google.com/search");
    HttpRequestHelper.Response response = helper.get(Map.of("p", "Java"));
    assertEquals(HttpURLConnection.HTTP_OK, response.getHttpStatusCode());
    assertTrue(response.getContent().contains("Java"));
  }

  @Test
  void restExceptionStackTraceContainsHttpCode() {
    int httpCode = 42;
    String message = "MESSAGE";
    HttpRequestHelper.RestException ex = new HttpRequestHelper.RestException(httpCode, message);

    StringWriter sw = new StringWriter();
    ex.printStackTrace(new PrintWriter(sw, true));

    String stackTrace = sw.toString();
    assertThat(stackTrace, containsString(String.valueOf(httpCode)));
    assertThat(stackTrace, containsString(message));
  }

}
