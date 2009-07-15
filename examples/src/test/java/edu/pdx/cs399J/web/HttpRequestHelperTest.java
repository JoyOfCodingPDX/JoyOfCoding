package edu.pdx.cs399J.web;

import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

import java.io.IOException;
import java.net.HttpURLConnection;

/**
 * Unit tests for the <code>HttpRequestHelper</code> class
 */
public class HttpRequestHelperTest {

  private HttpRequestHelper helper = new HttpRequestHelper();

  @Test
  public void testGet() throws IOException {
    HttpRequestHelper.Response response = helper.get("http://www.yahoo.com");
    assertEquals(HttpURLConnection.HTTP_OK, response.getCode());
    assertTrue(response.getContent().contains("Yahoo"));
  }

  @Test
  public void testGetWithParameters() throws IOException {
    HttpRequestHelper.Response response = helper.get("http://search.yahoo.com/search", "p", "Java");
    assertEquals(HttpURLConnection.HTTP_OK, response.getCode());
    assertTrue(response.getContent().contains("Java"));

  }
}
