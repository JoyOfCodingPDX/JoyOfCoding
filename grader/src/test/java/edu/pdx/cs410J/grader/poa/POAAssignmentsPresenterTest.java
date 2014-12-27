package edu.pdx.cs410J.grader.poa;

import edu.pdx.cs410J.grader.Assignment;
import edu.pdx.cs410J.grader.GradeBook;
import org.junit.Test;

import java.util.Arrays;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class POAAssignmentsPresenterTest extends EventBusTestCase {

  private POAAssignmentsView view;

  @Override
  public void setUp() {
    super.setUp();

    this.view = mock(POAAssignmentsView.class);

    new POAAssignmentsPresenter(this.bus, this.view);
  }

  @Test
  public void assignmentListPopulatedInViewWhenGradeBookLoaded() {

    GradeBook book = new GradeBook("Test Grade Book");
    book.addAssignment(new Assignment("POA 0", 1.0).setType(Assignment.AssignmentType.POA));
    book.addAssignment(new Assignment("POA 1", 1.0).setType(Assignment.AssignmentType.POA));
    book.addAssignment(new Assignment("POA 2", 1.0).setType(Assignment.AssignmentType.POA));
    book.addAssignment(new Assignment("Quiz 0", 3.0).setType(Assignment.AssignmentType.QUIZ));

    this.bus.post(new GradeBookLoaded(book));

    verify(this.view).setAssignments(Arrays.asList("POA 0", "POA 1", "POA 2"));
    verify(this.view).setSelectedAssignment(0);
  }
}
