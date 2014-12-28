package edu.pdx.cs410J.grader.poa;

import edu.pdx.cs410J.grader.Assignment;
import edu.pdx.cs410J.grader.Student;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDateTime;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class IsPOALatePresenterTest extends EventBusTestCase {

  private IsPOALateView view;
  private POASubmission submission;
  private Student student;
  private Assignment assignment;

  @Before
  @Override
  public void setUp() {
    super.setUp();

    this.view = mock(IsPOALateView.class);

    new IsPOALatePresenter(this.bus, this.view);
    submission = new POASubmission("Subject", "Submitter", LocalDateTime.now());
    student = new Student("id");
    assignment = new Assignment("assignment", 1.0);
  }

  @Test
  public void onlySubmissionSelectedDisablesView() {
    this.bus.post(new POASubmissionSelected(submission));

    verify(this.view).setIsLate(eq(false));
    verify(this.view).setIsEnabled(eq(false));
  }

  @Test
  public void onlyStudentSelectedDisablesView() {
    this.bus.post(new StudentSelectedEvent(student));

    verify(this.view).setIsLate(eq(false));
    verify(this.view).setIsEnabled(eq(false));
  }

  @Test
  public void onlyAssignmentSelectedDisablesView() {
    this.bus.post(new AssignmentSelectedEvent(assignment));

    verify(this.view).setIsLate(eq(false));
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
    verify(this.view).setIsLate(true);
  }

  @Test
  public void onTimeSubmissionIsNotLateInView() {
    assignment.setDueDate(LocalDateTime.now().plusDays(5));

    this.bus.post(new POASubmissionSelected(submission));
    this.bus.post(new StudentSelectedEvent(student));
    this.bus.post(new AssignmentSelectedEvent(assignment));

    verify(this.view).setIsEnabled(true);
    verify(this.view, times(3)).setIsLate(eq(false));
  }

}
