package edu.pdx.cs.joy.apptbook;

import edu.pdx.cs.joy.InvokeMainTestCase;
import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Integration tests for the {@link Project1} main class.
 */
class Project1IT extends InvokeMainTestCase {

  /**
   * Invokes the main method of {@link Project1} with the given arguments.
   */
  private MainMethodResult invokeMain(String... args) {
    return invokeMain( Project1.class, args );
  }

  /**
   * Tests that invoking the main method with no arguments issues an error
   */
  @Test
  void testNoCommandLineArguments() {
    MainMethodResult result = invokeMain();
    assertThat(result.getTextWrittenToStandardError(), containsString("Missing appointment book information"));
  }

}