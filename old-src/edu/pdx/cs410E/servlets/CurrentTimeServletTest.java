package edu.pdx.cs410E.servlets;

import java.io.IOException;
import java.util.Date;
import javax.servlet.ServletException;
import junit.framework.*;
import org.apache.cactus.*;

/**
 * A simple Cactus test that exercises the {@link CurrentTimeServlet}
 * servlet. 
 *
 * @author David Whitlock
 * @since Summer 2005
 */
public class CurrentTimeServletTest extends ServletTestCase {

	/**
	 * Makes sure that the time returned by the servlet is close to the
	 * current time.
	 */
	public void testCurrentTime() throws ServletException, IOException {
		CurrentTimeServlet servlet = new CurrentTimeServlet();
		servlet.init(this.config);
		servlet.doGet(this.request, this.response);
	}

	public void endCurrentTime(WebResponse response) {
		String text = response.getText().trim();
		assertNotNull(text);
		
		String expected = CurrentTimeServlet.FORMAT.format(new Date());
		assertEquals(expected, text);
	}

}