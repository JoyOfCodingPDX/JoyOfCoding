package edu.pdx.cs.joy.grader;

import com.google.common.annotations.VisibleForTesting;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.Attributes;
import java.util.jar.Manifest;
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

  public FindUngradedSubmissions() {
    this(new SubmissionDetailsProviderFromZipFile(), new TestOutputProviderInParentDirectory(), new TestOutputDetailsProviderFromTestOutputFile());
  }


  @VisibleForTesting
  SubmissionAnalysis analyzeSubmission(Path submissionPath) {
    SubmissionDetails submission = this.submissionDetailsProvider.getSubmissionDetails(submissionPath);
    Path submissionDirectory = submissionPath.getParent();
    Path testOutputPath = this.testOutputProvider.getTestOutput(submissionDirectory, submission.studentId());
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
  record SubmissionDetails(String studentId, LocalDateTime submissionTime) {

  }

  public static void main(String[] args) {
    Stream<Path> submissions = findSubmissionsIn(args);
    FindUngradedSubmissions finder = new FindUngradedSubmissions();
    Stream<SubmissionAnalysis> analyses = submissions.map(finder::analyzeSubmission);
    List<SubmissionAnalysis> needsToBeTested = new ArrayList<>();
    List<SubmissionAnalysis> needsToBeGraded = new ArrayList<>();
    analyses.forEach(analysis -> {
      if (analysis.needsToBeTested()) {
        needsToBeTested.add(analysis);

      } else if (analysis.needsToBeGraded()) {
        needsToBeGraded.add(analysis);
      }
    });

    System.out.println(needsToBeTested.size() + " submissions need to be tested: ");
    needsToBeTested.forEach(System.out::println);

    System.out.println(needsToBeGraded.size() + " submissions need to be graded: ");
    needsToBeGraded.forEach(System.out::println);
  }

  private static Stream<Path> findSubmissionsIn(String... fileNames) {
    return Stream.of(fileNames)
        .map(Path::of)
        .filter(Files::exists)
        .flatMap(FindUngradedSubmissions::findSubmissionsIn);
  }

  private static Stream<? extends Path> findSubmissionsIn(Path path) {
    if (Files.isDirectory(path)) {
      try {
        // If we put the walk into a try-with-resources, the consumer of the stream will encounter and
        // exception, because the stream will be closed immediately.
        Stream<Path> walk = Files.walk(path, FileVisitOption.FOLLOW_LINKS);
        return walk.filter(FindUngradedSubmissions::isZipFile);

      } catch (IOException e) {
        throw new RuntimeException("Error while walking through directory: " + path, e);
      }
    } else if (isZipFile(path)) {
      return Stream.of(path);
    } else {
      return Stream.empty();
    }
  }

  private static boolean isZipFile(Path p) {
    return Files.isRegularFile(p) && p.getFileName().toString().endsWith(".zip");
  }

  interface SubmissionDetailsProvider {
    SubmissionDetails getSubmissionDetails(Path submission);
  }

  interface TestOutputPathProvider {
    Path getTestOutput(Path submissionDirectory, String studentId);
  }

  interface TestOutputDetailsProvider {
    TestOutputDetails getTestOutputDetails(Path testOutput);
  }

  @VisibleForTesting
    record TestOutputDetails(LocalDateTime testedTime, boolean hasGrade) {
  }

  @VisibleForTesting
  record SubmissionAnalysis (boolean needsToBeTested, boolean needsToBeGraded) {

  }

  private static class SubmissionDetailsProviderFromZipFile implements SubmissionDetailsProvider {
    @Override
    public SubmissionDetails getSubmissionDetails(Path submission) {
      try (InputStream zipFile = Files.newInputStream(submission)) {
        Manifest manifest = ProjectSubmissionsProcessor.getManifestFromZipFile(zipFile);
        return getSubmissionDetails(manifest);

      } catch (IOException | StudentEmailAttachmentProcessor.SubmissionException e) {
        throw new RuntimeException(e);
      }
    }

    private SubmissionDetails getSubmissionDetails(Manifest manifest) throws StudentEmailAttachmentProcessor.SubmissionException {
      Attributes attrs = manifest.getMainAttributes();
      String studentId = ProjectSubmissionsProcessor.getStudentIdFromManifestAttributes(attrs);
      LocalDateTime submissionTime = ProjectSubmissionsProcessor.getSubmissionTime(attrs);
      return new SubmissionDetails(studentId, submissionTime);
    }
  }

  private static class TestOutputProviderInParentDirectory implements TestOutputPathProvider {
    @Override
    public Path getTestOutput(Path submissionDirectory, String studentId) {
      throw new UnsupportedOperationException("This method is not implemented yet");
    }
  }

  private static class TestOutputDetailsProviderFromTestOutputFile implements TestOutputDetailsProvider {
    @Override
    public TestOutputDetails getTestOutputDetails(Path testOutput) {
      throw new UnsupportedOperationException("This method is not implemented yet");
    }
  }
}
