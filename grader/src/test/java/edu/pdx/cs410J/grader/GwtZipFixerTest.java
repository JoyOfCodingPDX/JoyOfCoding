package edu.pdx.cs410J.grader;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;

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

}
