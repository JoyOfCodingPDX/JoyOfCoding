package edu.pdx.cs399J.familyTree.tests;

import edu.pdx.cs399J.familyTree.*;
import java.io.*;
import java.util.*;
import junit.framework.*;

/**
 * This class tests the functionality of the <code>XmlDumper</code>
 * and <code>XmlParser</code> classes.
 */
public class XmlTest extends TestCase {

  /**
   * Returns a suite containing all of the tests in this class
   */
  public static Test suite() {
    return(new TestSuite(XmlTest.class));
  }

  /**
   * Creates a new <code>XmlTest</code> for running the test of a
   * given name
   */
  public XmlTest(String name) {
    super(name);
  }

  //////// main program

  /**
   * A program that allow the user to run tests as named on the
   * command line.
   */
  public static void main(String[] args) {
    TestSuite suite = new TestSuite();

    if (args.length == 0) {
      suite.addTest(suite());

    } else {
      for (int i = 0; i < args.length; i++) {
        suite.addTest(new XmlTest(args[i]));
      }
    }

    junit.textui.TestRunner.run(suite);
  }

  ////////  Helper Methods

  /**
   * Converts a FamilyTree to XML and returns the XML as a String.
   */
  private String getXmlFor(FamilyTree tree) {
    // Write the XML to a String
    StringWriter sw = new StringWriter();
    PrintWriter pw = new PrintWriter(sw, true);
    XmlDumper dumper = new XmlDumper(pw);
    dumper.dump(tree);
    String xml = sw.toString();
    return xml;
  }

  /**
   * Parsers a FamilyTree from a String containing XML
   */
  private FamilyTree getFamilyTreeFor(String xml) 
    throws FamilyTreeException {

    // Parse the XML from the String
    StringReader sr = new StringReader(xml);
    XmlParser parser = new XmlParser(sr);
    return parser.parse();
  }

  ////////  Test cases

  public void testConformingData() {
    Person father = new Person(1, Person.MALE);
    Person mother = new Person(2, Person.FEMALE);

    Person child = new Person(3, Person.FEMALE);
    child.setFather(father);
    child.setMother(mother);

    Marriage m = new Marriage(father, mother);
    father.addMarriage(m);
    mother.addMarriage(m);

    FamilyTree tree = new FamilyTree();
    tree.addPerson(father);
    tree.addPerson(mother);
    tree.addPerson(child);

    String xml = getXmlFor(tree);
    System.out.println(xml);
    FamilyTree tree2 = null;

    try {
      tree2 = getFamilyTreeFor(xml);

    } catch (FamilyTreeException ex) {
      StringWriter sw = new StringWriter();
      ex.printStackTrace(new PrintWriter(sw, true));
      fail(sw.toString());
    }

    Collection people = tree2.getPeople();
    assertEquals(tree.getPeople().size(), people.size());
    assertTrue(people.contains(father));
    assertTrue(people.contains(mother));
    assertTrue(people.contains(child));

    Person father1 = tree2.getPerson(1);
    assertEquals(father, father1);
    Marriage m1 = 
      (Marriage) (new ArrayList(father1.getMarriages())).get(0);
    assertNotNull(m1);
    assertEquals(mother, m1.getWife());

    Person child1 = tree2.getPerson(3);
    assertEquals(father, child1.getFather());
    assertEquals(mother, child1.getMother());
  }

  public void testFatherNotMale() {

  }

  public void testMotherNotFemale() {

  }

  public void testPersonNotMale() {
    // We expect the person to be male because he was previously
    // somebody's father
  }

  public void testPersonNotFemale() {
    // We expect the person to be female because she was previously
    // somebody's mother
  }

}
