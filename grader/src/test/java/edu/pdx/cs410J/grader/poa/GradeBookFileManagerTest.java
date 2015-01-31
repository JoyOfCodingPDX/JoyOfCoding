package edu.pdx.cs410J.grader.poa;

import com.google.common.eventbus.Subscribe;
import edu.pdx.cs410J.grader.GradeBook;
import edu.pdx.cs410J.grader.XmlDumper;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.io.File;
import java.io.IOException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.any;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class GradeBookFileManagerTest extends EventBusTestCase {

  @Override
  public void setUp() {
    super.setUp();
    new GradeBookFileManager(this.bus);
  }

  @Test
  public void gradeBookLoadedOnLoadGradeBookEvent() throws IOException {
    String gradeBookName = "Test Grade Book";
    File file = writeGradeBookToFile(gradeBookName);

    GradeBookLoadedHandler handler = mock(GradeBookLoadedHandler.class);
    this.bus.register(handler);

    this.bus.post(new LoadGradeBook(file));

    ArgumentCaptor<GradeBookLoaded> event = ArgumentCaptor.forClass(GradeBookLoaded.class);
    verify(handler).handle(event.capture());

    assertThat(event.getValue().getGradeBook().getClassName(), equalTo(gradeBookName));
  }

  protected File writeGradeBookToFile(String gradeBookName) throws IOException {
    File file = File.createTempFile("testGradeBook", "xml");

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
  public void unhandledExceptionEventPublishedWhenLoadingBadGradeBook() throws IOException {
    File badFile = File.createTempFile("badGradeBook", "xml");

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
}
