package edu.pdx.cs399J.family.tests;

import edu.pdx.cs399J.family.*;
import java.io.*;
import java.util.*;
import junit.framework.*;

/**
 * This class tests the behavior of the {@link AddPerson} program to
 * ensure that I don't again suffer the kind of embarrassment I suffer
 * in the Spring of 2002 when my students found a bunch of bugs in my
 * code.
 */
public class AddPersonTest extends TestCase {

  public AddPersonTest(String name) {
    super(name);
  }

  ////////  Abstract methods

  /**
   * Returns the option for what kind of file to use
   */

  ////////  Helper methods

  /**
   * Invokes the main method of AddPerson with the given command line
   * string 
   */
  private void addPerson(String cmdLine) {
    // Create an array of Strings to send to main
    StringTokenizer st = new StringTokenizer(cmdLine);
    String[] args = new String[st.countTokens()];
    for (int i = 0; st.hasMoreTokens(); i++) {
      args[i] = st.nextToken();
    }

    AddPerson.main(args);
  }
  
  /**
   * Invokes the main method of NoteMarriage with the given command line
   * string 
   */
  private void noteMarriage(String cmdLine) {
    // Create an array of Strings to send to main
    StringTokenizer st = new StringTokenizer(cmdLine);
    String[] args = new String[st.countTokens()];
    for (int i = 0; st.hasMoreTokens(); i++) {
      args[i] = st.nextToken();
    }

    NoteMarriage.main(args);
  }
  
  /**
   * Create a family tree that describes Dave's family.
   *
   * @param fileOption
   *        The kind of file to use
   */
  private void createDavesFamily(String fileOption) {
    // Create a temp file to write the family tree to
    File file;
    try {
      file = File.createTempFile("DavesFamily", fileOption);
 
      // XML parser complains about empty file, so delete it before we
      // start
      file.delete();

    } catch (IOException ex) {
      fail("Couldn't create temp file");
      return;
    }

    String fileName = file.getAbsolutePath();

    
    addPerson("-id 1 -file " + fileName + " -gender male " +
              "-firstName David -middleName Michael " +
              "-lastName Whitlock " + fileOption);
    
    addPerson("-id 2 -file " + fileName + " -gender male " +
              "-firstName Stanley -middleName Jay " +
              "-lastName Whitlock -dob Feb 27, 1948 -parentOf 1 " +
              fileOption);

    addPerson("-id 3 -file " + fileName + " -gender female " +
              "-firstName Carolyn -middleName Joyce " +
              "-lastName Granger -dob Feb 27, 1948 -parentOf 1 " +
              fileOption);

    noteMarriage("-husbandId 2 -wifeId 3 -date Jun 12, 1969 " +
                 "-file " + fileName + " " + fileOption);

    // If we get this far, delete the file
    file.delete();
  }

  ////////  Test cases

  public void testText() {
    createDavesFamily("-text");
  }

  public void testXml() {
    createDavesFamily("-xml");
  }
}
