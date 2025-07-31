package edu.pdx.cs.joy.grader;

import com.google.common.annotations.VisibleForTesting;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.ZonedDateTime;

public class FindUngradedSubmissions {
  private final SubmissionDetailsProvider submissionDetailsProvider;
  private final TestOutputPathProvider testOutputProvider;
  private final TestOutputDetailsProvider testOutputDetailsProvider;

  @VisibleForTesting
  FindUngradedSubmissions(SubmissionDetailsProvider submissionDetailsProvider, TestOutputPathProvider testOutputProvider, TestOutputDetailsProvider testOutputDetailsProvider) {
    this.submissionDetailsProvider = submissionDetailsProvider;
    this.testOutputProvider = testOutputProvider;
    this.testOutputDetailsProvider = testOutputDetailsProvider;
  }


  public boolean isGraded(Path submissionPath) {
    SubmissionDetails submission = this.submissionDetailsProvider.getSubmissionDetails(submissionPath);
    Path testOutput = this.testOutputProvider.getTestOutput(submission.getStudentId());
    if (!Files.exists(testOutput)) {
      return false;
    }

    TestOutputDetails testOutputDetails = this.testOutputDetailsProvider.getTestOutputDetails(testOutput);
    if (submission.getSubmissionTime().isAfter(testOutputDetails.getGradedTime())) {
      return false;
    }

    throw new UnsupportedOperationException("Grading logic not implemented yet");
  }

  @VisibleForTesting
  static class SubmissionDetails {

    private final String studentId;
    private final ZonedDateTime submissionTime;

    public SubmissionDetails(String studentId, ZonedDateTime submissionTime) {
      this.studentId = studentId;
      this.submissionTime = submissionTime;
    }

    public String getStudentId() {
      return this.studentId;
    }

    public ZonedDateTime getSubmissionTime() {
      return this.submissionTime;
    }
  }

  interface SubmissionDetailsProvider {
    SubmissionDetails getSubmissionDetails(Path submission);
  }

  interface TestOutputPathProvider {
    Path getTestOutput(String studentId);
  }

  interface TestOutputDetailsProvider {
    TestOutputDetails getTestOutputDetails(Path testOutput);
  }

  @VisibleForTesting
  static
  class TestOutputDetails {
    private final ZonedDateTime gradedTime;

    public TestOutputDetails(ZonedDateTime gradedTime) {
      this.gradedTime = gradedTime;
    }

    public ZonedDateTime getGradedTime() {
      return this.gradedTime;
    }
  }
}
