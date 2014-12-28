package edu.pdx.cs410J.grader.poa;

import com.google.common.eventbus.Subscribe;
import edu.pdx.cs410J.grader.GradeBook;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.io.File;
import java.io.IOException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class GradeBookPresenterTest extends EventBusTestCase{

  private GradeBookView view;

  @Override
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
  public void loadGradeBookEventPublishedWhenFileProvidedByView() throws IOException {
    LoadGradeBookHandler handler = mock(LoadGradeBookHandler.class);
    bus.register(handler);

    ArgumentCaptor<GradeBookView.FileSelectedListener> listener = ArgumentCaptor.forClass(GradeBookView.FileSelectedListener.class);
    verify(view).addGradeBookFileListener(listener.capture());

    File file = File.createTempFile("testGradeBook", "xml");
    file.deleteOnExit();
    listener.getValue().fileSelected(file);

    ArgumentCaptor<LoadGradeBook> event = ArgumentCaptor.forClass(LoadGradeBook.class);
    verify(handler).handle(event.capture());

    assertThat(event.getValue().getFile(), equalTo(file));
  }

  private interface LoadGradeBookHandler {
    @Subscribe
    public void handle(LoadGradeBook loadGradeBook);
  }
}
