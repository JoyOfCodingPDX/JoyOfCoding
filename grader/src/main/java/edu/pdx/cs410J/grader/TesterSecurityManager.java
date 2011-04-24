package edu.pdx.cs410J.grader;


/**
 * <P>This class allows us to call a <code>main</code> method multiple
 * times without the JVM exiting.  Basically, every time the JVM goes
 * to exit, we throw a <code>TesterExitException</code> which we then
 * catch and ignore.</P>
 *
 * <P>Note that if you install this security manager, you will have to
 * write a policy file that grants permissions to the code base from
 * which the user class was loaded.</P>
 *
 * @author My good buddy, Nate Nystrom
 */
public class TesterSecurityManager extends SecurityManager {

  /**
   * Controls whether or not we allow the JVM to exit.
   */
  private boolean allowExit = false;

  /**
   * Sets whether or not this security manager will allow the VM to
   * exit.
   */
  public void setAllowExit(boolean allowExit) {
    this.allowExit = allowExit;
  }

  /** 
   * A <code>SecurityException</code> is thrown if we do not allow the
   * virtual machine to exit.  
   */
  public void checkExit(int status) {
//      System.err.println("exit " + status);
    if (! allowExit) {
      throw new TesterExitException("Tried to exit (status=" +
                                    status + ")");
    }
  }

//    public void checkCreateClassLoader() { 
//  //      System.err.println("Creating a class loader");
//    }
//    public void checkAccess(Thread t) {}
//    public void checkAccess(ThreadGroup g) {}
//    public void checkExec(String cmd) {}
//    public void checkLink(String lib) {}
//    public void checkRead(FileDescriptor fd) {}
//    public void checkRead(String file) {}
//    public void checkRead(String file, Object context) {}
//    public void checkWrite(FileDescriptor fd) {}
//    public void checkWrite(String file) {}
//    public void checkDelete(String file) {}
//    public void checkConnect(String host, int port) {}
//    public void checkConnect(String host, int port, Object context) {}
//    public void checkListen(int port) {}
//    public void checkAccept(String host, int port) {}
//    public void checkPermission(Permission perm) {}
//    public void checkPermission(Permission perm, Object context) {}
//    public void checkPropertiesAccess() {
//      System.out.println("Checking properties");
//      super.checkPropertiesAccess();
//    }
//    public void checkPropertyAccess(String key) {
//      System.out.println("Checking property: " + key);
//      super.checkPropertyAccess(key);
//    }
//    public boolean checkTopLevelWindow(Object window) { return true; }
//    public void checkPackageAccess(String pkg) {}
//    public void checkPackageDefinition(String pkg) {}
//    public void checkSetFactory() {}
}
