package edu.pdx.cs.joy.jdbc;

import edu.pdx.cs.joy.family.FamilyTree;
import edu.pdx.cs.joy.family.Marriage;
import edu.pdx.cs.joy.family.Person;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

/**
 * Unit test for FamilyTree DAO classes.
 * Tests persistence of Person, Marriage, and FamilyTree objects to an H2 database.
 */
public class FamilyTreeDAOTest {

  private Connection connection;
  private FamilyTreeDAO familyTreeDAO;

  @BeforeEach
  public void setUp() throws SQLException {
    // Create an in-memory H2 database
    connection = H2DatabaseHelper.createInMemoryConnection("familyTreeTest");

    // Drop and create tables
    FamilyTreeDAO.dropTables(connection);
    FamilyTreeDAO.createTables(connection);

    // Initialize the DAO
    familyTreeDAO = new FamilyTreeDAOImpl(connection);
  }

  @AfterEach
  public void tearDown() throws SQLException {
    if (connection != null && !connection.isClosed()) {
      FamilyTreeDAO.dropTables(connection);
      connection.close();
    }
  }

  @Test
  public void testPersistAndLoadSimpleFamilyTree() throws SQLException {
    // Create a simple family tree
    FamilyTree familyTree = new FamilyTree();

    Person father = new Person(1, Person.MALE);
    father.setFirstName("John");
    father.setLastName("Doe");
    father.setDateOfBirth(createDate(1970, 1, 15));

    Person mother = new Person(2, Person.FEMALE);
    mother.setFirstName("Jane");
    mother.setLastName("Smith");
    mother.setDateOfBirth(createDate(1972, 3, 20));

    Person child = new Person(3, Person.MALE);
    child.setFirstName("Jack");
    child.setLastName("Doe");
    child.setDateOfBirth(createDate(2000, 6, 10));
    child.setFather(father);
    child.setMother(mother);

    familyTree.addPerson(father);
    familyTree.addPerson(mother);
    familyTree.addPerson(child);

    // Add marriage
    Marriage marriage = new Marriage(father, mother);
    marriage.setDate(createDate(1998, 5, 15));
    marriage.setLocation("Portland, OR");
    father.addMarriage(marriage);
    mother.addMarriage(marriage);

    // Save to database
    familyTreeDAO.save(familyTree);

    // Load from database
    FamilyTree loadedTree = familyTreeDAO.load();

    // Validate
    assertThat(loadedTree.getPeople(), hasSize(3));
    assertThat(loadedTree.containsPerson(1), is(true));
    assertThat(loadedTree.containsPerson(2), is(true));
    assertThat(loadedTree.containsPerson(3), is(true));

    // Validate father
    Person loadedFather = loadedTree.getPerson(1);
    assertThat(loadedFather.getFirstName(), is(equalTo("John")));
    assertThat(loadedFather.getLastName(), is(equalTo("Doe")));
    assertThat(loadedFather.getGender(), is(equalTo(Person.MALE)));
    assertThat(loadedFather.getMarriages(), hasSize(1));

    // Validate mother
    Person loadedMother = loadedTree.getPerson(2);
    assertThat(loadedMother.getFirstName(), is(equalTo("Jane")));
    assertThat(loadedMother.getLastName(), is(equalTo("Smith")));
    assertThat(loadedMother.getGender(), is(equalTo(Person.FEMALE)));

    // Validate child
    Person loadedChild = loadedTree.getPerson(3);
    assertThat(loadedChild.getFirstName(), is(equalTo("Jack")));
    assertThat(loadedChild.getFather(), is(notNullValue()));
    assertThat(loadedChild.getFather().getId(), is(equalTo(1)));
    assertThat(loadedChild.getMother(), is(notNullValue()));
    assertThat(loadedChild.getMother().getId(), is(equalTo(2)));

    // Validate marriage
    Marriage loadedMarriage = loadedFather.getMarriages().iterator().next();
    assertThat(loadedMarriage.getHusband().getId(), is(equalTo(1)));
    assertThat(loadedMarriage.getWife().getId(), is(equalTo(2)));
    assertThat(loadedMarriage.getLocation(), is(equalTo("Portland, OR")));
  }

  @Test
  public void testPersistMultipleGenerations() throws SQLException {
    FamilyTree familyTree = new FamilyTree();

    // Grandparents
    Person grandfather = new Person(1, Person.MALE);
    grandfather.setFirstName("William");
    grandfather.setLastName("Doe");

    Person grandmother = new Person(2, Person.FEMALE);
    grandmother.setFirstName("Mary");
    grandmother.setLastName("Johnson");

    // Parents
    Person father = new Person(3, Person.MALE);
    father.setFirstName("John");
    father.setLastName("Doe");
    father.setFather(grandfather);
    father.setMother(grandmother);

    Person mother = new Person(4, Person.FEMALE);
    mother.setFirstName("Jane");
    mother.setLastName("Smith");

    // Child
    Person child = new Person(5, Person.FEMALE);
    child.setFirstName("Emily");
    child.setLastName("Doe");
    child.setFather(father);
    child.setMother(mother);

    familyTree.addPerson(grandfather);
    familyTree.addPerson(grandmother);
    familyTree.addPerson(father);
    familyTree.addPerson(mother);
    familyTree.addPerson(child);

    // Save and load
    familyTreeDAO.save(familyTree);
    FamilyTree loadedTree = familyTreeDAO.load();

    // Validate multi-generation relationships
    assertThat(loadedTree.getPeople(), hasSize(5));

    Person loadedChild = loadedTree.getPerson(5);
    assertThat(loadedChild.getFather(), is(notNullValue()));
    assertThat(loadedChild.getFather().getId(), is(equalTo(3)));

    Person loadedFather = loadedChild.getFather();
    assertThat(loadedFather.getFather(), is(notNullValue()));
    assertThat(loadedFather.getFather().getId(), is(equalTo(1)));
    assertThat(loadedFather.getMother(), is(notNullValue()));
    assertThat(loadedFather.getMother().getId(), is(equalTo(2)));
  }

  @Test
  public void testPersistPersonWithDates() throws SQLException {
    FamilyTree familyTree = new FamilyTree();

    Person person = new Person(1, Person.MALE);
    person.setFirstName("George");
    person.setLastName("Washington");
    person.setDateOfBirth(createDate(1732, 2, 22));
    person.setDateOfDeath(createDate(1799, 12, 14));

    familyTree.addPerson(person);

    familyTreeDAO.save(familyTree);
    FamilyTree loadedTree = familyTreeDAO.load();

    Person loadedPerson = loadedTree.getPerson(1);
    assertThat(loadedPerson.getDateOfBirth(), is(notNullValue()));
    assertThat(loadedPerson.getDateOfDeath(), is(notNullValue()));
  }

  @Test
  public void testLoadEmptyFamilyTree() throws SQLException {
    FamilyTree loadedTree = familyTreeDAO.load();
    assertThat(loadedTree.getPeople(), is(empty()));
  }

  @Test
  public void testPersistPersonWithoutParents() throws SQLException {
    FamilyTree familyTree = new FamilyTree();

    Person person = new Person(1, Person.FEMALE);
    person.setFirstName("Alice");
    person.setLastName("Unknown");

    familyTree.addPerson(person);

    familyTreeDAO.save(familyTree);
    FamilyTree loadedTree = familyTreeDAO.load();

    Person loadedPerson = loadedTree.getPerson(1);
    assertThat(loadedPerson, is(notNullValue()));
    assertThat(loadedPerson.getFather(), is(nullValue()));
    assertThat(loadedPerson.getMother(), is(nullValue()));
  }

  /**
   * Helper method to create a Date object.
   */
  private Date createDate(int year, int month, int day) {
    Calendar cal = Calendar.getInstance();
    cal.set(year, month - 1, day, 0, 0, 0);
    cal.set(Calendar.MILLISECOND, 0);
    return cal.getTime();
  }
}

