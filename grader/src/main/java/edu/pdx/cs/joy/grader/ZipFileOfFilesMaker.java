package edu.pdx.cs.joy.grader;

import com.google.common.collect.Sets;

import java.io.*;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.jar.Attributes;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;

class ZipFileOfFilesMaker extends ZipFileMaker {

  private final Map<File, String> sourceFilesAndNames;

  public ZipFileOfFilesMaker(Map<File, String> sourceFilesAndNames, File zipFile, Map<Attributes.Name, String> manifestEntries) throws FileNotFoundException {
    super(new FileOutputStream(zipFile), manifestEntries);
    this.sourceFilesAndNames = sourceFilesAndNames;
  }

  public void makeZipFile() throws IOException {
    Map<ZipEntry, InputStream> zipFileEntries = new HashMap<>();

    for (Map.Entry<File, String> fileEntry : sourceFilesAndNames.entrySet()) {
      File file = fileEntry.getKey();
      String fileName = fileEntry.getValue();
      logger.debug("Adding " + fileName + " to zip");
      ZipEntry entry = new ZipEntry(fileName);
      entry.setTime(file.lastModified());
      entry.setSize(file.length());

      entry.setMethod(ZipEntry.DEFLATED);

      zipFileEntries.put(entry, new FileInputStream(file));
    }


    makeZipFile(zipFileEntries);
  }

  public static void main(String[] args) throws IOException {
    String zipFileName = null;
    Set<File> files = Sets.newHashSet();

    for (String arg : args) {
      if (zipFileName == null) {
        zipFileName = arg;

      } else {
        File file = new File(arg);
        if (file.exists()) {
          files.add(file);
        }
      }
    }

    if (zipFileName == null) {
      usage("Missing zip file name");
    }

    if (files.isEmpty()) {
      usage("Missing files");
    }

    Map<File, String> sourceFilesAndNames =
      files.stream().collect(Collectors.toMap(file -> file, File::getPath));

    assert zipFileName != null;
    File zipFile = new File(zipFileName);

    Map<Attributes.Name, String> manifestEntries = new HashMap<>();
    manifestEntries.put(new Attributes.Name("Created-By"), System.getProperty("user.name"));
    manifestEntries.put(Attributes.Name.MANIFEST_VERSION, new Date().toString());

    new ZipFileOfFilesMaker(sourceFilesAndNames, zipFile, manifestEntries).makeZipFile();
  }

  private static void usage(String message) {
    PrintStream err = System.err;
    err.println("** " + message);
    err.println("java ZipMaker zipFileName files+");
    err.println("  ziprFileName    The name of the zip file to create");
    err.println("  files+         One or more files to include in the zip");
    err.println();

    System.exit(1);
  }
}
