package edu.pdx.cs.joy;

import java.util.List;

/**
 * Throws when the <code>main</code> method of a class with mutable <code>static</code>
 * fields is invoked with {@link InvokeMainTestCase#invokeMain(Class, String...)}.
 *
 * Main classes with mutable static fields have proven to be problematic because the
 * values of the mutable fields are not reset in between tests.  As a result, the values
 * of these fields from previous tests might stick around.  This has proven to be very
 * confusing.
 *
 * @see InvokeMainTestCase#invokeMainAllowingMutableStaticFields(Class, String...)
 */
public class MainClassContainsMutableStaticFields extends RuntimeException {

  private final Iterable<String> fieldNames;

  public MainClassContainsMutableStaticFields(String className, List<String> fieldNames) {
    super(getMessage(className, fieldNames));
    this.fieldNames = fieldNames;
  }

  private static String getMessage(String className, List<String> fieldNames) {
    return "Main class \"" +
      className +
      "\" contains " +
      fieldNames.size() +
      " non-final static fields: " +
      String.join(", ", fieldNames) +
      ".  You probably don't want to store data in mutable static fields.  " +
      "It might cause confusing behavior with your integration tests.";
  }

  public Iterable<String> getNamesOfMutableStaticFields() {
    return this.fieldNames;
  }
}
