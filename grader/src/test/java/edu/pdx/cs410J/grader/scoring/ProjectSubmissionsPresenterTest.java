package edu.pdx.cs410J.grader.scoring;

import com.google.common.eventbus.Subscribe;
import edu.pdx.cs410J.grader.mvp.EventBusTestCase;
import edu.pdx.cs410J.grader.scoring.ProjectSubmissionsView.SubmissionNameSelectedListener;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

public class ProjectSubmissionsPresenterTest extends EventBusTestCase {

  private ProjectSubmissionsView view;

  @Override
  @Before
  public void setUp() {
    super.setUp();
    view = mock(ProjectSubmissionsView.class);
    new ProjectSubmissionsPresenter(this.bus, view);
  }

  @Test
  public void viewIsPopulatedWithSubmissionNamesWhenSubmissionsAreLoaded() {
    String projectName = "Project";
    String studentId = "student";

    ProjectSubmission submission = createProjectSubmission(projectName, studentId);

    ProjectSubmissionsLoaded loaded = new ProjectSubmissionsLoaded(Collections.singletonList(submission));
    this.bus.post(loaded);

    String submissionName = ProjectSubmissionsPresenter.getProjectSubmissionName(studentId, projectName);
    verify(view).setProjectSubmissionNames(Collections.singletonList(submissionName));
  }

  private ProjectSubmission createProjectSubmission(String projectName, String studentId) {
    ProjectSubmission submission = new ProjectSubmission();
    submission.setProjectName(projectName);
    submission.setStudentId(studentId);
    return submission;
  }

  @Test
  public void eventIsPublishedWhenProjectSubmissionIsSelected() {
    ProjectSubmissionSelectedHandler handler = mock(ProjectSubmissionSelectedHandler.class);
    bus.register(handler);

    ArgumentCaptor<SubmissionNameSelectedListener> listener = ArgumentCaptor.forClass(SubmissionNameSelectedListener.class);
    verify(view).addSubmissionNameSelectedListener(listener.capture());

    String projectName = "Project";
    String studentId = "student1";

    List<ProjectSubmission> submissions = new ArrayList<>();
    submissions.add(createProjectSubmission(projectName, "student0"));
    ProjectSubmission submission1 = createProjectSubmission(projectName, studentId);
    submissions.add(submission1);
    ProjectSubmissionsLoaded loaded = new ProjectSubmissionsLoaded(submissions);
    bus.post(loaded);

    // When the user selects the second POA submission...
    listener.getValue().submissionSelected(1);

    // Then a ProjectSubmissionSelected event for that submission is published

    ArgumentCaptor<ProjectSubmissionSelected> eventCaptor = ArgumentCaptor.forClass(ProjectSubmissionSelected.class);
    verify(handler).handle(eventCaptor.capture());
    assertThat(eventCaptor.getValue().getProjectSubmission(), equalTo(submission1));

    verifyNoMoreInteractions(handler);
  }

  private interface ProjectSubmissionSelectedHandler {
    @Subscribe
    public void handle(ProjectSubmissionSelected event);
  }
}
