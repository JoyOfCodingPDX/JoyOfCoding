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

  private HttpRequestHelper helper = new HttpRequestHelper();

  @Test
  public void testGet() throws IOException {
    HttpRequestHelper.Response response = helper.get("http://www.google.com", Map.of());
    assertEquals(HttpURLConnection.HTTP_OK, response.getCode());
    assertTrue(response.getContent().contains("Google"));
  }

  @Test
  public void testGetWithParameters() throws IOException {
    HttpRequestHelper.Response response = helper.get("https://www.google.com/search", Map.of("p", "Java"));
    assertEquals(HttpURLConnection.HTTP_OK, response.getCode());
    assertTrue(response.getContent().contains("Java"));

  }
}
