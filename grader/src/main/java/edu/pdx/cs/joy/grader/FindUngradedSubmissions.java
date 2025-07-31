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
    Path testOutput = this.testOutputProvider.getTestOutput(submission.studentId());
    if (!Files.exists(testOutput)) {
      return false;
    }

    TestOutputDetails testOutputDetails = this.testOutputDetailsProvider.getTestOutputDetails(testOutput);
    if (!testOutputDetails.hasGrade()) {
      return false;

    } else if (submission.submissionTime().isAfter(testOutputDetails.testedTime())) {
      return false;
    }

    throw new UnsupportedOperationException("Grading logic not implemented yet");
  }

  @VisibleForTesting
  record SubmissionDetails(String studentId, ZonedDateTime submissionTime) {

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
    record TestOutputDetails(ZonedDateTime testedTime, boolean hasGrade) {
  }
}
