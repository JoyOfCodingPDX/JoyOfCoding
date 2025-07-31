package edu.pdx.cs.joy.grader;

import org.junit.jupiter.api.Test;

import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.spi.FileSystemProvider;
import java.time.ZonedDateTime;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
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
    assertThat(Files.exists(testOutput), equalTo(exists));
    return testOutput;
  }

  @Test
  void submissionWithNoTestOutputNeedsToBeTested() {
    FindUngradedSubmissions.SubmissionDetailsProvider submissionDetailsProvider = mock(FindUngradedSubmissions.SubmissionDetailsProvider.class);

    String studentId = "student123";
    FindUngradedSubmissions.SubmissionDetails submissionDetails = new FindUngradedSubmissions.SubmissionDetails(studentId, ZonedDateTime.now());
    Path submission = mock(Path.class);
    when(submissionDetailsProvider.getSubmissionDetails(submission)).thenReturn(submissionDetails);

    Path testOutput = getPathToNonExistingFile();

    FindUngradedSubmissions.TestOutputPathProvider testOutputProvider = mock(FindUngradedSubmissions.TestOutputPathProvider.class);
    when(testOutputProvider.getTestOutput(studentId)).thenReturn(testOutput);

    FindUngradedSubmissions finder = new FindUngradedSubmissions(submissionDetailsProvider, testOutputProvider, mock(FindUngradedSubmissions.TestOutputDetailsProvider.class));
    FindUngradedSubmissions.SubmissionAnalysis analysis = finder.analyzeSubmission(submission);
    assertThat(analysis.needsToBeTested(), equalTo(true));
    assertThat(analysis.needsToBeGraded(), equalTo(true));
  }

  @Test
  void submissionWithTestOutputOlderThanSubmissionNeedsToBeTested() {
    FindUngradedSubmissions.SubmissionDetailsProvider submissionDetailsProvider = mock(FindUngradedSubmissions.SubmissionDetailsProvider.class);

    String studentId = "student123";
    ZonedDateTime submissionTime = ZonedDateTime.now();
    FindUngradedSubmissions.SubmissionDetails submissionDetails = new FindUngradedSubmissions.SubmissionDetails(studentId, submissionTime);
    Path submission = mock(Path.class);
    when(submissionDetailsProvider.getSubmissionDetails(submission)).thenReturn(submissionDetails);

    Path testOutput = getPathToExistingFile();

    FindUngradedSubmissions.TestOutputPathProvider testOutputProvider = mock(FindUngradedSubmissions.TestOutputPathProvider.class);
    when(testOutputProvider.getTestOutput(studentId)).thenReturn(testOutput);

    FindUngradedSubmissions.TestOutputDetailsProvider testOutputDetailsProvider = mock(FindUngradedSubmissions.TestOutputDetailsProvider.class);
    ZonedDateTime gradedTime = submissionTime.minusDays(1); // Simulate test output older than submission
    when(testOutputDetailsProvider.getTestOutputDetails(testOutput)).thenReturn(new FindUngradedSubmissions.TestOutputDetails(gradedTime, true));

    FindUngradedSubmissions finder = new FindUngradedSubmissions(submissionDetailsProvider, testOutputProvider, testOutputDetailsProvider);
    FindUngradedSubmissions.SubmissionAnalysis analysis = finder.analyzeSubmission(submission);
    assertThat(analysis.needsToBeTested(), equalTo(true));
    assertThat(analysis.needsToBeGraded(), equalTo(true));
  }

  @Test
  void submissionWithNoGradeNeedsToBeGraded() {
    FindUngradedSubmissions.SubmissionDetailsProvider submissionDetailsProvider = mock(FindUngradedSubmissions.SubmissionDetailsProvider.class);

    String studentId = "student123";
    ZonedDateTime submissionTime = ZonedDateTime.now();
    FindUngradedSubmissions.SubmissionDetails submissionDetails = new FindUngradedSubmissions.SubmissionDetails(studentId, submissionTime);
    Path submission = mock(Path.class);
    when(submissionDetailsProvider.getSubmissionDetails(submission)).thenReturn(submissionDetails);

    Path testOutput = getPathToExistingFile();

    FindUngradedSubmissions.TestOutputPathProvider testOutputProvider = mock(FindUngradedSubmissions.TestOutputPathProvider.class);
    when(testOutputProvider.getTestOutput(studentId)).thenReturn(testOutput);

    FindUngradedSubmissions.TestOutputDetailsProvider testOutputDetailsProvider = mock(FindUngradedSubmissions.TestOutputDetailsProvider.class);
    ZonedDateTime gradedTime = submissionTime.plusDays(1); // Simulate test output newer than submission
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
    ZonedDateTime submissionTime = ZonedDateTime.now();
    FindUngradedSubmissions.SubmissionDetails submissionDetails = new FindUngradedSubmissions.SubmissionDetails(studentId, submissionTime);
    Path submission = mock(Path.class);
    when(submissionDetailsProvider.getSubmissionDetails(submission)).thenReturn(submissionDetails);

    Path testOutput = getPathToExistingFile();

    FindUngradedSubmissions.TestOutputPathProvider testOutputProvider = mock(FindUngradedSubmissions.TestOutputPathProvider.class);
    when(testOutputProvider.getTestOutput(studentId)).thenReturn(testOutput);

    FindUngradedSubmissions.TestOutputDetailsProvider testOutputDetailsProvider = mock(FindUngradedSubmissions.TestOutputDetailsProvider.class);
    ZonedDateTime gradedTime = submissionTime.plusDays(1);
    when(testOutputDetailsProvider.getTestOutputDetails(testOutput)).thenReturn(new FindUngradedSubmissions.TestOutputDetails(gradedTime, true));

    FindUngradedSubmissions finder = new FindUngradedSubmissions(submissionDetailsProvider, testOutputProvider, testOutputDetailsProvider);
    FindUngradedSubmissions.SubmissionAnalysis analysis = finder.analyzeSubmission(submission);
    assertThat(analysis.needsToBeTested(), equalTo(false));
    assertThat(analysis.needsToBeGraded(), equalTo(false));
  }
}
