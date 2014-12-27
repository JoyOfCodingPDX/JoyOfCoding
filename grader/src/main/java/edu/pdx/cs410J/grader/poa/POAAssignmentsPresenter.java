package edu.pdx.cs410J.grader.poa;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import edu.pdx.cs410J.grader.Assignment;
import edu.pdx.cs410J.grader.GradeBook;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class POAAssignmentsPresenter {
  private final EventBus bus;
  private final POAAssignmentsView view;
  private List<Assignment> poaAssignments = Collections.emptyList();

  public POAAssignmentsPresenter(EventBus bus, POAAssignmentsView view) {
    this.bus = bus;
    this.view = view;
    this.view.addAssignmentSelectedHandler(this::fireAssignmentSelectedForAssignment);

    this.bus.register(this);
  }

  private void fireAssignmentSelectedForAssignment(int index) {
    Assignment assignment = this.poaAssignments.get(index);
    this.bus.post(new AssignmentSelectedEvent(assignment));
  }

  @Subscribe
  public void populateAssignmentNamesInView(GradeBookLoaded event) {
    GradeBook book = event.getGradeBook();

    poaAssignments = book.assignmentsStream().filter(this::isAssignmentPOA).collect(Collectors.toList());

    List<String> poaNames = poaAssignments.stream().map(Assignment::getName).collect(Collectors.toList());
    this.view.setAssignments(poaNames);

    this.view.setSelectedAssignment(0);
  }

  private boolean isAssignmentPOA(Assignment assignment) {
    return assignment.getType() == Assignment.AssignmentType.POA;
  }
}
