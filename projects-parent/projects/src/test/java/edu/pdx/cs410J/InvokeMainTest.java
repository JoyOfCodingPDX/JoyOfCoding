package edu.pdx.cs410J;

import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;

public final class InvokeMainTest extends InvokeMainTestCase {

  @Test
  public void commandLineArgsWrittenToStandardOutAreCaptured() {
    String[] args = { "One", "Two", "Three"};
    MainMethodResult result = invokeMain(InvokeMainTest.class, args);

    String out = result.getTextWrittenToStandardOut();
    for(String arg : args) {
      assertThat(out, containsString(arg));
    }
  }

  public static void main(String... args) {
    for (String arg : args) {
      System.out.println(arg);
    }
  }
}
