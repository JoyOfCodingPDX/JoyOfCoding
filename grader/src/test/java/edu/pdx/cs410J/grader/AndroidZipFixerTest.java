package edu.pdx.cs410J.grader;

import com.google.common.io.ByteStreams;
import edu.pdx.cs410J.grader.gradebook.Assignment;
import edu.pdx.cs410J.grader.gradebook.Grade;
import edu.pdx.cs410J.grader.gradebook.GradeBook;
import edu.pdx.cs410J.grader.gradebook.Student;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import static edu.pdx.cs410J.grader.Submit.ManifestAttributes;
import static edu.pdx.cs410J.grader.Submit.ManifestAttributes.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.hamcrest.core.IsNull.nullValue;

public class AndroidZipFixerTest {

  @Test
  public void unitTestEntriesAreIgnored() {
    assertThat(AndroidZipFixer.getFixedEntryName("src/test/java"), equalTo(null));
  }

  @Test
  public void androidTestEntriesAreIgnored() {
    assertThat(AndroidZipFixer.getFixedEntryName("src/androidTest/java"), equalTo(null));
  }

  @Test
  public void androidTestInDirectoryIsIgnored() {
    String entry = "student/app/src/androidTest/java/edu/pdx/cs410J/";
    assertThat(AndroidZipFixer.getFixedEntryName(entry), equalTo(null));
  }

  @Test
  public void buildDirectoriesAreIgnored() {
    assertThat(AndroidZipFixer.getFixedEntryName("build"), equalTo(null));
  }

  @Test
  public void buildGradleRemainsAtTopLevel() {
    assertThat(AndroidZipFixer.getFixedEntryName("build.gradle"), equalTo("build.gradle"));
  }

  @Test
  public void buildGradleInAppDirectoryRemainsInItsDirectory() {
    assertThat(AndroidZipFixer.getFixedEntryName("app/build.gradle"), equalTo("app/build.gradle"));
  }

  @Test
  public void buildGradleInNonAppDirectoryIsMoveToTop() {
    assertThat(AndroidZipFixer.getFixedEntryName("fred/build.gradle"), equalTo("build.gradle"));
  }

  @Test
  public void buildGradleInAppDirectoryInOtherDirectoryIsMovedToTopAppDirectory() {
    assertThat(AndroidZipFixer.getFixedEntryName("fred/app/build.gradle"), equalTo("app/build.gradle"));
  }

  @Test
  public void gradlePropertiesRemainsAtTopLevel() {
    assertThat(AndroidZipFixer.getFixedEntryName("gradle.properties"), equalTo("gradle.properties"));
  }

  @Test
  public void gradlePropertiesMovedToTopLevel() {
    assertThat(AndroidZipFixer.getFixedEntryName("fred/gradle.properties"), equalTo("gradle.properties"));
  }

  @Test
  public void gradleDirectoryMovedToTopLevel() {
    assertThat(AndroidZipFixer.getFixedEntryName("fred/gradle"), equalTo("gradle"));
  }

  @Test
  public void gradlewScriptMovedToTopLevel() {
    assertThat(AndroidZipFixer.getFixedEntryName("fred/gradlew"), equalTo("gradlew"));
  }

  @Test
  public void settingsDotGradleScriptMovedToTopLevel() {
    assertThat(AndroidZipFixer.getFixedEntryName("fred/settings.gradle"), equalTo("settings.gradle"));
  }

  @Test
  public void localDotPropertiesIsIgnored() {
    assertThat(AndroidZipFixer.getFixedEntryName("fred/local.properties"), equalTo(null));
  }

  @Test
  public void gradleWrapperJarFileMovedToTopLevel() {
    String entry = "gradle/wrapper/gradle-wrapper.jar";
    assertThat(AndroidZipFixer.getFixedEntryName("student/" + entry), equalTo(entry));
  }

  @Test
  public void gradlewDotBatIsIgnored() {
    assertThat(AndroidZipFixer.getFixedEntryName("student/gradlew.bat"), equalTo(null));
  }

  @Test
  public void javaSourceRemainsAtTopLevel() {
    String entry = "app/src/main/java/edu/pdx/cs410J/student/client/MainActivity.java";
    assertThat(AndroidZipFixer.getFixedEntryName(entry), equalTo(entry));
  }

  @Test
  public void javaSourceIsMovedToTopLevel() {
    String entry = "app/src/main/java/edu/pdx/cs410J/student/client/MainActivity.java";
    assertThat(AndroidZipFixer.getFixedEntryName("directory/" + entry), equalTo(entry));
  }

  @Test
  public void ideaDirectoryIsIgnored() {
    String entry = "5Proj/apptbook-gwt/.idea/artifacts/";
    assertThat(AndroidZipFixer.getFixedEntryName(entry), equalTo(null));
  }

  @Test
  public void imlFilesAreIgnored() {
    assertThat(AndroidZipFixer.getFixedEntryName("project.iml"), equalTo(null));
  }

  @Test
  public void resourcesDirectoryIsMovedToTopLevel() {
    String resource = "app/src/main/res/mipmap-mdpi/ic_launcher.png";
    String entry = "5Proj/apptbook-gwt" + resource;
    assertThat(AndroidZipFixer.getFixedEntryName(entry), equalTo(resource));
  }

  @Test
  public void srcIsAddedToMainDirectory() {
    String entry = "main/java/edu/pdx/cs410J/student/client/PrettyPrinter.java";
    assertThat(AndroidZipFixer.getFixedEntryName(entry), equalTo("app/src/" + entry));
  }

  @Test
  public void macosDirectoryIsIgnored() {
    String entry = "__MACOSX/apptbook-gwt/._pom.xml";
    assertThat(AndroidZipFixer.getFixedEntryName(entry), equalTo(null));
  }
  @Test
  public void mainThatIsNotSourceMainIsIgnored() {
    String entry = "apptbook-gwt/out/production/main/";
    assertThat(AndroidZipFixer.getFixedEntryName(entry), equalTo(null));
  }

  @Test
  public void directoryWithJavaIsMovedToSrcMain() {
    String entry = "student/java/edu/pdx/cs410J/student/client/Appointment.java";
    String fixed = "app/src/main/java/edu/pdx/cs410J/student/client/Appointment.java";
    assertThat(AndroidZipFixer.getFixedEntryName(entry), equalTo(fixed));
  }

  @Test
  public void srcTestIsIgnored() {
    String entry = "apptbook-gwt-submission/src/test/java/edu/";
    assertThat(AndroidZipFixer.getFixedEntryName(entry), equalTo(null));
  }

  @Test
  public void srcAndroidTestIsIgnored() {
    String entry = "apptbook-gwt-submission/src/androidTest/java/edu/";
    assertThat(AndroidZipFixer.getFixedEntryName(entry), equalTo(null));
  }

  @Test
  public void directoryWithJavaSubPackageNamedDomainIsMovedToSrcMain() {
    String entry = "student/java/edu/pdx/cs410J/student/client/domain/Appointment.java";
    String fixed = "app/src/main/java/edu/pdx/cs410J/student/client/domain/Appointment.java";
    assertThat(AndroidZipFixer.getFixedEntryName(entry), equalTo(fixed));
  }

  @Test
  public void directoryWithJavaSubPackageNamedDomainRemainsInSrcMain() {
    String entry = "app/src/main/java/edu/pdx/cs410J/student/client/domain/Appointment.java";
    assertThat(AndroidZipFixer.getFixedEntryName(entry), equalTo(entry));
  }

  @Test
  public void fileSubmittedWithSubmitProgramIsMovedToSrcMainJava() {
    String entry = "edu/pdx/cs410J/student/client/domain/Appointment.java";
    String fixed = "app/src/main/java/edu/pdx/cs410J/student/client/domain/Appointment.java";
    assertThat(AndroidZipFixer.getFixedEntryName(entry), equalTo(fixed));
  }

  @Test
  public void packageWithMainIsMovedToAppropriateLocation() {
    String entry = "AndroidAppointmentBook/app/src/main/java/edu/pdx/cs410j/student/androidappointmentbook/ui/main/SectionsPagerAdapter.java";
    String fixed = "app/src/main/java/edu/pdx/cs410j/student/androidappointmentbook/ui/main/SectionsPagerAdapter.java";
    assertThat(AndroidZipFixer.getFixedEntryName(entry), equalTo(fixed));
  }

  @Test
  public void pomFileInTargetIsIgnored() {
    String entry = "airline-gwt/target/airline/META-INF/maven/edu.pdx.cs410J.student/airline-gwt/pom.xml";
    assertThat(AndroidZipFixer.getFixedEntryName(entry), nullValue());
  }

  @Test
  public void dsStoreIsIgnored() {
    String entry = "student/airline/src/main/.DS_Store";
    assertThat(AndroidZipFixer.getFixedEntryName(entry), nullValue());
  }

  @Test
  public void manifestContainsStudentId() {
    String studentId = "studentId";

    GradeBook book = new GradeBook("test");
    book.addStudent(new Student(studentId));

    AndroidZipFixer fixer = new AndroidZipFixer(book);
    assertThat(fixer.getManifestEntriesForStudent(studentId).get(USER_ID), equalTo(studentId));
  }

  @Test
  public void manifestContainsStudentName() {

    GradeBook book = new GradeBook("test");
    Student student = new Student("studentId");
    student.setFirstName("First");
    student.setLastName("Last");
    book.addStudent(student);

    AndroidZipFixer fixer = new AndroidZipFixer(book);
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
    grade.noteSubmission(submissionTime);

    student.setGrade(gwtProject, grade);

    AndroidZipFixer fixer = new AndroidZipFixer(book);
    assertThat(fixer.getManifestEntriesForStudent(studentId).get(SUBMISSION_TIME), equalTo(ManifestAttributes.formatSubmissionTime(submissionTime)));
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
    grade.noteSubmission(firstSubmissionTime);
    LocalDateTime secondSubmissionTime = now.minusHours(6);
    grade.noteSubmission(secondSubmissionTime);

    student.setGrade(gwtProject, grade);

    AndroidZipFixer fixer = new AndroidZipFixer(book);
    assertThat(fixer.getManifestEntriesForStudent(studentId).get(SUBMISSION_TIME), equalTo(ManifestAttributes.formatSubmissionTime(secondSubmissionTime)));
  }

  @Test
  public void missingSubmissionDateResultsInNullManifestEntry() {
    GradeBook book = new GradeBook("test");
    String studentId = "studentId";
    Student student = new Student(studentId);
    book.addStudent(student);

    Assignment gwtProject = new Assignment("Project5", 12.0);
    book.addAssignment(gwtProject);

    Grade grade = new Grade(gwtProject, Grade.NO_GRADE);

    student.setGrade(gwtProject, grade);

    AndroidZipFixer fixer = new AndroidZipFixer(book);
    assertThat(fixer.getManifestEntriesForStudent(studentId).get(SUBMISSION_TIME), equalTo(null));
  }

  @Test
  public void contentsOfZipEntriesAreNotModified() throws IOException {
    String entryName = "build.gradle";
    String entryContent = "This is a test entry";

    ByteArrayOutputStream zippedBytes = new ByteArrayOutputStream();
    ZipOutputStream zos = new ZipOutputStream(zippedBytes);
    zos.setMethod(ZipOutputStream.DEFLATED);

    ZipEntry entry = new ZipEntry(entryName);
    zos.putNextEntry(entry);
    zos.write(entryContent.getBytes());
    zos.closeEntry();
    zos.close();

    AndroidZipFixer fixer = new AndroidZipFixer(new GradeBook("test"));
    ByteArrayOutputStream fixedBytes = new ByteArrayOutputStream();
    fixer.fixZipFile(new ByteArrayInputStream(zippedBytes.toByteArray()), fixedBytes, new HashMap<>());

    ZipInputStream zis = new ZipInputStream(new ByteArrayInputStream(fixedBytes.toByteArray()));

    ZipEntry fixedEntry = zis.getNextEntry();
    while (fixedEntry != null) {
      if (fixedEntry.getName().equals(entryName)) {
        break;
      }
      fixedEntry = zis.getNextEntry();
    }

    assertThat(fixedEntry, notNullValue());

    ByteArrayOutputStream fixedEntryBytes = new ByteArrayOutputStream();
    ByteStreams.copy(zis, fixedEntryBytes);
    String fixedEntryContent = new String(fixedEntryBytes.toByteArray());

    assertThat(fixedEntryContent, equalTo(entryContent));


  }

}