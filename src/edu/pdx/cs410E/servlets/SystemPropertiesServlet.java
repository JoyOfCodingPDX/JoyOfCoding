package edu.pdx.cs410E.servlets;

import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;

/**
 * A simple servlet that returns an HTML table that contains the
 * values of all of the Java system properties.
 *
 * @see System#getProperties
 *
 * @author David Whitlock
 * @since Summer 2005
 */
public class SystemPropertiesServlet extends HttpServlet {

	/**
	 * Respond to a web request by generating HTML that contains this
	 * VM's system properties.
	 */
	protected void doGet(HttpServletRequest request,
											 HttpServletResponse response)
		throws ServletException, IOException {

		response.setContentType("text/html");
		response.setBufferSize(8192);

		PrintWriter out = response.getWriter();
		out.println("<HTML>");
		out.println("<TITLE>Java System Properties</TITLE");
		out.println("<BODY>");
		out.println("<P>Java System Properties</P>");
		out.println("<TABLE BORDER=\"1\">");
		out.println("<TR>");
		out.println("<TH>Key</TH><TH>Value</TH>");
		out.println("</TR>");

		Properties props = System.getProperties();

		for (Iterator iter = props.entrySet().iterator(); iter.hasNext(); ) {
			out.println("  <TR>");
			Map.Entry entry = (Map.Entry) iter.next();
			out.print("<TD>");
			out.print(entry.getKey());
			out.print("  </TD><TD>");
			out.print(entry.getValue());
			out.println("</TD>");

			out.println("</TR>");
		}

		out.println("</TABLE>");
		out.println("</BODY>");
		out.println("</HTML>");

		out.close();
	}

}