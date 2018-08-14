package edu.pdx.cs410J.grader;

import com.google.common.annotations.VisibleForTesting;
import edu.pdx.cs410J.ParserException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.Attributes;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class GwtZipFixer {

  private static final Logger logger = LoggerFactory.getLogger("edu.pdx.cs410J.grader");

  private final GradeBook gradeBook;

  @VisibleForTesting
  GwtZipFixer(GradeBook book) {
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
    GwtZipFixer fixer = new GwtZipFixer(book);

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

    System.exit(1);
  }

  private void fixZipFile(File zipFile, File outputDirectory) throws IOException {
    File fixedZipFile = getFixedZipFile(zipFile, outputDirectory);
    String studentId = getStudentIdFromZipFileName(zipFile);
    ZipFileMaker maker = new ZipFileMaker(fixedZipFile, getManifestEntriesForStudent(studentId));

    try (
      ZipInputStream input = new ZipInputStream(new FileInputStream(zipFile))
    ) {
      Map<ZipEntry, InputStream> zipFileEntries = new HashMap<>();

      for (ZipEntry entry = input.getNextEntry(); entry != null; entry = input.getNextEntry()) {
        String entryName = entry.getName();
        String fixedEntryName = getFixedEntryName(entryName);

        if (fixedEntryName != null) {
          logger.debug(entryName + " fixed to " + fixedEntryName);

          ZipEntry fixedEntry = new ZipEntry(fixedEntryName);
          fixedEntry.setLastModifiedTime(entry.getLastModifiedTime());
          fixedEntry.setSize(entry.getSize());
          fixedEntry.setMethod(ZipEntry.DEFLATED);

          zipFileEntries.put(fixedEntry, input);

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
    return manifest;
  }

  @VisibleForTesting
  static String getFixedEntryName(String entryName) {
    Stream<String> ignore = Stream.of("__MACOSX", "/test", "/it", "/target/", ".DS_Store");
    if (ignore.anyMatch(entryName::contains)) {
      return null;
    }

    if (entryName.contains("pom.xml")) {
      return "pom.xml";
    }

    String fixedName = replaceRegexWithPrefix(entryName, ".*/main/(.*)", "src/main/");

    if (fixedName == null) {
      fixedName = replaceRegexWithPrefix(entryName, ".*/java/(.*)", "src/main/java/");
    }

    if (fixedName == null) {
      fixedName = replaceRegexWithPrefix(entryName, ".*/resources/(.*)", "src/main/resources/");
    }

    if (fixedName == null) {
      fixedName = replaceRegexWithPrefix(entryName, ".*/webapp/(.*)", "src/main/webapp/");
    }

    if (fixedName == null) {
      fixedName = replaceRegexWithPrefix(entryName, "^edu/(.*)", "src/main/java/edu/");
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
