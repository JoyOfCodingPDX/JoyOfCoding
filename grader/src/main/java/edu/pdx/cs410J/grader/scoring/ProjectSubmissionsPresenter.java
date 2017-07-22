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
  private List<ProjectSubmission> submissions;

  @Inject
  public ProjectSubmissionsPresenter(EventBus bus, ProjectSubmissionsView view) {
    super(bus);
    this.view = view;

    view.addSubmissionNameSelectedListener(this::publishSubmissionSelected);
  }

  private void publishSubmissionSelected(int selectedIndex) {
    if (this.submissions != null) {
      ProjectSubmission selected = this.submissions.get(selectedIndex);
      publishEvent(new ProjectSubmissionSelected(selected));
    }
  }

  @Subscribe
  public void populateViewWithNamesOfSubmissions(ProjectSubmissionsLoaded loaded) {
    submissions = loaded.getSubmissions();

    List<String> submissionNames = submissions.stream().map(this::getSubmissionName).collect(Collectors.toList());
    this.view.setProjectSubmissionNames(submissionNames);

    this.view.setSelectedSubmission(0);
  }

  private String getSubmissionName(ProjectSubmission submission) {
    return getProjectSubmissionName(submission.getStudentId(), submission.getProjectName());
  }

  @VisibleForTesting
  static String getProjectSubmissionName(String studentId, String projectName) {
    return studentId + "/" + projectName;
  }
}
