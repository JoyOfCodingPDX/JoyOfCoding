package edu.pdx.cs.joy.grader;

import com.google.common.annotations.VisibleForTesting;
import org.jspecify.annotations.NonNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TestedProjectSubmissionOutputParser {
  static final Pattern scorePattern = Pattern.compile("(\\d+\\.?\\d*) out of (\\d+\\.?\\d*)", Pattern.CASE_INSENSITIVE);
  static final Pattern projectNamePattern = Pattern.compile(".*The Joy of Coding Project \\d+: edu\\.pdx\\.[\\w.]*\\.(Project\\d+)", Pattern.CASE_INSENSITIVE);

  static @NonNull ProjectScore parseTestedSubmissionOutput(Reader reader) throws ScoreNotFoundException, IOException {
    try (BufferedReader br = new BufferedReader(reader)) {
      String score = null;
      String totalPoints = null;
      String projectName = null;

      for (String line = br.readLine(); line != null; line = br.readLine()) {
        if (score == null && totalPoints == null) {
          Matcher matcher = scorePattern.matcher(line);
          if (matcher.find()) {
            score = matcher.group(1);
            totalPoints = matcher.group(2);
          }
        }

        if (projectName == null) {
          Matcher matcher = projectNamePattern.matcher(line);
          if (matcher.find()) {
            projectName = matcher.group(1);
          }
        }
      }

      if (score == null || totalPoints == null) {
        throw new ScoreNotFoundException();
      }

      return new ProjectScore(score, totalPoints, projectName);
    }
  }

  static class ProjectScore {
    private final double score;
    private final double totalPoints;
    private final String projectName;

    ProjectScore(String score, String totalPoints, String projectName) {
      this.score = Double.parseDouble(score);
      this.totalPoints = Double.parseDouble(totalPoints);
      this.projectName = projectName;
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
  }

  @VisibleForTesting
  static class ScoreNotFoundException extends Exception {

  }
}
