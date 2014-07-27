package edu.pdx.cs410J.grader;

import java.io.BufferedReader;
import java.io.Reader;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ProjectGradesImporter {
  static final Pattern scorePattern = Pattern.compile("(\\d+\\.?\\d*) out of (\\d+\\.?\\d*)", Pattern.CASE_INSENSITIVE);

  private final GradeBook gradeBook;
  private final Assignment assignment;

  public ProjectGradesImporter(GradeBook gradeBook, Assignment assignment) {
    this.gradeBook = gradeBook;
    this.assignment = assignment;
  }

  public static ProjectScore getScoreFrom(Reader reader) {
    BufferedReader br = new BufferedReader(reader);
    Optional<String> scoreLine = br.lines().filter(scorePattern.asPredicate()).findFirst();

    if (scoreLine.isPresent()) {
      Matcher matcher = scorePattern.matcher(scoreLine.get());
      if (matcher.matches()) {
        return new ProjectScore(matcher.group(1), matcher.group(2));

      } else {
        throw new IllegalStateException("Matcher didn't match after all??");
      }

    } else {
      throw new IllegalStateException("No score found");
    }

  }

  public void recordScoreFromProjectReport(String studentId, Reader report) {
    ProjectScore score = getScoreFrom(report);
    Student student = gradeBook.getStudent(studentId);
    Grade grade = student.getGrade(this.assignment);
    if (grade == null) {
      grade = new Grade(assignment, score.getScore());
      student.setGrade(assignment.getName(), grade);

    } else {
      grade.setScore(score.getScore());
    }
  }

  static class ProjectScore {
    private final double score;
    private final double totalPoints;

    private ProjectScore(String score, String totalPoints) {
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
}
