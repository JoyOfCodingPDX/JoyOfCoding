package edu.pdx.cs.joy.xml;

import edu.pdx.cs.joy.InvokeMainTestCase;
import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

public class BuildPhonebookTest extends InvokeMainTestCase {

  @Test
  public void runningMainGenerateXmlAndNoErrors() {
    MainMethodResult result = invokeMain(BuildPhonebook.class);
    assertThat(result.getTextWrittenToStandardError(), equalTo(""));
    assertThat(result.getTextWrittenToStandardOut(), containsString("<name>Tripwire, Inc.</name>"));
  }
}
