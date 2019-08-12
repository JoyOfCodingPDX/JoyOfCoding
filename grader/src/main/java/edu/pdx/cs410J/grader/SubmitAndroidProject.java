package edu.pdx.cs410J.grader;

import javax.mail.MessagingException;
import java.io.File;
import java.io.IOException;
import java.util.Set;

public class SubmitAndroidProject extends Submit {

  private SubmitAndroidProject() {
    this.setProjectName("Project5");
  }

  @Override
  protected boolean canBeSubmitted(File file) {
    return fileExists(file) && getZipEntryNameFor(file) != null;
  }

  @Override
  protected void warnIfMainProjectClassIsNotSubmitted(Set<File> sourceFiles) {

  }

  @Override
  protected String getZipEntryNameFor(File file) {
    return AndroidZipFixer.getFixedEntryName(file.getAbsolutePath());
  }

  public static void main(String[] args) throws IOException, MessagingException {
    SubmitAndroidProject submit = new SubmitAndroidProject();
    submit.parseCommandLineAndSubmit(args);
  }

}
