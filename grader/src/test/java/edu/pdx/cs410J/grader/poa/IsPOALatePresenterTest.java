package edu.pdx.cs410J.grader.poa;

import edu.pdx.cs410J.grader.Assignment;
import edu.pdx.cs410J.grader.Student;
import org.junit.Test;

import java.time.LocalDateTime;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class IsPOALatePresenterTest extends EventBusTestCase {

  private IsPOALateView view;

  @Override
  public void setUp() {
    super.setUp();

    this.view = mock(IsPOALateView.class);

    new IsPOALatePresenter(this.bus, this.view);
  }

  @Test
  public void onlySubmissionSelectedDisablesView() {
    POASubmission submission = new POASubmission("Subject", "Submitter", LocalDateTime.now());
    this.bus.post(new POASubmissionSelected(submission));

    verify(this.view).setIsLate(eq(false));
    verify(this.view).setIsEnabled(eq(false));
  }

  @Test
  public void onlyStudentSelectedDisablesView() {
    Student student = new Student("id");
    this.bus.post(new StudentSelectedEvent(student));

    verify(this.view).setIsLate(eq(false));
    verify(this.view).setIsEnabled(eq(false));
  }

  @Test
  public void onlyAssignmentSelectedDisablesView() {
    Assignment assignment = new Assignment("assignment", 1.0);
    this.bus.post(new AssignmentSelectedEvent(assignment));

    verify(this.view).setIsLate(eq(false));
    verify(this.view).setIsEnabled(eq(false));
  }


  // lateSubmissionIsLateInView

  // selectedAssignmentWithLateAssignmentIsLateInView

  // onTimeSubmissionIsNotLateInView

  // changingAssignmentMakesOnTimeAssignmentNotLate
}
