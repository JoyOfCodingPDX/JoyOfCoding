package edu.pdx.cs410J.grader.scoring;

import edu.pdx.cs410J.grader.mvp.EventBusTestCase;
import org.junit.Test;

import java.util.Collections;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class ProjectSubmissionsPresenterTest extends EventBusTestCase {

  @Test
  public void viewIsPopulatedWithSubmissionNamesWhenSubmissionsAreLoaded() {
    ProjectSubmissionsView view = mock(ProjectSubmissionsView.class);

    new ProjectSubmissionsPresenter(this.bus, view);

    String projectName = "Project";
    String studentId = "student";

    ProjectSubmission submission = new ProjectSubmission();
    submission.setProjectName(projectName);
    submission.setStudentId(studentId);

    ProjectSubmissionsLoaded loaded = new ProjectSubmissionsLoaded(Collections.singletonList(submission));
    this.bus.post(loaded);

    String submissionName = ProjectSubmissionsPresenter.getProjectSubmissionName(studentId, projectName);
    verify(view).setProjectSubmissionNames(Collections.singletonList(submissionName));
  }
}
