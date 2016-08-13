package edu.pdx.cs410J.grader;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.io.ByteStreams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class GwtZipFixer {

  private static final Logger logger = LoggerFactory.getLogger("edu.pdx.cs410J.grader");

  public static void main(String[] args) {
    String outputDirectoryName = null;
    List<String> zipFileNames = new ArrayList<>();

    for (String arg : args) {
      if (outputDirectoryName == null) {
        outputDirectoryName = arg;

      } else {
        zipFileNames.add(arg);
      }
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

    for (String zipFileName : zipFileNames) {
      File zipFile = new File(zipFileName);
      try {
        fixZipFile(zipFile, outputDirectory);

      } catch (IOException e) {
        usage("While fixing zip file " + zipFile + ": " + e);
      }

    }
  }

  private static void fixZipFile(File zipFile, File outputDirectory) throws IOException {
    File fixedZipFile = getFixedZipFile(zipFile, outputDirectory);

    try (
      ZipInputStream input = new ZipInputStream(new FileInputStream(zipFile));
      ZipOutputStream output = new ZipOutputStream(new FileOutputStream(fixedZipFile))
    ) {
      for (ZipEntry entry = input.getNextEntry(); entry != null; entry = input.getNextEntry()) {
        String entryName = entry.getName();
        String fixedEntryName = getFixedEntryName(entryName);

        if (fixedEntryName != null) {
          logger.debug(entryName + " fixed to " + fixedEntryName);

          ZipEntry fixedEntry = new ZipEntry(fixedEntryName);
          output.putNextEntry(fixedEntry);

          ByteStreams.copy(input, output);

        } else {
          logger.debug(entryName + " ignored");
        }

        output.flush();
      }
    }
  }

  @VisibleForTesting
  static String getFixedEntryName(String entryName) {
    Stream<String> ignore = Stream.of("__MACOSX", "/test", "/it");
    if (ignore.anyMatch(entryName::contains)) {
      return null;
    }

    if (entryName.contains("pom.xml")) {
      return "pom.xml";
    }

    String fixedName = replaceRegexWithPrefix(entryName, ".*main/(.*)", "src/main/");

    if (fixedName == null) {
      fixedName = replaceRegexWithPrefix(entryName, ".*java/(.*)", "src/main/java/");
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
    err.println("usage: java GwtZipFixer outputDirectory zipFile+");
    err.println("    outputDirectory     Name of direct into which fixed zip files should be written");
    err.println("    zipFile             Name of zip file to be fixed");
    err.println();
    err.println("Fixes the contents of a zip file submitted for the GWT ");
    err.println("project so that it will work with the grading scripts");
    err.println();

    System.exit(1);
  }
}
