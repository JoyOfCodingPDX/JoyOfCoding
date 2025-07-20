package edu.pdx.cs.joy.grader;

import org.junit.jupiter.api.Test;

import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.spi.FileSystemProvider;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class FindUngradedSubmissionsTest {

  @Test
  void submissionWithNoTestOutputIsGraded() {
    FindUngradedSubmissions.SubmissionDetailsProvider submissionDetailsProvider = mock(FindUngradedSubmissions.SubmissionDetailsProvider.class);

    String studentId = "student123";
    FindUngradedSubmissions.SubmissionDetails submissionDetails = new FindUngradedSubmissions.SubmissionDetails(studentId);
    Path submission = mock(Path.class);
    when(submissionDetailsProvider.getSubmissionDetails(submission)).thenReturn(submissionDetails);

    Path testOutput = getPathToNonExistingFile();

    FindUngradedSubmissions.TestOutputProvider testOutputProvider = mock(FindUngradedSubmissions.TestOutputProvider.class);
    when(testOutputProvider.getTestOutput(studentId)).thenReturn(testOutput);

    FindUngradedSubmissions finder = new FindUngradedSubmissions(submissionDetailsProvider, testOutputProvider);
    assertThat(finder.isGraded(submission), equalTo(false));
  }

  private static Path getPathToNonExistingFile() {
    Path testOutput = mock(Path.class);
    FileSystemProvider provider = mock(FileSystemProvider.class);
    when(provider.exists(testOutput)).thenReturn(false);
    FileSystem fileSystem = mock(FileSystem.class);
    when(fileSystem.provider()).thenReturn(provider);
    when(testOutput.getFileSystem()).thenReturn(fileSystem);
    assertThat(Files.exists(testOutput), equalTo(false));
    return testOutput;
  }
}
