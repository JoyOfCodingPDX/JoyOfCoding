package edu.pdx.cs.joy.grader;

import com.google.common.annotations.VisibleForTesting;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.jar.Attributes;
import java.util.jar.Manifest;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
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
    String reason;

    if (!Files.exists(testOutputPath)) {
      needsToBeTested = true;
      needsToBeGraded = true;
      reason = "Test output file does not exist: " + testOutputPath;

    } else {

      TestOutputDetails testOutput = this.testOutputDetailsProvider.getTestOutputDetails(testOutputPath);
      if (submittedAfterTesting(submission, testOutput)) {
        needsToBeTested = true;
        needsToBeGraded = true;
        reason = "Submission on " + submission.submissionTime() + " is after testing on " + testOutput.testedSubmissionTime();

      } else if (!testOutput.hasGrade()) {
        needsToBeTested = false;
        needsToBeGraded = true;
        reason = "Test output file does not have a grade: " + testOutputPath;

      } else {
        needsToBeTested = false;
        needsToBeGraded = false;
        reason = "Test output file was graded after submission: " + testOutputPath;
      }
    }

    return new SubmissionAnalysis(submissionPath, needsToBeTested, needsToBeGraded, reason);
  }

  private static boolean submittedAfterTesting(SubmissionDetails submission, TestOutputDetails testOutput) {
    LocalDateTime submissionTime = submission.submissionTime();
    LocalDateTime testedSubmissionTime = testOutput.testedSubmissionTime();
    return submissionTime.isAfter(testedSubmissionTime.plusMinutes(1L));
  }

  @VisibleForTesting
  record SubmissionDetails(String studentId, LocalDateTime submissionTime) {

  }

  public static void main(String[] args) {
    if (args.length == 0) {
      System.err.println("Usage: java FindUngradedSubmissions -includeReason submissionZipOrDirectory+");
      System.exit(1);
    }

    boolean includeReason = false;
    List<String> fileNames = new ArrayList<>();

    for (String arg : args) {
      if (arg.equals("-includeReason")) {
        includeReason = true;

      } else if (arg.startsWith("-")) {
        System.err.println("Unknown option: " + arg);
        System.exit(1);

      } else {
        fileNames.add(arg);
      }
    }

    Stream<Path> submissions = findSubmissionsIn(fileNames);
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

    printOutAnalyses(needsToBeTested, "tested", includeReason);
    printOutAnalyses(needsToBeGraded, "graded", includeReason);
  }

  private static void printOutAnalyses(List<SubmissionAnalysis> analyses, String action, boolean includeReason) {
    int size = analyses.size();
    String description = (size == 1 ? " submission needs" : " submissions need");
    System.out.println(size + description + " to be " + action + ": ");
    analyses.forEach(analysis -> {
      System.out.print("  " + analysis.submission);
      if (includeReason) {
        System.out.print("   " + analysis.reason);
      }
      System.out.println();
    });
  }

  private static Stream<Path> findSubmissionsIn(List<String> fileNames) {
    return fileNames.stream()
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
    record TestOutputDetails(LocalDateTime testedSubmissionTime, boolean hasGrade) {
  }

  @VisibleForTesting
  record SubmissionAnalysis (Path submission, boolean needsToBeTested, boolean needsToBeGraded, String reason) {

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
      return submissionDirectory.resolve(studentId + ".out");
    }
  }

  @VisibleForTesting
  static class TestOutputDetailsProviderFromTestOutputFile implements TestOutputDetailsProvider {
    private static final Pattern SUBMISSION_TIME_PATTERN = Pattern.compile(".*Submitted on (.+)");

    public static LocalDateTime parseSubmissionTime(String line) {
      if (line.contains("Submitted on")) {
        Matcher matcher = TestOutputDetailsProviderFromTestOutputFile.SUBMISSION_TIME_PATTERN.matcher(line);
        if (matcher.matches()) {
          String timeString = matcher.group(1).trim();
          return parseTime(timeString);
        } else {
          throw new IllegalArgumentException("Could not parse submission time from line: " + line);
        }
      }

      return null;
    }

    private static LocalDateTime parseTime(String timeString) {
      try {
        ZonedDateTime zoned;
        try {
          DateTimeFormatter formatter = DateTimeFormatter.ofPattern("E MMM d hh:mm:ss a z yyyy");
          zoned = ZonedDateTime.parse(timeString, formatter);

        } catch (DateTimeParseException ex) {
          // Single-digit day format
          DateTimeFormatter formatter = DateTimeFormatter.ofPattern("E MMM  d hh:mm:ss a z yyyy");
          zoned = ZonedDateTime.parse(timeString, formatter);
        }
        return zoned.toLocalDateTime();

      } catch (DateTimeParseException ex) {
        return LocalDateTime.parse(timeString);
      }
    }

    public static Double parseGrade(String line) {
      if (line.contains("out of")) {
        String[] parts = line.split("out of");
        if (parts.length == 2) {
          try {
            return Double.parseDouble(parts[0].trim());
          } catch (NumberFormatException e) {
            return Double.NaN;
          }
        }
      }
      return null;
    }

    @Override
    public TestOutputDetails getTestOutputDetails(Path testOutput) {
      try {
        return parseTestOutputDetails(Files.lines(testOutput));
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }

    static TestOutputDetails parseTestOutputDetails(Stream<String> lines) {
      TestOutputDetailsCreator creator = new TestOutputDetailsCreator();
      lines.forEach(creator);
      return creator.createTestOutputDetails();
    }

    private static class TestOutputDetailsCreator implements Consumer<String> {
      private LocalDateTime testedSubmissionTime;
      private Boolean hasGrade;
      private int lineCount;

      @Override
      public void accept(String line) {
        this.lineCount++;

        LocalDateTime submissionTime = parseSubmissionTime(line);
        if (submissionTime != null) {
          this.testedSubmissionTime = submissionTime;
        }

        Double grade = parseGrade(line);
        if (grade != null) {
          boolean testOutputHasNotesForStudent = lineCount > 7;
          this.hasGrade = testOutputHasNotesForStudent || !grade.isNaN();
        }
      }

      public TestOutputDetails createTestOutputDetails() {
        if (this.testedSubmissionTime == null) {
          throw new IllegalStateException("Tested submission time was not set");

        } else if( this.hasGrade == null) {
          throw new IllegalStateException("Has grade was not set");
        }

        return new TestOutputDetails(this.testedSubmissionTime, hasGrade);
      }
    }
  }
}
