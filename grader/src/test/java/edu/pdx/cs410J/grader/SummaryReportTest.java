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
import java.util.Collections;

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
        Files.delete(file);
        return FileVisitResult.CONTINUE;
      }
    });
    Files.delete(tempDirectory.toPath());

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
  public void reportsAreGenerated() throws IOException {
    GradeBook gradeBook = new GradeBook("test");
    Student student = new Student("student");
    gradeBook.addStudent(student);

    Assignment assignment = new Assignment("assignment", 4.0);
    gradeBook.addAssignment(assignment);

    student.setGrade(assignment, 3.0);

    SummaryReport.dumpReports(Collections.singletonList(student.getId()), gradeBook, tempDirectory, false);

    File file = new File(tempDirectory, SummaryReport.getReportFileName(student.getId()));
    assertThat(file.exists(), equalTo(true));

    String reportContents = readTextFromFile(file);
    assertThat(reportContents, containsString("3.0"));
  }

  private String readTextFromFile(File file) throws IOException {
    StringWriter writer = new StringWriter();
    CharStreams.copy(new FileReader(file), writer);
    return writer.getBuffer().toString();
  }
}
