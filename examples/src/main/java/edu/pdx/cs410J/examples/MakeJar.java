package edu.pdx.cs410J.examples;

import java.io.*;
import java.util.*;
import java.util.jar.*;
import java.util.zip.*;

/**
 * This class demonstrates the file compression utilities in the
 * <code>java.util.zip</code> and <code>java.util.jar</code> packages
 * by creating a jar file whose contents are specified on the command
 * line.
 *
 * @author David Whitlock
 */
public class MakeJar {

  private static PrintWriter err = new PrintWriter(System.err, true);

  /**
   * Prints out information about how this program is used.
   */
  private static void usage() {
    err.println("usage: MakeJar [options] jarFile [file]+");
    err.println("  Where [options] are:");
    err.println("  -nocompress    Don't compress Jar file");
    err.println("  -author name   Author of Jar file (default: CS410J)");
    err.println("  -version n     Version of Jar file " +
		"(default: 1.0)");
    System.exit(1);
  }

  /**
   * Reads the name of the Jar file followed by the names of the files
   * to be added to the jar file from the command line.
   */
  public static void main(String[] args) {
    String jarFileName = null;
    Set<String> fileNames = new HashSet<String>();
    boolean compress = true;
    String author = "CS410J";   // Author of Jar file
    String version = "1.0";     // Version of Jar file

    // Parse the command line
    for (int i = 0; i < args.length; i++) {
      if (args[i].equals("-nocompress")) {
	compress = false;

      } else if (args[i].equals("-author")) {
	if(++i >= args.length) {
	  err.println("** Missing author name");
	  usage();
	}

	author = args[i];

      } else if (args[i].equals("-version")) {
	if(++i >= args.length) {
	  err.println("** Missing version");
	  usage();
	}

	version = args[i];

      } else if (jarFileName == null) {
	jarFileName = args[i];

      } else {
	// Add this file to the Jar
	fileNames.add(args[i]);
      }
    }

    if (jarFileName == null) {
      err.println("** No Jar file specified");
      usage();
    }

    if (fileNames.isEmpty()) {
      err.println("** No files specified");
      usage();
    }

    // Make note of the author and version in the Manifest file in the
    // Jar
    Manifest manifest = new Manifest();
    Attributes global = manifest.getMainAttributes();
    global.put(Attributes.Name.MANIFEST_VERSION, version);
    global.put(new Attributes.Name("Created-By"), author);

    // Create a JarOutputStream around the jar file
    JarOutputStream jos = null;
    try {
      File jarFile = new File(jarFileName);
      OutputStream os = new FileOutputStream(jarFile);
      jos = new JarOutputStream(os, manifest);
 
    } catch (IOException ex) {
      err.println("** IOException: " + ex.getMessage());
      System.exit(1);
    }

    if (compress) {
      jos.setMethod(JarOutputStream.DEFLATED);
    } else {
      jos.setMethod(JarOutputStream.STORED);
    }

    // Now open all the files and add them to the JAR file
    Iterator names = fileNames.iterator();
    while (names.hasNext()) {
      String fileName = (String) names.next();
      try {
	File file = new File(fileName);

	JarEntry entry = new JarEntry(fileName);
	entry.setTime((new Date()).getTime());
	entry.setSize(file.length());

	InputStream is = 
	  new BufferedInputStream(new FileInputStream(file));
	byte[] buffer = new byte[1024];
	int read = 0;

	if(compress) {
	  entry.setMethod(JarEntry.DEFLATED);

	} else {
	  entry.setMethod(JarEntry.STORED);

	  // Compute the checksum of the file using CRC32
	  CRC32 checksum = new CRC32();
	  checksum.reset();
	  long total = 0;
	  while ((read = is.read(buffer)) != -1) {
	    checksum.update(buffer, 0, read);
	    total += read;
	  }
	  if (total != file.length()) {
	    throw new JarException("File length problems during " +
				   file.getPath() + " (" + total + 
				   " out of " + file.length() + ")");
	  }
	  entry.setCrc(checksum.getValue());
	}

	// Add the entry to the JAR file
	jos.putNextEntry(entry);
	is = new BufferedInputStream(new FileInputStream(file));
	while((read = is.read(buffer, 0, buffer.length)) != -1) {
	  jos.write(buffer, 0, read);
	}
	is.close();
	jos.closeEntry();

      } catch (IOException ex) {
	err.println("** IOException: " + ex.getMessage());
	System.exit(1);
      }
    }

  }

}
