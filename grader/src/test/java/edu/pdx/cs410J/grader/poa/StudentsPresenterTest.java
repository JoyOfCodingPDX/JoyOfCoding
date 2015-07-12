package edu.pdx.cs410J.grader.poa;

import com.google.common.eventbus.Subscribe;
import edu.pdx.cs410J.grader.GradeBook;
import edu.pdx.cs410J.grader.Student;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.time.LocalDateTime;
import java.util.Arrays;

import static edu.pdx.cs410J.grader.poa.StudentsView.SelectStudentHandler;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.*;

public class StudentsPresenterTest extends POASubmissionTestCase {

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

    verify(this.view).setStudents(Arrays.asList("<unknown student>", "First0 Last0 <email0@mail.com>", "First1 Last1 <email1@mail.com>", "First2 Last2 <email2@mail.com>"));
  }

  @Test
  public void studentsAreListedInAlphabeticalOrderByLastName() {
    GradeBook book = new GradeBook("Test In Alphabetical Order");
    book.addStudent(new Student("4").setFirstName("First1").setLastName("Last1").setEmail("email1@mail.com"));
    book.addStudent(new Student("3").setFirstName("First3").setLastName("Last3").setEmail("email3@mail.com"));
    book.addStudent(new Student("1").setFirstName("First2").setLastName("Last2").setEmail("email2@mail.com"));

    this.bus.post(new GradeBookLoaded(book));

    verify(this.view).setStudents(Arrays.asList("<unknown student>", "First1 Last1 <email1@mail.com>", "First2 Last2 <email2@mail.com>", "First3 Last3 <email3@mail.com>"));
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

    viewHandler.getValue().studentSelected(2);

    ArgumentCaptor<StudentSelectedEvent> event = ArgumentCaptor.forClass(StudentSelectedEvent.class);
    verify(eventHandler).handle(event.capture());

    assertThat(event.getValue().getSelectedStudent(), equalTo(student1));
  }

  @Test
  public void selectingStudentInAlphabetizedViewFiresStudentSelectedEvent() {
    ArgumentCaptor<SelectStudentHandler> viewHandler = ArgumentCaptor.forClass(SelectStudentHandler.class);
    verify(this.view).addSelectStudentHandler(viewHandler.capture());

    StudentSelectedEventHandler eventHandler = mock(StudentSelectedEventHandler.class);
    this.bus.register(eventHandler);

    GradeBook book = new GradeBook("Test In Alphabetical Order");
    book.addStudent(new Student("4").setFirstName("First1").setLastName("Last1").setEmail("email1@mail.com"));
    book.addStudent(new Student("3").setFirstName("First3").setLastName("Last3").setEmail("email3@mail.com"));

    Student student2 = new Student("1").setFirstName("First2").setLastName("Last2").setEmail("email2@mail.com");
    book.addStudent(student2);

    this.bus.post(new GradeBookLoaded(book));

    viewHandler.getValue().studentSelected(2);

    ArgumentCaptor<StudentSelectedEvent> event = ArgumentCaptor.forClass(StudentSelectedEvent.class);
    verify(eventHandler).handle(event.capture());

    assertThat(event.getValue().getSelectedStudent(), equalTo(student2));
  }

  @Test
  public void selectingUnknownStudentInViewFiresNullStudentSelectedEvent() {
    ArgumentCaptor<SelectStudentHandler> viewHandler = ArgumentCaptor.forClass(SelectStudentHandler.class);
    verify(this.view).addSelectStudentHandler(viewHandler.capture());

    StudentSelectedEventHandler eventHandler = mock(StudentSelectedEventHandler.class);
    this.bus.register(eventHandler);

    this.bus.post(new GradeBookLoaded(book));

    viewHandler.getValue().studentSelected(0);

    ArgumentCaptor<StudentSelectedEvent> event = ArgumentCaptor.forClass(StudentSelectedEvent.class);
    verify(eventHandler).handle(event.capture());

    assertThat(event.getValue().getSelectedStudent(), equalTo(null));
  }

  private interface StudentSelectedEventHandler {
    @Subscribe
    void handle(StudentSelectedEvent event);
  }

  @Test
  public void submissionWithMatchingStudentNameFiresStudentSelectedEvent() {
    StudentSelectedEventHandler eventHandler = mock(StudentSelectedEventHandler.class);
    this.bus.register(eventHandler);

    this.bus.post(new GradeBookLoaded(this.book));

    POASubmission submission =
      createPOASubmission("Subject", student1.getFirstName() + " " + student1.getLastName() + " <wrong@mail.com>", LocalDateTime.now());
    this.bus.post(new POASubmissionSelected(submission));

    verify(this.view).setSelectedStudentIndex(2);

    ArgumentCaptor<StudentSelectedEvent> event = ArgumentCaptor.forClass(StudentSelectedEvent.class);
    verify(eventHandler).handle(event.capture());

    assertThat(event.getValue().getSelectedStudent(), equalTo(student1));
  }

  @Test
  public void submissionWithMatchingStudentEmailFiresStudentSelectedEvent() {
    StudentSelectedEventHandler eventHandler = mock(StudentSelectedEventHandler.class);
    this.bus.register(eventHandler);

    this.bus.post(new GradeBookLoaded(this.book));

    POASubmission submission =
      createPOASubmission("Subject", student2.getEmail(), LocalDateTime.now());
    this.bus.post(new POASubmissionSelected(submission));

    verify(this.view).setSelectedStudentIndex(3);

    ArgumentCaptor<StudentSelectedEvent> event = ArgumentCaptor.forClass(StudentSelectedEvent.class);
    verify(eventHandler).handle(event.capture());

    assertThat(event.getValue().getSelectedStudent(), equalTo(student2));
  }

  @Test
  public void submissionByNonMatchingStudentFiresUnknownStudentSelectedEvent() {
    StudentSelectedEventHandler eventHandler = mock(StudentSelectedEventHandler.class);
    this.bus.register(eventHandler);

    this.bus.post(new GradeBookLoaded(this.book));

    POASubmission submission =
      createPOASubmission("Subject", "Unknown Student <unknown@mail.com>", LocalDateTime.now());
    this.bus.post(new POASubmissionSelected(submission));

    verify(this.view, times(2)).setSelectedStudentIndex(0);

    ArgumentCaptor<StudentSelectedEvent> event = ArgumentCaptor.forClass(StudentSelectedEvent.class);
    verify(eventHandler).handle(event.capture());

    assertThat(event.getValue().getSelectedStudent(), equalTo(null));
  }

}
