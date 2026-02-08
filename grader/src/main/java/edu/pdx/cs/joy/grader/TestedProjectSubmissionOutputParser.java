package edu.pdx.cs.joy.grader;

import com.google.common.annotations.VisibleForTesting;
import org.jspecify.annotations.NonNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class TestedProjectSubmissionOutputParser {
  static final Pattern scorePattern = Pattern.compile("(\\d+\\.?\\d*)? out of (\\d+\\.?\\d*)", Pattern.CASE_INSENSITIVE);
  static final Pattern projectNamePattern = Pattern.compile(".*The Joy of Coding Project \\d+: edu\\.pdx\\.[\\w.]*\\.(Project\\d+)", Pattern.CASE_INSENSITIVE);
  private static final Pattern submissionTimePattern = Pattern.compile(".*Submitted on (.+)", Pattern.CASE_INSENSITIVE);



  static @NonNull ProjectScore parseTestedSubmissionOutput(Reader reader) throws TestedProjectSubmissionOutputParsingException, IOException {
    try (BufferedReader br = new BufferedReader(reader)) {
      return parseTestedSubmissionOutput(br.lines());
    }
  }

  static @NonNull ProjectScore parseTestedSubmissionOutput(Stream<String> lines) throws TestedProjectSubmissionOutputParsingException {
    String score = null;
    int scoreLineNumber = 0;
    String totalPoints = null;
    String projectName = null;
    LocalDateTime submissionTime = null;

    int lineNumber = 0;

    Iterator<String> iterator = lines.iterator();
    while (iterator.hasNext()) {
      String line = iterator.next();
      lineNumber++;
      if (score == null && totalPoints == null) {
        Matcher matcher = scorePattern.matcher(line);
        if (matcher.find()) {
          score = matcher.group(1);
          totalPoints = matcher.group(2);
          scoreLineNumber = lineNumber;
        }
      }

      if (projectName == null) {
        Matcher matcher = projectNamePattern.matcher(line);
        if (matcher.find()) {
          projectName = matcher.group(1);
        }
      }

      if (submissionTime == null && line.contains("Submitted on")) {
        Matcher matcher = submissionTimePattern.matcher(line);
        if (matcher.matches()) {
          String timeString = matcher.group(1).trim();
          submissionTime = parseTime(timeString);

        } else {
          throw new IllegalArgumentException("Could not parse submission time from line: " + line);
        }
      }
    }

    if (totalPoints == null) {
      throw new TestedProjectSubmissionOutputParsingException("Could not find score line in project report");
    }

    if (projectName == null) {
      throw new TestedProjectSubmissionOutputParsingException("Could not find project name in project report");
    }

    if (submissionTime == null) {
      throw new TestedProjectSubmissionOutputParsingException("Could not find submission time in project report");
    }

    return new ProjectScore(score, scoreLineNumber, totalPoints, projectName, submissionTime);
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

  static class ProjectScore {
    static final int UNREVIEWED_SCORE_LINE_NUMBER = 7;
    private final double score;
    private final double totalPoints;
    private final String projectName;
    private final boolean reviewed;
    private final LocalDateTime submissionTime;

    ProjectScore(String score, int scoreLineNumber, String totalPoints, String projectName, LocalDateTime submissionTime) {
      this.score = score == null ? Double.NaN : Double.parseDouble(score);
      this.totalPoints = Double.parseDouble(totalPoints);
      this.projectName = projectName;
      this.reviewed = !Double.isNaN(this.score) || scoreLineNumber > UNREVIEWED_SCORE_LINE_NUMBER;
      this.submissionTime = submissionTime;
    }

    public double getScore() {
      return this.score;
    }

    public double getTotalPoints() {
      return this.totalPoints;
    }

    public String getProjectName() {
      return projectName;
    }

    /**
     * Determines if a submission has been reviewed by a grader.
     * A submission is considered reviewed if:
     * 1. It has a numeric score, OR
     * 2. The score line appears after line 7 (indicating grader comments are present)
     * <p>
     * When a score line like " out of X.X" appears after line 7, it indicates
     * the grader has left feedback for the student to fix issues and resubmit.
     */
    public boolean isReviewed() {
      return reviewed;
    }

    public LocalDateTime getSubmissionTime() {
      return submissionTime;
    }
  }

  @VisibleForTesting
  static class TestedProjectSubmissionOutputParsingException extends Exception {

    public TestedProjectSubmissionOutputParsingException(String message) {
      super(message);
    }
  }
}
