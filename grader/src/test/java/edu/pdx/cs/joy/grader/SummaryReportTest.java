package edu.pdx.cs.joy.grader;

import com.google.common.io.CharStreams;
import edu.pdx.cs.joy.grader.gradebook.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.*;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class SummaryReportTest {

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
  public void reportsAreGeneratedInCorrectDirectories(@TempDir File tempDirectory) throws IOException {
    GradeBook gradeBook = new GradeBook("test");
    Assignment assignment = new Assignment("assignment", 4.0);
    gradeBook.addAssignment(assignment);

    double undergradScore = 3.0;
    Student undergrad = addStudentInSectionWithScore(gradeBook, assignment, "undergrad", Student.Section.UNDERGRADUATE, undergradScore);

    double gradScore = 3.5;
    Student grad = addStudentInSectionWithScore(gradeBook, assignment, "grad", Student.Section.GRADUATE, gradScore);

    SummaryReport.dumpReports(gradeBook.getStudentIds(), gradeBook, tempDirectory, false);

    assertThatStudentHasReportInDirectoryWithScore(undergrad, tempDirectory, SummaryReport.UNDERGRADUATE_DIRECTORY_NAME, undergradScore);
    assertThatStudentHasReportInDirectoryWithScore(grad, tempDirectory, SummaryReport.GRADUATE_DIRECTORY_NAME, gradScore);
  }

  private Student addStudentInSectionWithScore(GradeBook gradeBook, Assignment assignment, String studentId, Student.Section enrolledSection, double undergradScore) {
    Student undergrad = new Student(studentId);
    undergrad.setEnrolledSection(enrolledSection);
    gradeBook.addStudent(undergrad);
    undergrad.setGrade(assignment, undergradScore);
    return undergrad;
  }

  private void assertThatStudentHasReportInDirectoryWithScore(Student student, File tempDirectory, String dirFileName, double studentScore) throws IOException {
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
    addStudentInSectionWithScore(gradeBook, assignment, worseUndergradStudentName, Student.Section.UNDERGRADUATE, 3.0);

    String betterUndergradStudentName = "betterUndergrad";
    addStudentInSectionWithScore(gradeBook, assignment, betterUndergradStudentName, Student.Section.UNDERGRADUATE, 3.5);

    String worseGradStudentName = "worseGrad";
    addStudentInSectionWithScore(gradeBook, assignment, worseGradStudentName, Student.Section.GRADUATE, 3.0);

    String betterGradStudentName = "betterGrad";
    addStudentInSectionWithScore(gradeBook, assignment, betterGradStudentName, Student.Section.GRADUATE, 3.5);

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
      public void write(char[] cbuf, int off, int len) {

      }

      @Override
      public void flush() {

      }

      @Override
      public void close() {

      }
    });

    gradeBook.studentsStream().forEach(student -> SummaryReport.dumpReportTo(gradeBook, student, pw, assignLetterGrades));
  }

  private static class CapturingPrintWriter extends PrintWriter {
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
    Student betterUndergrad = addStudentInSectionWithScore(gradeBook, assignment, betterUndergradStudentName, Student.Section.UNDERGRADUATE, 9.7);
    assertThat(betterUndergrad.getLetterGrade(), nullValue());

    String worseUndergradStudentName = "worseUndergrad";
    Student worseUndergrad = addStudentInSectionWithScore(gradeBook, assignment, worseUndergradStudentName, Student.Section.UNDERGRADUATE, 9.2);
    assertThat(worseUndergrad.getLetterGrade(), nullValue());

    String gradStudentName = "grad";
    Student grad = addStudentInSectionWithScore(gradeBook, assignment, gradStudentName, Student.Section.GRADUATE, 9.2);
    assertThat(grad.getLetterGrade(), nullValue());

    calculateTotalGradesForStudents(gradeBook, true);

    assertThat(betterUndergrad.getLetterGrade(), equalTo(LetterGrade.A));
    assertThat(worseUndergrad.getLetterGrade(), equalTo(LetterGrade.A_MINUS));
    assertThat(grad.getLetterGrade(), equalTo(LetterGrade.A));
  }

  @Test
  public void assignmentDueInThePastIsLate() {
    Assignment assignment = new Assignment("Test", 4.0);
    LocalDateTime dueDate = LocalDateTime.now().minusDays(3);
    assignment.setDueDate(dueDate);

    assertThat(SummaryReport.dueDateHasPassed(assignment), equalTo(true));
  }

  @Test
  public void assignmentDueInTheFutureIsNotLate() {
    Assignment assignment = new Assignment("Test", 4.0);
    LocalDateTime dueDate = LocalDateTime.now().plusDays(3);
    assignment.setDueDate(dueDate);

    assertThat(SummaryReport.dueDateHasPassed(assignment), equalTo(false));
  }

  @Test
  void assignmentWithAllZeroGradesDoesNotAppearInReport(@TempDir File tempDirectory) throws IOException {
    GradeBook gradeBook = new GradeBook("test");
    String someNonZeroGradesName = "someNonZeroGrades";
    String allZeroGradesName = "allZeroGrades";

    Assignment someNonZeroGrades = gradeBook.addAssignment(new Assignment(someNonZeroGradesName, 10.0));
    Assignment allZeroGrades = gradeBook.addAssignment(new Assignment(allZeroGradesName, 10.0));

    Student studentWithSomeNonZeroGrades =
      gradeBook.addStudent(new Student("studentWithSomeNonZeroGrades"))
        .setLastName("studentWithSomeNonZeroGrades")
        .setEnrolledSection(Student.Section.UNDERGRADUATE)
        .setGrade(someNonZeroGrades, 5.0)
        .setGrade(allZeroGrades, 0.0);

    Student studentWithAllZeroGrades =
      gradeBook.addStudent(new Student("studentWithAllZeroGrades"))
        .setLastName("studentWithAllZeroGrades")
        .setEnrolledSection(Student.Section.UNDERGRADUATE)
        .setGrade(someNonZeroGrades, 0.0)
        .setGrade(allZeroGrades, 0.0);

    assertThat(SummaryReport.noStudentHasGradeFor(allZeroGrades, gradeBook), equalTo(true));

    SummaryReport.dumpReports(gradeBook.getStudentIds(), gradeBook, tempDirectory, false);

    File studentWithSomeNonZeroGradesReportFile = getStudentReportFile(tempDirectory, studentWithSomeNonZeroGrades);
    assertThat(studentWithSomeNonZeroGradesReportFile.exists(), equalTo(true));

    String studentWithSomeNonZeroGradesReport = readTextFromFile(studentWithSomeNonZeroGradesReportFile);
    assertThat(studentWithSomeNonZeroGradesReport, containsString(someNonZeroGradesName));
    assertThat(studentWithSomeNonZeroGradesReport, not(containsString(allZeroGradesName)));

    File studentWithAllZeroGradesReportFile = getStudentReportFile(tempDirectory, studentWithAllZeroGrades);
    assertThat(studentWithAllZeroGradesReportFile.exists(), equalTo(true));

    String studentWithAllZeroGradesReport = readTextFromFile(studentWithAllZeroGradesReportFile);
    assertThat(studentWithAllZeroGradesReport, containsString(someNonZeroGradesName));
    assertThat(studentWithAllZeroGradesReport, not(containsString(allZeroGradesName)));

  }

  private static File getStudentReportFile(File directory, Student student) {
    String sectionDirName;
    switch (student.getEnrolledSection()) {
      case GRADUATE:
        sectionDirName = SummaryReport.GRADUATE_DIRECTORY_NAME;
        break;
      case UNDERGRADUATE:
        sectionDirName = SummaryReport.UNDERGRADUATE_DIRECTORY_NAME;
        break;
      default:
        throw new UnsupportedOperationException("Don't know how to handle section: " + student.getEnrolledSection());
    }

    File sectionDirectory = new File(directory, sectionDirName);
    return new File(sectionDirectory, SummaryReport.getReportFileName(student.getId()));
  }

  @Test
  void missingGradeIsClearInReport(@TempDir File tempDirectory) throws IOException {
    GradeBook gradeBook = new GradeBook("test");

    String assignmentName = "Assignment";
    Assignment assignment = gradeBook.addAssignment(new Assignment(assignmentName, 10.0));
    Assignment assignment2 = gradeBook.addAssignment(new Assignment("Assignment 2", 10.0));

    Student studentWithMissingGrade =
      gradeBook.addStudent(new Student("studentWithMissingGrade"))
        .setLastName("studentWithMissingGrade")
        .setEnrolledSection(Student.Section.UNDERGRADUATE)
        .setGrade(assignment, Grade.NO_GRADE)
        .setGrade(assignment2, 10.0);

    Student studentWithGrade =
      gradeBook.addStudent(new Student("studentWithGrade"))
        .setLastName("studentWithGrade")
        .setEnrolledSection(Student.Section.UNDERGRADUATE)
        .setGrade(assignment, 10.0)
        .setGrade(assignment2, 10.0);

    SummaryReport.dumpReports(gradeBook.getStudentIds(), gradeBook, tempDirectory, false);

    File studentWithMissingGradeReportFile = getStudentReportFile(tempDirectory, studentWithMissingGrade);
    assertThat(studentWithMissingGradeReportFile.exists(), equalTo(true));

    String studentWithSomeNonZeroGradesReport = readTextFromFile(studentWithMissingGradeReportFile);
    assertThat(studentWithSomeNonZeroGradesReport, not(containsString(String.valueOf(Grade.NO_GRADE))));
    assertThat(studentWithSomeNonZeroGradesReport, containsString("NOT GRADED"));
  }

}
