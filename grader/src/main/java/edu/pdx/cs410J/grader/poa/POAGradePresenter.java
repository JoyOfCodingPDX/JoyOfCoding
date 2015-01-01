package edu.pdx.cs410J.grader.poa;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import edu.pdx.cs410J.grader.Assignment;
import edu.pdx.cs410J.grader.Student;

@Singleton
public class POAGradePresenter {
  private final EventBus bus;
  private final POAGradeView view;
  private POASubmission submission;
  private Assignment assignment;
  private Student student;

  @Inject
  public POAGradePresenter(EventBus bus, POAGradeView view) {
    this.bus = bus;
    this.view = view;

    this.bus.register(this);
  }

  @Subscribe
  public void determineIfSubmissionIsLate(POASubmissionSelected event) {
    this.submission = event.getSubmission();

    determineIfPOAIsLate();
  }

  @Subscribe
  public void determineIfStudentsPOAIsLate(StudentSelectedEvent event) {
    this.student = event.getSelectedStudent();

    determineIfPOAIsLate();
  }

  @Subscribe
  public void determineIfAssignmentIsLate(AssignmentSelectedEvent event) {
    this.assignment = event.getAssignment();

    determineIfPOAIsLate();
  }

  private void determineIfPOAIsLate() {
    if (this.submission != null && this.assignment != null && this.student != null) {
      enableView();

      if (this.assignment.isSubmissionLate(this.submission.getSubmitTime())) {
        this.view.setIsLate(true);

      } else {
        this.view.setIsLate(false);
      }

    } else {
      disableView();
    }
  }

  private void enableView() {
    this.view.setIsEnabled(true);
  }

  private void disableView() {
    this.view.setIsLate(false);
    this.view.setIsEnabled(false);
  }
}
