package edu.pdx.cs.joy.grader;

import com.google.common.annotations.VisibleForTesting;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.ZonedDateTime;
import java.util.stream.Stream;

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

  public static void main(String[] args) {
    Stream<Path> submissions = findSubmissionsIn(args);
    submissions.forEach(System.out::println);
  }

  private static Stream<Path> findSubmissionsIn(String... fileNames) {
    return Stream.of(fileNames)
      .map(Path::of)
      .filter(Files::exists)
      .flatMap(path -> {
        if (Files.isDirectory(path)) {
          try (Stream<Path> walk = Files.walk(path)) {
            return walk.filter(FindUngradedSubmissions::isZipFile);

          } catch (IOException e) {
            throw new RuntimeException(e);
          }
        } else if (isZipFile(path)) {
          return Stream.of(path);
        }
        return Stream.empty();
      });
  }

  private static boolean isZipFile(Path p) {
    return Files.isRegularFile(p) && p.getFileName().toString().endsWith(".zip");
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
