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


  @VisibleForTesting
  SubmissionAnalysis analyzeSubmission(Path submissionPath) {
    SubmissionDetails submission = this.submissionDetailsProvider.getSubmissionDetails(submissionPath);
    Path testOutputPath = this.testOutputProvider.getTestOutput(submission.studentId());
    boolean needsToBeTested;
    boolean needsToBeGraded;

    if (!Files.exists(testOutputPath)) {
      needsToBeTested = true;
      needsToBeGraded = true;

    } else {

      TestOutputDetails testOutput = this.testOutputDetailsProvider.getTestOutputDetails(testOutputPath);
      if (submission.submissionTime().isAfter(testOutput.testedTime())) {
        needsToBeTested = true;
        needsToBeGraded = true;

      } else if (!testOutput.hasGrade()) {
        needsToBeTested = false;
        needsToBeGraded = true;

      } else {
        needsToBeTested = false;
        needsToBeGraded = false;
      }
    }

    return new SubmissionAnalysis(needsToBeTested, needsToBeGraded);
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

  @VisibleForTesting
  record SubmissionAnalysis (boolean needsToBeTested, boolean needsToBeGraded) {

  }
}
