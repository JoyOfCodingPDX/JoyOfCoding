package edu.pdx.cs410J.grader.poa;

import com.google.common.eventbus.Subscribe;
import edu.pdx.cs410J.grader.Assignment;
import edu.pdx.cs410J.grader.Grade;
import edu.pdx.cs410J.grader.GradeBook;
import edu.pdx.cs410J.grader.Student;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;

import java.time.LocalDateTime;

import static edu.pdx.cs410J.grader.poa.POAGradePresenter.formatTotalPoints;
import static edu.pdx.cs410J.grader.poa.POAGradeView.RecordGradeHandler;
import static edu.pdx.cs410J.grader.poa.POAGradeView.ScoreValueHandler;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

public class POAGradePresenterTest extends POASubmissionTestCase {

  private final double scoreForPOAWithGrade = 1.5;
  private POAGradeView view;
  private POASubmission submission;
  private Student student;
  private Assignment assignment;
  private GradeBook book;
  private POAGradePresenter presenter;
  private POASubmission submissionForPOAWithGrade;
  private Assignment assignmentWithPOAGrade;

  @Before
  @Override
  public void setUp() {
    super.setUp();

    this.view = mock(POAGradeView.class);

    presenter = new POAGradePresenter(this.bus, this.view);
    submission = createPOASubmission("Subject", "Submitter", LocalDateTime.now());
    student = new Student("id");
    assignment = new Assignment("assignment", 1.0);

    book = new GradeBook("POAGradePresenterTest");
    book.addAssignment(assignment);
    book.addStudent(student);

    submissionForPOAWithGrade = createPOASubmission("POA for Project with Grade", "Submitter", LocalDateTime.now());
    assignmentWithPOAGrade = new Assignment("assignmentWithPOAGrade", 2.0);
    book.addAssignment(assignmentWithPOAGrade);
    student.setGrade(assignmentWithPOAGrade, scoreForPOAWithGrade);
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
    verifyIsLate(wantedNumberOfInvocations, false);
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
    ArgumentCaptor<ScoreValueHandler> handler = ArgumentCaptor.forClass(ScoreValueHandler.class);
    verify(view).addScoreValueHandler(handler.capture());

    handler.getValue().scoreValue("Not an Integer");

    verify(this.view).setErrorInScore(true);
    assertThat(presenter.getScore(), nullValue());
  }

  @Test
  public void setPresentersScoreFromView() {
    ArgumentCaptor<ScoreValueHandler> handler = ArgumentCaptor.forClass(ScoreValueHandler.class);
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
    ArgumentCaptor<ScoreValueHandler> handler = ArgumentCaptor.forClass(ScoreValueHandler.class);
    verify(view).addScoreValueHandler(handler.capture());

    handler.getValue().scoreValue("-4.3");

    verify(this.view).setErrorInScore(true);
    assertThat(presenter.getScore(), nullValue());
  }

  @Test
  public void errorStateShouldBeClearedWhenNewSubmissionIsSelected() {
    ArgumentCaptor<ScoreValueHandler> handler = ArgumentCaptor.forClass(ScoreValueHandler.class);
    verify(view).addScoreValueHandler(handler.capture());

    handler.getValue().scoreValue("-4.3");

    verify(this.view).setErrorInScore(true);

    this.bus.post(new POASubmissionSelected(submission));

    verify(this.view).setErrorInScore(false);
  }

  @Test
  public void scoreShouldBeClearedWhenNewSubmissionIsSelected() {
    ArgumentCaptor<ScoreValueHandler> handler = ArgumentCaptor.forClass(ScoreValueHandler.class);
    verify(view).addScoreValueHandler(handler.capture());

    handler.getValue().scoreValue("-4.3");

    this.bus.post(new POASubmissionSelected(submission));

    verify(this.view).setScore("");
    assertThat(this.presenter.getScore(), nullValue());
  }

  @Test
  public void scoreAndErrorShouldBeClearedWhenNewStudentIsSelected() {
    ArgumentCaptor<ScoreValueHandler> handler = ArgumentCaptor.forClass(ScoreValueHandler.class);
    verify(view).addScoreValueHandler(handler.capture());

    handler.getValue().scoreValue("-4.3");
    verify(this.view).setErrorInScore(true);

    this.bus.post(new StudentSelectedEvent(student));

    verify(this.view).setErrorInScore(false);
    verify(this.view, times(2)).setScore("");
    assertThat(this.presenter.getScore(), nullValue());
  }

  @Test
  public void scoreAndErrorShouldBeClearedWhenNewAssignmentIsSelected() {
    ArgumentCaptor<ScoreValueHandler> handler = ArgumentCaptor.forClass(ScoreValueHandler.class);
    verify(view).addScoreValueHandler(handler.capture());

    handler.getValue().scoreValue("-4.3");
    verify(this.view).setErrorInScore(true);

    this.bus.post(new GradeBookLoaded(book));
    this.bus.post(new AssignmentSelectedEvent(assignment));

    verify(this.view, times(2)).setErrorInScore(false);
    verify(this.view).setScore(formatTotalPoints(assignment.getPoints()));
    assertThat(this.presenter.getScore(), equalTo(assignment.getPoints()));
  }

  @Test
  public void scoreDefaultsToTotalPoints() {
    postEventsToBus();

    verify(this.view).setScore(formatTotalPoints(assignment.getPoints()));
  }

  private void postEventsToBus() {
    this.bus.post(new GradeBookLoaded(book));
    this.bus.post(new POASubmissionSelected(submission));
    this.bus.post(new StudentSelectedEvent(student));
    this.bus.post(new AssignmentSelectedEvent(assignment));
  }

  @Test
  public void aScoreOfEmptyStringShouldNotBeAnError() {
    ArgumentCaptor<ScoreValueHandler> handler = ArgumentCaptor.forClass(ScoreValueHandler.class);
    verify(view).addScoreValueHandler(handler.capture());

    handler.getValue().scoreValue("");

    verify(this.view).setErrorInScore(false);
    assertThat(presenter.getScore(), nullValue());
  }

  @Test
  public void aScoreThatIsGreaterThanTheTotalPointsShouldBeAnError() {
    ArgumentCaptor<ScoreValueHandler> handler = ArgumentCaptor.forClass(ScoreValueHandler.class);
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

    verify(this.view).setScore(formatTotalPoints(score));
  }

  @Test
  public void scoreDefaultsToTotalPointsWhenNoStudentIsSelected() {
    this.bus.post(new GradeBookLoaded(book));
    this.bus.post(new AssignmentSelectedEvent(assignment));

    verify(this.view).setScore(formatTotalPoints(assignment.getPoints()));
  }

  @Test
  public void scoreHasNotBeenRecordedWhenNoStudentIsSelected() {
    this.bus.post(new GradeBookLoaded(book));
    this.bus.post(new AssignmentSelectedEvent(assignment));

    verify(this.view, times(2)).setScoreHasBeenRecorded(false);
  }

  @Test
  public void scoreHasNotBeenRecordedWhenThereIsNoGradeInGradeBook() {
    assertThat(student.getGrade(assignment), nullValue());

    postEventsToBus();

    verify(this.view, times(5)).setScoreHasBeenRecorded(false);
  }

  @Test
  public void scoreHasBeenRecordedForSelectedStudentWhenItIsInGradeBook() {
    double score = 0.75;
    student.setGrade(assignment, new Grade(assignment, score));

    postEventsToBus();

    verify(this.view).setScoreHasBeenRecorded(true);
  }

  @Test
  public void scoreHasBeenRecordedShouldBeFalseWhenNewSubmissionIsSelected() {
    this.bus.post(new POASubmissionSelected(submission));

    verify(this.view).setScoreHasBeenRecorded(false);
  }

  @Test
  public void scoreHasBeenRecordedShouldBeFalseWhenNewStudentIsSelected() {
    this.bus.post(new StudentSelectedEvent(student));

    verify(this.view, times(2)).setScoreHasBeenRecorded(false);
  }

  @Test
  public void recordingGradeInViewPublishesRecordGradeMessage() {
    ArgumentCaptor<ScoreValueHandler> scoreHandler = ArgumentCaptor.forClass(ScoreValueHandler.class);
    verify(this.view).addScoreValueHandler(scoreHandler.capture());

    ArgumentCaptor<RecordGradeHandler> recordGrade = ArgumentCaptor.forClass(RecordGradeHandler.class);
    verify(this.view).addRecordGradeHandler(recordGrade.capture());

    RecordGradeEventHandler eventHandler = mock(RecordGradeEventHandler.class);
    this.bus.register(eventHandler);

    this.assignment.setDueDate(LocalDateTime.now().minusDays(5));
    postEventsToBus();

    double score = 0.75;
    scoreHandler.getValue().scoreValue(formatTotalPoints(score));
    recordGrade.getValue().recordGrade();

    ArgumentCaptor<RecordGradeEvent> eventCaptor = ArgumentCaptor.forClass(RecordGradeEvent.class);
    verify(eventHandler).handleRecordGradeEvent(eventCaptor.capture());

    RecordGradeEvent event = eventCaptor.getValue();
    assertThat(event.getScore(), equalTo(score));
    assertThat(event.getStudent(), equalTo(this.student));
    assertThat(event.getAssignment(), equalTo(this.assignment));
    assertThat(event.isLate(), equalTo(true));

    verify(this.view).setScoreHasBeenRecorded(true);
  }

  @Test
  public void recordingGradeInViewPublishesDisplayNextPOAMessage() {
    ArgumentCaptor<ScoreValueHandler> scoreHandler = ArgumentCaptor.forClass(ScoreValueHandler.class);
    verify(this.view).addScoreValueHandler(scoreHandler.capture());

    ArgumentCaptor<RecordGradeHandler> recordGrade = ArgumentCaptor.forClass(RecordGradeHandler.class);
    verify(this.view).addRecordGradeHandler(recordGrade.capture());

    DisplayNextPOAEventHandler eventHandler = mock(DisplayNextPOAEventHandler.class);
    this.bus.register(eventHandler);

    this.assignment.setDueDate(LocalDateTime.now().minusDays(5));
    postEventsToBus();

    double score = 0.75;
    scoreHandler.getValue().scoreValue(formatTotalPoints(score));
    recordGrade.getValue().recordGrade();

    ArgumentCaptor<SelectNextPOAEvent> eventCaptor = ArgumentCaptor.forClass(SelectNextPOAEvent.class);
    verify(eventHandler).handleDisplayNextPOAEvent(eventCaptor.capture());
  }

  private interface RecordGradeEventHandler {
    @Subscribe
    void handleRecordGradeEvent(RecordGradeEvent event);
  }

  @Test
  public void currentGradeIsDisplayedWhenGradedSubmissionIsSelected() {
    this.bus.post(new GradeBookLoaded(book));
    this.bus.post(new POASubmissionSelected(submissionForPOAWithGrade));
    verify(this.view).setScore("");

    this.bus.post(new StudentSelectedEvent(student));
    verify(this.view, times(3)).setScore("");

    this.bus.post(new AssignmentSelectedEvent(assignmentWithPOAGrade));

    InOrder inOrder = inOrder(this.view);
    inOrder.verify(this.view).setScore(formatTotalPoints(scoreForPOAWithGrade));
  }

  @Test
  public void currentGradeIsDisplayedWhenStudentIsSelected() {
    this.bus.post(new GradeBookLoaded(book));
    this.bus.post(new POASubmissionSelected(submissionForPOAWithGrade));
    verify(this.view, times(1)).setScore("");

    this.bus.post(new StudentSelectedEvent(student));
    verify(this.view, times(3)).setScore("");

    this.bus.post(new AssignmentSelectedEvent(assignmentWithPOAGrade));

    InOrder inOrder = inOrder(this.view);
    inOrder.verify(this.view).setScore(formatTotalPoints(scoreForPOAWithGrade));

    this.bus.post(new StudentSelectedEvent(student));
    inOrder.verify(this.view).setScore(formatTotalPoints(scoreForPOAWithGrade));
  }

  @Test
  public void unknownStudentClearsUI() {
    postEventsToBus();
    verify(this.view, times(4)).setScore("");
    verifyIsLate(3, false);

    this.bus.post(new StudentSelectedEvent(null));

    verify(this.view, times(5)).setScore("");
    verifyIsLate(4, false);
  }

  private interface DisplayNextPOAEventHandler {
    @Subscribe
    public void handleDisplayNextPOAEvent(SelectNextPOAEvent event);
  }
}
