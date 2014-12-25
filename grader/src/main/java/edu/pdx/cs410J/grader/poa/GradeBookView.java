package edu.pdx.cs410J.grader.poa;

import java.io.File;

public interface GradeBookView {
  void setGradeBookName(String className);

  void addGradeBookFileListener(FileSelectedListener listener);

  interface FileSelectedListener {
    void fileSelected(File file);
  }
}
