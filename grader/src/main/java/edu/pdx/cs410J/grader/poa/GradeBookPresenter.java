package edu.pdx.cs410J.grader.poa;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import edu.pdx.cs410J.grader.Assignment;
import edu.pdx.cs410J.grader.GradeBook;
import edu.pdx.cs410J.grader.Student;

import java.io.File;

public class GradeBookPresenter {
  private final GradeBookView view;
  private final EventBus bus;
  private GradeBook gradeBook;

  @Inject
  public GradeBookPresenter(EventBus bus, GradeBookView view) {
    this.bus = bus;
    this.view = view;

    bus.register(this);

    view.addGradeBookFileListener(this::publishLoadGradeBookEvent);
    view.addSaveGradeBookListener(this::publishSaveGradeBookEvent);
  }

  private void publishSaveGradeBookEvent() {
    this.bus.post(new SaveGradeBook(this.gradeBook));
  }

  private void publishLoadGradeBookEvent(File file) {
    this.bus.post(new LoadGradeBook(file));
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
