package edu.pdx.cs.joy.grader;

import edu.pdx.cs.joy.InvokeMainTestCase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.isEmptyString;

class ProjectTimeEstimatesSummaryIT extends InvokeMainTestCase {

  @Test
  void commandLineReportIncludesAiAssistedColumn(@TempDir Path tempDir) throws IOException {
    Path gradebook = tempDir.resolve("gradebook.xml");
    Files.writeString(gradebook, """
      <!DOCTYPE gradebook PUBLIC "-//Joy of Coding at PSU//DTD Grade Book//EN"
        "http://www.cs.pdx.edu/~whitlock/dtds/gradebook.dtd">
      <gradebook><name>test</name><assignments/><students/></gradebook>
      """);

    MainMethodResult result = invokeMain(ProjectTimeEstimatesSummary.class, gradebook.toString());

    assertThat(result.getTextWrittenToStandardOut(), containsString("| DATABASE | AI Assisted | REST | Android |"));
    assertThat(result.getTextWrittenToStandardOut(), containsString("| Count | 0 | 0 | 0 | 0 | 0 | 0 | 0 | 0 | 0 |"));
    assertThat(result.getTextWrittenToStandardError(), isEmptyString());
  }
}
