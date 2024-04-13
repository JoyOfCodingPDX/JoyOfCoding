package edu.pdx.cs.joy.grader;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.io.ByteStreams;
import edu.pdx.cs.joy.ParserException;
import edu.pdx.cs.joy.grader.gradebook.Grade;
import edu.pdx.cs.joy.grader.gradebook.GradeBook;
import edu.pdx.cs.joy.grader.gradebook.Student;
import edu.pdx.cs.joy.grader.gradebook.XmlGradeBookParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.jar.Attributes;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class AndroidZipFixer {

  private static final Logger logger = LoggerFactory.getLogger("edu.pdx.cs.joy.grader");

  private final GradeBook gradeBook;

  @VisibleForTesting
  AndroidZipFixer(GradeBook book) {
    this.gradeBook = book;
  }

  public static void main(String[] args) {
    String gradeBookFileName = null;
    String outputDirectoryName = null;
    List<String> zipFileNames = new ArrayList<>();

    for (String arg : args) {
      if (gradeBookFileName == null) {
        gradeBookFileName = arg;

      } else if (outputDirectoryName == null) {
        outputDirectoryName = arg;

      } else {
        zipFileNames.add(arg);
      }
    }

    if (gradeBookFileName == null) {
      usage("Missing grade book file name");
      return;
    }

    if (outputDirectoryName == null) {
      usage("Missing output directory");
      return;
    }

    if (zipFileNames.isEmpty()) {
      usage("Missing zip file");
      return;
    }

    File outputDirectory = new File(outputDirectoryName);
    GradeBook book;
    try {
      XmlGradeBookParser parser = new XmlGradeBookParser(gradeBookFileName);
      book = parser.parse();

    } catch (IOException | ParserException ex) {
      exitWithExceptionMessage("While parsing grade book", ex);
      return;
    }
    AndroidZipFixer fixer = new AndroidZipFixer(book);

    for (String zipFileName : zipFileNames) {
      File zipFile = new File(zipFileName);
      try {
        fixer.fixZipFile(zipFile, outputDirectory);

      } catch (IOException e) {
        exitWithExceptionMessage("While fixing zip file " + zipFile, e);
      }

    }
  }

  private static void exitWithExceptionMessage(String message, Exception ex) {
    System.err.println("+++ " + message);
    System.err.println(ex.getMessage());

    logger.debug(message, ex);

    System.exit(1);
  }

  private void fixZipFile(File zipFile, File outputDirectory) throws IOException {
    File fixedZipFile = getFixedZipFile(zipFile, outputDirectory);
    String studentId = getStudentIdFromZipFileName(zipFile);
    FileOutputStream fixZipStream = new FileOutputStream(fixedZipFile);

    InputStream zipStream = new FileInputStream(zipFile);

    HashMap<Attributes.Name, String> manifestEntries = getManifestEntriesForStudent(studentId);
    fixZipFile(zipStream, fixZipStream, manifestEntries);
  }

  @VisibleForTesting
  void fixZipFile(InputStream zipStream, OutputStream fixedZipStream, HashMap<Attributes.Name, String> manifestEntries) throws IOException {
    ZipFileMaker maker = new ZipFileMaker(fixedZipStream, manifestEntries);
    try (
      ZipInputStream input = new ZipInputStream(zipStream)
    ) {
      Map<ZipEntry, InputStream> zipFileEntries = new HashMap<>();

      for (ZipEntry entry = input.getNextEntry(); entry != null; entry = input.getNextEntry()) {
        String entryName = entry.getName();
        String fixedEntryName = getFixedEntryName(entryName);

        if (fixedEntryName != null) {
          logger.debug(entryName + " fixed to " + fixedEntryName);

          ZipEntry fixedEntry = new ZipEntry(fixedEntryName);
          fixedEntry.setLastModifiedTime(entry.getLastModifiedTime());
          fixedEntry.setMethod(ZipEntry.DEFLATED);

          byte[] entryBytes = ByteStreams.toByteArray(input);
          zipFileEntries.put(fixedEntry, new ByteArrayInputStream(entryBytes));

        } else {
          logger.debug(entryName + " ignored");
        }
      }

      maker.makeZipFile(zipFileEntries);
    }
  }

  private String getStudentIdFromZipFileName(File zipFile) {
    String fileName = zipFile.getName();
    int index = fileName.indexOf(".zip");
    if (index < 0) {
      return fileName;

    } else {
      return fileName.substring(0, index);
    }
  }

  @VisibleForTesting
  HashMap<Attributes.Name, String> getManifestEntriesForStudent(String studentId) {
    Student student =
      this.gradeBook.getStudent(studentId).orElseThrow(() -> new IllegalArgumentException("Unknown student: " + studentId));

    HashMap<Attributes.Name, String> manifest = new HashMap<>();
    manifest.put(Submit.ManifestAttributes.USER_ID, student.getId());
    manifest.put(Submit.ManifestAttributes.USER_NAME, student.getFullName());

    LocalDateTime submissionTime = getSubmissionTime(student);
    if (submissionTime != null) {
      manifest.put(Submit.ManifestAttributes.SUBMISSION_TIME, Submit.ManifestAttributes.formatSubmissionTime(submissionTime));
    }

    return manifest;
  }

  private LocalDateTime getSubmissionTime(Student student) {
    Grade project = student.getGrade("Project5");
    if (project == null) {
      logger.warn("No Project5 submission for " + student.getId());
      return null;
    }

    return project.getSubmissionTimes().stream().max(Comparator.naturalOrder()).orElse(null);
  }

  @VisibleForTesting
  static String getFixedEntryName(String entryName) {
    Stream<String> ignore = Stream.of("__MACOSX", "/test", "/androidTest", "/build/", ".DS_Store", "gradlew.bat");
    if (ignore.anyMatch(entryName::contains)) {
      return null;
    }

    List<String> moveToTopLevel =
      List.of("app/build.gradle", "build.gradle", "gradle.properties", "gradlew", "settings.gradle" );
    for (String special : moveToTopLevel) {
      if (entryName.contains(special)) {
        return special;
      }
    }

    if (entryName.endsWith("gradle")) {
      return "gradle";
    }

    String fixedName = replaceRegexWithPrefix(entryName, ".*/src/main/(.*)", "app/src/main/");

    if (fixedName == null) {
      fixedName = replaceRegexWithPrefix(entryName, ".*/main/(.*)", "app/src/main/");
    }

    if (fixedName == null) {
      fixedName = replaceRegexWithPrefix(entryName, ".*/java/(.*)", "app/src/main/java/");
    }

    if (fixedName == null) {
      fixedName = replaceRegexWithPrefix(entryName, "^edu/(.*)", "app/src/main/java/edu/");
    }

    if (fixedName == null) {
      fixedName = replaceRegexWithPrefix(entryName, ".*/gradle/(.*)", "gradle/");
    }

    return fixedName;
  }

  private static String replaceRegexWithPrefix(String entryName, String regex, String replaceWithPrefix) {
    Pattern pattern = Pattern.compile(regex);
    Matcher matcher = pattern.matcher(entryName);

    if (matcher.matches()) {
      String portionUnderMain = matcher.group(1);
      if ("".equals(portionUnderMain)) {
        return null;

      } else {
        return replaceWithPrefix + portionUnderMain;
      }

    } else {
      return null;
    }
  }

  private static File getFixedZipFile(File zipFile, File outputDirectory) {
    return new File(outputDirectory, zipFile.getName());
  }

  private static void usage(String message) {
    PrintStream err = System.err;

    err.println("+++ " + message);
    err.println();
    err.println("usage: java GwtZipFixer gradeBook outputDirectory zipFile+");
    err.println("    gradeBook           Grade book XML file");
    err.println("    outputDirectory     Name of direct into which fixed zip files should be written");
    err.println("    zipFile             Name of zip file to be fixed");
    err.println();
    err.println("Fixes the contents of a zip file submitted for the GWT ");
    err.println("project so that it will work with the grading scripts");
    err.println();

    System.exit(1);
  }
}