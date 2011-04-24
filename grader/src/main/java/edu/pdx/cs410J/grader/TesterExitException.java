package edu.pdx.cs410J.grader;

/**
 * This exception is thrown when the
 * <code>TesterSecurityManager</code> attempts to exit the JVM.  This
 * allows us to execute multiple <code>main</code> methods inside the
 * same JVM.  Note that it must subclass
 * <code>RuntimeException</code>.  If it didn't we'd have to
 * explicitly declare it in the <code>throws</code> clause.
 */
@SuppressWarnings("serial")
public class TesterExitException extends RuntimeException {

  /**
   * Create a new <code>TesterExitException</code>
   */
  public TesterExitException(String message) {
    super(message);
  }

}
