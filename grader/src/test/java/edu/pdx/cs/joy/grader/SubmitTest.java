package edu.pdx.cs.joy.grader;

import edu.pdx.cs.joy.grader.gradebook.Assignment;
import edu.pdx.cs.joy.grader.gradebook.Grade;
import edu.pdx.cs.joy.grader.gradebook.GradeBook;
import edu.pdx.cs.joy.grader.gradebook.Student;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class SubmitTest {

  private final File tempDir;

  public SubmitTest(@TempDir File tempDir) {
    this.tempDir = tempDir;
  }

  @Test
  public void nineDigitStringIsInvalidLoginId() {
    Submit submit = new Submit();
    assertThat(submit.isNineDigitStudentId("123456789"), equalTo(true));
  }

  @Test
  public void eightDigitStringIsValidLoginId() {
    Submit submit = new Submit();
    assertThat(submit.isNineDigitStudentId("12345678"), equalTo(false));
  }

  @Test
  public void userNameIsValidLoginId() {
    Submit submit = new Submit();
    assertThat(submit.isNineDigitStudentId("whitlock"), equalTo(false));
  }

  @Test
  public void emailAddressIsInvalidLoginId() {
    Submit submit = new Submit();
    assertThat(submit.looksLikeAnEmailAddress("me@email.com"), equalTo(true));
  }

  @Test
  public void canSubmitClassesInStudentPackage() {
    String userId = "student";

    File file = makeFileWithPath("src", "main", "java", "edu", "pdx", "cs", "joy", userId, "ProjectClass.java");
    Submit submit = new Submit();
    submit.setStudent(new Student(userId));
    assertThat(submit.isInMavenProjectDirectory(file), equalTo(true));
  }

  private File makeFileWithPath(String... path) {
    File dir = new File(System.getProperty("user.dir"));

    for (String part : path) {
      dir = new File(dir, part);
    }

    return dir;
  }

  @Test
  public void canSubmitClassesInSubPackage() {
    String userId = "student";

    File file = makeFileWithPath("src", "main", "java", "edu", "pdx", "cs", "joy", userId, "subpackage", "ProjectClass.java");
    Submit submit = new Submit();
    submit.setStudent(new Student(userId));
    assertThat(submit.isInMavenProjectDirectory(file), equalTo(true));
  }

  @Test
  public void submissionTimeIsInIsoDateTime() {
    LocalDateTime time = LocalDateTime.of(2018, 7, 8, 5, 59, 32);
    String formattedTime = Submit.ManifestAttributes.formatSubmissionTime(time);
    assertThat(formattedTime, equalTo("2018-07-08T05:59:32"));
  }

  @Test
  public void legacySubmissionTimeCanBeParsed() {
    String legacySubmissionTime = "2018-Jul-08 05:59:32";
    LocalDateTime expected = LocalDateTime.of(2018, 7, 8, 5, 59, 32);

    LocalDateTime actual = Submit.ManifestAttributes.parseSubmissionTime(legacySubmissionTime);
    assertThat(actual, equalTo(expected));
  }

  @Test
  public void canSubmitTestClasses() {
    String userId = "student";

    File file = makeFileWithPath("src", "test", "java", "edu", "pdx", "cs", "joy", userId, "subpackage", "ProjectTest.java");
    Submit submit = new Submit();
    submit.setStudent(new Student(userId));
    assertThat(submit.isInMavenProjectDirectory(file), equalTo(true));
  }

  @Test
  public void canSubmitIntegrationTestClasses() {
    String userId = "student";

    File file = makeFileWithPath("src", "it", "java", "edu", "pdx", "cs", "joy", userId, "subpackage", "ProjectTest.java");
    Submit submit = new Submit();
    submit.setStudent(new Student(userId));
    assertThat(submit.isInMavenProjectDirectory(file), equalTo(true));
  }

  @Test
  public void canSubmitMainJavaDocPackageHtml() {
    String userId = "student";

    File file = makeFileWithPath("src", "main", "javadoc", "edu", "pdx", "cs", "joy", userId, "package.html");
    Submit submit = new Submit();
    submit.setStudent(new Student(userId));
    assertThat(submit.isInMavenProjectDirectory(file), equalTo(true));
  }

  @Test
  public void cantSubmitPomXml() {
    String userId = "student";

    File file = makeFileWithPath("proj", "pom.xml");
    Submit submit = new Submit();
    submit.setStudent(new Student(userId));
    assertThat(submit.isInMavenProjectDirectory(file), equalTo(false));
  }

  @Test
  public void cantSubmitJavaFileInGroovyDirectory() {
    String userId = "student";

    File file = makeFileWithPath("src", "main", "groovy", "edu", "pdx", "cs", "joy", userId, "Project.java");
    Submit submit = new Submit();
    submit.setStudent(new Student(userId));
    assertThat(submit.isInMavenProjectDirectory(file), equalTo(false));
  }

  @Test
  public void zipFileEntryNameForSourceFile() {
    String entryName = "src/main/java/edu/pdx/cs/joy/student/Project.java";
    String fileName = "directory" + "/" + entryName;
    assertThat(Submit.getZipEntryNameFor(fileName), equalTo(entryName));
  }

  @Test
  public void zipFileEntryNameForBadSourceFileThrowsException() {
    String badFileName = "dir/src/bad/java/edu/pdx/cs/joy/student/File.java";
    assertThrows(IllegalStateException.class, () -> Submit.getZipEntryNameFor(badFileName));
  }

  @Test
  public void canSubmitPackageHtmlForMainTestAndIT() {
    assertThat(canFileBeSubmitted("src/main/javadoc/edu/pdx/cs/joy/student/package.html"), equalTo(true));
    assertThat(canFileBeSubmitted("src/test/javadoc/edu/pdx/cs/joy/student/package.html"), equalTo(true));
    assertThat(canFileBeSubmitted("src/it/javadoc/edu/pdx/cs/joy/student/package.html"), equalTo(true));
  }

  private boolean canFileBeSubmitted(String fileName) {
    return Submit.canFileBeSubmitted(new File(fileName));
  }

  @Test
  public void zipFileEntryNameForSourceJavadocFile() {
    String entryName = "src/main/javadoc/edu/pdx/cs/joy/student/package.html";
    String fileName = "directory" + "/" + entryName;
    assertThat(Submit.getZipEntryNameFor(fileName), equalTo(entryName));
  }

  @Test
  public void canSubmitXmlFilesFromTestResourcesDirectory() {
    assertThat(canFileBeSubmitted("src/test/resources/edu/pdx/cs/joy/student/testData.xml"), equalTo(true));
    assertThat(canFileBeSubmitted("src/it/resources/edu/pdx/cs/joy/student/testData.xml"), equalTo(true));
  }

  @Test
  public void mainResourcesDirectoryIsAnAllowedMavenDirectory() {
    String userId = "student";

    File file = makeFileWithPath("src", "main", "resources", "edu", "pdx", "cs", "joy", userId, "testData.xml");
    Submit submit = new Submit();
    submit.setStudent(new Student(userId));
    assertThat(submit.isInMavenProjectDirectory(file), equalTo(true));
  }

  @Test
  public void canSubmitTestResourcesXmlFile() {
    String userId = "student";

    File file = makeFileWithPath("src", "test", "resources", "edu", "pdx", "cs", "joy", userId, "testData.xml");
    Submit submit = new Submit();
    submit.setStudent(new Student(userId));
    assertThat(submit.isInMavenProjectDirectory(file), equalTo(true));
  }

  @Test
  public void canSubmitIntegrationTestResourcesXmlFile() {
    String userId = "student";

    File file = makeFileWithPath("src", "it", "resources", "edu", "pdx", "cs", "joy", userId, "testData.xml");
    Submit submit = new Submit();
    submit.setStudent(new Student(userId));
    assertThat(submit.isInMavenProjectDirectory(file), equalTo(true));
  }

  @Test
  public void zipFileEntryNameForTestResourcesFile() {
    String entryName = "src/test/resources/edu/pdx/cs/joy/student/testData.xml";
    String fileName = "directory" + "/" + entryName;
    assertThat(Submit.getZipEntryNameFor(fileName), equalTo(entryName));
  }

  @Test
  public void canSubmitTxtFilesFromResourcesDirectory() {
    assertThat(canFileBeSubmitted("src/main/resources/edu/pdx/cs/joy/student/text.txt"), equalTo(true));
    assertThat(canFileBeSubmitted("src/test/resources/edu/pdx/cs/joy/student/testData.txt"), equalTo(true));
    assertThat(canFileBeSubmitted("src/it/resources/edu/pdx/cs/joy/student/testData.txt"), equalTo(true));
  }

  @Test
  public void cannotSubmitTxtFilesFromJavaDirectory() {
    assertThat(canFileBeSubmitted("src/main/java/edu/pdx/cs/joy/student/text.txt"), equalTo(false));
    assertThat(canFileBeSubmitted("src/test/java/edu/pdx/cs/joy/student/testData.txt"), equalTo(false));
    assertThat(canFileBeSubmitted("src/it/java/edu/pdx/cs/joy/student/testData.txt"), equalTo(false));
  }

  @Test
  public void canSubmitTxtFilesFromResourcesDirectoryInCodeDirectoryWithJavaInTheName() {
    assertThat(canFileBeSubmitted("code/java/checkout/src/main/resources/edu/pdx/cs/joy/student/text.txt"), equalTo(true));
    assertThat(canFileBeSubmitted("code/java/checkout/src/test/resources/edu/pdx/cs/joy/student/testData.txt"), equalTo(true));
    assertThat(canFileBeSubmitted("code/java/checkout/src/it/resources/edu/pdx/cs/joy/student/testData.txt"), equalTo(true));
  }

  @Test
  public void testClassesWereSubmitted() {
    Set<File> files = Set.of(
      makeFileWithPath("src", "test", "java", "edu", "pdx", "cs", "joy", "student", "StudentTest.java")
    );

    assertThat(Submit.submittedTestClasses(files), equalTo(true));
  }

  @Test
  public void notestClassesWereSubmitted() {
    Set<File> files = Set.of(
      makeFileWithPath("src", "main", "java", "edu", "pdx", "cs", "joy", "student", "Student.java")
    );

    assertThat(Submit.submittedTestClasses(files), equalTo(false));
  }

  @Test
  public void canSubmitKoansFile() throws IOException {
    String[] dirs = {"src", "beginner"};
    String fileName = "AboutKoans.java";

    assertFileCanBeSubmitted("whitlock", fileName, dirs, true);
  }

  @Test
  public void canSubmitProjectFile() throws IOException {
    String userId = "whitlock";
    String fileName = "Project1.java";
    String[] dirs = {"src", "main", "java", "edu", "pdx", "cs", "joy", userId};


    assertFileCanBeSubmitted(userId, fileName, dirs, true);
  }

  private void assertFileCanBeSubmitted(String userId, String fileName, String[] dirs, boolean canSubmit) throws IOException {
    File dir = this.tempDir;
    for (String dirName : dirs) {
      dir = new File(dir, dirName);
      dir.mkdirs();
    }

    File projectFile = new File(dir, fileName);
    projectFile.createNewFile();

    Submit submit = new Submit();
    submit.setStudent(new Student(userId));
    assertThat(submit.canBeSubmitted(projectFile), equalTo(canSubmit));
  }

  @Test
  public void cannotSubmitFilesWithoutFileExtension() throws IOException {
    String userId = "whitlock";
    String fileName = "noExtension";
    String[] dirs = {"src", "main", "resources", "edu", "pdx", "cs", "joy", userId};

    assertFileCanBeSubmitted(userId, fileName, dirs, false);
  }

  @Test
  public void cannotSubmitFilesOnNoSubmitList() throws IOException {
    String userId = "whitlock";
    String fileName = "AbstractAirline.java";
    String[] dirs = {"src", "main", "java", "edu", "pdx", "cs", "joy", userId};

    assertFileCanBeSubmitted(userId, fileName, dirs, false);
  }

  @Test
  public void invalidProjectNameThrowsIllegalStateException() {
    Submit submit = new Submit();
    submit.setProjectName("invalid");
    assertThrows(IllegalStateException.class, submit::validateProjectName);
  }

  @Test
  public void validateProjectName() {
    List.of("koans", "Project1", "Project4").forEach(
      projectName -> {
        Submit submit = new Submit();
        submit.setProjectName(projectName);
        submit.validateProjectName();
      }
    );
  }

  @Test
  void zipFileAttachmentDoesNotContainStudentNickName() {
    Student student = new Student("studentId");
    String firstName = "FirstName";
    String nickName = "NickName";
    String lastName = "LastName";

    student.setFirstName(firstName);
    student.setNickName(nickName);
    student.setLastName(lastName);

    Submit submit = new Submit();
    submit.setStudent(student);

    String zipFileTitle = submit.getZipFileTitle();

    assertThat(zipFileTitle, containsString(firstName));
    assertThat(zipFileTitle, containsString(lastName));
    assertThat(zipFileTitle, not(containsString(nickName)));
  }

  @Test
  void aiUsageIsIncludedInManifestEntries() {
    Submit submit = submitWithTime();
    submit.setAiUsage(Grade.SubmissionInfo.AiUsage.REVIEWED);

    Map<Attributes.Name, String> entries = submit.getManifestEntries();

    assertThat(entries.get(Submit.ManifestAttributes.AI_USAGE), equalTo("REVIEWED"));
    assertThat(entries, not(hasKey(Submit.ManifestAttributes.AI_USAGE_DESCRIPTION)));
  }

  @Test
  void aiUsageDescriptionIsIncludedInManifestEntries() {
    Submit submit = submitWithTime();
    submit.setAiUsage(Grade.SubmissionInfo.AiUsage.OTHER);
    submit.setAiUsageDescription("AI chose music for me to listen to");

    Map<Attributes.Name, String> entries = submit.getManifestEntries();

    assertThat(entries.get(Submit.ManifestAttributes.AI_USAGE), equalTo("OTHER"));
    assertThat(entries.get(Submit.ManifestAttributes.AI_USAGE_DESCRIPTION), equalTo("AI chose music for me to listen to"));
  }

  @Test
  void manifestEntriesOmitAiUsageWhenNotAnswered() {
    Map<Attributes.Name, String> entries = submitWithTime().getManifestEntries();

    assertThat(entries, not(hasKey(Submit.ManifestAttributes.AI_USAGE)));
    assertThat(entries, not(hasKey(Submit.ManifestAttributes.AI_USAGE_DESCRIPTION)));
  }

  @Test
  void aiUsageDescriptionWithNewlinesSurvivesManifestRoundTrip() throws IOException, StudentEmailAttachmentProcessor.SubmissionException {
    String projectName = "Project1";
    String description = "Line one\nLine two with a \\ backslash and \u00fcn\u00efcode\r\nLine three";

    Student student = new Student("studentId");
    student.setFirstName("First");
    student.setLastName("Last");
    student.setEmail("student@email.com");

    Submit submit = submitWithTime();
    submit.setProjectName(projectName);
    submit.setStudent(student);
    submit.setAiUsage(Grade.SubmissionInfo.AiUsage.OTHER);
    submit.setAiUsageDescription(description);

    Manifest manifest = writeAndReadManifest(submit.getManifestEntries());
    manifest.getMainAttributes().put(Submit.ManifestAttributes.SUBMISSION_COMMENT, "comment");

    GradeBook gradeBook = new GradeBook("test");
    gradeBook.addStudent(student);
    gradeBook.addAssignment(new Assignment(projectName, 10.0));

    new ProjectSubmissionsProcessor(tempDir, gradeBook).noteSubmissionInGradeBook(manifest);

    Grade.SubmissionInfo info = student.getGrade(projectName).getSubmissionInfos().get(0);
    assertThat(info.getAiUsage(), equalTo(Grade.SubmissionInfo.AiUsage.OTHER));
    assertThat(info.getAiUsageDescription(), equalTo(description));
  }

  private Submit submitWithTime() {
    Submit submit = new Submit();
    submit.setSubmitTime(LocalDateTime.of(2026, 7, 15, 20, 43, 46));
    return submit;
  }

  private Manifest writeAndReadManifest(Map<Attributes.Name, String> entries) throws IOException {
    Manifest manifest = new Manifest();
    Attributes attrs = manifest.getMainAttributes();
    attrs.put(Attributes.Name.MANIFEST_VERSION, "1.0");
    entries.forEach((name, value) -> {
      if (value != null) {
        attrs.put(name, value);
      }
    });

    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
    manifest.write(bytes);
    return new Manifest(new ByteArrayInputStream(bytes.toByteArray()));
  }

}
