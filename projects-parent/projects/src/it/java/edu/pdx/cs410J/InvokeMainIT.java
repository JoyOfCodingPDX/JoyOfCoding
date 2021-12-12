package edu.pdx.cs410J;

import org.junit.jupiter.api.Test;

import java.io.*;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests the {@link #invokeMain(Class, String[])} method of {@link InvokeMainTestCase}
 */
class InvokeMainIT extends InvokeMainTestCase {

  private static final String UNCAUGHT_EXCEPTION_MESSAGE = "Uncaught exception in main";

  @Test
  void testNoExitCode() {
        MainMethodResult result = invokeMain( NoExitCode.class );
        assertThat(result.getExitCode(), nullValue());
    }

    @Test
    void testZeroExitCode() {
        MainMethodResult result = invokeMain( ZeroExitCode.class );
        assertThat(result.getExitCode(), equalTo(0));
    }

    @Test
    void testExitCodeOf1() {
        MainMethodResult result = invokeMain( ExitCodeOf1.class );
        assertThat(result.getExitCode(), equalTo(1));
    }

    @Test
    void testCommandLineArguments() {
        Integer exitCode = 27;
        MainMethodResult result = invokeMain( ExitCodeFromCommandLine.class, String.valueOf(exitCode) );
        assertThat(result.getExitCode(), equalTo(exitCode));
    }

    @Test
    void testStandardOutput() {
        MainMethodResult result = invokeMain( WriteArgsToStandardOut.class, "1", "2", "3", "4", "5" );
        assertThat(result.getTextWrittenToStandardOut(), equalTo("12345"));
    }

    @Test
    void testStandardError() {
        MainMethodResult result = invokeMain( WriteArgsToStandardErr.class, "1", "2", "3", "4", "5" );
        assertThat(result.getTextWrittenToStandardError(), equalTo("12345"));
    }

  @Test
  void codeAfterSystemExitIsNotExecuted() {
    MainMethodResult result = invokeMain(CodeAfterSystemExit.class);
    assertThat(result.getExitCode(), equalTo(1));
    assertThat(result.getTextWrittenToStandardOut(), equalTo(""));
  }

  @Test
  void uncaughtExceptionInMainThrowsUncaughtExceptionInMain() {
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

  @Test
  void invokingMainOfClassWithMutableStaticFieldsThrowsException() {
    MainClassContainsMutableStaticFields ex = assertThrows(MainClassContainsMutableStaticFields.class, () -> invokeMain(ClassWithMutableStaticFields.class));
    assertThat(ex.getNamesOfMutableStaticFields(), hasItem(ClassWithMutableStaticFields.MUTUAL_FIELD_NAME));
  }

  @Test
  void invokeMainAllowingMutableStaticFieldsIsOkay() {
    MainMethodResult result = invokeMainAllowingMutableStaticFields(ClassWithMutableStaticFields.class);
    assertThat(result.getTextWrittenToStandardOut(), containsString(ClassWithMutableStaticFields.getMutableField()));
  }

  @Test
  void textWrittenToStandardInputIsReadByMainMethod() {
    String text = "Text read from standard input";

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    PrintWriter pw = new PrintWriter(baos, true);
    pw.println(text);
    byte[] bytes = baos.toByteArray();
    InputStream in = new ByteArrayInputStream(bytes);
    System.setIn(in);

    MainMethodResult result = invokeMain(WriteStandardInToStandardOut.class);
    assertThat(result.getTextWrittenToStandardOut().trim(), equalTo(text));
  }

  private static class WriteStandardInToStandardOut {
    public static void main(String[] args) throws IOException {
      try (
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in))
      ) {
        for (String line = br.readLine(); line != null; line = br.readLine()) {
          System.out.println(line);
        }
        System.out.flush();
      }
    }
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

  private static class ClassWithMutableStaticFields {

    private static final String MUTUAL_FIELD_NAME = "mutableField";

    private static final String MAIN_WAS_INVOKED_MESSAGE = "main was invoked";

    private static String mutableField;

    public static void main(String[] args) {
      mutableField = MAIN_WAS_INVOKED_MESSAGE;
      System.out.println(mutableField);
    }

    public static String getMutableField() {
      return mutableField;
    }

  }
}