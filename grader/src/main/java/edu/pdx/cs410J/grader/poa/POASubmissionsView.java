package edu.pdx.cs410J.grader.poa;

import java.util.List;

public interface POASubmissionsView {
  void setPOASubmissionsDescriptions(List<String> strings);

  void addSubmissionSelectedListener(SubmissionSelectedListener listener);

  interface SubmissionSelectedListener {
    public void submissionSelected(int index);
  }
}
