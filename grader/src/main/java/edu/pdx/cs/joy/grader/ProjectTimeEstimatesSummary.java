package edu.pdx.cs.joy.grader;

import com.google.common.annotations.VisibleForTesting;
import edu.pdx.cs.joy.ParserException;
import edu.pdx.cs.joy.grader.gradebook.*;
import edu.pdx.cs.joy.grader.gradebook.Assignment.ProjectType;

import java.io.*;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static edu.pdx.cs.joy.grader.gradebook.Assignment.ProjectType.*;

public class ProjectTimeEstimatesSummary {
  public TimeEstimatesSummaries getTimeEstimateSummaries(GradeBook book) {
    return getTimeEstimateSummaries(Collections.singletonList(book));
  }

  public TimeEstimatesSummaries getTimeEstimateSummaries(List<GradeBook> books) {
    TimeEstimatesSummaries summaries = new TimeEstimatesSummaries();

    Set<ProjectType> projectTypes = new HashSet<>();
    for (GradeBook book : books) {
      book.assignmentsStream()
        .map(Assignment::getProjectType)
        .filter(Objects::nonNull)
        .forEach(projectTypes::add);
    }

    projectTypes.forEach(projectType -> {
      summaries.addSummariesFor(books, projectType);
    });

    return summaries;
  }

  public static void main(String[] args) {
    List<String> gradeBookFileNames = new ArrayList<>();

    Collections.addAll(gradeBookFileNames, args);

    if (gradeBookFileNames.isEmpty()) {
      usage("Missing grade book file name");
      return;
    }

    List<GradeBook> gradeBooks = gradeBookFileNames.stream()
      .map(ProjectTimeEstimatesSummary::getExistingFile)
      .map(ProjectTimeEstimatesSummary::parseGradeBookFile)
      .collect(Collectors.toList());

    ProjectTimeEstimatesSummary summary = new ProjectTimeEstimatesSummary();
    TimeEstimatesSummaries summaries = summary.getTimeEstimateSummaries(gradeBooks);

    PrintWriter pw = new PrintWriter(System.out, true);
    summaries.generateMarkdown(pw, List.of(APP_CLASSES, TEXT_FILE, PRETTY_PRINT, KOANS, XML, DATABASE, REST, ANDROID));
    pw.flush();

  }

  private static GradeBook parseGradeBookFile(File file) {
    try {
      return new XmlGradeBookParser(file).parse();

    } catch (ParserException | IOException e) {
      usage("While parsing " + file + ": " + e);
      return null;
    }
  }

  private static File getExistingFile(String fileName) {
    File gradeBookFile = new File(fileName);
    if (!gradeBookFile.exists()) {
      usage("Cannot find grade book file: " + gradeBookFile);
    }
    return gradeBookFile;
  }

  private static void usage(String message) {
    PrintStream err = System.err;
    err.println("** " + message);
    err.println();
    err.println("usage: ProjectTimeEstimatesSummary gradeBookFile*");
    err.println();

    System.exit(1);
  }

  static class TimeEstimatesSummaries {
    private final Map<ProjectType, TimeEstimatesSummary> summaries = new HashMap<>();

    public TimeEstimatesSummary getTimeEstimateSummary(ProjectType projectType) {
      return this.summaries.get(projectType);
    }

    void addSummariesFor(List<GradeBook> books, ProjectType projectType) {
      Collection<Double> allEstimates = new ArrayList<>();
      books.forEach(book -> {
        book.assignmentsStream()
          .filter(assignment -> assignment.getProjectType() == projectType)
          .map(assignment -> getEstimates(book, assignment))
          .forEach(allEstimates::addAll);
      });

      if (!allEstimates.isEmpty()) {
          summaries.put(projectType, new TimeEstimatesSummary(allEstimates));
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
      formatRow(pw, projectTypes, rowTitle, projectType -> {
        TimeEstimatesSummary summary = getTimeEstimateSummary(projectType);
        if (summary == null) {
          return "n/a";

        } else {
          return String.format("%.0f hours", cellValue.apply(summary));
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

        case DATABASE:
          return "DATABASE";

        case KOANS:
          return "Koans";

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
