package edu.pdx.cs410J.grader.scoring;

import edu.pdx.cs410J.grader.scoring.ProjectSubmissionScoreView.ScoreChangedListener;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class ProjectSubmissionScorePresenterTest extends ProjectSubmissionTestCase {

  private ProjectSubmissionScoreView view;

  @Override
  public void setUp() {
    super.setUp();
    view = mock(ProjectSubmissionScoreView.class);
    new ProjectSubmissionScorePresenter(bus, view);
  }

  @Test
  public void viewIsPopulatedWhenSubmissionIsSelected() {
    ProjectSubmission submission = createProjectSubmission("Project", "student");
    submission.setTotalPoints(6.0);
    submission.setScore(4.5);
    this.bus.post(new ProjectSubmissionSelected(submission));

    verify(view).setTotalPoints("6.0");
    verify(view).setScore("4.5");
    verify(view).setScoreIsValid(true);
  }

  @Test
  public void submissionWithNoScoreDisplaysEmptyString() {
    ProjectSubmission submission = createProjectSubmission("Project", "student");
    submission.setTotalPoints(6.0);
    submission.setScore(null);
    this.bus.post(new ProjectSubmissionSelected(submission));

    verify(view).setTotalPoints("6.0");
    verify(view).setScore("");
  }

  @Test
  public void enteringValidScoreUpdatesSubmissionScore() {
    ArgumentCaptor<ScoreChangedListener> listener = ArgumentCaptor.forClass(ScoreChangedListener.class);
    verify(view).addScoreChangedListener(listener.capture());

    ProjectSubmission submission = createProjectSubmission("Project", "student");
    submission.setTotalPoints(6.0);
    submission.setScore(null);
    this.bus.post(new ProjectSubmissionSelected(submission));

    listener.getValue().scoreChanged("5.0");

    verify(view, times(2)).setScoreIsValid(true);
    assertThat(submission.getScore(), equalTo(5.0));
  }

  @Test
  public void enteringInvalidScorePutsPresenterInErrorState() {
    ArgumentCaptor<ScoreChangedListener> listener = ArgumentCaptor.forClass(ScoreChangedListener.class);
    verify(view).addScoreChangedListener(listener.capture());

    ProjectSubmission submission = createProjectSubmission("Project", "student");
    submission.setTotalPoints(6.0);
    submission.setScore(null);
    this.bus.post(new ProjectSubmissionSelected(submission));

    listener.getValue().scoreChanged("qq");

    verify(view).setScoreIsValid(false);

    assertThat(submission.getScore(), equalTo(null));
  }

  @Test
  public void enteringEmptyScoreClearsScore() {
    ArgumentCaptor<ScoreChangedListener> listener = ArgumentCaptor.forClass(ScoreChangedListener.class);
    verify(view).addScoreChangedListener(listener.capture());

    ProjectSubmission submission = createProjectSubmission("Project", "student");
    submission.setTotalPoints(6.0);
    submission.setScore(null);
    this.bus.post(new ProjectSubmissionSelected(submission));

    listener.getValue().scoreChanged("");

    verify(view, times(2)).setScoreIsValid(true);
    assertThat(submission.getScore(), equalTo(null));
  }

  @Test
  public void negativeScoreIsInvalid() {
    ArgumentCaptor<ScoreChangedListener> listener = ArgumentCaptor.forClass(ScoreChangedListener.class);
    verify(view).addScoreChangedListener(listener.capture());

    ProjectSubmission submission = createProjectSubmission("Project", "student");
    submission.setTotalPoints(6.0);
    submission.setScore(null);
    this.bus.post(new ProjectSubmissionSelected(submission));
    verify(view).setScoreIsValid(true);

    listener.getValue().scoreChanged("-1.0");

    verify(view).setScoreIsValid(false);
    assertThat(submission.getScore(), equalTo(null));
  }

  @Test
  public void scoreGreaterThanTotalIsInvalid() {
    ArgumentCaptor<ScoreChangedListener> listener = ArgumentCaptor.forClass(ScoreChangedListener.class);
    verify(view).addScoreChangedListener(listener.capture());

    ProjectSubmission submission = createProjectSubmission("Project", "student");
    submission.setTotalPoints(6.0);
    submission.setScore(null);
    this.bus.post(new ProjectSubmissionSelected(submission));
    verify(view).setScoreIsValid(true);

    listener.getValue().scoreChanged("7.0");

    verify(view).setScoreIsValid(false);
    assertThat(submission.getScore(), equalTo(null));
  }


}
