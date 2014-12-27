package edu.pdx.cs410J.grader.poa;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import edu.pdx.cs410J.grader.Assignment;
import edu.pdx.cs410J.grader.GradeBook;

import java.util.List;
import java.util.stream.Collectors;

public class POAAssignmentsPresenter {
  private final EventBus bus;
  private final POAAssignmentsView view;

  public POAAssignmentsPresenter(EventBus bus, POAAssignmentsView view) {
    this.bus = bus;
    this.view = view;

    this.bus.register(this);
  }

  @Subscribe
  public void populateAssignmentNamesInView(GradeBookLoaded event) {
    GradeBook book = event.getGradeBook();

    List<String> poaAssignments = book.assignmentsStream().
      filter(this::isAssignmentPOA).
      map(Assignment::getName).
      collect(Collectors.toList());
    this.view.setAssignments(poaAssignments);
    this.view.setSelectedAssignment(0);
  }

  private boolean isAssignmentPOA(Assignment assignment) {
    return assignment.getType() == Assignment.OTHER;
  }
}
