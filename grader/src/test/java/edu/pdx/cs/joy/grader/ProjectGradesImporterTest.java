package edu.pdx.cs.joy.grader;

import edu.pdx.cs.joy.grader.TestedProjectSubmissionOutputParserTest.GradedProject;
import edu.pdx.cs.joy.grader.gradebook.Assignment;
import edu.pdx.cs.joy.grader.gradebook.GradeBook;
import edu.pdx.cs.joy.grader.gradebook.Student;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class ProjectGradesImporterTest {

  private final Logger logger = LoggerFactory.getLogger(this.getClass().getPackage().getName());

  @Test
  public void scoreIsRecordedInGradeBook() throws TestedProjectSubmissionOutputParser.ScoreNotFoundException {
    String studentId = "student";
    Assignment assignment = new Assignment("project", 6.0);

    GradeBook gradeBook = new GradeBook("test");
    gradeBook.addStudent(new Student(studentId));
    gradeBook.addAssignment(assignment);

    String score = "5.8";
    GradedProject project = new GradedProject();
    project.addLine(score + " out of 6.0");
    project.addLine("");
    project.addLine("asdfasd");

    Logger logger = mock(Logger.class);
    ProjectGradesImporter importer = new ProjectGradesImporter(gradeBook, assignment, logger);
    importer.recordScoreFromProjectReport(studentId, project.getReader());

    assertThat(gradeBook.getStudent(studentId).get().getGrade(assignment.getName()).getScore(), equalTo(5.8));
    assertThat(gradeBook.isDirty(), equalTo(true));

    ArgumentCaptor<String> message = ArgumentCaptor.forClass(String.class);
    verify(logger).info(message.capture());
    assertThat(message.getValue(), containsString("Recorded grade of " + score + " for " + studentId));
  }

  @Test
  public void throwIllegalStateExceptionWhenTotalPointsInReportDoesNotMatchGradeBook() throws TestedProjectSubmissionOutputParser.ScoreNotFoundException {
    String studentId = "student";
    Assignment assignment = new Assignment("project", 8.0);

    GradeBook gradeBook = new GradeBook("test");
    gradeBook.addStudent(new Student(studentId));
    gradeBook.addAssignment(assignment);

    GradedProject project = new GradedProject();
    project.addLine("5.8 out of 6.0");
    project.addLine("");
    project.addLine("asdfasd");

    ProjectGradesImporter importer = new ProjectGradesImporter(gradeBook, assignment, logger);
    assertThrows(IllegalStateException.class, () ->
      importer.recordScoreFromProjectReport(studentId, project.getReader())
    );
  }

  @Test
  public void logWarningWhenStudentDoesNotExistInGradeBook() throws TestedProjectSubmissionOutputParser.ScoreNotFoundException {
    String studentId = "student";
    Assignment assignment = new Assignment("project", 6.0);

    GradeBook gradeBook = new GradeBook("test");
    gradeBook.addAssignment(assignment);

    GradedProject project = new GradedProject();
    project.addLine("5.8 out of 6.0");
    project.addLine("");
    project.addLine("asdfasd");

    Logger logger = mock(Logger.class);
    ProjectGradesImporter importer = new ProjectGradesImporter(gradeBook, assignment, logger);

    importer.recordScoreFromProjectReport(studentId, project.getReader());

    assertThat(gradeBook.containsStudent(studentId), equalTo(false));

    ArgumentCaptor<String> message = ArgumentCaptor.forClass(String.class);
    verify(logger, times(1)).warn(message.capture());
    assertThat(message.getValue(), containsString("Student \"" + studentId + "\" not found in gradebook"));
  }

}
