package edu.pdx.cs410J.family;

import org.junit.After;
import org.junit.Assert;
import static org.junit.Assert.assertNotNull;
import org.junit.Before;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Calendar;
import java.util.Date;


/**
 * This is the abstract superclass for all of the remote family
 * tree tests.
 */
public abstract class RemoteTestCase {

  /** The port on which the RMI registry runs */
  private static final int RMI_PORT =
    Integer.getInteger("RMI_PORT", 1999).intValue();

  /** An RMI Registry that is co-located in the VM in which the test
   * runs */
  private static Registry registry;

  /** The name of this test */
  private String name = "RemoteTestCase" + System.currentTimeMillis();

    /**
   * Creates an empty <code>RemoteFamilyTree</code> and binds it into
   * the RMI namespace.
   */
  @Before
  public void setUp() {
    try {
      File file = File.createTempFile("familyTree", "xml");
      file.delete();
      file.deleteOnExit();
      RemoteFamilyTree tree = new XmlRemoteFamilyTree(file);
      this.bind(tree);

    } catch (Exception ex) {
      fail("While getting creating remote family tree", ex);
    }
  }

  /**
   * Unbinds the remote family tree from the RMI namespace
   */
  @After
  public void tearDown() {
    this.unbind();
  }

  /**
   * Returns the name that the <code>RemoteFamilyTree</code> is bound
   * into in the RMI namespace.
   */
  protected String getFamilyName() {
    return "RemoteTestCase";
  }

  /**
   * Returns an RMI registry that is co-located in this VM.  If one
   * doesn't exist, it is started on {@link #RMI_PORT}
   */
  private Registry getRegistry() {
    if (registry != null) {
      return registry;
    }

    synchronized (RemoteTestCase.class) {
      if (registry != null) {
        return registry;
      }

      try {
        registry = LocateRegistry.createRegistry(RMI_PORT);

      } catch (RemoteException ex) {
        try {
          registry = LocateRegistry.createRegistry(RMI_PORT);

        } catch (RemoteException ex1) {
          fail("Couldn't create local registry", ex);
        }
      }

      assertNotNull(registry);
      return registry;
    }
  }

  /**
   * Returns the <code>RemoteFamilyTree</code> used by this test
   */
  protected RemoteFamilyTree getTree() {
//     if (System.getSecurityManager() == null) {
//       System.setSecurityManager(new RMISecurityManager());
//     }

    Registry registry = getRegistry();
    try {
      return (RemoteFamilyTree) registry.lookup(this.getName());

    } catch (Exception ex) {
      fail("While getting remote family tree on " + this.getName(), ex);
      return null;
    }
  }

  /**
   * Convenience method that binds a given
   * <code>RemoteFamilyTree</code> into the RMI namespace.
   */
  protected void bind(RemoteFamilyTree tree) {
//     if (System.getSecurityManager() == null) {
//       System.setSecurityManager(new RMISecurityManager());
//     }

    Registry registry = getRegistry();
    try {
      registry.rebind(this.getName(), tree);
      System.out.println("Successfully bound RemoteFamilyTree");

    } catch (Exception ex) {
      fail("While getting remote family tree on " + this.getName(), ex);
    }
  }

  /**
   * Convenience method that unbinds and shuts down the remote family
   * tree from the RMI namespace.
   */
  protected void unbind() {
//     if (System.getSecurityManager() == null) {
//       System.setSecurityManager(new RMISecurityManager());
//     }

    Registry registry = getRegistry();
    try {
      RemoteFamilyTree tree = 
        (RemoteFamilyTree) registry.lookup(this.getName());
      tree.shutdown();
      registry.unbind(this.getName());
      System.out.println("Successfully unbound RemoteFamilyTree");

    } catch (Exception ex) {
      fail("While getting remote family tree on " + this.getName(), ex);
    }
  }

  private String getName()
  {
    return name;
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

    Assert.assertEquals(cal1.get(Calendar.DAY_OF_YEAR),
                 cal2.get(Calendar.DAY_OF_YEAR));
    Assert.assertEquals(cal1.get(Calendar.YEAR), cal2.get(Calendar.YEAR));
  }

  /**
   * A JUnit failure that is caused by an exception.  This method
   * provides us with a nice strack trace for the failure.
   *
   * @since Winter 2004
   */
  public static void fail(String message, Throwable cause) {
    StringWriter sw = new StringWriter();
    sw.write(message);
    sw.write("\nCaused by: ");

    PrintWriter pw = new PrintWriter(sw, true);
    cause.printStackTrace(pw);
    Assert.fail(sw.toString());
  }

}
