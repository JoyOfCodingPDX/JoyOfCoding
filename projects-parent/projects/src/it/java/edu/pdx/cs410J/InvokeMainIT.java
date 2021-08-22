package edu.pdx.cs410J;

import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests the {@link #invokeMain(Class, String[])} method of {@link InvokeMainTestCase}
 */
public class InvokeMainIT extends InvokeMainTestCase {

  private static final String UNCAUGHT_EXCEPTION_MESSAGE = "Uncaught exception in main";

  @Test
    public void testNoExitCode() {
        MainMethodResult result = invokeMain( NoExitCode.class );
        assertThat(result.getExitCode(), nullValue());
    }

    @Test
    public void testZeroExitCode() {
        MainMethodResult result = invokeMain( ZeroExitCode.class );
        assertThat(result.getExitCode(), equalTo(0));
    }

    @Test
    public void testExitCodeOf1() {
        MainMethodResult result = invokeMain( ExitCodeOf1.class );
        assertThat(result.getExitCode(), equalTo(1));
    }

    @Test
    public void testCommandLineArguments() {
        Integer exitCode = 27;
        MainMethodResult result = invokeMain( ExitCodeFromCommandLine.class, String.valueOf(exitCode) );
        assertThat(result.getExitCode(), equalTo(exitCode));
    }

    @Test
    public void testStandardOutput() {
        MainMethodResult result = invokeMain( WriteArgsToStandardOut.class, "1", "2", "3", "4", "5" );
        assertThat(result.getTextWrittenToStandardOut(), equalTo("12345"));
    }

    @Test
    public void testStandardError() {
        MainMethodResult result = invokeMain( WriteArgsToStandardErr.class, "1", "2", "3", "4", "5" );
        assertThat(result.getTextWrittenToStandardError(), equalTo("12345"));
    }

  @Test
  public void codeAfterSystemExitIsNotExecuted() {
    MainMethodResult result = invokeMain(CodeAfterSystemExit.class);
    assertThat(result.getExitCode(), equalTo(1));
    assertThat(result.getTextWrittenToStandardOut(), equalTo(""));
  }

  @Test
  public void uncaughtExceptionInMainThrowsUncaughtExceptionInMain() {
    UncaughtExceptionInMain ex = assertThrows(UncaughtExceptionInMain.class, () -> invokeMain(MainThrowsIllegalStateException.class));

    Throwable cause = ex.getCause();
    assertThat(cause, instanceOf(IllegalStateException.class));
    assertThat(cause.getMessage(), equalTo(UNCAUGHT_EXCEPTION_MESSAGE));
  }

  @Test
  void canNotRelyOnEqualsOperatorToCompareArgs() {
    MainMethodResult result = invokeMain(MainComparesArgStringWithEqualsOperator.class, (String) MainComparesArgStringWithEqualsOperator.ARG_STRING);
    assertThat(result.getExitCode(), equalTo(0));
  }

    private static class NoExitCode
    {
        public static void main(String... args) {
            // Doesn't invoke System.exit()
        }
    }

    private static class ZeroExitCode
    {
        public static void main(String... args) {
            System.exit(0);
        }
    }

    private static class ExitCodeOf1
    {
        public static void main(String... args) {
            System.exit(1);
        }
    }

    private static class ExitCodeFromCommandLine
    {
        public static void main(String... args)
        {
            System.exit(Integer.parseInt(args[0]));
        }
    }

    private static class WriteArgsToStandardOut
    {
        public static void main(String... args) {
            for (String arg : args) {
                System.out.print(arg);
            }
            System.out.flush();
        }
    }

    private static class WriteArgsToStandardErr
    {
        public static void main(String... args) {
            for (String arg : args) {
                System.err.print(arg);
            }
            System.err.flush();
        }
    }

  private static class CodeAfterSystemExit {
    public static void main(String... args) {
      callSystemExit1();
      System.out.println("Should not get here");
      throw new RuntimeException("Should not get here");
    }

    private static void callSystemExit1() {
      System.exit(1);
    }
  }

  private static class MainThrowsIllegalStateException {

    public static void main(String[] args) {
      throw new IllegalStateException(UNCAUGHT_EXCEPTION_MESSAGE);
    }

  }

  private static class MainComparesArgStringWithEqualsOperator {

    private static final Object ARG_STRING = "ARG";

    public static void main(String[] args) {
      if (ARG_STRING == args[0]) {
        System.exit(1);

      } else if (ARG_STRING.equals(args[0])) {
        System.exit(0);
      }
    }
  }
}