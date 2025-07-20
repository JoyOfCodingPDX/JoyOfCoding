package edu.pdx.cs.joy.grader;

import com.google.common.annotations.VisibleForTesting;

import java.nio.file.Files;
import java.nio.file.Path;

public class FindUngradedSubmissions {
  private final SubmissionDetailsProvider submissionDetailsProvider;
  private final TestOutputProvider testOutputProvider;

  @VisibleForTesting
  FindUngradedSubmissions(SubmissionDetailsProvider submissionDetailsProvider, TestOutputProvider testOutputProvider) {
    this.submissionDetailsProvider = submissionDetailsProvider;
    this.testOutputProvider = testOutputProvider;
  }


  public boolean isGraded(Path submission) {
    SubmissionDetails details = this.submissionDetailsProvider.getSubmissionDetails(submission);
    Path testOutput = this.testOutputProvider.getTestOutput(details.getStudentId());
    if (!Files.exists(testOutput)) {
      return false;
    }
    throw new UnsupportedOperationException("Not implemented yet: isGraded(Path submission)");
  }

  @VisibleForTesting
  static class SubmissionDetails {

    private final String studentId;

    public SubmissionDetails(String studentId) {
      this.studentId = studentId;
    }

    public String getStudentId() {
      return this.studentId;
    }
  }

  interface SubmissionDetailsProvider {
    SubmissionDetails getSubmissionDetails(Path submission);
  }

  interface TestOutputProvider {
    Path getTestOutput(String studentId);
  }
}
