package edu.pdx.cs410J.servlets;

import org.junit.Test;
import static org.junit.Assert.assertTrue;

import java.net.URL;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;

/**
 * Tests that we can get information about the servlet container
 */
public class ServletInfoIT {

  @Test
  public void testServletInfo() throws IOException {
    URL url = new URL("http://localhost:8080/web/info");
    BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
    StringBuilder sb = new StringBuilder();
    int lines = 0;
    while (br.ready()) {
      String line = br.readLine();
      sb.append(line);
      sb.append("\n");
      if (lines < 10) {
        lines++;
        System.out.println(line);
      }
    }

    String s = sb.toString();
    assertTrue(s.indexOf("Servlet Information") >= 0);
    
  }
}
