package edu.pdx.cs410J.grader;

import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;

public class SubmitTest {

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

    File file = makeFileWithPath("src", "main", "java", "edu", "pdx", "cs410J", userId, "ProjectClass.java");
    Submit submit = new Submit();
    submit.setUserId(userId);
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

    File file = makeFileWithPath("src", "main", "java", "edu", "pdx", "cs410J", userId, "subpackage", "ProjectClass.java");
    Submit submit = new Submit();
    submit.setUserId(userId);
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

    File file = makeFileWithPath("src", "test", "java", "edu", "pdx", "cs410J", userId, "subpackage", "ProjectTest.java");
    Submit submit = new Submit();
    submit.setUserId(userId);
    assertThat(submit.isInMavenProjectDirectory(file), equalTo(true));
  }

  @Test
  public void canSubmitIntegrationTestClasses() {
    String userId = "student";

    File file = makeFileWithPath("src", "it", "java", "edu", "pdx", "cs410J", userId, "subpackage", "ProjectTest.java");
    Submit submit = new Submit();
    submit.setUserId(userId);
    assertThat(submit.isInMavenProjectDirectory(file), equalTo(true));
  }

  @Test
  public void canSubmitMainJavaDocPackageHtml() {
    String userId = "student";

    File file = makeFileWithPath("src", "main", "javadoc", "edu", "pdx", "cs410J", userId, "package.html");
    Submit submit = new Submit();
    submit.setUserId(userId);
    assertThat(submit.isInMavenProjectDirectory(file), equalTo(true));
  }

  @Test
  public void cantSubmitPomXml() {
    String userId = "student";

    File file = makeFileWithPath("proj", "pom.xml");
    Submit submit = new Submit();
    submit.setUserId(userId);
    assertThat(submit.isInMavenProjectDirectory(file), equalTo(false));
  }

  @Test
  public void cantSubmitJavaFileInGroovyDirectory() {
    String userId = "student";

    File file = makeFileWithPath("src", "main", "groovy", "edu", "pdx", "cs410J", userId, "Project.java");
    Submit submit = new Submit();
    submit.setUserId(userId);
    assertThat(submit.isInMavenProjectDirectory(file), equalTo(false));
  }

  @Test
  public void zipFileEntryNameForSourceFile() {
    String entryName = "src/main/java/edu/pdx/cs410J/student/Project.java";
    String fileName = "directory" + "/" + entryName;
    assertThat(Submit.getZipEntryNameFor(fileName), equalTo(entryName));
  }

  @Test(expected = IllegalStateException.class)
  public void zipFileEntryNameForBadSourceFileThrowsException() {
    String badFileName = "dir/src/bad/java/edu/pdx/cs410J/student/File.java";
    Submit.getZipEntryNameFor(badFileName);
  }

  @Test
  public void canSubmitPackageHtmlForMainTestAndIT() {
    assertThat(canFileBeSubmitted("src/main/javadoc/edu/pdx/cs410J/student/package.html"), equalTo(true));
    assertThat(canFileBeSubmitted("src/test/javadoc/edu/pdx/cs410J/student/package.html"), equalTo(true));
    assertThat(canFileBeSubmitted("src/it/javadoc/edu/pdx/cs410J/student/package.html"), equalTo(true));
  }

  private boolean canFileBeSubmitted(String fileName) {
    return Submit.canFileBeSubmitted(new File(fileName));
  }

  @Test
  public void zipFileEntryNameForSourceJavadocFile() {
    String entryName = "src/main/javadoc/edu/pdx/cs410J/student/package.html";
    String fileName = "directory" + "/" + entryName;
    assertThat(Submit.getZipEntryNameFor(fileName), equalTo(entryName));
  }

  @Test
  public void canSubmitXmlFilesFromTestResourcesDirectory() {
    assertThat(canFileBeSubmitted("src/test/resources/edu/pdx/cs410J/student/testData.xml"), equalTo(true));
    assertThat(canFileBeSubmitted("src/it/resources/edu/pdx/cs410J/student/testData.xml"), equalTo(true));
  }

  @Test
  public void mainResourcesDirectoryIsAnAllowedMavenDirectory() {
    String userId = "student";

    File file = makeFileWithPath("src", "main", "resources", "edu", "pdx", "cs410J", userId, "testData.xml");
    Submit submit = new Submit();
    submit.setUserId(userId);
    assertThat(submit.isInMavenProjectDirectory(file), equalTo(true));
  }

  @Test
  public void canSubmitTestResourcesXmlFile() {
    String userId = "student";

    File file = makeFileWithPath("src", "test", "resources", "edu", "pdx", "cs410J", userId, "testData.xml");
    Submit submit = new Submit();
    submit.setUserId(userId);
    assertThat(submit.isInMavenProjectDirectory(file), equalTo(true));
  }

  @Test
  public void canSubmitIntegrationTestResourcesXmlFile() {
    String userId = "student";

    File file = makeFileWithPath("src", "it", "resources", "edu", "pdx", "cs410J", userId, "testData.xml");
    Submit submit = new Submit();
    submit.setUserId(userId);
    assertThat(submit.isInMavenProjectDirectory(file), equalTo(true));
  }

  @Test
  public void zipFileEntryNameForTestResourcesFile() {
    String entryName = "src/test/resources/edu/pdx/cs410J/student/testData.xml";
    String fileName = "directory" + "/" + entryName;
    assertThat(Submit.getZipEntryNameFor(fileName), equalTo(entryName));
  }

  @Test
  public void canSubmitTxtFilesFromResourcesDirectory() {
    assertThat(canFileBeSubmitted("src/main/resources/edu/pdx/cs410J/student/text.txt"), equalTo(true));
    assertThat(canFileBeSubmitted("src/test/resources/edu/pdx/cs410J/student/testData.txt"), equalTo(true));
    assertThat(canFileBeSubmitted("src/it/resources/edu/pdx/cs410J/student/testData.txt"), equalTo(true));
  }

  @Test
  public void cannotSubmitTxtFilesFromJavaDirectory() {
    assertThat(canFileBeSubmitted("src/main/java/edu/pdx/cs410J/student/text.txt"), equalTo(false));
    assertThat(canFileBeSubmitted("src/test/java/edu/pdx/cs410J/student/testData.txt"), equalTo(false));
    assertThat(canFileBeSubmitted("src/it/java/edu/pdx/cs410J/student/testData.txt"), equalTo(false));
  }

  @Test
  public void canSubmitTxtFilesFromResourcesDirectoryInCodeDirectoryWithJavaInTheName() {
    assertThat(canFileBeSubmitted("code/java/checkout/src/main/resources/edu/pdx/cs410J/student/text.txt"), equalTo(true));
    assertThat(canFileBeSubmitted("code/java/checkout/src/test/resources/edu/pdx/cs410J/student/testData.txt"), equalTo(true));
    assertThat(canFileBeSubmitted("code/java/checkout/src/it/resources/edu/pdx/cs410J/student/testData.txt"), equalTo(true));
  }

  @Test
  public void testClassesWereSubmitted() {
    Set files = Set.of(
      makeFileWithPath("src", "test", "java", "edu", "pdx", "cs410J", "student", "StudentTest.java")
    );

    assertThat(Submit.submittedTestClasses(files), equalTo(true));
  }

  @Test
  public void notestClassesWereSubmitted() {
    Set files = Set.of(
      makeFileWithPath("src", "main", "java", "edu", "pdx", "cs410J", "student", "Student.java")
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
    String[] dirs = {"src", "main", "java", "edu", "pdx", "cs410J", userId};


    assertFileCanBeSubmitted(userId, fileName, dirs, true);
  }

  private void assertFileCanBeSubmitted(String userId, String fileName, String[] dirs, boolean canSubmit) throws IOException {
    File dir = new File(System.getProperty("user.dir"));
    for (String dirName : dirs) {
      dir = new File(dir, dirName);
      dir.mkdirs();
    }

    File projectFile = new File(dir, fileName);
    projectFile.createNewFile();

    Submit submit = new Submit();
    submit.setUserId(userId);
    assertThat(submit.canBeSubmitted(projectFile), equalTo(canSubmit));
  }

  @Test
  public void cannotSubmitFilesWithoutFileExtension() throws IOException {
    String userId = "whitlock";
    String fileName = "noExtension";
    String[] dirs = {"src", "main", "resources", "edu", "pdx", "cs410J", userId};

    assertFileCanBeSubmitted(userId, fileName, dirs, false);
  }

  @Test(expected = IllegalStateException.class)
  public void invalidProjectNameThrowsIllegalStateException() {
    Submit submit = new Submit();
    submit.setProjectName("invalid");
    submit.validateProjectName();
  }

  @Test
  public void validateProjectName() {
    List.of("koans", "Project0", "Project4").forEach(
      projectName -> {
        Submit submit = new Submit();
        submit.setProjectName("koans");
        submit.validateProjectName();
      }
    );
  }

}
