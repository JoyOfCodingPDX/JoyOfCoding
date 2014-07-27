package edu.pdx.cs410J.grader;

import edu.pdx.cs410J.ParserException;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
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

    if (score.getTotalPoints() != this.assignment.getPoints()) {
      String message = "Assignment " + this.assignment.getName() + " should be worth " + this.assignment.getPoints() +
        " points, but the report for " + studentId + " was out of " + score.getTotalPoints();
      throw new IllegalStateException(message);
    }

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

  public static void main(String[] args) {
    String gradeBookFileName = null;
    String assignmentName = null;
    List<String> projectFileNames = new ArrayList<>();

    for (String arg : args) {
      if (gradeBookFileName == null) {
        gradeBookFileName = arg;

      } else if (assignmentName == null) {
        assignmentName = arg;

      } else {
        projectFileNames.add(arg);
      }
    }

    usageIfNull(gradeBookFileName, "Missing grade book file");
    usageIfNull(assignmentName, "Missing assignment name");
    usageIfEmpty(projectFileNames, "No project file names provided");

    GradeBook gradeBook = getGradeBook(gradeBookFileName);
    Assignment assignment = getAssignment(assignmentName, gradeBook);
    ProjectGradesImporter importer = new ProjectGradesImporter(gradeBook, assignment);

    for (String projectFileName : projectFileNames) {
      File projectFile = getProjectFile(projectFileName);
      String studentId = getStudentIdFromFileName(projectFile);
      try {
        importer.recordScoreFromProjectReport(studentId, new FileReader(projectFile));
      } catch (FileNotFoundException e) {
        throw new IllegalStateException("Could not find file \"" + projectFile + "\"");
      }
    }

    saveGradeBookIfModified(gradeBook, gradeBookFileName);
  }

  private static void saveGradeBookIfModified(GradeBook gradeBook, String gradeBookFileName) {
    if (gradeBook.isDirty()) {
      File file = new File(gradeBookFileName);
      try {
        XmlDumper dumper = new XmlDumper(file);
        dumper.dump(gradeBook);

      } catch (IOException e) {
        usage("Can't write grade book in \"" + gradeBookFileName + "\"");
      }
    }
  }

  private static String getStudentIdFromFileName(File projectFile) {
    String fileName = projectFile.getName();
    int index = fileName.lastIndexOf('.');
    if (index < 0) {
      return usage("Project file \"" + fileName + "\" does not have a file extension");
    }

    return fileName.substring(0, index);
  }

  private static File getProjectFile(String projectFileName) {
    File projectFile = new File(projectFileName);
    if (!projectFile.exists()) {
      return usage("Project file \"" + projectFileName + "\" does not exist");

    } else if (!projectFile.isFile()) {
      return usage("Project file \"" + projectFileName + "\" is not a file");
    }

    return projectFile;
  }

  private static Assignment getAssignment(String assignmentName, GradeBook gradeBook) {
    Assignment assignment = gradeBook.getAssignment(assignmentName);
    if (assignment == null) {
      return usage("Could not find assignment \"" + assignmentName + "\" in grade book");
    }
    return assignment;
  }

  private static GradeBook getGradeBook(String gradeBookFileName) {
    try {
      XmlGradeBookParser parser = new XmlGradeBookParser(gradeBookFileName);
      return parser.parse();

    } catch (IOException ex) {
      return usage("Couldn't read grade book file \"" + gradeBookFileName + "\"");

    } catch (ParserException ex) {
      return usage("Couldn't parse grade book file \"" + gradeBookFileName + "\"");
    }
  }

  private static void usageIfEmpty(List<String> list, String message) {
    if (list.isEmpty()) {
      usage(message);
    }
  }

  private static void usageIfNull(String argument, String message) {
    if (argument == null) {
      usage(message);
    }
  }

  private static <T> T usage(String message) {
    PrintStream err = System.err;

    err.println("** " + message);

    err.println("java ProjectGradesImporter gradeBookFileName assignmentName projectFileName+");
    err.println();
    err.println("Imports grades (of the form \"5.8 out of 6.0\") from project reports into a grade book.");
    err.println("The name of the project report file begins with the student's id.");
    err.println();
    err.println("  gradeBookFileName     Name of file containing grade book");
    err.println("  assignmentName        Assignment/project whose score is to be recorded");
    err.println("  projectFileName       Name of file containing graded project");
    err.println();

    System.exit(1);

    return null;
  }
}
