package edu.pdx.cs410J.web;

import org.junit.Test;

import java.io.IOException;
import java.net.HttpURLConnection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Unit tests for the <code>HttpRequestHelper</code> class
 */
public class HttpRequestHelperTest {

  private HttpRequestHelper helper = new HttpRequestHelper();

  @Test
  public void testGet() throws IOException {
    HttpRequestHelper.Response response = helper.get("http://www.google.com");
    assertEquals(HttpURLConnection.HTTP_OK, response.getCode());
    assertTrue(response.getContent().contains("Google"));
  }

  @Test
  public void testGetWithParameters() throws IOException {
    HttpRequestHelper.Response response = helper.get("http://search.yahoo.com/search", "p", "Java");
    assertEquals(HttpURLConnection.HTTP_OK, response.getCode());
    assertTrue(response.getContent().contains("Java"));

  }
}
