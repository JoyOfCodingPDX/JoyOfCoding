package edu.pdx.cs.joy.grader;

import edu.pdx.cs.joy.grader.FindUngradedSubmissions.TestOutputDetailsProviderFromTestOutputFile;
import edu.pdx.cs.joy.grader.gradebook.Grade;
import edu.pdx.cs.joy.grader.gradebook.GradeBook;
import edu.pdx.cs.joy.grader.gradebook.Student;
import org.junit.jupiter.api.Test;

import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.spi.FileSystemProvider;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.stream.Stream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class FindUngradedSubmissionsTest {

  private static Path getPathToNonExistingFile() {
    return getMockPath(false);
  }

  private Path getPathToExistingFile() {
    return getMockPath(true);
  }

  private static Path getMockPath(boolean exists) {
    Path testOutput = mock(Path.class);

    FileSystemProvider provider = mock(FileSystemProvider.class);
    when(provider.exists(testOutput)).thenReturn(exists);

    FileSystem fileSystem = mock(FileSystem.class);
    when(fileSystem.provider()).thenReturn(provider);
    when(testOutput.getFileSystem()).thenReturn(fileSystem);

    Path parent = mock(Path.class);
    when(parent.getFileSystem()).thenReturn(fileSystem);
    when(testOutput.getParent()).thenReturn(parent);
    when(provider.exists(parent)).thenReturn(true);

    assertThat(Files.exists(testOutput), equalTo(exists));
    return testOutput;
  }

  @Test
  void submissionWithNoTestOutputNeedsToBeTested() {
    FindUngradedSubmissions.SubmissionDetailsProvider submissionDetailsProvider = mock(FindUngradedSubmissions.SubmissionDetailsProvider.class);

    String studentId = "student123";
    FindUngradedSubmissions.SubmissionDetails submissionDetails = new FindUngradedSubmissions.SubmissionDetails(studentId, LocalDateTime.now());
    Path submission = getPathToExistingFile();
    when(submissionDetailsProvider.getSubmissionDetails(submission)).thenReturn(submissionDetails);

    Path testOutput = getPathToNonExistingFile();

    FindUngradedSubmissions.TestOutputPathProvider testOutputProvider = mock(FindUngradedSubmissions.TestOutputPathProvider.class);
    when(testOutputProvider.getTestOutput(any(Path.class), eq(studentId))).thenReturn(testOutput);

    FindUngradedSubmissions.GradeBookProvider gradeBookProvider = mock(FindUngradedSubmissions.GradeBookProvider.class);
    FindUngradedSubmissions finder = new FindUngradedSubmissions(submissionDetailsProvider, testOutputProvider, mock(FindUngradedSubmissions.TestOutputDetailsProvider.class), gradeBookProvider);
    FindUngradedSubmissions.SubmissionAnalysis analysis = finder.analyzeSubmission(submission);
    assertThat(analysis.needsToBeTested(), equalTo(true));
    assertThat(analysis.needsToBeGraded(), equalTo(true));
    assertThat(analysis.submission(), equalTo(submission));
  }

  @Test
  void submissionWithTestOutputOlderThanSubmissionNeedsToBeTested() {
    FindUngradedSubmissions.SubmissionDetailsProvider submissionDetailsProvider = mock(FindUngradedSubmissions.SubmissionDetailsProvider.class);

    String studentId = "student123";
    LocalDateTime submissionTime = LocalDateTime.now();
    FindUngradedSubmissions.SubmissionDetails submissionDetails = new FindUngradedSubmissions.SubmissionDetails(studentId, submissionTime);
    Path submission = getPathToExistingFile();
    when(submissionDetailsProvider.getSubmissionDetails(submission)).thenReturn(submissionDetails);

    Path testOutput = getPathToExistingFile();

    FindUngradedSubmissions.TestOutputPathProvider testOutputProvider = mock(FindUngradedSubmissions.TestOutputPathProvider.class);
    when(testOutputProvider.getTestOutput(any(Path.class), eq(studentId))).thenReturn(testOutput);

    FindUngradedSubmissions.TestOutputDetailsProvider testOutputDetailsProvider = mock(FindUngradedSubmissions.TestOutputDetailsProvider.class);
    LocalDateTime testedSubmissionTime = submissionTime.minusDays(1); // Old test output
    when(testOutputDetailsProvider.getTestOutputDetails(testOutput)).thenReturn(new FindUngradedSubmissions.TestOutputDetails(testOutput, testedSubmissionTime, true, null, null, true));

    FindUngradedSubmissions.GradeBookProvider gradeBookProvider = mock(FindUngradedSubmissions.GradeBookProvider.class);
    FindUngradedSubmissions finder = new FindUngradedSubmissions(submissionDetailsProvider, testOutputProvider, testOutputDetailsProvider, gradeBookProvider);
    FindUngradedSubmissions.SubmissionAnalysis analysis = finder.analyzeSubmission(submission);
    assertThat(analysis.needsToBeTested(), equalTo(true));
    assertThat(analysis.needsToBeGraded(), equalTo(true));
  }

  @Test
  void submissionWithTestOutputLessThanAMinuteOlderThanSubmissionDoesNotNeedToBeTested() {
    FindUngradedSubmissions.SubmissionDetailsProvider submissionDetailsProvider = mock(FindUngradedSubmissions.SubmissionDetailsProvider.class);

    String studentId = "student123";
    LocalDateTime submissionTime = LocalDateTime.now();
    FindUngradedSubmissions.SubmissionDetails submissionDetails = new FindUngradedSubmissions.SubmissionDetails(studentId, submissionTime);
    Path submission = getPathToExistingFile();
    when(submissionDetailsProvider.getSubmissionDetails(submission)).thenReturn(submissionDetails);

    Path testOutput = getPathToExistingFile();

    FindUngradedSubmissions.TestOutputPathProvider testOutputProvider = mock(FindUngradedSubmissions.TestOutputPathProvider.class);
    when(testOutputProvider.getTestOutput(any(Path.class), eq(studentId))).thenReturn(testOutput);

    FindUngradedSubmissions.TestOutputDetailsProvider testOutputDetailsProvider = mock(FindUngradedSubmissions.TestOutputDetailsProvider.class);
    LocalDateTime testedSubmissionTime = submissionTime.plusMinutes(30); // Still within 1 minute tolerance
    when(testOutputDetailsProvider.getTestOutputDetails(testOutput)).thenReturn(new FindUngradedSubmissions.TestOutputDetails(testOutput, testedSubmissionTime, true, null, null, true));

    FindUngradedSubmissions.GradeBookProvider gradeBookProvider = mock(FindUngradedSubmissions.GradeBookProvider.class);
    FindUngradedSubmissions finder = new FindUngradedSubmissions(submissionDetailsProvider, testOutputProvider, testOutputDetailsProvider, gradeBookProvider);
    FindUngradedSubmissions.SubmissionAnalysis analysis = finder.analyzeSubmission(submission);
    assertThat(analysis.needsToBeTested(), equalTo(false));
  }

  @Test
  void submissionWithNoGradeNeedsToBeGraded() {
    FindUngradedSubmissions.SubmissionDetailsProvider submissionDetailsProvider = mock(FindUngradedSubmissions.SubmissionDetailsProvider.class);

    String studentId = "student123";
    LocalDateTime submissionTime = LocalDateTime.now();
    FindUngradedSubmissions.SubmissionDetails submissionDetails = new FindUngradedSubmissions.SubmissionDetails(studentId, submissionTime);
    Path submission = getPathToExistingFile();
    when(submissionDetailsProvider.getSubmissionDetails(submission)).thenReturn(submissionDetails);

    Path testOutput = getPathToExistingFile();

    FindUngradedSubmissions.TestOutputPathProvider testOutputProvider = mock(FindUngradedSubmissions.TestOutputPathProvider.class);
    when(testOutputProvider.getTestOutput(any(Path.class), eq(studentId))).thenReturn(testOutput);

    FindUngradedSubmissions.TestOutputDetailsProvider testOutputDetailsProvider = mock(FindUngradedSubmissions.TestOutputDetailsProvider.class);
    LocalDateTime gradedTime = submissionTime.plusDays(1); // Simulate test output newer than submission
    boolean hasBeenReviewed = false;
    when(testOutputDetailsProvider.getTestOutputDetails(testOutput)).thenReturn(new FindUngradedSubmissions.TestOutputDetails(testOutput, gradedTime, false, null, null, hasBeenReviewed));

    FindUngradedSubmissions.GradeBookProvider gradeBookProvider = mock(FindUngradedSubmissions.GradeBookProvider.class);
    FindUngradedSubmissions finder = new FindUngradedSubmissions(submissionDetailsProvider, testOutputProvider, testOutputDetailsProvider, gradeBookProvider);
    FindUngradedSubmissions.SubmissionAnalysis analysis = finder.analyzeSubmission(submission);
    assertThat(analysis.needsToBeTested(), equalTo(false));
    assertThat(analysis.needsToBeGraded(), equalTo(true));
    assertThat(analysis.testOutput(), equalTo(testOutput));
  }

  @Test
  void submissionWithGradeIsGraded() {
    FindUngradedSubmissions.SubmissionDetailsProvider submissionDetailsProvider = mock(FindUngradedSubmissions.SubmissionDetailsProvider.class);

    String studentId = "student123";
    LocalDateTime submissionTime = LocalDateTime.now();
    FindUngradedSubmissions.SubmissionDetails submissionDetails = new FindUngradedSubmissions.SubmissionDetails(studentId, submissionTime);
    Path submission = getPathToExistingFile();
    when(submissionDetailsProvider.getSubmissionDetails(submission)).thenReturn(submissionDetails);

    Path testOutput = getPathToExistingFile();

    FindUngradedSubmissions.TestOutputPathProvider testOutputProvider = mock(FindUngradedSubmissions.TestOutputPathProvider.class);
    when(testOutputProvider.getTestOutput(any(Path.class), eq(studentId))).thenReturn(testOutput);

    FindUngradedSubmissions.TestOutputDetailsProvider testOutputDetailsProvider = mock(FindUngradedSubmissions.TestOutputDetailsProvider.class);
    LocalDateTime gradedTime = submissionTime.plusDays(1);
    when(testOutputDetailsProvider.getTestOutputDetails(testOutput)).thenReturn(new FindUngradedSubmissions.TestOutputDetails(testOutput, gradedTime, true, null, null, true));

    FindUngradedSubmissions.GradeBookProvider gradeBookProvider = mock(FindUngradedSubmissions.GradeBookProvider.class);
    FindUngradedSubmissions finder = new FindUngradedSubmissions(submissionDetailsProvider, testOutputProvider, testOutputDetailsProvider, gradeBookProvider);
    FindUngradedSubmissions.SubmissionAnalysis analysis = finder.analyzeSubmission(submission);
    assertThat(analysis.needsToBeTested(), equalTo(false));
    assertThat(analysis.needsToBeGraded(), equalTo(false));
  }

  @Test
  void parseProjectNameFromTestOutputLine() {
    String line = "              The Joy of Coding Project 1: edu.pdx.cs410J.studentId.Project1";
    String projectName = TestOutputDetailsProviderFromTestOutputFile.parseProjectName(line);
    assertThat(projectName, equalTo("Project1"));
  }

  @Test
  void parseProjectNameFromTestOutputLineWithDifferentProject() {
    String line = "              The Joy of Coding Project 3: edu.pdx.cs.joy.student.Project3";
    String projectName = TestOutputDetailsProviderFromTestOutputFile.parseProjectName(line);
    assertThat(projectName, equalTo("Project3"));
  }

  @Test
  void lineWithoutProjectNameReturnsNull() {
    String line = "Some random line without a project";
    String projectName = TestOutputDetailsProviderFromTestOutputFile.parseProjectName(line);
    assertThat(projectName, equalTo(null));
  }

  @Test
  void parseProject6NameFromTestOutputLine() {
    String line = "              The Joy of Coding Project 6: Android";
    String projectName = TestOutputDetailsProviderFromTestOutputFile.parseProjectName(line);
    assertThat(projectName, equalTo("Project6"));
  }

  @Test
  void lineWithGradeHasGrade() {
    String line = "12.5 out of 13.0";
    Double grade = TestOutputDetailsProviderFromTestOutputFile.parseGrade(line);
    assertThat(grade, equalTo(12.5));
  }

  @Test
  void lineWithNoGradeHasNoGrade() {
    String line = "No grade";
    Double grade = TestOutputDetailsProviderFromTestOutputFile.parseGrade(line);
    assertThat(grade, equalTo(null));
  }

  @Test
  void lineWithMissingGradeHasNaNGrade() {
    String line = " out of 13.0";
    Double nan = TestOutputDetailsProviderFromTestOutputFile.parseGrade(line);
    assertThat(nan, equalTo(Double.NaN));
  }

  @Test
  void parseTestOutputDetails() throws TestedProjectSubmissionOutputParser.TestedProjectSubmissionOutputParsingException {
    Stream<String> lines = Stream.of(
        "              The Joy of Coding Project 2: edu.pdx.cs410J.whitlock.Project2",
        "              Submitted on Wed Aug  6 01:13:59 PM PDT 2025",
        "",
        "12.5 out of 13.0"
    );
    Path testOutput = mock(Path.class);
    FindUngradedSubmissions.TestOutputDetails details = TestOutputDetailsProviderFromTestOutputFile.parseTestOutputDetails(testOutput, lines);
    LocalDateTime submissionTime = LocalDateTime.of(2025, 8, 6, 13, 13, 59);
    assertThat(details.testOutput(), equalTo(testOutput));
    assertThat(details.testedSubmissionTime(), equalTo(submissionTime));
    assertThat(details.hasGrade(), equalTo(true));
  }

  @Test
  void testOutputDetailsIncludesProjectNameAndGrade() throws TestedProjectSubmissionOutputParser.TestedProjectSubmissionOutputParsingException {
    Stream<String> lines = Stream.of(
        "              The Joy of Coding Project 2: edu.pdx.cs410J.whitlock.Project2",
        "              Submitted by Dave Whitlock",
        "              Submitted on Wed Aug  6 01:13:59 PM PDT 2025",
        "              Graded on    Wed Aug  6 02:30:00 PM PDT 2025",
        "",
        "12.5 out of 13.0"
    );
    Path testOutput = mock(Path.class);
    FindUngradedSubmissions.TestOutputDetails details = TestOutputDetailsProviderFromTestOutputFile.parseTestOutputDetails(testOutput, lines);
    LocalDateTime submissionTime = LocalDateTime.of(2025, 8, 6, 13, 13, 59);
    assertThat(details.testOutput(), equalTo(testOutput));
    assertThat(details.testedSubmissionTime(), equalTo(submissionTime));
    assertThat(details.hasGrade(), equalTo(true));
    assertThat(details.projectName(), equalTo("Project2"));
    assertThat(details.grade(), equalTo(12.5));
  }

  @Test
  void testOutputDetailsWithMessageToStudentDoesNotNeedToBeGraded() throws TestedProjectSubmissionOutputParser.TestedProjectSubmissionOutputParsingException {
    Stream<String> lines = Stream.of(
        "Hi, Student.  There were some problems with your submission",
        "",
        "I ran it through the testing script and there are a couple of things",
        "I'd like you to fix before I ask the Graders to score it",
        "",
        "              The Joy of Coding Project 3: edu.pdx.cs.joy.student.Project3",
        "              Submitted by Student Name",
        "              Submitted on Wed Jul 30 05:10:26 PM PDT 2025",
        "              Graded on    Wed Jul 30 05:41:04 PM PDT 2025",
        "",
        " out of 7.0"
    );
    Path testOutput = mock(Path.class);
    FindUngradedSubmissions.TestOutputDetails details = TestOutputDetailsProviderFromTestOutputFile.parseTestOutputDetails(testOutput, lines);
    assertThat(details.hasGrade(), equalTo(false));
    assertThat(details.hasBeenReviewed(), equalTo(true));
  }

  @Test
  void submissionAnalysisIncludesGradeNeedsToBeRecorded() {
    FindUngradedSubmissions.SubmissionDetailsProvider submissionDetailsProvider = mock(FindUngradedSubmissions.SubmissionDetailsProvider.class);

    String studentId = "student123";
    LocalDateTime submissionTime = LocalDateTime.now();
    FindUngradedSubmissions.SubmissionDetails submissionDetails = new FindUngradedSubmissions.SubmissionDetails(studentId, submissionTime);
    Path submission = getPathToExistingFile();
    when(submissionDetailsProvider.getSubmissionDetails(submission)).thenReturn(submissionDetails);

    Path testOutput = getPathToExistingFile();

    FindUngradedSubmissions.TestOutputPathProvider testOutputProvider = mock(FindUngradedSubmissions.TestOutputPathProvider.class);
    when(testOutputProvider.getTestOutput(any(Path.class), eq(studentId))).thenReturn(testOutput);

    FindUngradedSubmissions.TestOutputDetailsProvider testOutputDetailsProvider = mock(FindUngradedSubmissions.TestOutputDetailsProvider.class);
    LocalDateTime gradedTime = submissionTime.plusDays(1);
    when(testOutputDetailsProvider.getTestOutputDetails(testOutput)).thenReturn(new FindUngradedSubmissions.TestOutputDetails(testOutput, gradedTime, true, "Project1", 12.5, true));

    FindUngradedSubmissions.GradeBookProvider gradeBookProvider = mock(FindUngradedSubmissions.GradeBookProvider.class);
    FindUngradedSubmissions finder = new FindUngradedSubmissions(submissionDetailsProvider, testOutputProvider, testOutputDetailsProvider, gradeBookProvider);
    FindUngradedSubmissions.SubmissionAnalysis analysis = finder.analyzeSubmission(submission);

    assertThat(analysis.needsToBeTested(), equalTo(false));
    assertThat(analysis.needsToBeGraded(), equalTo(false));
    assertThat(analysis.gradeNeedsToBeRecorded(), equalTo(false));
  }

  @Test
  void submissionWithGradeMissingFromGradeBookNeedsToBeRecorded() {
    FindUngradedSubmissions.SubmissionDetailsProvider submissionDetailsProvider = mock(FindUngradedSubmissions.SubmissionDetailsProvider.class);

    String studentId = "student123";
    LocalDateTime submissionTime = LocalDateTime.now();
    FindUngradedSubmissions.SubmissionDetails submissionDetails = new FindUngradedSubmissions.SubmissionDetails(studentId, submissionTime);
    Path submission = getPathToExistingFile();
    when(submissionDetailsProvider.getSubmissionDetails(submission)).thenReturn(submissionDetails);

    Path testOutput = getPathToExistingFile();

    FindUngradedSubmissions.TestOutputPathProvider testOutputProvider = mock(FindUngradedSubmissions.TestOutputPathProvider.class);
    when(testOutputProvider.getTestOutput(any(Path.class), eq(studentId))).thenReturn(testOutput);

    FindUngradedSubmissions.TestOutputDetailsProvider testOutputDetailsProvider = mock(FindUngradedSubmissions.TestOutputDetailsProvider.class);
    LocalDateTime gradedTime = submissionTime.plusDays(1);
    when(testOutputDetailsProvider.getTestOutputDetails(testOutput)).thenReturn(new FindUngradedSubmissions.TestOutputDetails(testOutput, gradedTime, true, "Project1", 12.5, true));

    // Create a mock GradeBook with a student that doesn't have a grade for Project1
    GradeBook gradeBook = mock(GradeBook.class);
    Student student = mock(Student.class);
    when(gradeBook.getStudent(studentId)).thenReturn(Optional.of(student));
    when(student.getGrade("Project1")).thenReturn(null); // No grade recorded

    FindUngradedSubmissions.GradeBookProvider gradeBookProvider = mock(FindUngradedSubmissions.GradeBookProvider.class);
    when(gradeBookProvider.getGradeBook()).thenReturn(Optional.of(gradeBook));

    FindUngradedSubmissions finder = new FindUngradedSubmissions(submissionDetailsProvider, testOutputProvider, testOutputDetailsProvider, gradeBookProvider);
    FindUngradedSubmissions.SubmissionAnalysis analysis = finder.analyzeSubmission(submission);

    assertThat(analysis.needsToBeTested(), equalTo(false));
    assertThat(analysis.needsToBeGraded(), equalTo(false));
    assertThat(analysis.gradeNeedsToBeRecorded(), equalTo(true));  // This should be true!
  }

  @Test
  void submissionWithDifferentGradeInGradeBookNeedsToBeRecorded() {
    FindUngradedSubmissions.SubmissionDetailsProvider submissionDetailsProvider = mock(FindUngradedSubmissions.SubmissionDetailsProvider.class);

    String studentId = "student456";
    LocalDateTime submissionTime = LocalDateTime.now();
    FindUngradedSubmissions.SubmissionDetails submissionDetails = new FindUngradedSubmissions.SubmissionDetails(studentId, submissionTime);
    Path submission = getPathToExistingFile();
    when(submissionDetailsProvider.getSubmissionDetails(submission)).thenReturn(submissionDetails);

    Path testOutput = getPathToExistingFile();

    FindUngradedSubmissions.TestOutputPathProvider testOutputProvider = mock(FindUngradedSubmissions.TestOutputPathProvider.class);
    when(testOutputProvider.getTestOutput(any(Path.class), eq(studentId))).thenReturn(testOutput);

    FindUngradedSubmissions.TestOutputDetailsProvider testOutputDetailsProvider = mock(FindUngradedSubmissions.TestOutputDetailsProvider.class);
    LocalDateTime gradedTime = submissionTime.plusDays(1);
    when(testOutputDetailsProvider.getTestOutputDetails(testOutput)).thenReturn(new FindUngradedSubmissions.TestOutputDetails(testOutput, gradedTime, true, "Project2", 12.5, true));

    // Create a mock GradeBook with a student that has a DIFFERENT grade for Project2
    GradeBook gradeBook = mock(GradeBook.class);
    Student student = mock(Student.class);
    Grade grade = mock(Grade.class);

    when(gradeBook.getStudent(studentId)).thenReturn(Optional.of(student));
    when(student.getGrade("Project2")).thenReturn(grade);
    when(grade.getScore()).thenReturn(10.0); // GradeBook has 10.0, test output has 12.5

    FindUngradedSubmissions.GradeBookProvider gradeBookProvider = mock(FindUngradedSubmissions.GradeBookProvider.class);
    when(gradeBookProvider.getGradeBook()).thenReturn(Optional.of(gradeBook));

    FindUngradedSubmissions finder = new FindUngradedSubmissions(submissionDetailsProvider, testOutputProvider, testOutputDetailsProvider, gradeBookProvider);
    FindUngradedSubmissions.SubmissionAnalysis analysis = finder.analyzeSubmission(submission);

    assertThat(analysis.needsToBeTested(), equalTo(false));
    assertThat(analysis.needsToBeGraded(), equalTo(false));
    assertThat(analysis.gradeNeedsToBeRecorded(), equalTo(true));  // Grades differ!
  }

  @Test
  void submissionWithMatchingGradeInGradeBookDoesNotNeedToBeRecorded() {
    FindUngradedSubmissions.SubmissionDetailsProvider submissionDetailsProvider = mock(FindUngradedSubmissions.SubmissionDetailsProvider.class);

    String studentId = "student789";
    LocalDateTime submissionTime = LocalDateTime.now();
    FindUngradedSubmissions.SubmissionDetails submissionDetails = new FindUngradedSubmissions.SubmissionDetails(studentId, submissionTime);
    Path submission = getPathToExistingFile();
    when(submissionDetailsProvider.getSubmissionDetails(submission)).thenReturn(submissionDetails);

    Path testOutput = getPathToExistingFile();

    FindUngradedSubmissions.TestOutputPathProvider testOutputProvider = mock(FindUngradedSubmissions.TestOutputPathProvider.class);
    when(testOutputProvider.getTestOutput(any(Path.class), eq(studentId))).thenReturn(testOutput);

    FindUngradedSubmissions.TestOutputDetailsProvider testOutputDetailsProvider = mock(FindUngradedSubmissions.TestOutputDetailsProvider.class);
    LocalDateTime gradedTime = submissionTime.plusDays(1);
    when(testOutputDetailsProvider.getTestOutputDetails(testOutput)).thenReturn(new FindUngradedSubmissions.TestOutputDetails(testOutput, gradedTime, true, "Project3", 15.0, true));

    // Create a mock GradeBook with a student that has a MATCHING grade for Project3
    GradeBook gradeBook = mock(GradeBook.class);
    Student student = mock(Student.class);
    Grade grade = mock(Grade.class);

    when(gradeBook.getStudent(studentId)).thenReturn(Optional.of(student));
    when(student.getGrade("Project3")).thenReturn(grade);
    when(grade.getScore()).thenReturn(15.0); // GradeBook and test output both have 15.0

    FindUngradedSubmissions.GradeBookProvider gradeBookProvider = mock(FindUngradedSubmissions.GradeBookProvider.class);
    when(gradeBookProvider.getGradeBook()).thenReturn(Optional.of(gradeBook));

    FindUngradedSubmissions finder = new FindUngradedSubmissions(submissionDetailsProvider, testOutputProvider, testOutputDetailsProvider, gradeBookProvider);
    FindUngradedSubmissions.SubmissionAnalysis analysis = finder.analyzeSubmission(submission);

    assertThat(analysis.needsToBeTested(), equalTo(false));
    assertThat(analysis.needsToBeGraded(), equalTo(false));
    assertThat(analysis.gradeNeedsToBeRecorded(), equalTo(false));  // Grades match!
  }

  @Test
  void ungradedSubmissionIsNotConsideredUnrecordedEvenIfGradebookHasGrade() {
    FindUngradedSubmissions.SubmissionDetailsProvider submissionDetailsProvider = mock(FindUngradedSubmissions.SubmissionDetailsProvider.class);

    String studentId = "student999";
    LocalDateTime submissionTime = LocalDateTime.now();
    FindUngradedSubmissions.SubmissionDetails submissionDetails = new FindUngradedSubmissions.SubmissionDetails(studentId, submissionTime);
    Path submission = getPathToExistingFile();
    when(submissionDetailsProvider.getSubmissionDetails(submission)).thenReturn(submissionDetails);

    Path testOutput = getPathToExistingFile();

    FindUngradedSubmissions.TestOutputPathProvider testOutputProvider = mock(FindUngradedSubmissions.TestOutputPathProvider.class);
    when(testOutputProvider.getTestOutput(any(Path.class), eq(studentId))).thenReturn(testOutput);

    FindUngradedSubmissions.TestOutputDetailsProvider testOutputDetailsProvider = mock(FindUngradedSubmissions.TestOutputDetailsProvider.class);
    LocalDateTime gradedTime = submissionTime.plusDays(1);
    // Test output has NO grade yet (hasGrade = false, grade = null)
    boolean hasBeenReviewed = false;
    when(testOutputDetailsProvider.getTestOutputDetails(testOutput)).thenReturn(new FindUngradedSubmissions.TestOutputDetails(testOutput, gradedTime, false, "Project4", null, hasBeenReviewed));

    // Create a mock GradeBook with a student that HAS a grade for Project4 in the gradebook
    GradeBook gradeBook = mock(GradeBook.class);
    Student student = mock(Student.class);
    Grade grade = mock(Grade.class);

    when(gradeBook.getStudent(studentId)).thenReturn(Optional.of(student));
    when(student.getGrade("Project4")).thenReturn(grade);
    when(grade.getScore()).thenReturn(20.0); // GradeBook has a grade, but test output doesn't

    FindUngradedSubmissions.GradeBookProvider gradeBookProvider = mock(FindUngradedSubmissions.GradeBookProvider.class);
    when(gradeBookProvider.getGradeBook()).thenReturn(Optional.of(gradeBook));

    FindUngradedSubmissions finder = new FindUngradedSubmissions(submissionDetailsProvider, testOutputProvider, testOutputDetailsProvider, gradeBookProvider);
    FindUngradedSubmissions.SubmissionAnalysis analysis = finder.analyzeSubmission(submission);

    // The submission needs to be graded (no grade in test output yet)
    assertThat(analysis.needsToBeTested(), equalTo(false));
    assertThat(analysis.needsToBeGraded(), equalTo(true));
    // Even though gradebook has a grade, since submission isn't graded yet, it should NOT be considered unrecorded
    assertThat(analysis.gradeNeedsToBeRecorded(), equalTo(false));
  }


  @Test
  void reviewedSubmissionDoesNotNeedToBeTestedGradedOrRecorded() {
    FindUngradedSubmissions.SubmissionDetailsProvider submissionDetailsProvider = mock(FindUngradedSubmissions.SubmissionDetailsProvider.class);

    String studentId = "student999";
    LocalDateTime submissionTime = LocalDateTime.now();
    FindUngradedSubmissions.SubmissionDetails submissionDetails = new FindUngradedSubmissions.SubmissionDetails(studentId, submissionTime);
    Path submission = getPathToExistingFile();
    when(submissionDetailsProvider.getSubmissionDetails(submission)).thenReturn(submissionDetails);

    Path testOutput = getPathToExistingFile();

    FindUngradedSubmissions.TestOutputPathProvider testOutputProvider = mock(FindUngradedSubmissions.TestOutputPathProvider.class);
    when(testOutputProvider.getTestOutput(any(Path.class), eq(studentId))).thenReturn(testOutput);

    FindUngradedSubmissions.TestOutputDetailsProvider testOutputDetailsProvider = mock(FindUngradedSubmissions.TestOutputDetailsProvider.class);
    LocalDateTime gradedTime = submissionTime.plusDays(1);
    // Test output has NO grade yet (hasGrade = false, grade = null)
    boolean hasGrade = false;
    boolean hasBeenReviewed = true;
    when(testOutputDetailsProvider.getTestOutputDetails(testOutput)).thenReturn(new FindUngradedSubmissions.TestOutputDetails(testOutput, gradedTime, hasGrade, "Project4", null, hasBeenReviewed));

    FindUngradedSubmissions.GradeBookProvider gradeBookProvider = mock(FindUngradedSubmissions.GradeBookProvider.class);

    FindUngradedSubmissions finder = new FindUngradedSubmissions(submissionDetailsProvider, testOutputProvider, testOutputDetailsProvider, gradeBookProvider);
    FindUngradedSubmissions.SubmissionAnalysis analysis = finder.analyzeSubmission(submission);

    // The submission needs to be graded (no grade in test output yet)
    assertThat(analysis.needsToBeTested(), equalTo(false));
    assertThat(analysis.needsToBeGraded(), equalTo(false));
    assertThat(analysis.gradeNeedsToBeRecorded(), equalTo(false));
  }
}

