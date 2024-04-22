package edu.pdx.cs.joy.servlets;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * This servlet sets cookies on the client's browser to determine when the last
 * time the page was visited.
 */
public class LastVisitServlet extends HttpServlet {

  private static final String FIRST_VISIT_COOKIE = "firstVisited";

  private static final String LAST_VISIT_COOKIE = "lastVisited";

  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    PrintWriter pw = response.getWriter();
    LocalDateTime now = LocalDateTime.now();
    Cookie firstVisitCookie = getCookie(request, FIRST_VISIT_COOKIE);
    Cookie lastVisitCookie;
    if (firstVisitCookie == null) {
      pw.println("<h1>Welcome to the Last Visit servlet</h1>");
      pw.println("<p>Check your browser cookies</p>");
      firstVisitCookie = createSecureCookie(FIRST_VISIT_COOKIE, now);
      firstVisitCookie.setComment("This first time you viewed this page");
      response.addCookie(firstVisitCookie);

    } else {
      pw.println("<h1>Welcome back to the Last Visit servlet</h1>");
      pw.println("<p>You first visited on " + firstVisitCookie.getValue() + "</p>");

      lastVisitCookie = getLastVisitCookie(request);
      pw.println("<p>You were last here on " + lastVisitCookie.getValue() + "</p>");
    }

    lastVisitCookie = createSecureCookie(LAST_VISIT_COOKIE, now);
    response.addCookie(lastVisitCookie);
  }

  private Cookie createSecureCookie(String name, LocalDateTime now) {
    Cookie cookie = new Cookie(name, now.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
    cookie.setSecure(true);
    cookie.setMaxAge(3600);
    return cookie;
  }

  private Cookie getLastVisitCookie(HttpServletRequest request) {
    Cookie cookie = getCookie(request, LAST_VISIT_COOKIE);
    if (cookie == null) {
      throw new NullPointerException("Expected to have a " + LAST_VISIT_COOKIE + " cookie");
    }
    return cookie;
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
