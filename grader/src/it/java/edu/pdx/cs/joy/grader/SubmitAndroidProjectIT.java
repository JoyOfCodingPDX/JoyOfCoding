package edu.pdx.cs.joy.grader;

import org.junit.jupiter.api.BeforeEach;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

public class SubmitAndroidProjectIT extends  SubmitIT {

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
}
