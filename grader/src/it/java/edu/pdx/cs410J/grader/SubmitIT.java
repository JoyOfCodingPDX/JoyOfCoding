package edu.pdx.cs410J.grader;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.mail.MessagingException;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class SubmitIT {

  private final String userEmail = "test@email.com";
  private final String userName = "Student Name";
  private Collection<File> filesToSubmit = new ArrayList<>();
  private final String userId = "student";
  private final String projectName = "Project";

  @Before
  public void createFilesToSubmit() throws IOException {
    File dir = createDirectoriesWithFilesToSubmit("edu", "pdx", "cs410J", userId);
    List<String> fileNames = Arrays.asList(projectName + ".java", "File1.java", "File2.java");
    for (String fileName : fileNames) {
      this.filesToSubmit.add(createEmptyFile(dir, fileName));
    }
  }

  @After
  public void deleteFilesToSubmit() {
    filesToSubmit.forEach(File::delete);
  }

  private File createEmptyFile(File dir, String fileName) throws IOException {
    File file = new File(dir, fileName);
    FileWriter writer = new FileWriter(file);
    writer.write("");
    return file;
  }

  private File createDirectoriesWithFilesToSubmit(String... directoryNames) {
    File dir = new File(System.getProperty("java.io.tmpdir"));
    for (String dirName : directoryNames) {
      dir = new File(dir, dirName);
      dir.mkdir();
    }

    return dir;
  }

  @Test
  public void submitFilesAndDownloadSubmission() throws IOException, MessagingException {
    Submit submit = new Submit();
    submit.setProjectName(projectName);
    submit.setUserEmail(userEmail);
    submit.setUserId(userId);
    submit.setUserName(userName);

    for (File file : filesToSubmit) {
      submit.addFile(file.getAbsolutePath());
    }

    submit.setDebug(true);
    submit.submit(false);
  }
}
