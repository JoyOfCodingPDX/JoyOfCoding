package edu.pdx.cs410E.familyTree;

import edu.pdx.cs410J.familyTree.Person;
import java.rmi.*;
import junit.framework.*;

/**
 * This class tests the functionality of the various implementation of
 * <code>RemoteFamilyTree</code>.
 */
public class RemoteFamilyTreeTest extends TestCase {

  /** The the name of the host on which the RMI registry runs */
  protected static String host =
    System.getProperty("RemoteFamilyTreeTest.HOST", "localhost");

  /** The port on which the RMI registry runs */
  protected static String port =
    System.getProperty("RemoteFamilyTreeTest.PORT", "1099");

  ////////  Constructors

  /**
   * Creates a new <code>RemoteFamilyTreeTest</code> for running the
   * test of a given name
   */
  public RemoteFamilyTreeTest(String name) {
    super(name);
  }

  public static Test suite() {
    return(new TestSuite(RemoteFamilyTreeTest.class));
  }

  /**
   * Run one test method from this class
   */
  public static void main(String[] args) throws Throwable {
    TestSuite suite = new TestSuite();

    if (args.length == 0) {
      suite.addTest(suite());

    } else {
      for (int i = 0; i < args.length; i++) {
        suite.addTest(new RemoteFamilyTreeTest(args[i]));
      }
    }

    junit.textui.TestRunner.run(suite);
  }

  ////////  Helper methods

  /**
   * Returns the name that the <code>RemoteFamilyTree</code> is bound
   * into in the RMI namespace.
   */
  protected String getFamilyName() {
    return "RemoteFamilyTreeTest";
  }

  /**
   * Returns the URL for locating the remote family tree
   */
  private String getURL() {
    return "rmi://" + host + ":" + port + "/" + this.getFamilyName();
  }

  /**
   * Returns the <code>RemoteFamilyTree</code> used by this test
   */
  protected RemoteFamilyTree getTree() {
    if (System.getSecurityManager() == null) {
      System.setSecurityManager(new RMISecurityManager());
    }

    try {
      return (RemoteFamilyTree) Naming.lookup(this.getURL());

    } catch (Exception ex) {
      fail("While getting remote family tree on " + this.getURL() +
           ": " + ex);
      return null;
    }
  }

  /**
   * Convenience method that binds a given
   * <code>RemoteFamilyTree</code> into the RMI namespace.
   */
  protected void bind(RemoteFamilyTree tree) {
    if (System.getSecurityManager() == null) {
      System.setSecurityManager(new RMISecurityManager());
    }

    try {
      Naming.rebind(this.getURL(), tree);
      System.out.println("Successfully bound RemoteFamilyTree");

    } catch (Exception ex) {
      fail("While getting remote family tree on " + this.getURL() +
           ": " + ex);
    }
  }

  /**
   * Convenience method that unbinds and shuts down the remote family
   * tree from the RMI namespace.
   */
  protected void unbind() {
    if (System.getSecurityManager() == null) {
      System.setSecurityManager(new RMISecurityManager());
    }

    try {
      RemoteFamilyTree tree = 
        (RemoteFamilyTree) Naming.lookup(this.getURL());
      tree.shutdown();
      Naming.unbind(this.getURL());
      System.out.println("Successfully unbound RemoteFamilyTree");

    } catch (Exception ex) {
      fail("While getting remote family tree on " + this.getURL() +
           ": " + ex);
    }
  }

  ////////  Test methods

  public void testCreatePerson() throws RemoteException {
    RemoteFamilyTree tree = getTree();
    RemotePerson person = tree.createPerson(Person.MALE);
  }

  public void testCreatePersonBadGender() throws RemoteException {
    try {
      RemoteFamilyTree tree = getTree();
      tree.createPerson(42);
      fail("Should have thrown IllegalArgumentException");

    } catch (IllegalArgumentException ex) {
      // pass...
    }
  }

  public void testGetPersonById() throws RemoteException {
    RemoteFamilyTree tree = getTree();
    RemotePerson person = tree.createPerson(Person.MALE);
    int id = person.getId();
    RemotePerson person2 = tree.getPerson(id);
    assertNotNull(person2);
    assertEquals(person, person2);
  }

  public void testGetNonexistingPersonById() throws RemoteException {
    assertNull(getTree().getPerson(-79));
  }

  public void testGetPersonByName() throws RemoteException {
    RemoteFamilyTree tree = getTree();
    RemotePerson person = tree.createPerson(Person.MALE);
    String firstName = "Test";
    String lastName = "Person" + System.currentTimeMillis();;
    person.setFirstName(firstName);
    person.setLastName(lastName);

    RemotePerson person2 = tree.getPerson(firstName, lastName);
    assertNotNull(person2);
    assertEquals(person, person2);
  }

  public void testGetPersonSameNameTwice() throws RemoteException {
    RemoteFamilyTree tree = getTree();
    RemotePerson person = tree.createPerson(Person.MALE);
    String firstName = "Test";
    String lastName = "Person" + System.currentTimeMillis();;
    person.setFirstName(firstName);
    person.setLastName(lastName);

    RemotePerson person2 = tree.createPerson(Person.MALE);
    person2.setFirstName(firstName);
    person2.setLastName(lastName);

    try {
      tree.getPerson(firstName, lastName);
      fail("Should have thrown an IllegalArgumentException");

    } catch (IllegalArgumentException ex) {
      // pass...
    }

  }

  public void testGetNonexistingPersonByName()
    throws RemoteException {
    assertNull(getTree().getPerson("No such", "Person"));
  }

}
