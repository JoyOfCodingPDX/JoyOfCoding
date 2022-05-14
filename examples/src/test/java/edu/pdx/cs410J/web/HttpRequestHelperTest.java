package edu.pdx.cs410J.web;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for the <code>HttpRequestHelper</code> class
 */
public class HttpRequestHelperTest {

  @Test
  public void testGet() throws IOException {
    NewHttpRequestHelper helper = new NewHttpRequestHelper("http://www.google.com");
    NewHttpRequestHelper.Response response = helper.get(Map.of());
    assertEquals(HttpURLConnection.HTTP_OK, response.getHttpStatusCode());
    assertTrue(response.getContent().contains("Google"));
  }

  @Test
  public void testGetWithParameters() throws IOException {
    NewHttpRequestHelper helper = new NewHttpRequestHelper("https://www.google.com/search");
    NewHttpRequestHelper.Response response = helper.get(Map.of("p", "Java"));
    assertEquals(HttpURLConnection.HTTP_OK, response.getHttpStatusCode());
    assertTrue(response.getContent().contains("Java"));
  }
}
