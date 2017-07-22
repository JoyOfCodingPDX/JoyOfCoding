package edu.pdx.cs410J.grader.scoring;

import com.google.common.eventbus.Subscribe;
import edu.pdx.cs410J.grader.scoring.ProjectSubmissionsView.SubmissionNameSelectedListener;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.mockito.Mockito.*;

public class ProjectSubmissionsPresenterTest extends ProjectSubmissionTestCase {

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

  @Test
  public void eventIsPublishedWhenProjectSubmissionIsSelected() {
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

    // When the user selects the second project submission...
    ProjectSubmissionSelectedHandler handler = mock(ProjectSubmissionSelectedHandler.class);
    bus.register(handler);
    listener.getValue().submissionNameSelected(1);

    // Then a ProjectSubmissionSelected event for that submission is published

    ArgumentCaptor<ProjectSubmissionSelected> eventCaptor = ArgumentCaptor.forClass(ProjectSubmissionSelected.class);
    verify(handler).handle(eventCaptor.capture());
    assertThat(eventCaptor.getValue().getProjectSubmission(), equalTo(submission1));

    verifyNoMoreInteractions(handler);
  }

  @Test
  public void firstSubmissionIsSelectedWhenSubmissionsAreLoaded() {
    ArgumentCaptor<SubmissionNameSelectedListener> listener = ArgumentCaptor.forClass(SubmissionNameSelectedListener.class);
    verify(view).addSubmissionNameSelectedListener(listener.capture());

    String projectName = "Project";

    List<ProjectSubmission> submissions = new ArrayList<>();
    ProjectSubmission submission0 = createProjectSubmission(projectName, "student0");
    submissions.add(submission0);
    submissions.add(createProjectSubmission(projectName, "student1"));

    ProjectSubmissionsLoaded loaded = new ProjectSubmissionsLoaded(submissions);
    bus.post(loaded);
    verify(view).setSelectedSubmission(0);
  }

  private interface ProjectSubmissionSelectedHandler {
    @Subscribe
    public void handle(ProjectSubmissionSelected event);
  }
}
