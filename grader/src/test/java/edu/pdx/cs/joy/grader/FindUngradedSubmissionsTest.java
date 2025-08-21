package edu.pdx.cs.joy.grader;

import edu.pdx.cs.joy.grader.FindUngradedSubmissions.TestOutputDetailsProviderFromTestOutputFile;
import org.junit.jupiter.api.Test;

import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.spi.FileSystemProvider;
import java.time.LocalDateTime;
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

    FindUngradedSubmissions finder = new FindUngradedSubmissions(submissionDetailsProvider, testOutputProvider, mock(FindUngradedSubmissions.TestOutputDetailsProvider.class));
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
    LocalDateTime testedSubmissionTime = submissionTime.minusDays(1); // Simulate test output older than submission
    when(testOutputDetailsProvider.getTestOutputDetails(testOutput)).thenReturn(new FindUngradedSubmissions.TestOutputDetails(testedSubmissionTime, true));

    FindUngradedSubmissions finder = new FindUngradedSubmissions(submissionDetailsProvider, testOutputProvider, testOutputDetailsProvider);
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
    LocalDateTime testedSubmissionTime = submissionTime.minusSeconds(10); // Simulate test output older than submission
    when(testOutputDetailsProvider.getTestOutputDetails(testOutput)).thenReturn(new FindUngradedSubmissions.TestOutputDetails(testedSubmissionTime, true));

    FindUngradedSubmissions finder = new FindUngradedSubmissions(submissionDetailsProvider, testOutputProvider, testOutputDetailsProvider);
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
    when(testOutputDetailsProvider.getTestOutputDetails(testOutput)).thenReturn(new FindUngradedSubmissions.TestOutputDetails(gradedTime, false));

    FindUngradedSubmissions finder = new FindUngradedSubmissions(submissionDetailsProvider, testOutputProvider, testOutputDetailsProvider);
    FindUngradedSubmissions.SubmissionAnalysis analysis = finder.analyzeSubmission(submission);
    assertThat(analysis.needsToBeTested(), equalTo(false));
    assertThat(analysis.needsToBeGraded(), equalTo(true));
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
    when(testOutputDetailsProvider.getTestOutputDetails(testOutput)).thenReturn(new FindUngradedSubmissions.TestOutputDetails(gradedTime, true));

    FindUngradedSubmissions finder = new FindUngradedSubmissions(submissionDetailsProvider, testOutputProvider, testOutputDetailsProvider);
    FindUngradedSubmissions.SubmissionAnalysis analysis = finder.analyzeSubmission(submission);
    assertThat(analysis.needsToBeTested(), equalTo(false));
    assertThat(analysis.needsToBeGraded(), equalTo(false));
  }

  @Test
  void parseSubmissionTimeFromAndroidProjectTestOutputLine() {
    LocalDateTime submissionTime = TestOutputDetailsProviderFromTestOutputFile.parseSubmissionTime("              Submitted on 2025-08-18T11:34:19.017953486");
    LocalDateTime expectedTime = LocalDateTime.of(2025, 8, 18, 11, 34, 19, 17953486);
    assertThat(submissionTime, equalTo(expectedTime));
  }

  @Test
  void parseSubmissionTimeFromTestOutputLine() {
    LocalDateTime submissionTime = TestOutputDetailsProviderFromTestOutputFile.parseSubmissionTime("              Submitted on Wed Aug  6 01:13:59 PM PDT 2025");
    LocalDateTime expectedTime = LocalDateTime.of(2025, 8, 6, 13, 13, 59);
    assertThat(submissionTime, equalTo(expectedTime));
  }

  @Test
  void parseSubmissionTimeFromTestOutputLineWithTwoDigitDay() {
    LocalDateTime submissionTime = TestOutputDetailsProviderFromTestOutputFile.parseSubmissionTime("              Submitted on Wed Jul 23 12:59:13 PM PDT 2025");
    LocalDateTime expectedTime = LocalDateTime.of(2025, 7, 23, 12, 59, 13);
    assertThat(submissionTime, equalTo(expectedTime));
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
  void parseTestOutputDetails() {
    Stream<String> lines = Stream.of(
        "              Submitted on Wed Aug  6 01:13:59 PM PDT 2025",
        "",
        "12.5 out of 13.0"
    );
    FindUngradedSubmissions.TestOutputDetails details = TestOutputDetailsProviderFromTestOutputFile.parseTestOutputDetails(lines);
    LocalDateTime submissionTime = LocalDateTime.of(2025, 8, 6, 13, 13, 59);
    assertThat(details.testedSubmissionTime(), equalTo(submissionTime));
    assertThat(details.hasGrade(), equalTo(true));
  }
}
