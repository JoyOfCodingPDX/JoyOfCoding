package edu.pdx.cs410E.servlets;

import java.io.*;
import java.text.DateFormat;
import java.util.Date;
import javax.servlet.*;
import javax.servlet.http.*;

/**
 * A simple servlet that returns the current date/time as text.  This
 * servlet is used to demonstrate the functionality of the Cactus
 * testing framework.
 *
 * @author David Whitlock
 * @since Summer 2005
 */
public class CurrentTimeServlet extends HttpServlet {

	/** Used to format the date/time.  This field is package protected
	 * for testing purposes. */
	static DateFormat FORMAT =
		DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG); 

	////////////////////////  Constructors  ////////////////////////

	public CurrentTimeServlet() {
		super();
	}

	//////////////////////  Instance Methods  //////////////////////

	/**
	 * Simply reply with the date and time in a specific {@link
	 * #FORMAT format}.
	 */
	protected void doGet(HttpServletRequest request,
											 HttpServletResponse response)
		throws ServletException, IOException {

		response.setContentType("text/plain");
		response.setBufferSize(8192);
		PrintWriter out = response.getWriter();

		String text = FORMAT.format(new Date());
		out.println(text);
		out.close();
	}

}