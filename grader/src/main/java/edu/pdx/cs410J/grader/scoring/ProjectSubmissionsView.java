package edu.pdx.cs410J.grader.scoring;

import java.util.List;

public interface ProjectSubmissionsView {
  void setUngradedProjectSubmissionNames(List<String> submissionNames);

  void addUngradedSubmissionNameSelectedListener(SubmissionNameSelectedListener listener);

  void setSelectedUngradedSubmission(int index);

  void setGradedProjectSubmissionNames(List<String> submissionNames);

  void addGradedSubmissionNameSelectedListener(SubmissionNameSelectedListener listener);

  interface SubmissionNameSelectedListener {
    void submissionNameSelected(int index);
  }
}
