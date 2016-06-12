package edu.pdx.cs410J.grader;

import com.google.common.io.CharStreams;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

import static edu.pdx.cs410J.grader.Student.Section.GRADUATE;
import static edu.pdx.cs410J.grader.Student.Section.UNDERGRADUATE;
import static edu.pdx.cs410J.grader.SummaryReport.GRADUATE_DIRECTORY_NAME;
import static edu.pdx.cs410J.grader.SummaryReport.UNDERGRADUATE_DIRECTORY_NAME;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;

public class SummaryReportTest {

  private File tempDirectory;

  @Before
  public void createTempDirectoryForTest() {
    String tempDirName = "SummaryReportTestTempDirectory-" + System.currentTimeMillis();
    tempDirectory = new File(System.getProperty("java.io.tmpdir"), tempDirName);
    System.out.println("Temp dir is " + tempDirectory.getAbsolutePath());

    boolean dirWasCreated = tempDirectory.mkdirs();
    assertThat(dirWasCreated, equalTo(true));
  }

  @After
  public void deleteTempDirectoryForTest() throws IOException {
    Files.walkFileTree(tempDirectory.toPath(), new SimpleFileVisitor<Path>() {
      @Override
      public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
        return deleteFile(file);
      }

      @Override
      public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
        return deleteFile(dir);
      }

      private FileVisitResult deleteFile(Path file) throws IOException {
        Files.delete(file);
        return FileVisitResult.CONTINUE;
      }
    });

    assertThat(tempDirectory.exists(), equalTo(false));
  }

  @Test
  public void ungradedAssignmentsDoNotCountTowardsGraded() {
    GradeBook gradeBook = new GradeBook("test");
    Student student = new Student("student");
    gradeBook.addStudent(student);
    Assignment assignment = new Assignment("assignment", 4.0);
    gradeBook.addAssignment(assignment);

    Grade grade = new Grade(assignment, Grade.NO_GRADE);
    student.setGrade(assignment.getName(), grade);

    assertThat(SummaryReport.noStudentHasGradeFor(assignment, gradeBook), equalTo(true));

  }

  @Test
  public void reportsAreGeneratedInCorrectDirectories() throws IOException {
    GradeBook gradeBook = new GradeBook("test");
    Assignment assignment = new Assignment("assignment", 4.0);
    gradeBook.addAssignment(assignment);

    double undergradScore = 3.0;
    Student undergrad = addStudentInSectionWithScore(gradeBook, assignment, "undergrad", UNDERGRADUATE, undergradScore);

    double gradScore = 3.5;
    Student grad = addStudentInSectionWithScore(gradeBook, assignment, "grad", GRADUATE, gradScore);

    SummaryReport.dumpReports(gradeBook.getStudentIds(), gradeBook, tempDirectory, false);

    assertThatStudentHasReportInDirectoryWithScore(undergrad, UNDERGRADUATE_DIRECTORY_NAME, undergradScore);
    assertThatStudentHasReportInDirectoryWithScore(grad, GRADUATE_DIRECTORY_NAME, gradScore);
  }

  private Student addStudentInSectionWithScore(GradeBook gradeBook, Assignment assignment, String studentId, Student.Section enrolledSection, double undergradScore) {
    Student undergrad = new Student(studentId);
    undergrad.setEnrolledSection(enrolledSection);
    gradeBook.addStudent(undergrad);
    undergrad.setGrade(assignment, undergradScore);
    return undergrad;
  }

  private void assertThatStudentHasReportInDirectoryWithScore(Student student, String dirFileName, double studentScore) throws IOException {
    File directory = new File(tempDirectory, dirFileName);
    assertThat(directory.exists(), equalTo(true));

    File reportFile = new File(directory, SummaryReport.getReportFileName(student.getId()));
    assertThat(reportFile.exists(), equalTo(true));

    String reportContents = readTextFromFile(reportFile);
    assertThat(reportContents, containsString(String.valueOf(studentScore)));
  }

  private String readTextFromFile(File file) throws IOException {
    StringWriter writer = new StringWriter();
    CharStreams.copy(new FileReader(file), writer);
    return writer.getBuffer().toString();
  }
}
