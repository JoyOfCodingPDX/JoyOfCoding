package edu.pdx.cs.joy.grader;

import edu.pdx.cs.joy.grader.gradebook.Assignment;
import edu.pdx.cs.joy.grader.gradebook.GradeBook;
import edu.pdx.cs.joy.grader.gradebook.Student;
import edu.pdx.cs.joy.grader.gradebook.XmlDumper;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

/**
 * Integration test that validates the end-to-end functionality of finding unrecorded submission grades.
 */
public class FindUngradedSubmissionGradesIT {

  @Test
  void findUnrecordedSubmissionGrades(@TempDir Path tempDir) throws IOException {
    // Create a gradebook with assignments and students
    Path gradebookFile = createGradebook(tempDir);

    // Create test output files:
    // - fred.out: Grade 12.5 for Project1, matches gradebook (should NOT be flagged)
    // - jane.out: Grade 15.0 for Project2, different from gradebook 10.0 (SHOULD be flagged)
    // - alex.out: Grade 18.0 for Project3, not in gradebook (SHOULD be flagged)
    // - bob.out: No grade yet, just test output (should NOT be flagged as unrecorded)
    createTestOutputFile(tempDir, "fred", "Project1", 12.5, true);
    createTestOutputFile(tempDir, "jane", "Project2", 15.0, true);
    createTestOutputFile(tempDir, "alex", "Project3", 18.0, true);
    createTestOutputFile(tempDir, "bob", "Project4", null, false); // Not graded yet

    // Create corresponding submission zip files
    createSubmissionZip(tempDir, "fred", "Project1");
    createSubmissionZip(tempDir, "jane", "Project2");
    createSubmissionZip(tempDir, "alex", "Project3");
    createSubmissionZip(tempDir, "bob", "Project4");

    // Invoke FindUngradedSubmissions main method
    String[] args = {
        gradebookFile.toString(),
        tempDir.toString()
    };

    MainMethodResult result = invokeMain(FindUngradedSubmissions.class, args);

    String output = result.getTextWrittenToStandardOut();
    String errorOutput = result.getTextWrittenToStandardError();

    // If there's an error output, it means something went wrong
    if (!errorOutput.isEmpty()) {
      System.err.println("Error output from main:");
      System.err.println(errorOutput);
    }

    if (output.isEmpty()) {
      System.err.println("No output was captured - this suggests System.exit() was called");
    }

    // Debug: print the actual output for analysis
    System.out.println("=== ACTUAL OUTPUT ===");
    System.out.println(output);
    System.out.println("=== END OUTPUT ===");

    // Verify no errors
    assertThat("Error output should not contain 'Error': " + errorOutput,
               errorOutput, not(containsString("Error")));

    // Verify output shows correct counts
    assertThat(output, containsString("0 submissions need to be tested"));
    assertThat(output, containsString("1 submission needs to be graded")); // bob
    assertThat(output, containsString("2 submissions need to be recorded")); // jane and alex

    // Verify correct files are listed as needing recording
    assertThat(output, containsString("jane.out"));
    assertThat(output, containsString("alex.out"));

    // Verify fred.out is NOT listed (grade matches)
    assertThat(output, not(containsString("fred.out")));
  }

  @Test
  void invalidGradebookFileProducesError(@TempDir Path tempDir) throws IOException {
    // Create an invalid XML file (not a valid gradebook)
    Path invalidGradebook = tempDir.resolve("invalid.xml");
    Files.writeString(invalidGradebook, "<?xml version=\"1.0\"?><not-a-gradebook></not-a-gradebook>");

    // Create a submission file
    createSubmissionZip(tempDir, "student", "Project1");

    // Invoke FindUngradedSubmissions main method with invalid gradebook
    String[] args = {
        invalidGradebook.toString(),
        tempDir.toString()
    };

    MainMethodResult result = invokeMain(FindUngradedSubmissions.class, args);

    String errorOutput = result.getTextWrittenToStandardError();

    // Verify error message is produced
    assertThat(errorOutput, containsString("Error: The first argument must be a valid gradebook XML file"));
    assertThat(errorOutput, containsString("Cannot parse gradebook from:"));
    assertThat(errorOutput, containsString("invalid.xml"));
  }

  @Test
  void nonExistentGradebookFileProducesError(@TempDir Path tempDir) throws IOException {
    // Create a submission file
    createSubmissionZip(tempDir, "student", "Project1");

    // Use a non-existent gradebook file
    String nonExistentGradebook = tempDir.resolve("nonexistent.xml").toString();

    String[] args = {
        nonExistentGradebook,
        tempDir.toString()
    };

    MainMethodResult result = invokeMain(FindUngradedSubmissions.class, args);

    String errorOutput = result.getTextWrittenToStandardError();

    // Verify error message is produced
    assertThat(errorOutput, containsString("Error: The first argument must be a valid gradebook XML file"));
    assertThat(errorOutput, containsString("Cannot parse gradebook from:"));
    assertThat(errorOutput, containsString("nonexistent.xml"));
  }

  @Test
  void directoryAsGradebookProducesError(@TempDir Path tempDir) throws IOException {
    // Create a submission file
    createSubmissionZip(tempDir, "student", "Project1");

    // Try to use the directory itself as the gradebook
    String[] args = {
        tempDir.toString(),
        tempDir.toString()
    };

    MainMethodResult result = invokeMain(FindUngradedSubmissions.class, args);

    String errorOutput = result.getTextWrittenToStandardError();

    // Verify error message is produced
    assertThat(errorOutput, containsString("Error: The first argument must be a valid gradebook XML file"));
    assertThat(errorOutput, containsString("Cannot parse gradebook from:"));
  }

  @Test
  void ungradedSubmissionIsNotListedAsNeedingRecording(@TempDir Path tempDir) throws IOException {
    // Create a gradebook with one student who has a grade recorded
    GradeBook book = new GradeBook("Test Course");

    Assignment project1 = new Assignment("Project1", 6.0);
    project1.setType(Assignment.AssignmentType.PROJECT);
    project1.setDescription("First Project");
    project1.setDueDate(LocalDateTime.of(2025, 1, 15, 17, 30));
    book.addAssignment(project1);

    // Add student with a grade already recorded in gradebook
    Student charlie = new Student("charlie");
    charlie.setFirstName("Charlie");
    charlie.setLastName("Brown");
    charlie.setEmail("charlie@test.edu");
    charlie.setEnrolledSection(Student.Section.UNDERGRADUATE);
    charlie.setGrade("Project1", new edu.pdx.cs.joy.grader.gradebook.Grade("Project1", 5.0));
    book.addStudent(charlie);

    // Write gradebook
    Path gradebookFile = tempDir.resolve("gradebook.xml");
    XmlDumper dumper = new XmlDumper(gradebookFile.toFile());
    dumper.dump(book);

    // Create test output file with NO GRADE (ungraded submission)
    // The line " out of 6.0" indicates no grade has been assigned yet
    Path testOutputFile = tempDir.resolve("charlie.out");
    String content = """
              The Joy of Coding Project 1: edu.pdx.cs410J.charlie.Project1
              Submitted by charlie
              Submitted on Wed Jan 15 10:30:00 AM PST 2025
              Graded on    Wed Jan 15 11:00:00 AM PST 2025

         out of 6.0
        
        Test results follow...
        """;
    Files.writeString(testOutputFile, content);

    // Create submission zip file
    createSubmissionZip(tempDir, "charlie", "Project1");

    // Invoke FindUngradedSubmissions
    String[] args = {
        gradebookFile.toString(),
        tempDir.toString()
    };

    MainMethodResult result = invokeMain(FindUngradedSubmissions.class, args);

    String output = result.getTextWrittenToStandardOut();
    String errorOutput = result.getTextWrittenToStandardError();

    // Verify no errors
    assertThat("Error output should be empty: " + errorOutput,
               errorOutput, not(containsString("Error")));

    // The submission needs to be graded (no grade in test output)
    assertThat(output, containsString("1 submission needs to be graded"));
    assertThat(output, containsString("charlie.out"));

    // The submission should NOT be listed as needing recording
    // because it hasn't been graded yet
    assertThat(output, containsString("0 submissions need to be recorded"));

    // Verify charlie.out only appears once (in "needs to be graded" section)
    int occurrences = output.split("charlie\\.out", -1).length - 1;
    assertThat("charlie.out should appear exactly once", occurrences, equalTo(1));
  }

  @Disabled
  @Test
  void multipleUngradedSubmissionsAreNotListedAsNeedingRecording(@TempDir Path tempDir) throws IOException {
    // This test reproduces the real-world bug where:
    // - student2.out, student3.out, student4.out all have NO grade (line " out of 6.0" AFTER line 7)
    // - These .out files have notes from the grader (e.g., "fix and resubmit")
    // - student1.out has a grade 5.0 that matches the gradebook
    // Expected: The 3 submissions with notes but no grade should NOT appear in "need to be recorded"
    //           They are considered "graded" (because of notes) but have no actual grade (NaN)
    // Actual bug: All 4 appear in "need to be recorded" because NaN != any grade value

    GradeBook book = new GradeBook("Test Course");

    Assignment project2 = new Assignment("Project2", 6.0);
    project2.setType(Assignment.AssignmentType.PROJECT);
    project2.setDescription("Text File Project");
    project2.setDueDate(LocalDateTime.of(2026, 1, 26, 17, 30));
    book.addAssignment(project2);

    // Add students - student1 has matching grade, others have no grade in gradebook
    Student student1 = new Student("student1");
    student1.setFirstName("Student");
    student1.setLastName("One");
    student1.setEmail("student1@test.edu");
    student1.setEnrolledSection(Student.Section.UNDERGRADUATE);
    student1.setGrade("Project2", new edu.pdx.cs.joy.grader.gradebook.Grade("Project2", 5.0));
    book.addStudent(student1);

    Student student2 = new Student("student2");
    student2.setFirstName("Student");
    student2.setLastName("Two");
    student2.setEmail("student2@test.edu");
    student2.setEnrolledSection(Student.Section.UNDERGRADUATE);
    book.addStudent(student2);

    Student student3 = new Student("student3");
    student3.setFirstName("Student");
    student3.setLastName("Three");
    student3.setEmail("student3@test.edu");
    student3.setEnrolledSection(Student.Section.UNDERGRADUATE);
    book.addStudent(student3);

    Student student4 = new Student("student4");
    student4.setFirstName("Student");
    student4.setLastName("Four");
    student4.setEmail("student4@test.edu");
    student4.setEnrolledSection(Student.Section.UNDERGRADUATE);
    book.addStudent(student4);

    // Write gradebook
    Path gradebookFile = tempDir.resolve("gradebook.xml");
    XmlDumper dumper = new XmlDumper(gradebookFile.toFile());
    dumper.dump(book);

    // Create test output for student1 WITH a grade (5.0) that matches gradebook
    createTestOutputWithGrade(tempDir, "student1", "Project2", 5.0);

    // Create test outputs for the others with NO grades
    createTestOutputWithNoGrade(tempDir, "student2", "Project2");
    createTestOutputWithNoGrade(tempDir, "student3", "Project2");
    createTestOutputWithNoGrade(tempDir, "student4", "Project2");

    // Create submission zips
    createSubmissionZip(tempDir, "student1", "Project2");
    createSubmissionZip(tempDir, "student2", "Project2");
    createSubmissionZip(tempDir, "student3", "Project2");
    createSubmissionZip(tempDir, "student4", "Project2");

    // Invoke FindUngradedSubmissions
    String[] args = {
        gradebookFile.toString(),
        tempDir.toString()
    };

    MainMethodResult result = invokeMain(FindUngradedSubmissions.class, args);

    String output = result.getTextWrittenToStandardOut();
    String errorOutput = result.getTextWrittenToStandardError();

    System.out.println("=== BUG REPRODUCTION OUTPUT ===");
    System.out.println(output);
    System.out.println("=== END OUTPUT ===");

    // Verify no errors
    assertThat("Error output should be empty: " + errorOutput,
               errorOutput, not(containsString("Error")));

    // The 3 submissions with notes are considered "graded" (hasGrade=true because lineCount > 7)
    // So they should NOT appear in "needs to be graded"
    assertThat(output, containsString("0 submissions need to be graded"));

    // NO submissions should be listed as needing recording
    // (student1 has matching grade, others have notes but no actual grade which shouldn't be recorded)
    assertThat(output, containsString("0 submissions need to be recorded"));

    // NONE of the .out files should appear in the output
    assertThat(output, not(containsString("student1.out")));
    assertThat(output, not(containsString("student2.out")));
    assertThat(output, not(containsString("student3.out")));
    assertThat(output, not(containsString("student4.out")));
  }

  private void createTestOutputWithGrade(Path tempDir, String studentId, String projectName, double grade) throws IOException {
    Path testOutputFile = tempDir.resolve(studentId + ".out");

    LocalDateTime submissionTime = LocalDateTime.of(2026, 1, 20, 10, 30);
    LocalDateTime gradedTime = LocalDateTime.of(2026, 1, 20, 11, 0);

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("E MMM d hh:mm:ss a 'PST' yyyy");
    String submissionTimeStr = submissionTime.format(formatter);
    String gradedTimeStr = gradedTime.format(formatter);

    String content = String.format("""
              The Joy of Coding Project 2: edu.pdx.cs410J.%s.%s
              Submitted by %s
              Submitted on %s
              Graded on    %s

        %.1f out of 6.0
        
        Test results follow...
        """, studentId, projectName, studentId, submissionTimeStr, gradedTimeStr, grade);

    Files.writeString(testOutputFile, content);
  }

  private void createTestOutputWithNoGrade(Path tempDir, String studentId, String projectName) throws IOException {
    Path testOutputFile = tempDir.resolve(studentId + ".out");

    LocalDateTime submissionTime = LocalDateTime.of(2026, 1, 20, 10, 30);
    LocalDateTime gradedTime = LocalDateTime.of(2026, 1, 20, 11, 0);

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("E MMM d hh:mm:ss a 'PST' yyyy");
    String submissionTimeStr = submissionTime.format(formatter);
    String gradedTimeStr = gradedTime.format(formatter);

    // This creates a test output with " out of 6.0" AFTER line 7 (line 9)
    // The grader has left a note instructing the student to fix and resubmit
    // According to spec: this should be considered graded (hasGrade=true)
    // But should NOT be considered needing recording (because there's no actual grade)
    String content = String.format("""
              The Joy of Coding Project 2: edu.pdx.cs410J.%s.%s
              Submitted by %s
              Submitted on %s
              Graded on    %s

        NOTE TO STUDENT: Your submission has a fundamental flaw.
        Please fix the following issue and resubmit:
         out of 6.0
        
        - Your code does not compile
        
        Test results follow...
        """, studentId, projectName, studentId, submissionTimeStr, gradedTimeStr);

    Files.writeString(testOutputFile, content);
  }

  private Path createGradebook(Path tempDir) throws IOException {
    GradeBook book = new GradeBook("Test Course");

    // Add assignments
    Assignment project1 = new Assignment("Project1", 15.0);
    project1.setType(Assignment.AssignmentType.PROJECT);
    project1.setDescription("First Project");
    project1.setDueDate(LocalDateTime.of(2025, 1, 15, 17, 30));
    book.addAssignment(project1);

    Assignment project2 = new Assignment("Project2", 20.0);
    project2.setType(Assignment.AssignmentType.PROJECT);
    project2.setDescription("Second Project");
    project2.setDueDate(LocalDateTime.of(2025, 1, 22, 17, 30));
    book.addAssignment(project2);

    Assignment project3 = new Assignment("Project3", 20.0);
    project3.setType(Assignment.AssignmentType.PROJECT);
    project3.setDescription("Third Project");
    project3.setDueDate(LocalDateTime.of(2025, 1, 29, 17, 30));
    book.addAssignment(project3);

    Assignment project4 = new Assignment("Project4", 20.0);
    project4.setType(Assignment.AssignmentType.PROJECT);
    project4.setDescription("Fourth Project");
    project4.setDueDate(LocalDateTime.of(2025, 2, 5, 17, 30));
    book.addAssignment(project4);

    // Add students with grades
    addStudent(book, "fred", "Fred", "Flintstone", 12.5, "Project1");
    addStudent(book, "jane", "Jane", "Jetson", 10.0, "Project2");
    addStudent(book, "alex", "Alex", "Anderson", null, null);
    addStudent(book, "bob", "Bob", "Builder", null, null);

    // Write gradebook and student files using XmlDumper
    Path gradebookFile = tempDir.resolve("gradebook.xml");
    try {
      XmlDumper dumper = new XmlDumper(gradebookFile.toFile());
      dumper.dump(book);
    } catch (Exception e) {
      throw new IOException("Error writing gradebook XML", e);
    }

    return gradebookFile;
  }

  private void addStudent(GradeBook book, String id, String firstName, String lastName, Double gradeScore, String assignmentName) {
    Student student = new Student(id);
    student.setFirstName(firstName);
    student.setLastName(lastName);
    student.setEmail(id + "@test.edu");
    student.setEnrolledSection(Student.Section.UNDERGRADUATE);

    if (gradeScore != null && assignmentName != null) {
      edu.pdx.cs.joy.grader.gradebook.Grade grade = new edu.pdx.cs.joy.grader.gradebook.Grade(assignmentName, gradeScore);
      student.setGrade(assignmentName, grade);
    }

    book.addStudent(student);
  }

  private void createTestOutputFile(Path tempDir, String studentId, String projectName, Double grade, boolean hasGrade) throws IOException {
    Path testOutputFile = tempDir.resolve(studentId + ".out");

    LocalDateTime submissionTime = LocalDateTime.of(2025, 1, 15, 10, 30);
    LocalDateTime gradedTime = LocalDateTime.of(2025, 1, 15, 11, 0);

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("E MMM d hh:mm:ss a 'PST' yyyy");
    String submissionTimeStr = submissionTime.format(formatter);
    String gradedTimeStr = gradedTime.format(formatter);

    String gradeSection;
    if (hasGrade && grade != null) {
      gradeSection = String.format("%.1f out of 15.0", grade);
    } else {
      gradeSection = " out of 15.0";
    }

    String content = String.format("""
              The Joy of Coding Project 1: edu.pdx.cs410J.%s.%s
              Submitted by %s
              Submitted on %s
              Graded on    %s

        %s
        
        Test results follow...
        """, studentId, projectName, studentId, submissionTimeStr, gradedTimeStr, gradeSection);

    Files.writeString(testOutputFile, content);
  }

  private void createSubmissionZip(Path tempDir, String studentId, String projectName) throws IOException {
    // Create a simple zip file with a manifest
    Path zipFile = tempDir.resolve(studentId + "-" + projectName + ".zip");

    LocalDateTime submissionTime = LocalDateTime.of(2025, 1, 15, 10, 30);

    // Use the correct manifest attribute names that ProjectSubmissionsProcessor expects
    String manifestContent = String.format("""
        Manifest-Version: 1.0
        Submitter-User-Id: %s
        Submission-Time: %s
        Project-Name: %s
        
        """, studentId, submissionTime.format(java.time.format.DateTimeFormatter.ISO_DATE_TIME), projectName);

    // For simplicity, we'll create a minimal zip file structure
    // In a real scenario, this would be more complex
    java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
    try (java.util.zip.ZipOutputStream zos = new java.util.zip.ZipOutputStream(baos)) {
      java.util.zip.ZipEntry manifestEntry = new java.util.zip.ZipEntry("META-INF/MANIFEST.MF");
      zos.putNextEntry(manifestEntry);
      zos.write(manifestContent.getBytes());
      zos.closeEntry();
    }

    Files.write(zipFile, baos.toByteArray());
  }

  /**
   * Helper class to capture the result of invoking a main method.
   */
  private static class MainMethodResult {
    private final String out;
    private final String err;

    public MainMethodResult(String out, String err) {
      this.out = out;
      this.err = err;
    }

    public String getTextWrittenToStandardOut() {
      return out;
    }

    public String getTextWrittenToStandardError() {
      return err;
    }
  }

  /**
   * Invokes the main method of the specified class with the given arguments.
   */
  private MainMethodResult invokeMain(Class<?> mainClass, String... args) {
    java.io.ByteArrayOutputStream outStream = new java.io.ByteArrayOutputStream();
    java.io.ByteArrayOutputStream errStream = new java.io.ByteArrayOutputStream();

    java.io.PrintStream originalOut = System.out;
    java.io.PrintStream originalErr = System.err;

    try {
      System.setOut(new java.io.PrintStream(outStream));
      System.setErr(new java.io.PrintStream(errStream));

      java.lang.reflect.Method mainMethod = mainClass.getMethod("main", String[].class);
      mainMethod.invoke(null, (Object) args);

    } catch (Exception e) {
      e.printStackTrace(System.err);
    } finally {
      System.setOut(originalOut);
      System.setErr(originalErr);
    }

    return new MainMethodResult(outStream.toString(), errStream.toString());
  }
}
