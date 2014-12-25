package edu.pdx.cs410J.grader.poa;

import edu.pdx.cs410J.grader.GradeBook;
import org.junit.Test;

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
}
