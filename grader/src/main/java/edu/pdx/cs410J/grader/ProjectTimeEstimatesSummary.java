package edu.pdx.cs410J.grader;

import com.google.common.annotations.VisibleForTesting;
import edu.pdx.cs410J.ParserException;
import edu.pdx.cs410J.grader.gradebook.*;
import edu.pdx.cs410J.grader.gradebook.Assignment.ProjectType;

import java.io.*;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static edu.pdx.cs410J.grader.gradebook.Assignment.ProjectType.*;

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

  public static void main(String[] args) {
    String gradeBookFileName = null;

    for (String arg : args) {
      if (gradeBookFileName == null) {
        gradeBookFileName = arg;
      }
    }

    if (gradeBookFileName == null) {
      usage("Missing grade book file name");
      return;
    }

    File gradeBookFile = new File(gradeBookFileName);
    if (!gradeBookFile.exists()) {
      usage("Cannot find grade book file: " + gradeBookFile);
      return;
    }

    try {
      GradeBook book = new XmlGradeBookParser(gradeBookFile).parse();

      ProjectTimeEstimatesSummary summary = new ProjectTimeEstimatesSummary();
      TimeEstimatesSummaries summaries = summary.getTimeEstimateSummaries(book);

      PrintWriter pw = new PrintWriter(System.out, true);
      summaries.generateMarkdown(pw, List.of(APP_CLASSES, TEXT_FILE, PRETTY_PRINT, XML, ANDROID));
      pw.flush();

    } catch (ParserException | IOException e) {
      usage("While parsing " + gradeBookFile + ": " + e);
    }
  }

  private static void usage(String message) {
    PrintStream err = System.err;
    err.println("** " + message);
    err.println();
    err.println("usage: ProjectTimeEstimatesSummary gradeBookFile");
    err.println();

    System.exit(1);
  }

  static class TimeEstimatesSummaries {
    private final Map<ProjectType, TimeEstimatesSummary> summaries = new HashMap<>();

    public TimeEstimatesSummary getTimeEstimateSummary(ProjectType projectType) {
      return this.summaries.get(projectType);
    }

    void addSummariesFor(GradeBook book, Assignment assignment) {
      Collection<Double> estimates = getEstimates(book, assignment);
      if (!estimates.isEmpty()) {
        summaries.put(assignment.getProjectType(), new TimeEstimatesSummary(estimates));
      }
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
      return submissions.stream()
        .map(Grade.SubmissionInfo::getEstimatedHours)
        .filter(Objects::nonNull)
        .max(Comparator.naturalOrder());
    }

    public void generateMarkdown(Writer writer, List<ProjectType> projectTypes) {
      PrintWriter pw = new PrintWriter(writer, true);
      headerRows(pw, projectTypes);
      countRow(pw, projectTypes);
      averageRow(pw, projectTypes);
      maximumRow(pw, projectTypes);
      upperQuartileRow(pw, projectTypes);
      medianRow(pw, projectTypes);
      lowerQuartileRow(pw, projectTypes);
      minimumRow(pw, projectTypes);
    }

    private void minimumRow(PrintWriter pw, List<ProjectType> projectTypes) {
      formatRowOfDoubles(pw, projectTypes, "Minimum", TimeEstimatesSummary::getMinimum);
    }

    private void lowerQuartileRow(PrintWriter pw, List<ProjectType> projectTypes) {
      formatRowOfDoubles(pw, projectTypes, "Bottom 25%", TimeEstimatesSummary::getLowerQuartile);
    }

    private void medianRow(PrintWriter pw, List<ProjectType> projectTypes) {
      formatRowOfDoubles(pw, projectTypes, "Median", TimeEstimatesSummary::getMedian);
    }

    private void upperQuartileRow(PrintWriter pw, List<ProjectType> projectTypes) {
      formatRowOfDoubles(pw, projectTypes, "Top 25%", TimeEstimatesSummary::getUpperQuartile);
    }

    private void maximumRow(PrintWriter pw, List<ProjectType> projectTypes) {
      formatRowOfDoubles(pw, projectTypes, "Maximum", TimeEstimatesSummary::getMaximum);
    }

    private void averageRow(PrintWriter pw, List<ProjectType> projectTypes) {
      formatRowOfDoubles(pw, projectTypes, "Average", TimeEstimatesSummary::getAverage);
    }

    private void formatRowOfDoubles(PrintWriter pw, List<ProjectType> projectTypes, String rowTitle, Function<TimeEstimatesSummary, Double> cellValue) {
      formatRow(pw, projectTypes, rowTitle,projectType -> {
        TimeEstimatesSummary summary = getTimeEstimateSummary(projectType);
        if (summary == null) {
          return "n/a";

        } else {
          return cellValue.apply(summary) + " hrs";
        }
      });
    }

    private void formatRow(PrintWriter pw, List<ProjectType> projectTypes, String rowTitle, Function<ProjectType, String> cellValue) {
      pw.print("| " + rowTitle + " |");
      projectTypes.forEach(projectType -> {
        pw.print(" ");
        pw.print(cellValue.apply(projectType));
        pw.print(" |");
      });
      pw.println();
    }

    private void countRow(PrintWriter pw, List<ProjectType> projectTypes) {
      formatRow(pw, projectTypes, "Count", projectType -> {
          TimeEstimatesSummary summary = getTimeEstimateSummary(projectType);
          if (summary == null) {
            return "0";

          } else {
            return String.valueOf(summary.getCount());
          }
        });
    }

    private void headerRows(PrintWriter pw, List<ProjectType> projectTypes) {
      formatRow(pw, projectTypes, "", TimeEstimatesSummaries::formatProjectType);
      formatRow(pw, projectTypes, ":---", summary -> "---:");
    }

    @VisibleForTesting
    static String formatProjectType(ProjectType projectType) {
      switch (projectType) {
        case APP_CLASSES:
          return "App Classes";

        case TEXT_FILE:
          return "Text File";

        case PRETTY_PRINT:
          return "Pretty Print";

        case XML:
          return "XML";

        case REST:
          return "REST";

        case ANDROID:
          return "Android";

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

    TimeEstimatesSummary(Collection<Double> estimates) {
      this.count = estimates.size();
      this.average = estimates.stream().reduce(Double::sum).get() / ((double) this.count);
      List<Double> sorted = estimates.stream().sorted().collect(Collectors.toList());
      this.minimum = sorted.get(0);
      this.maximum = sorted.get(sorted.size() - 1);
      this.median = median(sorted);
      this.upperQuartile = upperQuartile(sorted);
      this.lowerQuartile = lowerQuartile(sorted);
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

  }
}
