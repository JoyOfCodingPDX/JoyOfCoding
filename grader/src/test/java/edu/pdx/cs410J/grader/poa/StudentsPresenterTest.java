package edu.pdx.cs410J.grader.poa;

import edu.pdx.cs410J.grader.GradeBook;
import edu.pdx.cs410J.grader.Student;
import org.junit.Test;

import java.util.Arrays;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class StudentsPresenterTest extends EventBusTestCase {

  private StudentsView view;

  @Override
  public void setUp() {
    super.setUp();

    this.view = mock(StudentsView.class);
    new StudentsPresenter(this.bus, this.view);
  }

  @Test
  public void viewIsPopulatedWhenGradeBookLoaded() {
    GradeBook book = new GradeBook("Test Grade Book");
    book.addStudent(new Student("1").setFirstName("First1").setLastName("Last1").setEmail("email1@mail.com"));
    book.addStudent(new Student("2").setFirstName("First2").setLastName("Last2").setEmail("email2@mail.com"));
    book.addStudent(new Student("3").setFirstName("First3").setLastName("Last3").setEmail("email3@mail.com"));

    this.bus.post(new GradeBookLoaded(book));

    verify(this.view).setStudents(Arrays.asList("First1 Last1 <email1@mail.com>", "First2 Last2 <email2@mail.com>", "First3 Last3 <email3@mail.com>"));
  }

  // firstStudentIsSelectedWhenGradeBookLoaded

  // selectingStudentInViewFiresStudentSelectedEvent

  // submissionByNonMatchingStudentDoesNotFireStudentSelectedEvent

  // submissionWithMatchingStudentNameFiresStudentSelectedEvent

  // submissionWithMatchingStudentEmailFiresStudentSelectedEvent
}
