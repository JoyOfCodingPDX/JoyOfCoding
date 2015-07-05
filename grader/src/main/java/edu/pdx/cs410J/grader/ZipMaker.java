package edu.pdx.cs410J.grader;

import com.google.common.collect.Sets;
import com.google.common.io.ByteStreams;

import java.io.*;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.jar.Attributes;
import java.util.jar.Manifest;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

class ZipMaker {

  private final Map<Attributes.Name, String> manifestEntries;
  private final Map<File, String> sourceFilesAndNames;
  private final File zipFile;

  public ZipMaker(Map<File, String> sourceFilesAndNames, File zipFile, Map<Attributes.Name, String> manifestEntries) {
    this.sourceFilesAndNames = sourceFilesAndNames;
    this.zipFile = zipFile;
    this.manifestEntries = manifestEntries;
  }

  public File makeZipFile() throws IOException {
    // Create a Manifest for the Jar file containing the name of the
    // author (userName) and a version that is based on the current
    // date/time.
    Manifest manifest = new Manifest();
    addEntriesToMainManifest(manifest);

    ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(zipFile));
    zos.setMethod(ZipOutputStream.DEFLATED);

    // Add the source files to the Jar
    for (Map.Entry<File, String> fileEntry : sourceFilesAndNames.entrySet()) {
      File file = fileEntry.getKey();
      String fileName = fileEntry.getValue();
      System.out.println("Adding " + fileName + " to jar");
      ZipEntry entry = new ZipEntry(fileName);
      entry.setTime(file.lastModified());
      entry.setSize(file.length());

      entry.setMethod(ZipEntry.DEFLATED);

      // Add the entry to the JAR file
      zos.putNextEntry(entry);

      ByteStreams.copy(new FileInputStream(file), zos);

      zos.closeEntry();
    }

    zos.close();

    return zipFile;
  }

  private void addEntriesToMainManifest(Manifest manifest) {
    Attributes attrs = manifest.getMainAttributes();

    // If a manifest doesn't have a version, the other attributes won't get written out.  Lame.
    attrs.put(Attributes.Name.MANIFEST_VERSION, new Date().toString());

    for (Map.Entry<Attributes.Name, String> entry : this.manifestEntries.entrySet()) {
      attrs.put(entry.getKey(), entry.getValue());
    }
  }

  public static void main(String[] args) throws IOException {
    String jarFileName = null;
    Set<File> files = Sets.newHashSet();

    for (String arg : args) {
      if (jarFileName == null) {
        jarFileName = arg;

      } else {
        File file = new File(arg);
        if (file.exists()) {
          files.add(file);
        }
      }
    }

    if (jarFileName == null) {
      usage("Missing jar file name");
    }

    if (files.isEmpty()) {
      usage("Missing files");
    }

    Map<File, String> sourceFilesAndNames =
      files.stream().collect(Collectors.toMap(file -> file, File::getPath));

    assert jarFileName != null;
    File jarFile = new File(jarFileName);

    Map<Attributes.Name, String> manifestEntries = new HashMap<>();
    manifestEntries.put(new Attributes.Name("Created-By"), System.getProperty("user.name"));
    manifestEntries.put(Attributes.Name.MANIFEST_VERSION, new Date().toString());

    new ZipMaker(sourceFilesAndNames, jarFile, manifestEntries).makeZipFile();
  }

  private static void usage(String message) {
    PrintStream err = System.err;
    err.println("** " + message);
    err.println("java ZipMaker jarFileName files+");
    err.println("  jarFileName    The name of the jar file to create");
    err.println("  files+         One or more files to include in the jar");
    err.println();

    System.exit(1);
  }
}
