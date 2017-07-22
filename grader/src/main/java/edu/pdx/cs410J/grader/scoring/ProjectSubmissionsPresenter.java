package edu.pdx.cs410J.grader.scoring;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import edu.pdx.cs410J.grader.mvp.PresenterOnEventBus;

import java.util.List;
import java.util.stream.Collectors;

public class ProjectSubmissionsPresenter extends PresenterOnEventBus {
  private final ProjectSubmissionsView view;

  @Inject
  public ProjectSubmissionsPresenter(EventBus bus, ProjectSubmissionsView view) {
    super(bus);
    this.view = view;
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
