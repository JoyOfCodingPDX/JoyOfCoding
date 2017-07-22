package edu.pdx.cs410J.grader.scoring;

import java.util.List;

public interface TestCasesView {
  void setTestCaseNames(List<String> testCaseNames);

  void setSelectedTestCaseName(int index);

  void addTestCaseNameSelectedListener(TestCaseNameSelectedListener listener);

  interface TestCaseNameSelectedListener {
    void testCaseNameSelected(int index);
  }
}
