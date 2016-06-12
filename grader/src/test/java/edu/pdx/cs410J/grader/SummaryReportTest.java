package edu.pdx.cs410J.grader;

import com.google.common.io.CharStreams;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.*;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Arrays;
import java.util.stream.Collectors;

import static edu.pdx.cs410J.grader.Student.Section.GRADUATE;
import static edu.pdx.cs410J.grader.Student.Section.UNDERGRADUATE;
import static edu.pdx.cs410J.grader.SummaryReport.GRADUATE_DIRECTORY_NAME;
import static edu.pdx.cs410J.grader.SummaryReport.UNDERGRADUATE_DIRECTORY_NAME;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

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

  @Test
  public void totalsArePrintedForAllStudents() {
    GradeBook gradeBook = new GradeBook("test");
    Assignment assignment = new Assignment("assignment", 4.0);
    gradeBook.addAssignment(assignment);

    String worseUndergradStudentName = "worseUndergrad";
    addStudentInSectionWithScore(gradeBook, assignment, worseUndergradStudentName, UNDERGRADUATE, 3.0);

    String betterUndergradStudentName = "betterUndergrad";
    addStudentInSectionWithScore(gradeBook, assignment, betterUndergradStudentName, UNDERGRADUATE, 3.5);

    String worseGradStudentName = "worseGrad";
    addStudentInSectionWithScore(gradeBook, assignment, worseGradStudentName, GRADUATE, 3.0);

    String betterGradStudentName = "betterGrad";
    addStudentInSectionWithScore(gradeBook, assignment, betterGradStudentName, GRADUATE, 3.5);

    calculateTotalGradesForStudents(gradeBook, false);

    CapturingPrintWriter out = new CapturingPrintWriter();
    SummaryReport.printOutStudentTotals(gradeBook.studentsStream().collect(Collectors.toSet()), out);

    String written = out.getTextWrittenToWriter();
    String[] strings = { betterUndergradStudentName, worseUndergradStudentName, betterGradStudentName, worseGradStudentName };
    assertThat(written, stringContainsInOrder(Arrays.asList(strings)));
  }

  private void calculateTotalGradesForStudents(GradeBook gradeBook, boolean assignLetterGrades) {
    PrintWriter pw = new PrintWriter(new Writer() {
      @Override
      public void write(char[] cbuf, int off, int len) throws IOException {

      }

      @Override
      public void flush() throws IOException {

      }

      @Override
      public void close() throws IOException {

      }
    });

    gradeBook.studentsStream().forEach(student -> {
      SummaryReport.dumpReportTo(gradeBook, student, pw, assignLetterGrades);
    });
  }

  private class CapturingPrintWriter extends PrintWriter {
    public CapturingPrintWriter() {
      super(new StringWriter());
    }

    public String getTextWrittenToWriter() {
      return ((StringWriter) this.out).getBuffer().toString();
    }
  }

  @Test
  public void assignLetterGradesAccordingToGradeBookRangesForSection() {
    GradeBook gradeBook = new GradeBook("test");
    Assignment assignment = new Assignment("assignment", 10.0);
    gradeBook.addAssignment(assignment);

    GradeBook.LetterGradeRanges undergradLetterGradeRanges = gradeBook.getLetterGradeRanges(Student.Section.UNDERGRADUATE);
    undergradLetterGradeRanges.getRange(LetterGrade.A).setRange(95, 100);
    undergradLetterGradeRanges.getRange(LetterGrade.A_MINUS).setRange(90, 94);
    undergradLetterGradeRanges.validate();

    GradeBook.LetterGradeRanges gradLetterGradeRanges = gradeBook.getLetterGradeRanges(Student.Section.GRADUATE);
    gradLetterGradeRanges.getRange(LetterGrade.A).setRange(92, 100);
    gradLetterGradeRanges.getRange(LetterGrade.A_MINUS).setRange(90, 91);
    gradLetterGradeRanges.validate();

    String betterUndergradStudentName = "betterUndergrad";
    Student betterUndergrad = addStudentInSectionWithScore(gradeBook, assignment, betterUndergradStudentName, UNDERGRADUATE, 9.7);
    assertThat(betterUndergrad.getLetterGrade(), nullValue());

    String worseUndergradStudentName = "worseUndergrad";
    Student worseUndergrad = addStudentInSectionWithScore(gradeBook, assignment, worseUndergradStudentName, UNDERGRADUATE, 9.2);
    assertThat(worseUndergrad.getLetterGrade(), nullValue());

    String gradStudentName = "grad";
    Student grad = addStudentInSectionWithScore(gradeBook, assignment, gradStudentName, GRADUATE, 9.2);
    assertThat(grad.getLetterGrade(), nullValue());

    calculateTotalGradesForStudents(gradeBook, true);

    assertThat(betterUndergrad.getLetterGrade(), equalTo(LetterGrade.A));
    assertThat(worseUndergrad.getLetterGrade(), equalTo(LetterGrade.A_MINUS));
    assertThat(grad.getLetterGrade(), equalTo(LetterGrade.A));
  }

}
