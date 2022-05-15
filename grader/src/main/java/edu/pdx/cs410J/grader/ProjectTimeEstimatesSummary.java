package edu.pdx.cs410J.grader;

import com.google.common.annotations.VisibleForTesting;
import edu.pdx.cs410J.grader.gradebook.Assignment;
import edu.pdx.cs410J.grader.gradebook.Assignment.ProjectType;
import edu.pdx.cs410J.grader.gradebook.Grade;
import edu.pdx.cs410J.grader.gradebook.GradeBook;
import edu.pdx.cs410J.grader.gradebook.Student;

import java.util.*;
import java.util.stream.Collectors;

public class ProjectTimeEstimatesSummary {
  public TimeEstimatesSummaries getTimeEstimateSummaries(GradeBook book) {
    TimeEstimatesSummaries summaries = new TimeEstimatesSummaries();

    book.assignmentsStream().forEach(assignment -> {
      ProjectType projectType = assignment.getProjectType();
      if (projectType != null) {
        summaries.addSummariesFor(book, assignment);
      }
    });

    return summaries;
  }

  static class TimeEstimatesSummaries {
    private final Map<ProjectType, TimeEstimatesSummary> summaries = new HashMap<>();

    public TimeEstimatesSummary getTimeEstimateSummary(ProjectType projectType) {
      return this.summaries.get(projectType);
    }

    void addSummariesFor(GradeBook book, Assignment assignment) {
      summaries.put(assignment.getProjectType(), new TimeEstimatesSummary(book, assignment));
    }
  }

  static class TimeEstimatesSummary {
    private final int count;
    private final double maximum;
    private final double minimum;
    private final double median;

    TimeEstimatesSummary(GradeBook book, Assignment assignment) {
      Collection<Double> estimates = getEstimates(book, assignment);
      if (estimates.isEmpty()) {
        throw new IllegalStateException("No estimates for " + assignment);

      } else {
        this.count = estimates.size();
        this.maximum = estimates.stream().max(Comparator.naturalOrder()).get();
        this.minimum = estimates.stream().min(Comparator.naturalOrder()).get();
        this.median = median(estimates);
      }
    }

    @VisibleForTesting
    static double median(Collection<Double> doubles) {
      List<Double> values = doubles.stream().sorted().collect(Collectors.toList());
      int size = values.size();
      if (size % 2 == 0) {
        int midpoint = (size / 2);
        return average(values.get(midpoint - 1), values.get(midpoint));

      } else {
        int midpoint = (size / 2);
        return values.get(midpoint);
      }
    }

    private static double average(double d1, double d2) {
      return (d1 + d2) / 2.0;
    }

    private Collection<Double> getEstimates(GradeBook book, Assignment assignment) {
      List<Double> estimates = new ArrayList<>();

      book.studentsStream().forEach(student -> getEstimate(student, assignment).ifPresent(estimates::add));

      return estimates;
    }

    private Optional<Double> getEstimate(Student student, Assignment assignment) {
      Grade grade = student.getGrade(assignment);
      if (grade != null) {
        return getMaximumEstimate(grade.getSubmissionInfos());
      }

      return Optional.empty();
    }

    private Optional<Double> getMaximumEstimate(List<Grade.SubmissionInfo> submissions) {
      return submissions.stream().map(Grade.SubmissionInfo::getEstimatedHours).max(Comparator.naturalOrder());
    }

    public int getCount() {
      return count;
    }

    public double getMaximum() {
      return maximum;
    }

    public double getMinimum() {
      return minimum;
    }

    public double getMedian() {
      return median;
    }
  }
}
