package edu.pdx.cs410J.grader;

import com.google.common.annotations.VisibleForTesting;
import edu.pdx.cs410J.grader.gradebook.Assignment;
import edu.pdx.cs410J.grader.gradebook.Assignment.ProjectType;
import edu.pdx.cs410J.grader.gradebook.Grade;
import edu.pdx.cs410J.grader.gradebook.GradeBook;
import edu.pdx.cs410J.grader.gradebook.Student;

import java.io.PrintWriter;
import java.io.Writer;
import java.util.*;
import java.util.function.Function;
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

    public void generateMarkdown(Writer writer, List<ProjectType> projectTypes) {
      PrintWriter pw = new PrintWriter(writer, true);
      headerRows(pw, projectTypes);
      countRow(pw, projectTypes);
      averageRow(pw, projectTypes);
      maximumRow(pw, projectTypes);

    }

    private void maximumRow(PrintWriter pw, List<ProjectType> projectTypes) {
      formatRowOfDoubles(pw, projectTypes, "Maximum", TimeEstimatesSummary::getMaximum);
    }

    private void averageRow(PrintWriter pw, List<ProjectType> projectTypes) {
      formatRowOfDoubles(pw, projectTypes, "Average", TimeEstimatesSummary::getAverage);
    }

    private void formatRowOfDoubles(PrintWriter pw, List<ProjectType> projectTypes, String rowTitle, Function<TimeEstimatesSummary, Double> cellValue) {
      formatRow(pw, projectTypes, rowTitle, summary -> cellValue.apply(summary) + " hrs");
    }

    private void formatRow(PrintWriter pw, List<ProjectType> projectTypes, String rowTitle, Function<TimeEstimatesSummary, String> cellValue) {
      pw.print("| " + rowTitle + " |");
      projectTypes.forEach(projectType -> {
        TimeEstimatesSummary summary = getTimeEstimateSummary(projectType);
        pw.print(" ");
        pw.print(cellValue.apply(summary));
        pw.print(" |");
      });
      pw.println();
    }

    private void countRow(PrintWriter pw, List<ProjectType> projectTypes) {
      formatRow(pw, projectTypes, "Count", summary -> String.valueOf(summary.getCount()));
    }

    private void headerRows(PrintWriter pw, List<ProjectType> projectTypes) {
      formatRow(pw, projectTypes, "", summary -> formatProjectType(summary.getProjectType()));
      formatRow(pw, projectTypes, ":---", summary -> "---:");
    }

    private String formatProjectType(ProjectType projectType) {
      switch (projectType) {
        case APP_CLASSES:
          return "App Classes";

        case TEXT_FILE:
          return "Text File";

        default:
          throw new UnsupportedOperationException("Don't know how to format " + projectType);
      }
    }
  }

  static class TimeEstimatesSummary {
    private final int count;
    private final double maximum;
    private final double minimum;
    private final double median;
    private final double upperQuartile;
    private final double lowerQuartile;
    private final double average;
    private final ProjectType projectType;

    TimeEstimatesSummary(GradeBook book, Assignment assignment) {
      this.projectType = assignment.getProjectType();

      Collection<Double> estimates = getEstimates(book, assignment);
      if (estimates.isEmpty()) {
        throw new IllegalStateException("No estimates for " + assignment);

      } else {
        this.count = estimates.size();
        this.average = estimates.stream().reduce(Double::sum).get() / ((double) this.count);
        List<Double> sorted = estimates.stream().sorted().collect(Collectors.toList());
        this.minimum = sorted.get(0);
        this.maximum = sorted.get(sorted.size() - 1);
        this.median = median(sorted);
        this.upperQuartile = upperQuartile(sorted);
        this.lowerQuartile = lowerQuartile(sorted);
      }
    }

    @VisibleForTesting
    static double lowerQuartile(List<Double> doubles) {
      int midway = (int) Math.ceil(doubles.size() / 2.0f);
      return median(doubles.subList(0, midway));
    }

    @VisibleForTesting
    static double upperQuartile(List<Double> doubles) {
      int midway = (int) Math.floor(doubles.size() / 2.0f);
      return median(doubles.subList(midway, doubles.size()));
    }

    @VisibleForTesting
    static double median(Collection<Double> doubles) {
      List<Double> values = doubles.stream().sorted().collect(Collectors.toList());
      return median(values);
    }

    private static double median(List<Double> doubles) {
      int size = doubles.size();
      int midpoint = (size / 2);
      if (size % 2 == 0) {
        return average(doubles.get(midpoint - 1), doubles.get(midpoint));

      } else {
        return doubles.get(midpoint);
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

    public double getUpperQuartile() {
      return upperQuartile;
    }

    public double getLowerQuartile() {
      return lowerQuartile;
    }

    public double getAverage() {
      return average;
    }

    public ProjectType getProjectType() {
      return projectType;
    }
  }
}
