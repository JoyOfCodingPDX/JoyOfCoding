package edu.pdx.cs410J.grader;

import org.junit.Ignore;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

import static edu.pdx.cs410J.grader.Submit.ManifestAttributes;
import static edu.pdx.cs410J.grader.Submit.ManifestAttributes.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNull.nullValue;

public class GwtZipFixerTest {

  @Test
  public void testEntriesAreIgnored() {
    assertThat(GwtZipFixer.getFixedEntryName("src/test/java"), equalTo(null));
  }

  @Test
  public void pomXmlRemainsAtTopLevel() {
    assertThat(GwtZipFixer.getFixedEntryName("pom.xml"), equalTo("pom.xml"));
  }

  @Test
  public void pomXmlIsMovedToTopLevel() {
    assertThat(GwtZipFixer.getFixedEntryName("dir/pom.xml"), equalTo("pom.xml"));
  }

  @Test
  public void javaSourceRemainsAtTopLevel() {
    String entry = "src/main/java/edu/pdx/cs410J/student/client/AppointmentBookServiceAsync.java";
    assertThat(GwtZipFixer.getFixedEntryName(entry), equalTo(entry));
  }

  @Test
  public void javaSourceIsMovedToTopLevel() {
    String entry = "src/main/java/edu/pdx/cs410J/student/client/AppointmentBookServiceAsync.java";
    assertThat(GwtZipFixer.getFixedEntryName("directory/" + entry), equalTo(entry));
  }

  @Test
  public void ideaDirectoryIsIgnored() {
    String entry = "5Proj/apptbook-gwt/.idea/artifacts/";
    assertThat(GwtZipFixer.getFixedEntryName(entry), equalTo(null));
  }

  @Test
  public void resourcesDirectoryIsMovedToTopLevel() {
    String entry = "5Proj/apptbook-gwt/src/main/resources/AppointmentBookGwt.gwt.xml";
    assertThat(GwtZipFixer.getFixedEntryName(entry), equalTo("src/main/resources/AppointmentBookGwt.gwt.xml"));
  }

  @Test
  public void srcIsAddedToMainDirectory() {
    String entry = "main/java/edu/pdx/cs410J/student/client/PrettyPrinter.java";
    assertThat(GwtZipFixer.getFixedEntryName(entry), equalTo("src/" + entry));
  }

  @Test
  public void macosDirectoryIsIgnored() {
    String entry = "__MACOSX/apptbook-gwt/._pom.xml";
    assertThat(GwtZipFixer.getFixedEntryName(entry), equalTo(null));
  }
  @Test
  public void mainThatIsNotSourceMainIsIgnored() {
    String entry = "apptbook-gwt/out/production/main/";
    assertThat(GwtZipFixer.getFixedEntryName(entry), equalTo(null));
  }

  @Test
  public void directoryWithJavaIsMovedToSrcMain() {
    String entry = "student/java/edu/pdx/cs410J/student/client/Appointment.java";
    String fixed = "src/main/java/edu/pdx/cs410J/student/client/Appointment.java";
    assertThat(GwtZipFixer.getFixedEntryName(entry), equalTo(fixed));
  }

  @Test
  public void srcTestIsIgnored() {
    String entry = "apptbook-gwt-submission/src/test/java/edu/";
    assertThat(GwtZipFixer.getFixedEntryName(entry), equalTo(null));
  }

  @Test
  public void srcItIsIgnored() {
    String entry = "apptbook-gwt-submission/src/it/java/edu/";
    assertThat(GwtZipFixer.getFixedEntryName(entry), equalTo(null));
  }

  @Test
  public void directoryWithResourcesIsMovedToSrcMain() {
    String entry = "student/resources/edu/pdx/cs410J/student/AppointmentBookGwt.gwt.xml";
    String fixed = "src/main/resources/edu/pdx/cs410J/student/AppointmentBookGwt.gwt.xml";
    assertThat(GwtZipFixer.getFixedEntryName(entry), equalTo(fixed));
  }

  @Test
  public void directoryWithWebappIsMovedToSrcMain() {
    String entry = "student/webapp/WEB-INF/web.xml";
    String fixed = "src/main/webapp/WEB-INF/web.xml";
    assertThat(GwtZipFixer.getFixedEntryName(entry), equalTo(fixed));
  }

  @Test
  public void directoryWithJavaSubPackageNamedDomainIsMovedToSrcMain() {
    String entry = "student/java/edu/pdx/cs410J/student/client/domain/Appointment.java";
    String fixed = "src/main/java/edu/pdx/cs410J/student/client/domain/Appointment.java";
    assertThat(GwtZipFixer.getFixedEntryName(entry), equalTo(fixed));
  }

  @Test
  public void directoryWithJavaSubPackageNamedDomainRemainsInSrcMain() {
    String entry = "src/main/java/edu/pdx/cs410J/student/client/domain/Appointment.java";
    assertThat(GwtZipFixer.getFixedEntryName(entry), equalTo(entry));
  }

  @Test
  public void fileSubmittedWithSubmitProgramIsMovedToSrcMainJava() {
    String entry = "edu/pdx/cs410J/student/client/domain/Appointment.java";
    String fixed = "src/main/java/edu/pdx/cs410J/student/client/domain/Appointment.java";
    assertThat(GwtZipFixer.getFixedEntryName(entry), equalTo(fixed));
  }

  @Test
  public void pomFileInTargetIsIgnored() {
    String entry = "airline-gwt/target/airline/META-INF/maven/edu.pdx.cs410J.student/airline-gwt/pom.xml";
    assertThat(GwtZipFixer.getFixedEntryName(entry), nullValue());
  }

  @Test
  public void dsStoreIsIgnored() {
    String entry = "student/airline/src/main/.DS_Store";
    assertThat(GwtZipFixer.getFixedEntryName(entry), nullValue());
  }

  @Ignore
  @Test
  public void manifestContainsStudentId() {
    String studentId = "studentId";

    GradeBook book = new GradeBook("test");
    book.addStudent(new Student(studentId));

    GwtZipFixer fixer = new GwtZipFixer(book);
    assertThat(fixer.getManifestEntriesForStudent(studentId).get(USER_ID), equalTo(studentId));

  }

  @Test
  public void manifestContainsStudentName() {

    GradeBook book = new GradeBook("test");
    Student student = new Student("studentId");
    student.setFirstName("First");
    student.setLastName("Last");
    book.addStudent(student);

    GwtZipFixer fixer = new GwtZipFixer(book);
    assertThat(fixer.getManifestEntriesForStudent("studentId").get(USER_NAME), equalTo(student.getFullName()));

  }

  @Test
  public void manifestContainsSubmissionTime() {
    GradeBook book = new GradeBook("test");
    String studentId = "studentId";
    Student student = new Student(studentId);
    book.addStudent(student);

    Assignment gwtProject = new Assignment("Project5", 12.0);
    book.addAssignment(gwtProject);

    Grade grade = new Grade(gwtProject, Grade.NO_GRADE);
    LocalDateTime submissionTime = LocalDateTime.now();
    grade.addNote(ZipFileSubmissionsProcessor.getSubmissionNote(studentId, submissionTime));

    student.setGrade(gwtProject, grade);

    GwtZipFixer fixer = new GwtZipFixer(book);
    assertThat(fixer.getManifestEntriesForStudent(studentId).get(SUBMISSION_TIME), equalTo(ManifestAttributes.formatSubmissionTime(submissionTime)));
  }

  @Test
  public void canIdentifySubmissionTimesInGradeNotes() {
    LocalDateTime submissionTime = LocalDateTime.now();
    String note = ZipFileSubmissionsProcessor.getSubmissionNote("student", submissionTime);
    Stream<String> times = GwtZipFixer.getSubmissionTimes(List.of(note));
    assertThat(times.anyMatch(s -> s.equals(ManifestAttributes.formatSubmissionTime(submissionTime))), equalTo(true));
  }

  @Test
  public void manifestContainsLatestSubmissionTime() {
    GradeBook book = new GradeBook("test");
    String studentId = "studentId";
    Student student = new Student(studentId);
    book.addStudent(student);

    Assignment gwtProject = new Assignment("Project5", 12.0);
    book.addAssignment(gwtProject);

    Grade grade = new Grade(gwtProject, Grade.NO_GRADE);
    LocalDateTime now = LocalDateTime.now();
    LocalDateTime firstSubmissionTime = now.minusDays(1);
    grade.addNote(ZipFileSubmissionsProcessor.getSubmissionNote(studentId, firstSubmissionTime));
    LocalDateTime secondSubmissionTime = now.minusHours(6);
    grade.addNote(ZipFileSubmissionsProcessor.getSubmissionNote(studentId, secondSubmissionTime));

    student.setGrade(gwtProject, grade);

    GwtZipFixer fixer = new GwtZipFixer(book);
    assertThat(fixer.getManifestEntriesForStudent(studentId).get(SUBMISSION_TIME), equalTo(ManifestAttributes.formatSubmissionTime(secondSubmissionTime)));
  }

}
