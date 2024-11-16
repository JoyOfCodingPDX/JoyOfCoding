package edu.pdx.cs.joy.grader;

import edu.pdx.cs.joy.grader.gradebook.Assignment;
import edu.pdx.cs.joy.grader.gradebook.Grade;
import edu.pdx.cs.joy.grader.gradebook.GradeBook;
import edu.pdx.cs.joy.grader.gradebook.Student;
import jakarta.mail.MessagingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.equalTo;

public class SubmitAndroidProjectIT extends SubmitIT {

  @BeforeEach
  @Override
  public void createFilesToSubmit() throws IOException {
    File mainDir = createDirectories("app", "src", "main", "java", "edu", "pdx", "cs", "joy", studentLoginId);
    for (String fileName : Arrays.asList("MainActivity.java", "FirstFragment.java", "SecondFragment.java")) {
      this.filesToSubmit.add(createEmptyFile(mainDir, fileName));
    }

    File root = tempDirectory;
    for (String fileName : Arrays.asList("build.gradle", "gradle.properties", "gradlew", "settings.gradle")) {
      this.filesToSubmit.add(createEmptyFile(root, fileName));
    }

    File appDir = createDirectories("app");
    this.filesToSubmit.add(createEmptyFile(appDir, "build.gradle"));

    File gradleDir = createDirectories("gradle");
    this.filesToSubmit.add(createEmptyFile(gradleDir, "libs.versions.toml"));

    File wrapperDir = createDirectories("gradle", "wrapper");
    for (String fileName : Arrays.asList("gradle-wrapper.jar", "gradle-wrapper.properties")) {
      this.filesToSubmit.add(createEmptyFile(wrapperDir, fileName));
    }

    File dotGradleDir = createDirectories(".gradle");
    this.filesToSubmit.add(createEmptyFile(dotGradleDir, "config.properties"));
  }

  @Override
  protected ProjectSubmitter submitter() {
    return new AndroidProjectSubmitter();
  }

  private class AndroidProjectSubmitter extends ProjectSubmitter {
    @Override
    protected Submit getSubmitProgram() {
      return new SubmitAndroidProject(() -> submitTime);
    }
  }

  @Test
  void dotGradleFilesAreNotSubmitted() throws MessagingException, IOException {
    submitter().submitFiles();

    GradeBook gradeBook = new GradeBook("SubmitIT");
    gradeBook.addStudent(new Student(studentLoginId));
    gradeBook.addAssignment(new Assignment(projectName, 3.5));

    GraderEmailAccount account = new GraderEmailAccount(emailServerHost, imapsPort, graderEmail, imapPassword, true, m -> { });
    FetchAndProcessGraderEmail.fetchAndProcessGraderEmails("projects", account, this.tempDirectory, gradeBook);

    Grade grade = gradeBook.getStudent(studentLoginId).get().getGrade(projectName);
    assertThat(grade, is(notNullValue()));
    assertThat(grade.getScore(), equalTo(Grade.NO_GRADE));
    assertThat(grade.getSubmissionTimes().size(), equalTo(1));

    File zipFile = findNewestZipFileInTempDirectory();
    assertThat(zipFile, is(notNullValue()));
    List<String> entryNames = getZipFileEntryNames(zipFile);
    assertThat(entryNames, not(empty()));

    assertThat(entryNames, not(hasItem(".gradle")));
    assertThat(entryNames, not(hasItem(".gradle/config.properties")));
  }

  @Override
  protected Collection<File> getExpectedFilesToSubmit() {
    Collection<File> allFiles = super.getExpectedFilesToSubmit();
    return allFiles.stream()
      .filter(file -> !file.getPath().contains(".gradle"))
      .toList();
  }
}
