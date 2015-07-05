package edu.pdx.cs410J.grader;

import com.google.common.collect.Sets;
import com.google.common.io.ByteStreams;

import java.io.*;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
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
    ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(zipFile));
    zos.setMethod(ZipOutputStream.DEFLATED);

    // Create a Manifest for the Zip file containing the name of the
    // author (userName) and a version that is based on the current
    // date/time.
    writeManifestAsEntryInZipFile(zos);

    // Add the source files to the Zip
    for (Map.Entry<File, String> fileEntry : sourceFilesAndNames.entrySet()) {
      File file = fileEntry.getKey();
      String fileName = fileEntry.getValue();
      System.out.println("Adding " + fileName + " to zip");
      ZipEntry entry = new ZipEntry(fileName);
      entry.setTime(file.lastModified());
      entry.setSize(file.length());

      entry.setMethod(ZipEntry.DEFLATED);

      // Add the entry to the ZIP file
      zos.putNextEntry(entry);

      ByteStreams.copy(new FileInputStream(file), zos);

      zos.closeEntry();
    }

    zos.close();

    return zipFile;
  }

  private void writeManifestAsEntryInZipFile(ZipOutputStream zos) throws IOException {
    Manifest manifest = new Manifest();
    addEntriesToMainManifest(manifest);

    String entryName = JarFile.MANIFEST_NAME;

    System.out.println("Adding " + entryName + " to zip");
    ZipEntry entry = new ZipEntry(entryName);
    entry.setTime(System.currentTimeMillis());
    entry.setMethod(ZipEntry.DEFLATED);

    zos.putNextEntry(entry);
    manifest.write(new BufferedOutputStream(zos));
    zos.closeEntry();
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

    new ZipMaker(sourceFilesAndNames, zipFile, manifestEntries).makeZipFile();
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
