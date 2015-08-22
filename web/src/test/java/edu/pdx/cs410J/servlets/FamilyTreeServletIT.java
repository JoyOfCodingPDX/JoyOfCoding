package edu.pdx.cs410J.servlets;

import edu.pdx.cs410J.family.FamilyTree;
import edu.pdx.cs410J.family.Person;
import edu.pdx.cs410J.family.XmlParser;
import edu.pdx.cs410J.web.HttpRequestHelper;
import org.junit.Test;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static java.net.HttpURLConnection.HTTP_NOT_FOUND;
import static java.net.HttpURLConnection.HTTP_OK;
import static org.junit.Assert.*;


/**
 * Tests the REST web service provided by the <code>FamilyTreeServlet</code>
 */
public class FamilyTreeServletIT extends HttpRequestHelper {

  private static final String PERSON_URL = "http://localhost:8080/web/family/person";

  /**
   * Creates a new <code>Person</code> using REST
   *
   * @return the id of the newly created person
   */
  private Person createPerson(Person person) throws IOException {
    if (person.getId() != 0) {
      String s = "Person " + person.getId() + " has already been created";
      throw new IllegalArgumentException(s);
    }

    List<String> params = new ArrayList<String>();

    params.add("Gender");
    params.add(person.getGender() == Person.MALE ? "Male" : "Female");

    params.add("FirstName");
    params.add(person.getFirstName());
    params.add("MiddleName");
    params.add(person.getMiddleName());
    params.add("LastName");
    params.add(person.getLastName());

    params.add("DateOfBirth");
    params.add(String.valueOf(person.getDateOfBirth().getTime()));
    params.add("DateOfDeath");
    params.add(String.valueOf(person.getDateOfBirth().getTime()));

    Response response = post(PERSON_URL, params.toArray(new String[params.size()]));
    if (response.getCode() != HTTP_OK) {
      String s = "Could not create Person (error code " + response.getCode() + "): " + response.getContent();
      throw new IllegalArgumentException(s);
    }

    XmlParser parser = new XmlParser(new StringReader(response.getContent()));
    FamilyTree tree = parser.parse();
    assertEquals(1, tree.getPeople().size());
    return tree.getPeople().iterator().next();
  }

  /**
   * Returns the person with the given id
   */
  private Person getPerson(int id) throws IOException {
    Response response = get(PERSON_URL + "/" + id);

    if (response.getCode() == HTTP_NOT_FOUND) {
      return null;

    } else if (response.getCode() != HTTP_OK) {
      String s = "Could not get Person (error code " + response.getCode() + ")";
      throw new IllegalArgumentException(s);
    }

    XmlParser parser = new XmlParser(new StringReader(response.getContent()));
    FamilyTree tree = parser.parse();
    assertEquals(1, tree.getPeople().size());
    return tree.getPeople().iterator().next();
  }

  @Test
  public void testGetNonExistentPerson() throws IOException {
     assertNull(getPerson(-42));
  }

  @Test
  public void testCreateAndGetPerson() throws IOException {
    Person person = new Person(7, Person.Gender.MALE) {
      public int getId() {
        return 0;
      }
    };
    person.setDateOfBirth(new Date());
    person.setDateOfDeath(new Date());
    person.setFirstName("Test First Name");
    person.setMiddleName("Test Middle Name");
    person.setLastName("Test Last Name");

    Person person2 = createPerson(person);
    checkPerson(person, person2);

    Person person3 = getPerson(person2.getId());
    checkPerson(person2, person3);
  }

  /**
   * Asserts that two <code>Person</code>s have the same content.
   */
  public void checkPerson(Person p1, Person p2) {
    checkDate(p1.getDateOfBirth(), p2.getDateOfBirth());
    checkDate(p1.getDateOfDeath(), p2.getDateOfDeath());
    assertEquals(p1.getFirstName(), p2.getFirstName());
    assertEquals(p1.getMiddleName(), p2.getMiddleName());
    assertEquals(p1.getLastName(), p2.getLastName());

    if (p1.getFatherId() > 0) {
      assertEquals(p1.getFatherId(), p2.getFatherId());
    }

    if (p1.getMotherId() > 0) {
      assertEquals(p1.getFatherId(), p2.getFatherId());
    }

  }

  private void checkDate(Date d1, Date d2) {
    if (d1 != null) {
      assertNotNull(d2);

    } else {
      assertNull(d2);
    }

    Calendar c1 = Calendar.getInstance();
    c1.setTime(d1);
    Calendar c2 = Calendar.getInstance();
    c2.setTime(d2);

    assertEquals(c1.get(Calendar.YEAR), c2.get(Calendar.YEAR));
    assertEquals(c1.get(Calendar.MONTH), c2.get(Calendar.MONTH));
    assertEquals(c1.get(Calendar.DATE), c2.get(Calendar.DATE));    
  }

}
