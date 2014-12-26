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
  private List<Student> students;

  public StudentsPresenter(EventBus bus, StudentsView view) {
    this.bus = bus;
    this.view = view;

    this.bus.register(this);

    this.view.addSelectStudentHandler(this::fireSelectedStudentEventForStudentAtIndex);
  }

  private void fireSelectedStudentEventForStudentAtIndex(int index) {
    Student student = this.students.get(index);
    this.bus.post(new StudentSelectedEvent(student));
  }

  @Subscribe
  public void populateViewWithStudents(GradeBookLoaded event) {
    GradeBook book = event.getGradeBook();

    this.students = book.studentsStream().collect(Collectors.toList());

    List<String> studentsDisplayStrings = book.studentsStream().map(this::getStudentDisplayText).collect(Collectors.toList());
    this.view.setStudents(studentsDisplayStrings);

    this.view.setSelectedStudentIndex(0);
  }

  private String getStudentDisplayText(Student student) {
    return student.getFirstName() + " " + student.getLastName() + " <" + student.getEmail() + ">";
  }
}
