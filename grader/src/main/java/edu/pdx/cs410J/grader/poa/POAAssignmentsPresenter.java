package edu.pdx.cs410J.grader.poa;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import edu.pdx.cs410J.grader.Assignment;
import edu.pdx.cs410J.grader.GradeBook;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Singleton
public class POAAssignmentsPresenter {
  private final EventBus bus;
  private final POAAssignmentsView view;
  private List<Assignment> poaAssignments = Collections.emptyList();

  @Inject
  public POAAssignmentsPresenter(EventBus bus, POAAssignmentsView view) {
    this.bus = bus;
    this.view = view;
    this.view.addAssignmentSelectedHandler(this::fireAssignmentSelectedForAssignment);

    this.bus.register(this);
  }

  private void fireAssignmentSelectedForAssignment(int index) {
    Assignment assignment = this.poaAssignments.get(index);
    this.view.setSelectedAssignmentDueDate(formatDueDate(assignment));
    fireAssignmentSelectedEvent(assignment);
  }

  private void fireAssignmentSelectedEvent(Assignment assignment) {
    this.bus.post(new AssignmentSelectedEvent(assignment));
  }

  @Subscribe
  public void populateAssignmentNamesInView(GradeBookLoaded event) {
    GradeBook book = event.getGradeBook();

    poaAssignments = book.assignmentsStream().filter(this::isAssignmentPOA).collect(Collectors.toList());

    if (!poaAssignments.isEmpty()) {
      List<String> poaNames = poaAssignments.stream().map(Assignment::getName).collect(Collectors.toList());
      this.view.setAssignments(poaNames);

      selectAssignmentWithMostRecentlyPastDueDate();
    }
  }

  private void selectAssignmentWithMostRecentlyPastDueDate() {
    Optional<Assignment> mostRecentlyDueAssignment = this.poaAssignments.stream().
      filter(a -> a.getDueDate() != null).
      filter(a -> a.getDueDate().isBefore(LocalDateTime.now())).
      sorted((a1, a2) -> a1.getDueDate().compareTo(a2.getDueDate()) * -1).
      findFirst();

    int index;
    if (mostRecentlyDueAssignment.isPresent()) {
      index = poaAssignments.indexOf(mostRecentlyDueAssignment.get());

    } else {
      index = 0;
    }

    setSelectedAssignmentInView(index);
  }

  private void setSelectedAssignmentInView(int index) {
    this.view.setSelectedAssignment(index);
    this.view.setSelectedAssignmentDueDate(formatDueDate(this.poaAssignments.get(index)));
  }

  private boolean isAssignmentPOA(Assignment assignment) {
    return assignment.getType() == Assignment.AssignmentType.POA;
  }

  @Subscribe
  public void selectAssignmentThatMatchesSubmissionSubject(POASubmissionSelected submission) {
    List<String> numbersInSubject = findNumbersInString(submission.getSubmission().getSubject());
    for (int i = 0; i < this.poaAssignments.size(); i++) {
      Assignment assignment = this.poaAssignments.get(i);
      for (String numberInSubject : numbersInSubject) {
        if (assignment.getName().contains(numberInSubject)) {
          setSelectedAssignmentInView(i);
          fireAssignmentSelectedEvent(assignment);
        }
      }
    }
  }

  @VisibleForTesting
  static List<String> findNumbersInString(String string) {
    Pattern pattern = Pattern.compile(".*?(\\d+).*?");
    Matcher matcher = pattern.matcher(string);
    List<String> numbers = new ArrayList<>();
    while (matcher.find()) {
      numbers.add(matcher.group(1));
    }

    return numbers;
  }

  @VisibleForTesting
  static String formatDueDate(Assignment assignment) {
    LocalDateTime dueDate = assignment.getDueDate();
    if (dueDate == null) {
      return "";
    } else {
      return dueDate.format(DateTimeFormatter.ofPattern("M/d/yyyy h:mm a"));
    }
  }
}

