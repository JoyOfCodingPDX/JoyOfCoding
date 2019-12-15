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
    Student student;
    if (index == 0) {
      student = null;

    } else {
      student = this.students.get(index - 1);
    }
    fireStudentSelectedEvent(student);
  }

  private void fireStudentSelectedEvent(Student student) {
    this.bus.post(new StudentSelectedEvent(student));
  }

  @Subscribe
  public void populateViewWithStudents(GradeBookLoaded event) {
    GradeBook book = event.getGradeBook();

    this.students = book.studentsStream()
      .sorted(sortStudentsByLastNameFirstNameEmail())
      .collect(Collectors.toList());

    List<String> studentsDisplayStrings = this.students.stream()
      .map(this::getStudentDisplayText)
      .collect(Collectors.toList());
    studentsDisplayStrings.add(0, "<unknown student>");
    this.view.setStudents(studentsDisplayStrings);

    this.view.setSelectedStudentIndex(0);
  }

  private Comparator<Student> sortStudentsByLastNameFirstNameEmail() {
    return (student1, student2) -> {
      String lastName1 = getLastNameOfStudent(student1);
      String lastName2 = getLastNameOfStudent(student2);
      if (!lastName1.equals(lastName2)) {
        return lastName1.compareTo(lastName2);

      } else {
        String firstName1 = getFirstNameOfStudent(student1);
        String firstName2 = getFirstNameOfStudent(student2);
        if (!firstName1.equals(firstName2)) {
          return firstName1.compareTo(firstName2);

        } else {
          return student1.getEmail().compareTo(student2.getEmail());
        }
      }
    };
  }

  private String getLastNameOfStudent(Student student) {
    String lastName = student.getLastName();
    if (lastName == null) {
      throw new IllegalStateException("Student " + student.getId() + " doesn't have a last name");
    }
    return lastName;
  }

  private String getStudentDisplayText(Student student) {
    return getFirstNameOfStudent(student) + " " + getLastNameOfStudent(student) + " <" + student.getEmail() + ">";
  }

  @Subscribe
  public void attemptToMatchSubmissionWithStudent(POASubmissionSelected selected) {
    for (int i = 0; i < students.size(); i++) {
      Student student = students.get(i);
      if (submitterMatchesStudent(selected.getSubmission(), student)) {
        this.view.setSelectedStudentIndex(i + 1);
        fireStudentSelectedEvent(student);
        return;
      }
    }

    this.view.setSelectedStudentIndex(0);
    fireStudentSelectedEvent(null);
  }

  private boolean submitterMatchesStudent(POASubmission submission, Student student) {
    String submitter = submission.getSubmitter();
    return submitterContainsStudentName(student, submitter) || submitterContainsStudentEmail(student, submitter);
  }

  private boolean submitterContainsStudentEmail(Student student, String submitter) {
    return submitter.contains(student.getEmail());
  }

  private boolean submitterContainsStudentName(Student student, String submitter) {
    return submitter.contains(getFirstNameOfStudent(student)) && submitter.contains(getLastNameOfStudent(student));
  }

  private String getFirstNameOfStudent(Student student) {
    String firstName = student.getFirstName();
    if (firstName == null) {
      throw new IllegalStateException("Student " + student.getId() + " doesn't have a first name");
    }
    return firstName;
  }

}
