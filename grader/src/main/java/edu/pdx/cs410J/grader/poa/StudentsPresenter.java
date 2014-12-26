package edu.pdx.cs410J.grader.poa;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import edu.pdx.cs410J.grader.GradeBook;
import edu.pdx.cs410J.grader.Student;

import java.util.List;
import java.util.stream.Collectors;

public class StudentsPresenter {
  private final EventBus bus;
  private final StudentsView view;

  public StudentsPresenter(EventBus bus, StudentsView view) {
    this.bus = bus;
    this.view = view;

    this.bus.register(this);
  }

  @Subscribe
  public void populateViewWithStudents(GradeBookLoaded event) {
    GradeBook book = event.getGradeBook();

    List<String> students = book.studentsStream().map(this::getStudentDisplayText).collect(Collectors.toList());
    this.view.setStudents(students);
  }

  private String getStudentDisplayText(Student student) {
    return student.getFirstName() + " " + student.getLastName() + " <" + student.getEmail() + ">";
  }
}
