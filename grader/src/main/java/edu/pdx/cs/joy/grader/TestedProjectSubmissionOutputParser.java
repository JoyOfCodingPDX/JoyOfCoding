package edu.pdx.cs.joy.grader;

import com.google.common.annotations.VisibleForTesting;
import org.jspecify.annotations.NonNull;

import java.io.BufferedReader;
import java.io.Reader;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TestedProjectSubmissionOutputParser {
  static final Pattern scorePattern = Pattern.compile("(\\d+\\.?\\d*) out of (\\d+\\.?\\d*)", Pattern.CASE_INSENSITIVE);

  static @NonNull ProjectScore parseTestedSubmissionOutput(Reader reader) throws ScoreNotFoundException {
    BufferedReader br = new BufferedReader(reader);
    Optional<String> scoreLine = br.lines().filter(scorePattern.asPredicate()).findFirst();

    if (scoreLine.isPresent()) {
      Matcher matcher = scorePattern.matcher(scoreLine.get());
      if (matcher.find()) {
        return new ProjectScore(matcher.group(1), matcher.group(2));

      } else {
        throw new IllegalStateException("Matcher didn't match \"" + scoreLine.get() + "\"");
      }

    } else {
      throw new ScoreNotFoundException();
    }
  }

  static class ProjectScore {
    private final double score;
    private final double totalPoints;

    ProjectScore(String score, String totalPoints) {
      this.score = Double.parseDouble(score);
      this.totalPoints = Double.parseDouble(totalPoints);
    }

    public double getScore() {
      return this.score;
    }

    public double getTotalPoints() {
      return this.totalPoints;
    }
  }

  @VisibleForTesting
  static class ScoreNotFoundException extends Exception {

  }
}
