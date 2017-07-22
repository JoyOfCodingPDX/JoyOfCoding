package edu.pdx.cs410J.grader.scoring;

import java.util.List;

public interface ProjectSubmissionsView {
  void setProjectSubmissionNames(List<String> submissionNames);

  void addSubmissionNameSelectedListener(SubmissionNameSelectedListener listener);

  interface SubmissionNameSelectedListener {
    void submissionSelected(int index);
  }
}
