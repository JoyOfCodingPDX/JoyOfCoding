package edu.pdx.cs410J.grader.poa;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import edu.pdx.cs410J.grader.GradeBook;
import edu.pdx.cs410J.grader.Student;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Singleton
public class StudentsPresenter {
  private final EventBus bus;
  private final StudentsView view;
  private List<Student> students = Collections.emptyList();

  @Inject
  public StudentsPresenter(EventBus bus, StudentsView view) {
    this.bus = bus;
    this.view = view;

    this.bus.register(this);

    this.view.addSelectStudentHandler(this::fireSelectedStudentEventForStudentAtIndex);
  }

  private void fireSelectedStudentEventForStudentAtIndex(int index) {
    Student student = this.students.get(index);
    fireStudentSelectedEvent(student);
  }

  private void fireStudentSelectedEvent(Student student) {
    this.bus.post(new StudentSelectedEvent(student));
  }

  @Subscribe
  public void populateViewWithStudents(GradeBookLoaded event) {
    GradeBook book = event.getGradeBook();

    this.students = book.studentsStream().collect(Collectors.toList());

    List<String> studentsDisplayStrings = book.studentsStream()
      .sorted(sortStudentsByLastNameFirstNameEmail())
      .map(this::getStudentDisplayText)
      .collect(Collectors.toList());
    this.view.setStudents(studentsDisplayStrings);

    this.view.setSelectedStudentIndex(0);
  }

  private Comparator<Student> sortStudentsByLastNameFirstNameEmail() {
    return (student1, student2) -> {
      String lastName1 = student1.getLastName();
      String lastName2 = student2.getLastName();
      if (!lastName1.equals(lastName2)) {
        return lastName1.compareTo(lastName2);

      } else {
        String firstName1 = student1.getFirstName();
        String firstName2 = student2.getFirstName();
        if (!firstName1.equals(firstName2)) {
          return firstName1.compareTo(firstName2);

        } else {
          return student1.getEmail().compareTo(student2.getEmail());
        }
      }
    };
  }

  private String getStudentDisplayText(Student student) {
    return student.getFirstName() + " " + student.getLastName() + " <" + student.getEmail() + ">";
  }

  @Subscribe
  public void attemptToMatchSubmissionWithStudent(POASubmissionSelected selected) {
    for (int i = 0; i < students.size(); i++) {
      Student student = students.get(i);
      if (submitterMatchesStudent(selected.getSubmission(), student)) {
        this.view.setSelectedStudentIndex(i);
        fireStudentSelectedEvent(student);
        return;
      }
    }
  }

  private boolean submitterMatchesStudent(POASubmission submission, Student student) {
    String submitter = submission.getSubmitter();
    return submitterContainsStudentName(student, submitter) || submitterContainsStudentEmail(student, submitter);
  }

  private boolean submitterContainsStudentEmail(Student student, String submitter) {
    return submitter.contains(student.getEmail());
  }

  private boolean submitterContainsStudentName(Student student, String submitter) {
    return submitter.contains(student.getFirstName()) && submitter.contains(student.getLastName());
  }

}
