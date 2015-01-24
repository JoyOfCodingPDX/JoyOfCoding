package edu.pdx.cs410J.grader.poa;

import edu.pdx.cs410J.grader.Assignment;
import edu.pdx.cs410J.grader.Grade;
import edu.pdx.cs410J.grader.GradeBook;
import edu.pdx.cs410J.grader.Student;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.time.LocalDateTime;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

public class POAGradePresenterTest extends EventBusTestCase {

  private POAGradeView view;
  private POASubmission submission;
  private Student student;
  private Assignment assignment;
  private GradeBook book;
  private POAGradePresenter presenter;

  @Before
  @Override
  public void setUp() {
    super.setUp();

    this.view = mock(POAGradeView.class);

    presenter = new POAGradePresenter(this.bus, this.view);
    submission = new POASubmission("Subject", "Submitter", LocalDateTime.now());
    student = new Student("id");
    assignment = new Assignment("assignment", 1.0);

    book = new GradeBook("POAGradePresenterTest");
    book.addAssignment(assignment);
    book.addStudent(student);
  }

  @Test
  public void onlySubmissionSelectedDisablesView() {
    this.bus.post(new POASubmissionSelected(submission));

    verifyIsLate(false);
    verify(this.view).setIsEnabled(eq(false));
  }

  private void verifyIsLate(boolean isLate) {
    verifyIsLate(1, isLate);
  }

  @Test
  public void onlyStudentSelectedDisablesView() {
    this.bus.post(new StudentSelectedEvent(student));

    verifyIsLate(false);
    verify(this.view).setIsEnabled(eq(false));
  }

  @Test
  public void onlyAssignmentSelectedDisablesView() {
    this.bus.post(new GradeBookLoaded(book));
    this.bus.post(new AssignmentSelectedEvent(assignment));

    verifyIsLate(false);
    verify(this.view).setIsEnabled(eq(false));
  }

  @Test
  public void submissionStudentAndAssignmentEnablesView() {
    postEventsToBus();

    verify(this.view).setIsEnabled(true);
  }

  @Test
  public void lateSubmissionIsLateInView() {
    assignment.setDueDate(LocalDateTime.now().minusDays(5));

    postEventsToBus();

    verify(this.view).setIsEnabled(true);
    verifyIsLate(true);
  }

  @Test
  public void onTimeSubmissionIsNotLateInView() {
    assignment.setDueDate(LocalDateTime.now().plusDays(5));

    postEventsToBus();

    verify(this.view).setIsEnabled(true);
    verifyIsLate(3, false);
  }

  @Test
  public void assignmentWithNoDueDateIsNotLate() {
    assignment.setDueDate(null);

    postEventsToBus();

    verify(this.view).setIsEnabled(true);
    int wantedNumberOfInvocations = 3;
    boolean isLate = false;
    verifyIsLate(wantedNumberOfInvocations, isLate);
  }

  private void verifyIsLate(int wantedNumberOfInvocations, boolean isLate) {
    verify(this.view, times(wantedNumberOfInvocations)).setIsLate(eq(isLate));
    assertThat(this.presenter.isLate(), equalTo(isLate));
  }

  @Test
  public void latePOAIsStoredInPresenterWhenMarkedLateInView() {
    ArgumentCaptor<POAGradeView.IsLateHandler> handler = ArgumentCaptor.forClass(POAGradeView.IsLateHandler.class);
    verify(this.view).addIsLateHandler(handler.capture());

    assignment.setDueDate(LocalDateTime.now().plusDays(5));

    postEventsToBus();

    verify(this.view).setIsEnabled(true);
    verifyIsLate(3, false);

    handler.getValue().setIsLate(true);
    verifyIsLate(true);

    handler.getValue().setIsLate(false);
    verifyIsLate(4, false);
  }

  @Test
  public void assignmentTotalPointsDisplayedInViewWhenAssignmentSelected() {
    double points = 2.75;
    assignment.setPoints(points);
    this.bus.post(new GradeBookLoaded(book));
    this.bus.post(new AssignmentSelectedEvent(assignment));

    verify(this.view).setTotalPoints("2.75");
  }

  @Test
  public void scoreInViewIsInErrorWhenValueIsNotAnInteger() {
    ArgumentCaptor<POAGradeView.ScoreValueHandler> handler = ArgumentCaptor.forClass(POAGradeView.ScoreValueHandler.class);
    verify(view).addScoreValueHandler(handler.capture());

    handler.getValue().scoreValue("Not an Integer");

    verify(this.view).setErrorInScore(true);
    assertThat(presenter.getScore(), nullValue());
  }

  @Test
  public void setPresentersScoreFromView() {
    ArgumentCaptor<POAGradeView.ScoreValueHandler> handler = ArgumentCaptor.forClass(POAGradeView.ScoreValueHandler.class);
    verify(view).addScoreValueHandler(handler.capture());

    this.bus.post(new GradeBookLoaded(book));
    this.bus.post(new AssignmentSelectedEvent(assignment.setPoints(2.0d)));
    reset(this.view);

    handler.getValue().scoreValue("1.75");

    verify(this.view).setErrorInScore(false);
    assertThat(presenter.getScore(), equalTo(1.75));
  }

  @Test
  public void negativeScoreIsAnError() {
    ArgumentCaptor<POAGradeView.ScoreValueHandler> handler = ArgumentCaptor.forClass(POAGradeView.ScoreValueHandler.class);
    verify(view).addScoreValueHandler(handler.capture());

    handler.getValue().scoreValue("-4.3");

    verify(this.view).setErrorInScore(true);
    assertThat(presenter.getScore(), nullValue());
  }

  @Test
  public void errorStateShouldBeClearedWhenNewSubmissionIsSelected() {
    ArgumentCaptor<POAGradeView.ScoreValueHandler> handler = ArgumentCaptor.forClass(POAGradeView.ScoreValueHandler.class);
    verify(view).addScoreValueHandler(handler.capture());

    handler.getValue().scoreValue("-4.3");

    verify(this.view).setErrorInScore(true);

    this.bus.post(new POASubmissionSelected(submission));

    verify(this.view).setErrorInScore(false);
  }

  @Test
  public void scoreShouldBeClearedWhenNewSubmissionIsSelected() {
    ArgumentCaptor<POAGradeView.ScoreValueHandler> handler = ArgumentCaptor.forClass(POAGradeView.ScoreValueHandler.class);
    verify(view).addScoreValueHandler(handler.capture());

    handler.getValue().scoreValue("-4.3");

    this.bus.post(new POASubmissionSelected(submission));

    verify(this.view).setScore("");
    assertThat(this.presenter.getScore(), nullValue());
  }

  @Test
  public void scoreAndErrorShouldBeClearedWhenNewStudentIsSelected() {
    ArgumentCaptor<POAGradeView.ScoreValueHandler> handler = ArgumentCaptor.forClass(POAGradeView.ScoreValueHandler.class);
    verify(view).addScoreValueHandler(handler.capture());

    handler.getValue().scoreValue("-4.3");
    verify(this.view).setErrorInScore(true);

    this.bus.post(new StudentSelectedEvent(student));

    verify(this.view).setErrorInScore(false);
    verify(this.view).setScore("");
    assertThat(this.presenter.getScore(), nullValue());
  }

  @Test
  public void scoreAndErrorShouldBeClearedWhenNewAssignmentIsSelected() {
    ArgumentCaptor<POAGradeView.ScoreValueHandler> handler = ArgumentCaptor.forClass(POAGradeView.ScoreValueHandler.class);
    verify(view).addScoreValueHandler(handler.capture());

    handler.getValue().scoreValue("-4.3");
    verify(this.view).setErrorInScore(true);

    this.bus.post(new GradeBookLoaded(book));
    this.bus.post(new AssignmentSelectedEvent(assignment));

    verify(this.view).setErrorInScore(false);
    verify(this.view).setScore("");
    assertThat(this.presenter.getScore(), nullValue());
  }

  @Test
  public void scoreDefaultsToTotalPoints() {
    postEventsToBus();

    verify(this.view).setScore(POAGradePresenter.formatTotalPoints(assignment.getPoints()));
  }

  private void postEventsToBus() {
    this.bus.post(new GradeBookLoaded(book));
    this.bus.post(new POASubmissionSelected(submission));
    this.bus.post(new StudentSelectedEvent(student));
    this.bus.post(new AssignmentSelectedEvent(assignment));
  }

  @Test
  public void aScoreOfEmptyStringShouldNotBeAnError() {
    ArgumentCaptor<POAGradeView.ScoreValueHandler> handler = ArgumentCaptor.forClass(POAGradeView.ScoreValueHandler.class);
    verify(view).addScoreValueHandler(handler.capture());

    handler.getValue().scoreValue("");

    verify(this.view).setErrorInScore(false);
    assertThat(presenter.getScore(), nullValue());
  }

  @Test
  public void aScoreThatIsGreaterThanTheTotalPointsShouldBeAnError() {
    ArgumentCaptor<POAGradeView.ScoreValueHandler> handler = ArgumentCaptor.forClass(POAGradeView.ScoreValueHandler.class);
    verify(view).addScoreValueHandler(handler.capture());

    this.bus.post(new GradeBookLoaded(book));
    this.bus.post(new AssignmentSelectedEvent(assignment));

    handler.getValue().scoreValue(String.valueOf(assignment.getPoints() + 1.0d));

    verify(this.view).setErrorInScore(true);
    assertThat(presenter.getScore(), nullValue());
  }

  @Test
  public void scoreDefaultsToCurrentGrade() {
    double score = 0.75;
    student.setGrade(assignment, new Grade(assignment, score));

    postEventsToBus();

    verify(this.view).setScore(POAGradePresenter.formatTotalPoints(score));
  }

  @Test
  public void scoreDefaultsToTotalPointsWhenNoStudentIsSelected() {
    this.bus.post(new GradeBookLoaded(book));
    this.bus.post(new AssignmentSelectedEvent(assignment));

    verify(this.view).setScore(POAGradePresenter.formatTotalPoints(assignment.getPoints()));
  }

  @Test
  public void scoreHasNotBeenRecordedWhenNoStudentIsSelected() {
    this.bus.post(new GradeBookLoaded(book));
    this.bus.post(new AssignmentSelectedEvent(assignment));

    verify(this.view).setScoreHasBeenRecorded(false);
  }

  @Test
  public void scoreHasNotBeenRecordedWhenThereIsNoGradeInGradeBook() {
    assertThat(student.getGrade(assignment), nullValue());

    postEventsToBus();

    verify(this.view).setScoreHasBeenRecorded(false);
  }

  @Test
  public void scoreHasBeenRecordedForSelectedStudentWhenItIsInGradeBook() {
    double score = 0.75;
    student.setGrade(assignment, new Grade(assignment, score));

    postEventsToBus();

    verify(this.view).setScoreHasBeenRecorded(true);
  }

  @Ignore
  @Test
  public void scoreHasBeenRecordedShouldBeFalseWhenNewSubmissionIsSelected() {

  }

  @Ignore
  @Test
  public void clearingScoreMakesItSoGradeCanNotBeRecorded() {

  }
}
