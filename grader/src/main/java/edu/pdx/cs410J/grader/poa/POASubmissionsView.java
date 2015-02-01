package edu.pdx.cs410J.grader.poa;

import com.google.common.eventbus.Subscribe;

import java.util.List;

public interface POASubmissionsView {
  void setPOASubmissionsDescriptions(List<String> strings);

  void addSubmissionSelectedListener(POASubmissionSelectedListener listener);

  void addDownloadSubmissionsListener(DownloadSubmissionsListener listener);

  interface POASubmissionSelectedListener {
    public void submissionSelected(int index);
  }

  public interface DownloadSubmissionsListener {
    @Subscribe
    public void downloadSubmissions();
  }
}
