package edu.pdx.cs410J.grader.scoring;

import java.util.List;

public interface ProjectSubmissionsView {
  void setUngradedProjectSubmissionNames(List<String> submissionNames);

  void addSubmissionNameSelectedListener(SubmissionNameSelectedListener listener);

  void setSelectedUngradedSubmission(int index);

  void setGradedProjectSubmissionNames(List<String> submissionNames);

  interface SubmissionNameSelectedListener {
    void submissionNameSelected(int index);
  }
}
