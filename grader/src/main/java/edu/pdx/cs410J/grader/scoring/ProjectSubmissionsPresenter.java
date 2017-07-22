package edu.pdx.cs410J.grader.scoring;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

import java.util.List;
import java.util.stream.Collectors;

public class ProjectSubmissionsPresenter {
  private final EventBus bus;
  private final ProjectSubmissionsView view;

  public ProjectSubmissionsPresenter(EventBus bus, ProjectSubmissionsView view) {
    this.bus = bus;
    this.view = view;

    this.bus.register(this);
  }

  @Subscribe
  public void populateViewWithNamesOfSubmissions(ProjectSubmissionsLoaded loaded) {
    List<String> submissionNames = loaded.getSubmissions().stream().map(this::getSubmissionName).collect(Collectors.toList());
    this.view.setProjectSubmissionNames(submissionNames);
  }

  private String getSubmissionName(ProjectSubmission submission) {
    return getProjectSubmissionName(submission.getStudentId(), submission.getProjectName());
  }

  @VisibleForTesting
  static String getProjectSubmissionName(String studentId, String projectName) {
    return studentId + "/" + projectName;
  }
}
