package edu.pdx.cs410J.grader.scoring;

import com.google.common.eventbus.Subscribe;
import edu.pdx.cs410J.grader.scoring.ProjectSubmissionsView.SubmissionNameSelectedListener;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.util.ArrayList;
import java.util.Arrays;
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
    publishEvent(loaded);

    String submissionName = ProjectSubmissionsPresenter.getProjectSubmissionName(studentId, projectName);
    verify(view).setUngradedProjectSubmissionNames(Collections.singletonList(submissionName));
  }

  @Test
  public void viewIsPopulatedWithGradedAndUngradedSubmissions() {
    String projectName = "Project";
    String ungradedStudentName = "student0";
    ProjectSubmission ungraded = createProjectSubmission(projectName, ungradedStudentName);
    ungraded.setScore(null);

    String gradedStudentName = "student1";
    ProjectSubmission graded = createProjectSubmission(projectName, gradedStudentName);
    graded.setScore(4.0);

    publishEvent(new ProjectSubmissionsLoaded(Arrays.asList(ungraded, graded)));

    String ungradedName = ProjectSubmissionsPresenter.getProjectSubmissionName(ungradedStudentName, projectName);
    verify(view).setUngradedProjectSubmissionNames(Collections.singletonList(ungradedName));
    verify(view).setSelectedUngradedSubmission(0);

    String gradedName = ProjectSubmissionsPresenter.getProjectSubmissionName(gradedStudentName, projectName);
    verify(view).setGradedProjectSubmissionNames(Collections.singletonList(gradedName));
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
    publishEvent(loaded);

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
    publishEvent(loaded);
    verify(view).setSelectedUngradedSubmission(0);
  }

  @Test
  public void submissionMovedToGradedWhenUpdated() {
    String projectName = "Project";
    String studentId = "student";
    ProjectSubmission submission = createProjectSubmission(projectName, studentId);
    submission.setScore(null);

    publishEvent(new ProjectSubmissionsLoaded(Collections.singletonList(submission)));

    String submissionName = ProjectSubmissionsPresenter.getProjectSubmissionName(studentId, projectName);
    verify(view).setUngradedProjectSubmissionNames(Collections.singletonList(submissionName));
    verify(view).setSelectedUngradedSubmission(0);

    submission.setScore(3.0);
    publishEvent(new ProjectSubmissionScoreSaved(submission));

    verify(view).setGradedProjectSubmissionNames(Collections.singletonList(submissionName));
  }

  private interface ProjectSubmissionSelectedHandler {
    @Subscribe
    public void handle(ProjectSubmissionSelected event);
  }
}
