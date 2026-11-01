package edu.pdx.cs.joy.servlets;

import jakarta.servlet.http.*;
import jakarta.servlet.ServletException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

/**
 * A servlet that uses a session to maintain information about a user
 */
public class SessionServlet extends HttpServlet {
  private static final String VISIT_COUNT = "VISIT_COUNT";

  /**
   * Generates HTML that summarizes the session
   */
  protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    HttpSession session = request.getSession(false);
    if (session == null) {
      session = request.getSession(true);
      session.setAttribute(VISIT_COUNT, 0);
    }

    int visits = (Integer) session.getAttribute(VISIT_COUNT);
    visits++;
    session.setAttribute(VISIT_COUNT, visits);

    response.setContentType("text/html");
    PrintWriter out = response.getWriter();
    dumpHeader(out);

    out.println("<h1>You have visited this page " + visits + " times</h1>");

    dumpSession(session, out);
    dumpCoookies(request.getCookies(), out);

    out.println("<h2>End the session</h2>");

    out.println("<form action='session' method='post'>");
    out.println("<input type='submit' value='End Session'/>");
    out.println("</form>");

    dumpFooter(out);
  }

  private void dumpFooter(PrintWriter out) {
    out.println("</body>");
    out.println("</html>");
  }

  private void dumpHeader(PrintWriter out) {
    out.println("<html>");
    out.println("<head><title>Session Demo</title></head>");
    out.println("<body>");
  }

  /**
   * Ends the session
   */
  protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    HttpSession session = request.getSession(false);
    if (session != null) {
      session.invalidate();
    }

    response.setContentType("text/html");
    PrintWriter out = response.getWriter();
    dumpHeader(out);

    out.println("<h1>Your session has ended</h1>");

    dumpCoookies(request.getCookies(), out);

    out.println("<h2>Return to Session</h2>");
    out.println("<form action='session' method='get'>");
    out.println("<input type='submit' value='Go Back'/>");
    out.println("</form>");

    dumpFooter(out);
  }

  private void dumpCoookies(Cookie[] cookies, PrintWriter out) {
    out.println("<h2>Your cookies:</h2>");
    out.println("<table border='1'>");
    dumpCookiesTableHeader(out);
    out.println("<tbody>");
    Arrays.sort(cookies, Comparator.comparing(Cookie::getName));
    for (Cookie cookie : cookies) {
      out.println("<tr>");
      dumpTableCell(cookie.getName(), out);
      dumpTableCell(cookie.getValue(), out);
      dumpTableCell(cookie.getDomain(), out);
      dumpTableCell(cookie.getPath(), out);
      dumpTableCell(cookie.getMaxAge(), out);
      dumpTableCell(cookie.getSecure(), out);
      out.println("</tr>");
    }
    out.println("</tbody>");
    out.println("<table>");
  }

  private void dumpTableCell(Object value, PrintWriter out) {
    out.println("<td>" + value + "</td>");
  }

  private void dumpCookiesTableHeader(PrintWriter out) {
    out.println("<thead><tr>");
    out.println("<th>Name</th>");
    out.println("<th>Value</th>");
    out.println("<th>Domain</th>");
    out.println("<th>Path</th>");
    out.println("<th>Max Age</th>");
    out.println("<th>Secure</th>");
    out.println("</tr></thead>");
  }

  private void dumpSession(HttpSession session, PrintWriter out) {
    out.println("<h2>Your session:</h2>");
    dumpKeyValue("Id", session.getId(), out);
    dumpKeyValue("Creation Time" , new Date(session.getCreationTime()), out);
    dumpKeyValue("Last Accessed Time", new Date(session.getLastAccessedTime()), out);
    dumpKeyValue("Max inactive interval", session.getMaxInactiveInterval(), out);
    for (Enumeration e = session.getAttributeNames(); e.hasMoreElements(); ) {
      String name = (String) e.nextElement();
      dumpKeyValue("Attribute \"" + name + "\"", session.getAttribute(name), out);
    }
  }

  private void dumpKeyValue(String name, Object value, PrintWriter out) {
    out.println("<p><b>" + name + "</b>: " + value + "</p>");
  }
}
