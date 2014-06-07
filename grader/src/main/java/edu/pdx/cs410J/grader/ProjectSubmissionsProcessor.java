package edu.pdx.cs410J.grader;

import com.google.common.io.ByteStreams;

import java.io.*;
import java.util.jar.JarInputStream;
import java.util.jar.Manifest;

class ProjectSubmissionsProcessor extends StudentEmailAttachmentProcessor {

  public ProjectSubmissionsProcessor(File directory, GradeBook gradeBook) {
    super(directory, gradeBook);
  }

  @Override
  public void processAttachment(String fileName, InputStream inputStream) {
    warn("    File name: " + fileName);
    warn("    InputStream: " + inputStream);

    File file = new File(directory, fileName);
    try {

      if (file.exists()) {
        warnOfPreExistingFile(file);
      }

      ByteStreams.copy(inputStream, new FileOutputStream(file));
    } catch (IOException ex) {
      logException("While writing \"" + fileName + "\" to \"" + directory + "\"", ex);
    }

    Manifest manifest;
    try {
      manifest = getManifestFromJarFile(file);

    } catch (IOException ex) {
      logException("While reading jar file \"" + fileName + "\"", ex);
      return;
    }

    String createdBy = manifest.getMainAttributes().getValue(JarMaker.CREATED_BY);
    warn("  Created by: " + createdBy);

  }

  private Manifest getManifestFromJarFile(File file) throws IOException {
    JarInputStream in = new JarInputStream(new FileInputStream(file));
    return in.getManifest();
  }

  private void warnOfPreExistingFile(File file) {
    warn("Overwriting existing file \"" + file + "\"");
  }

  @Override
  public String getEmailFolder() {
    return "Project Submissions";
  }
}
