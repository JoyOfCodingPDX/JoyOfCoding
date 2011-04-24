package edu.pdx.cs410J.servlets;

import edu.pdx.cs410J.family.FamilyTree;
import edu.pdx.cs410J.family.Person;
import edu.pdx.cs410J.family.XmlDumper;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.Collection;

/**
 * A servlet that provides access to a {@link edu.pdx.cs410J.family.FamilyTree} via REST
 *
 * @since Summer 2008
 */
public class FamilyTreeServlet extends HttpServlet {
  private FamilyTree tree;

  private int nextPersonId = 1;

  public void init(ServletConfig servletConfig) throws ServletException {
    tree = new FamilyTree();
  }

  protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    createPerson(request, response);
  }

  protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    String uri = request.getRequestURI();
    String lastPart = uri.substring(uri.lastIndexOf('/') + 1, uri.length());

    if (lastPart.equals("person")) {
      Collection<Person> all = this.tree.getPeople();
      writePeople(response, all.toArray(new Person[all.size()]));

    } else {
      try {
        int id = Integer.parseInt(lastPart);
        Person person = this.tree.getPerson(id);
        if (person == null) {
          response.sendError(HttpServletResponse.SC_NOT_FOUND);
          
        } else {
         writePeople(response, person);
        }

      } catch (NumberFormatException ex) {
        response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Malformed person id: " + lastPart);
        return;
      }
    }
  }

  private void createPerson(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String gender = getParameter("Gender", request);
    if (gender == null) {
      response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing Gender");
      return;
    }

    Person person;
    try {
      person = new Person(nextPersonId++, Person.Gender.valueOf(gender.toUpperCase()));

    } catch (IllegalArgumentException ex) {
      response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Bad Gender: " + gender);
      return;
    }

    person.setFirstName(getParameter("FirstName", request));
    person.setMiddleName(getParameter("MiddleName", request));
    person.setLastName(getParameter("LastName", request));

    String motherId = getParameter("MotherId", request);
    if (motherId != null) {
      try {
        Person mother = tree.getPerson(Integer.parseInt(motherId));
        if (mother == null) {
          response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Unknown mother: " + motherId);
          return;

        } else {
          person.setMother(mother);
        }

      } catch (NumberFormatException ex) {
        response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Bad mother id: " + motherId);
        return;
      }
    }

    String fatherId = getParameter("FatherId", request);
    if (fatherId != null) {
      try {
        Person father = tree.getPerson(Integer.parseInt(fatherId));
        if (father == null) {
          response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Unknown father: " + fatherId);
          return;

        } else {
          person.setFather(father);
        }

      } catch (NumberFormatException ex) {
        response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Bad father id: " + fatherId);
        return;
      }
    }

    String dob = getParameter("DateOfBirth", request);
    if (dob != null) {
      try {
        person.setDateOfBirth(new Date(Long.parseLong(dob)));

      } catch (NumberFormatException ex) {
        response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Bad date of birth: " + dob);
        return;
      }
    }

    String dod = getParameter("DateOfDeath", request);
    if (dod != null) {
      try {
        person.setDateOfDeath(new Date(Long.parseLong(dod)));

      } catch (NumberFormatException ex) {
        response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Bad date of death: " + dod);
        return;
      }
    }

    tree.addPerson(person);

    writePeople(response, person);
  }

  /**
   * Writes people to the response
   */
  private void writePeople(HttpServletResponse response, Person... people) throws IOException {
    FamilyTree tree = new FamilyTree();
    for (Person person : people) {
      tree.addPerson(person);
    }

    new XmlDumper(new PrintWriter(response.getWriter(), true)).dump(tree);
  }

  private String getParameter(String name, HttpServletRequest request) {
    String value = request.getParameter(name);
    if (value == null || "".equals(value)) {
      return null;

    } else {
      return value;
    }
  }
}
