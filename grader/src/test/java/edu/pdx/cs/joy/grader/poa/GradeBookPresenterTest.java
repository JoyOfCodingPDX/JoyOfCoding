package edu.pdx.cs.joy.grader.poa;

import com.google.common.eventbus.Subscribe;
import edu.pdx.cs.joy.grader.gradebook.Assignment;
import edu.pdx.cs.joy.grader.gradebook.Grade;
import edu.pdx.cs.joy.grader.gradebook.GradeBook;
import edu.pdx.cs.joy.grader.gradebook.Student;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.ArgumentCaptor;

import java.io.File;
import java.io.IOException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class GradeBookPresenterTest extends EventBusTestCase {

  private GradeBookView view;

  @Override
  @BeforeEach
  public void setUp() {
    super.setUp();

    view = mock(GradeBookView.class);
    new GradeBookPresenter(this.bus, this.view);
  }

  @Test
  public void gradeBookNameDisplayedWhenLoaded() {
    String className = "Test Class";
    GradeBook book = new GradeBook(className);
    bus.post(new GradeBookLoaded(book));

    verify(view).setGradeBookName(className);
  }

  @Test
  public void loadGradeBookEventPublishedWhenFileProvidedByView(@TempDir File tempDir) throws IOException {
    LoadGradeBookHandler handler = mock(LoadGradeBookHandler.class);
    bus.register(handler);

    ArgumentCaptor<GradeBookView.FileSelectedListener> listener = ArgumentCaptor.forClass(GradeBookView.FileSelectedListener.class);
    verify(view).addGradeBookFileListener(listener.capture());

    File file = new File(tempDir, "testGradeBook.xml");
    listener.getValue().fileSelected(file);

    ArgumentCaptor<LoadGradeBook> event = ArgumentCaptor.forClass(LoadGradeBook.class);
    verify(handler).handle(event.capture());

    assertThat(event.getValue().getFile(), equalTo(file));
  }

  private interface LoadGradeBookHandler {
    @Subscribe
    public void handle(LoadGradeBook loadGradeBook);
  }

  @Test
  public void gradeBookIsUpdatedOnRecordGradeEventForLateSubmission() {
    GradeBook book = new GradeBook("Test");
    Student student = new Student("studentId");
    book.addStudent(student);
    Assignment poa = new Assignment("poa", 1.0);
    book.addAssignment(poa);
    bus.post(new GradeBookLoaded(book));

    boolean isLate = true;
    double score = 0.5;
    this.bus.post(new RecordGradeEvent(score, student, poa, isLate));

    Grade grade = student.getGrade(poa.getName());
    assertThat(grade, notNullValue());
    assertThat(grade.getScore(), equalTo(score));
    assertThat(student.getLate(), contains(poa.getName()));

  }

  @Test
  public void gradeBookIsUpdatedOnRecordGradeEventForOnTimeSubmission() {
    GradeBook book = new GradeBook("Test");
    Student student = new Student("studentId");
    book.addStudent(student);
    Assignment poa = new Assignment("poa", 1.0);
    book.addAssignment(poa);
    bus.post(new GradeBookLoaded(book));

    boolean isLate = false;
    double score = 0.6;
    this.bus.post(new RecordGradeEvent(score, student, poa, isLate));

    Grade grade = student.getGrade(poa.getName());
    assertThat(grade, notNullValue());
    assertThat(grade.getScore(), equalTo(score));
    assertThat(student.getLate(), not(contains(poa.getName())));
  }

  @Test
  public void gradeBookIsUpdatedOnRecordGradeEventForExistingGrade() {
    GradeBook book = new GradeBook("Test");
    Student student = new Student("studentId");
    book.addStudent(student);
    Assignment poa = new Assignment("poa", 1.0);
    book.addAssignment(poa);
    student.setGrade(poa, 0.9);
    bus.post(new GradeBookLoaded(book));

    boolean isLate = false;
    double newScore = 0.6;
    this.bus.post(new RecordGradeEvent(newScore, student, poa, isLate));

    Grade grade = student.getGrade(poa.getName());
    assertThat(grade, notNullValue());
    assertThat(grade.getScore(), equalTo(newScore));
    assertThat(student.getLate(), not(contains(poa.getName())));
  }

  @Test
  public void recordingGradeEnablesSaveGradeBookInView() {
    GradeBook book = new GradeBook("Test");
    Student student = new Student("studentId");
    book.addStudent(student);
    Assignment poa = new Assignment("poa", 1.0);
    book.addAssignment(poa);
    student.setGrade(poa, 0.9);
    bus.post(new GradeBookLoaded(book));

    verify(view).canSaveGradeBook(false);

    boolean isLate = false;
    double newScore = 0.6;
    this.bus.post(new RecordGradeEvent(newScore, student, poa, isLate));

    verify(view).canSaveGradeBook(true);
  }

  @Test
  public void savingGradeBookInViewPublishesSaveGradeBookEvent() throws IOException {
    ArgumentCaptor<GradeBookView.SaveGradeBookListener> listener = ArgumentCaptor.forClass(GradeBookView.SaveGradeBookListener.class);
    verify(this.view).addSaveGradeBookListener(listener.capture());

    GradeBook book = new GradeBook("Test");
    Student student = new Student("studentId");
    book.addStudent(student);
    Assignment poa = new Assignment("poa", 1.0);
    book.addAssignment(poa);
    student.setGrade(poa, 0.9);
    bus.post(new GradeBookLoaded(book));

    SaveGradeBookEventHandler eventHandler = mock(SaveGradeBookEventHandler.class);
    bus.register(eventHandler);

    listener.getValue().saveGradeBook();

    ArgumentCaptor<SaveGradeBook> event = ArgumentCaptor.forClass(SaveGradeBook.class);
    verify(eventHandler).saveGradeBook(event.capture());

    assertThat(event.getValue().getGradeBook(), equalTo(book));
  }

  public interface SaveGradeBookEventHandler {
    @Subscribe
    public void saveGradeBook(SaveGradeBook event);
  }
}
