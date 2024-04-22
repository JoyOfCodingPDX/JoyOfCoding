package edu.pdx.cs.joy.grader.poa;

import com.google.common.eventbus.Subscribe;
import edu.pdx.cs.joy.ParserException;
import edu.pdx.cs.joy.grader.gradebook.Assignment;
import edu.pdx.cs.joy.grader.gradebook.GradeBook;
import edu.pdx.cs.joy.grader.gradebook.XmlDumper;
import edu.pdx.cs.joy.grader.gradebook.XmlGradeBookParser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.ArgumentCaptor;

import java.io.File;
import java.io.IOException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.any;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class GradeBookFileManagerTest extends EventBusTestCase {

  @Override
  @BeforeEach
  public void setUp() {
    super.setUp();
    new GradeBookFileManager(this.bus);
  }

  @Test
  public void gradeBookLoadedOnLoadGradeBookEvent(@TempDir File tempDir) throws IOException {
    String gradeBookName = "Test Grade Book";
    File file = writeGradeBookToFile(tempDir, gradeBookName);

    GradeBookLoadedHandler handler = mock(GradeBookLoadedHandler.class);
    this.bus.register(handler);

    this.bus.post(new LoadGradeBook(file));

    ArgumentCaptor<GradeBookLoaded> event = ArgumentCaptor.forClass(GradeBookLoaded.class);
    verify(handler).handle(event.capture());

    assertThat(event.getValue().getGradeBook().getClassName(), equalTo(gradeBookName));
  }

  protected File writeGradeBookToFile(File tempDir, String gradeBookName) throws IOException {
    File file = new File(tempDir, "testGradeBook.xml");

    GradeBook book = new GradeBook(gradeBookName);
    XmlDumper dumper = new XmlDumper(file);
    dumper.dump(book);

    return file;
  }

  private interface GradeBookLoadedHandler {
    @Subscribe
    void handle(GradeBookLoaded event);
  }

  @Test
  public void unhandledExceptionEventPublishedWhenLoadingBadGradeBook(@TempDir File tempDir) throws IOException {
    File badFile = new File(tempDir, "badGradeBook.xml");

    UnhandledExceptionEventHandler handler = mock(UnhandledExceptionEventHandler.class);
    this.bus.register(handler);

    this.unhandledExceptionHandler = this::doNotFailTestWhenUnhandledExceptionEncountered;

    this.bus.post(new LoadGradeBook(badFile));

    ArgumentCaptor<UnhandledExceptionEvent> event = ArgumentCaptor.forClass(UnhandledExceptionEvent.class);
    verify(handler).handle(event.capture());

    assertThat(event.getValue().getUnhandledException(), any(Throwable.class));
  }

  private interface UnhandledExceptionEventHandler {
    @Subscribe
    void handle(UnhandledExceptionEvent event);
  }

  @Test
  public void gradeBookSavedToFileOnSaveGradeBookEvent(@TempDir File tempDir) throws IOException, ParserException {
    String gradeBookName = "Test Grade Book";
    File file = writeGradeBookToFile(tempDir, gradeBookName);

    GradeBookLoadedHandler handler = mock(GradeBookLoadedHandler.class);
    this.bus.register(handler);

    this.bus.post(new LoadGradeBook(file));

    ArgumentCaptor<GradeBookLoaded> event = ArgumentCaptor.forClass(GradeBookLoaded.class);
    verify(handler).handle(event.capture());

    GradeBook book = event.getValue().getGradeBook();
    Assignment poa = new Assignment("poa", 1.0);
    book.addAssignment(poa);

    this.bus.post(new SaveGradeBook(book));

    book = readGradeBookFromFile(file);
    assertThat(book.getAssignmentNames(), contains(poa.getName()));
  }

  private GradeBook readGradeBookFromFile(File file) throws IOException, ParserException {
    XmlGradeBookParser parser = new XmlGradeBookParser(file);
    return parser.parse();
  }

}
