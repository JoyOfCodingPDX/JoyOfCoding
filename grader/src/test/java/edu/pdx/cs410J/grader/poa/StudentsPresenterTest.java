package edu.pdx.cs410J.grader.poa;

import com.google.common.eventbus.Subscribe;
import edu.pdx.cs410J.grader.GradeBook;
import edu.pdx.cs410J.grader.Student;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.util.Arrays;

import static edu.pdx.cs410J.grader.poa.StudentsView.SelectStudentHandler;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class StudentsPresenterTest extends EventBusTestCase {

  private StudentsView view;
  private GradeBook book;
  private Student student0;
  private Student student1;
  private Student student2;

  @Override
  public void setUp() {
    super.setUp();

    this.view = mock(StudentsView.class);
    new StudentsPresenter(this.bus, this.view);

    this.book = new GradeBook("Test Grade Book");

    student0 = new Student("0").setFirstName("First0").setLastName("Last0").setEmail("email0@mail.com");
    book.addStudent(student0);
    student1 = new Student("1").setFirstName("First1").setLastName("Last1").setEmail("email1@mail.com");
    book.addStudent(student1);
    student2 = new Student("2").setFirstName("First2").setLastName("Last2").setEmail("email2@mail.com");
    book.addStudent(student2);

  }

  @Test
  public void viewIsPopulatedWhenGradeBookLoaded() {
    this.bus.post(new GradeBookLoaded(book));

    verify(this.view).setStudents(Arrays.asList("First0 Last0 <email0@mail.com>", "First1 Last1 <email1@mail.com>", "First2 Last2 <email2@mail.com>"));
  }

  @Test
  public void firstStudentIsSelectedWhenGradeBookLoaded() {
    this.bus.post(new GradeBookLoaded(book));

    verify(this.view).setSelectedStudentIndex(0);
  }

  @Test
  public void selectingStudentInViewFiresStudentSelectedEvent() {
    ArgumentCaptor<SelectStudentHandler> viewHandler = ArgumentCaptor.forClass(SelectStudentHandler.class);
    verify(this.view).addSelectStudentHandler(viewHandler.capture());

    StudentSelectedEventHandler eventHandler = mock(StudentSelectedEventHandler.class);
    this.bus.register(eventHandler);

    this.bus.post(new GradeBookLoaded(book));

    viewHandler.getValue().studentSelected(1);

    ArgumentCaptor<StudentSelectedEvent> event = ArgumentCaptor.forClass(StudentSelectedEvent.class);
    verify(eventHandler).handle(event.capture());

    assertThat(event.getValue().getSelectedStudent(), equalTo(student1));
  }

  private interface StudentSelectedEventHandler {
    @Subscribe
    void handle(StudentSelectedEvent event);
  }

  // submissionByNonMatchingStudentDoesNotFireStudentSelectedEvent

  // submissionWithMatchingStudentNameFiresStudentSelectedEvent

  // submissionWithMatchingStudentEmailFiresStudentSelectedEvent
}
