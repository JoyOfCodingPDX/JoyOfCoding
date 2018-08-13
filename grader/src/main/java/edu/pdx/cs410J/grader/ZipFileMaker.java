package edu.pdx.cs410J.grader;

import com.google.common.io.ByteStreams;

import java.io.*;
import java.util.Date;
import java.util.Map;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ZipFileMaker {
  protected final Map<Attributes.Name, String> manifestEntries;
  protected final File zipFile;

  public ZipFileMaker(File zipFile, Map<Attributes.Name, String> manifestEntries) {
    this.zipFile = zipFile;
    this.manifestEntries = manifestEntries;
  }

  protected File makeZipFile(Map<ZipEntry, InputStream> zipFileEntries) throws IOException {
    ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(zipFile));
    zos.setMethod(ZipOutputStream.DEFLATED);

    // Create a Manifest for the Zip file containing the name of the
    // author (userName) and a version that is based on the current
    // date/time.
    writeManifestAsEntryInZipFile(zos);

    // Add the source files to the Zip
    for (Map.Entry<ZipEntry, InputStream> entry : zipFileEntries.entrySet()) {
      // Add the entry to the ZIP file
      zos.putNextEntry(entry.getKey());

      ByteStreams.copy(entry.getValue(), zos);

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
}
