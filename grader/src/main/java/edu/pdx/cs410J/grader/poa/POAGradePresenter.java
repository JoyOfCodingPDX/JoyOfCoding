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
  private boolean isLate;

  @Inject
  public POAGradePresenter(EventBus bus, POAGradeView view) {
    this.bus = bus;
    this.view = view;

    this.bus.register(this);

    this.view.addIsLateHandler(POAGradePresenter.this::setIsLate);
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

    this.view.setTotalPoints(formatTotalPoints(this.assignment.getPoints()));
  }

  private void determineIfPOAIsLate() {
    if (this.submission != null && this.assignment != null && this.student != null) {
      enableView();

      if (this.assignment.isSubmissionLate(this.submission.getSubmitTime())) {
        setIsLate(true);

      } else {
        setIsLate(false);
      }

    } else {
      disableView();
    }
  }

  private void setIsLate(boolean isLate) {
    this.isLate = isLate;
    this.view.setIsLate(isLate);
  }

  private void enableView() {
    this.view.setIsEnabled(true);
  }

  private void disableView() {
    setIsLate(false);
    this.view.setIsEnabled(false);
  }

  public boolean isLate() {
    return this.isLate;
  }

  public static String formatTotalPoints(double points) {
    return String.format("%.2f", points);
  }
}
