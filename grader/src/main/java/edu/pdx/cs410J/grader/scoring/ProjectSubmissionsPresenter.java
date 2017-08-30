package edu.pdx.cs410J.grader.scoring;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import edu.pdx.cs410J.grader.mvp.PresenterOnEventBus;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class ProjectSubmissionsPresenter extends PresenterOnEventBus {
  private final ProjectSubmissionsView view;
  private List<ProjectSubmission> submissions;
  private List<ProjectSubmission> gradedSubmissions;
  private List<ProjectSubmission> ungradedSubmissions;

  @Inject
  public ProjectSubmissionsPresenter(EventBus bus, ProjectSubmissionsView view) {
    super(bus);
    this.view = view;

    view.addUngradedSubmissionNameSelectedListener((selectedIndex) -> publishSubmissionSelected(selectedIndex, ungradedSubmissions));
    view.addGradedSubmissionNameSelectedListener((selectedIndex) -> publishSubmissionSelected(selectedIndex, gradedSubmissions));
  }

  private void publishSubmissionSelected(int selectedIndex, List<ProjectSubmission> submissions) {
    if (submissions != null && !submissions.isEmpty()) {
      ProjectSubmission selected = submissions.get(selectedIndex);
      publishEvent(new ProjectSubmissionSelected(selected));
    }
  }

  @Subscribe
  public void populateViewWithNamesOfSubmissions(ProjectSubmissionsLoaded loaded) {
    List<ProjectSubmission> list = new ArrayList<>();
    loaded.getLoadedSubmissions().forEach(l -> {
      list.add(l.getSubmission());
    });
    populateViewWithSubmissions(list);
  }

  private void populateViewWithSubmissions(List<ProjectSubmission> submissions) {
    setSubmissions(submissions);

    List<String> ungradedSubmissionNames = ungradedSubmissions.stream().map(this::getSubmissionName).collect(Collectors.toList());
    this.view.setUngradedProjectSubmissionNames(ungradedSubmissionNames);
    this.view.setSelectedUngradedSubmission(0);

    List<String> gradedSubmissionNames = gradedSubmissions.stream().map(this::getSubmissionName).collect(Collectors.toList());
    this.view.setGradedProjectSubmissionNames(gradedSubmissionNames);
  }

  @Subscribe
  public void populateViewWithNamesOfUpdatedSubmissionsAndSelectFirstUngraded(ProjectSubmissionScoreSaved saved) {
    if (this.submissions.contains(saved.getProjectSubmission())) {
      populateViewWithSubmissions(this.submissions);
      publishSubmissionSelected(0, this.ungradedSubmissions);
    }
  }

  private void setSubmissions(List<ProjectSubmission> submissions) {
    this.submissions = submissions;

    gradedSubmissions = submissions.stream()
      .filter(this::isGraded)
      .collect(Collectors.toList());

    ungradedSubmissions = submissions.stream()
      .filter(((Predicate<ProjectSubmission>) this::isGraded).negate())
      .collect(Collectors.toList());
  }

  private boolean isGraded(ProjectSubmission submission) {
    return submission.getScore() != null;
  }

  private String getSubmissionName(ProjectSubmission submission) {
    return getProjectSubmissionName(submission.getStudentId(), submission.getProjectName());
  }

  @VisibleForTesting
  static String getProjectSubmissionName(String studentId, String projectName) {
    return studentId + "/" + projectName;
  }
}