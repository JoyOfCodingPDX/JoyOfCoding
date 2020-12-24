package edu.pdx.cs410J.xml;

import edu.pdx.cs410J.InvokeMainTestCase;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

public class BuildPhonebookTest extends InvokeMainTestCase {

  @Test
  public void runningMainGenerateXmlAndNoErrors() {
    MainMethodResult result = invokeMain(BuildPhonebook.class);
    assertThat(result.getExitCode(), nullValue());
    assertThat(result.getTextWrittenToStandardError(), equalTo(""));
    assertThat(result.getTextWrittenToStandardOut(), containsString("<name>Tripwire, Inc.</name>"));
  }
}
