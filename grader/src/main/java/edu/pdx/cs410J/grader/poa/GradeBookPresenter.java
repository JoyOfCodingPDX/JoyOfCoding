package edu.pdx.cs410J.grader.poa;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import edu.pdx.cs410J.grader.Assignment;
import edu.pdx.cs410J.grader.GradeBook;
import edu.pdx.cs410J.grader.Student;
import edu.pdx.cs410J.grader.mvp.PresenterOnEventBus;

import java.io.File;

public class GradeBookPresenter extends PresenterOnEventBus {
  private final GradeBookView view;
  private GradeBook gradeBook;

  @Inject
  public GradeBookPresenter(EventBus bus, GradeBookView view) {
    super(bus);
    this.view = view;

    view.addGradeBookFileListener(this::publishLoadGradeBookEvent);
    view.addSaveGradeBookListener(this::publishSaveGradeBookEvent);
  }

  private void publishSaveGradeBookEvent() {
    publishEvent(new SaveGradeBook(this.gradeBook));
  }

  private void publishLoadGradeBookEvent(File file) {
    publishEvent(new LoadGradeBook(file));
  }

  @Subscribe
  public void noteGradeBook(GradeBookLoaded event) {
    gradeBook = event.getGradeBook();
    this.view.setGradeBookName(gradeBook.getClassName());
    this.view.canSaveGradeBook(false);
  }

  @Subscribe
  public void recordGradeInGradeBook(RecordGradeEvent event) {
    Student student = event.getStudent();
    Assignment assignment = event.getAssignment();
    student.setGrade(assignment, event.getScore());

    if (event.isLate()) {
      student.addLate(assignment);
    }

    this.view.canSaveGradeBook(true);
  }
}
