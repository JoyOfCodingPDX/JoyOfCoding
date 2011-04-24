package edu.pdx.cs410J.servlets;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.ServletException;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.util.Date;

/**
 * This servlet sets cookies on the client's browser to determine when the last
 * time the page was visited.
 */
public class LastVisitServlet extends HttpServlet {

  private static final String FIRST_VISIT_COOKIE = "firstVisited";

  private static final String LAST_VISIT_COOKIE = "lastVisited";

  private static final DateFormat DATE_FORMAT = DateFormat.getDateTimeInstance();

  protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    PrintWriter pw = response.getWriter();
    String now = DATE_FORMAT.format(new Date());
    Cookie firstVisitCookie = getCookie(request, FIRST_VISIT_COOKIE);
    Cookie lastVisitCookie;
    if (firstVisitCookie == null) {
      pw.println("<h1>Welcome to the Last Visit servlet</h1>");
      pw.println("<p>Check your browser cookies</p>");
      firstVisitCookie = new Cookie(FIRST_VISIT_COOKIE, now);
      firstVisitCookie.setComment("This first time you viewed this page");
      firstVisitCookie.setMaxAge(3600);
      response.addCookie(firstVisitCookie);

    } else {
      pw.println("<h1>Welcome back to the Last Visit servlet</h1>");
      pw.println("<p>You first visited on " + firstVisitCookie.getValue() + "</p>");

      lastVisitCookie = getCookie(request, LAST_VISIT_COOKIE);
      pw.println("<p>You were last here on " + lastVisitCookie.getValue() + "</p>");
    }

    lastVisitCookie = new Cookie(LAST_VISIT_COOKIE, now);
    lastVisitCookie.setMaxAge(3600);
    response.addCookie(lastVisitCookie);
  }

  /**
   * Returns the cookie from the given request with the given name
   *
   * @return <code>null</code> if no cookie with that name exists
   */
  private Cookie getCookie(HttpServletRequest request, String name) {
    Cookie[] cookies = request.getCookies();
    if (cookies == null) {
      return null;
    }
    for (Cookie cookie : cookies) {
      if (cookie.getName().equals(name)) {
        return cookie;
      }
    }

    return null;
  }
}
