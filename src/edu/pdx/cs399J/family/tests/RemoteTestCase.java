package edu.pdx.cs399J.family.tests;

import edu.pdx.cs399J.family.*;

import java.rmi.*;
import java.util.*;
import junit.framework.*;

/**
 * This is the abstract superclass for all of the remote family
 * tree tests.
 */
public abstract class RemoteTestCase extends TestCase {

  /** The the name of the host on which the RMI registry runs */
  protected static String host =
    System.getProperty("RemoteTestCase.HOST", "localhost");

  /** The port on which the RMI registry runs */
  protected static String port =
    System.getProperty("RemoteTestCase.PORT", "1099");

  ////////  Constructors

  public RemoteTestCase(String name) {
    super(name);
  }

  ////////  Helper methods

  /**
   * Returns the name that the <code>RemoteFamilyTree</code> is bound
   * into in the RMI namespace.
   */
  protected String getFamilyName() {
    return "RemoteTestCase";
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

  /**
   * Asserts the equality of two dates.  Only takes the month, day,
   * and year into account.
   */
  protected void assertEquals(Date d1, Date d2) {
    Calendar cal1 = Calendar.getInstance();
    cal1.setTime(d1);

    Calendar cal2 = Calendar.getInstance();
    cal1.setTime(d2);

    assertEquals(cal1.get(Calendar.DAY_OF_YEAR),
                 cal2.get(Calendar.DAY_OF_YEAR));
    assertEquals(cal1.get(Calendar.YEAR), cal2.get(Calendar.YEAR));
  }

}
