package edu.pdx.cs410J.grader.scoring;

import com.google.common.eventbus.Subscribe;
import edu.pdx.cs410J.grader.scoring.ProjectSubmissionScoreView.ScoreChangedListener;
import edu.pdx.cs410J.grader.scoring.ProjectSubmissionScoreView.ScoreSavedListener;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.util.function.BiConsumer;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.mockito.Mockito.*;

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
    publishEvent(new ProjectSubmissionSelected(submission));

    verify(view).setTotalPoints("6.0");
    verify(view).setScore("4.5");
    verify(view).setScoreIsValid(true);
  }

  @Test
  public void enteringValidScoreUpdatesSubmissionScore() {
    ArgumentCaptor<ScoreChangedListener> listener = ArgumentCaptor.forClass(ScoreChangedListener.class);
    verify(view).addScoreChangedListener(listener.capture());

    ProjectSubmission submission = createProjectSubmission("Project", "student");
    submission.setTotalPoints(6.0);
    submission.setScore(null);
    publishEvent(new ProjectSubmissionSelected(submission));

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
    publishEvent(new ProjectSubmissionSelected(submission));

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
    publishEvent(new ProjectSubmissionSelected(submission));

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
    publishEvent(new ProjectSubmissionSelected(submission));
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
    publishEvent(new ProjectSubmissionSelected(submission));
    verify(view).setScoreIsValid(true);

    listener.getValue().scoreChanged("7.0");

    verify(view).setScoreIsValid(false);
    assertThat(submission.getScore(), equalTo(null));
  }

  @Test
  public void scoreInitiallyPopulatedWithTotalPoints() {
    ProjectSubmission submission = createProjectSubmission("Project", "student");
    submission.setTotalPoints(6.0);
    submission.setScore(null);
    publishEvent(new ProjectSubmissionSelected(submission));

    verify(view).setTotalPoints("6.0");
    verify(view).setScore("6.0");
    verify(view).setScoreIsValid(true);
  }

  @Test
  public void savingScorePublishesProjectSubmissionScoreSaved() {
    // When a score of 5.0 has been set for a project submission
    ProjectSubmission submission = createProjectSubmission("Project", "student");
    submission.setTotalPoints(6.0);
    submission.setScore(null);
    publishEvent(new ProjectSubmissionSelected(submission));

    double newScore = 5.2;
    ScoreChangedListener scoreChanged =
      captureListener(ScoreChangedListener.class, ProjectSubmissionScoreView::addScoreChangedListener);
    scoreChanged.scoreChanged(String.valueOf(newScore));

    ProjectSubmissionSavedHandler handler = mock(ProjectSubmissionSavedHandler.class);
    bus.register(handler);

    // When submission is saved...
    ScoreSavedListener scoreSaved =
      captureListener(ScoreSavedListener.class, ProjectSubmissionScoreView::addScoreSavedListener);
    scoreSaved.submissionSaved();

    // Then a ProjectSubmissionScoreSaved message is published
    ArgumentCaptor<ProjectSubmissionScoreSaved> eventCaptor = ArgumentCaptor.forClass(ProjectSubmissionScoreSaved.class);
    verify(handler).handle(eventCaptor.capture());
    assertThat(eventCaptor.getValue().getProjectSubmission().getScore(), equalTo(newScore));
  }

  @Test
  public void testCaseScoreChangeUpdatesSubmissionScore() {
    ProjectSubmission submission = createProjectSubmission("Project", "student");
    submission.setTotalPoints(8.0);
    TestCaseOutput testCase = new TestCaseOutput();
    submission.addTestCaseOutput(testCase);

    publishEvent(new ProjectSubmissionSelected(submission));
    verify(view).setScoreIsValid(true);
    verify(view).setScore("8.0");

    testCase.setPointsDeducted(3.0);
    publishEvent(new TestCaseOutputUpdated(testCase));

    verify(view, times(2)).setScoreIsValid(true);
    verify(view).setScore("5.0");
  }

  @Test
  public void testCaseScoreChangeUpdatesSubmissionScoreWhenATestCaseHasNoScore() {
    ProjectSubmission submission = createProjectSubmission("Project", "student");
    submission.setTotalPoints(8.0);
    TestCaseOutput testCase = new TestCaseOutput();
    submission.addTestCaseOutput(testCase);
    submission.addTestCaseOutput(new TestCaseOutput());

    publishEvent(new ProjectSubmissionSelected(submission));
    verify(view).setScoreIsValid(true);
    verify(view).setScore("8.0");

    testCase.setPointsDeducted(3.0);
    publishEvent(new TestCaseOutputUpdated(testCase));

    verify(view, times(2)).setScoreIsValid(true);
    verify(view).setScore("5.0");
  }


  private interface ProjectSubmissionSavedHandler {
    @Subscribe
    void handle(ProjectSubmissionScoreSaved event);
  }

  private <L> L captureListener(Class<L> listenerClass, BiConsumer<ProjectSubmissionScoreView, L> addListener) {
    ArgumentCaptor<L> listener = ArgumentCaptor.forClass(listenerClass);
    addListener.accept(verify(view), listener.capture());
    return listener.getValue();
  }
}
