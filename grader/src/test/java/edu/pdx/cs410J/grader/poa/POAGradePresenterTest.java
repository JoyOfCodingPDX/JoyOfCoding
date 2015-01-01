package edu.pdx.cs410J.grader.poa;

import edu.pdx.cs410J.grader.Assignment;
import edu.pdx.cs410J.grader.Student;
import org.junit.Before;
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
    this.bus.post(new AssignmentSelectedEvent(assignment));

    verifyIsLate(false);
    verify(this.view).setIsEnabled(eq(false));
  }

  @Test
  public void submissionStudentAndAssignmentEnablesView() {
    this.bus.post(new POASubmissionSelected(submission));
    this.bus.post(new StudentSelectedEvent(student));
    this.bus.post(new AssignmentSelectedEvent(assignment));

    verify(this.view).setIsEnabled(true);
  }

  @Test
  public void lateSubmissionIsLateInView() {
    assignment.setDueDate(LocalDateTime.now().minusDays(5));

    this.bus.post(new POASubmissionSelected(submission));
    this.bus.post(new StudentSelectedEvent(student));
    this.bus.post(new AssignmentSelectedEvent(assignment));

    verify(this.view).setIsEnabled(true);
    verifyIsLate(true);
  }

  @Test
  public void onTimeSubmissionIsNotLateInView() {
    assignment.setDueDate(LocalDateTime.now().plusDays(5));

    this.bus.post(new POASubmissionSelected(submission));
    this.bus.post(new StudentSelectedEvent(student));
    this.bus.post(new AssignmentSelectedEvent(assignment));

    verify(this.view).setIsEnabled(true);
    verifyIsLate(3, false);
  }

  @Test
  public void assignmentWithNoDueDateIsNotLate() {
    assignment.setDueDate(null);

    this.bus.post(new POASubmissionSelected(submission));
    this.bus.post(new StudentSelectedEvent(student));
    this.bus.post(new AssignmentSelectedEvent(assignment));

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

    this.bus.post(new POASubmissionSelected(submission));
    this.bus.post(new StudentSelectedEvent(student));
    this.bus.post(new AssignmentSelectedEvent(assignment));

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

}
