package edu.pdx.cs410J.grader.scoring;

import java.io.File;

public interface ProjectSubmissionsLoaderSaverView {
  void addDirectorySelectedListener(DirectorySelectedListener listener);

  void addSaveSubmissionsListener(SaveSubmissionsListener listener);

  interface DirectorySelectedListener {
    void directorySelected(File directory);
  }

  interface SaveSubmissionsListener {
    void onSaveSubmissions();
  }
}
