package edu.pdx.cs410J.family;

import org.junit.Test;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.StringTokenizer;

/**
 * This class tests the behavior of the {@link AddPerson} program to
 * ensure that I don't again suffer the kind of embarrassment I
 * suffered in the Spring of 2002 when my students found a bunch of
 * bugs in my code.
 */
public class AddPersonTest {

  private static final boolean DEBUG =
    Boolean.getBoolean("AddPersonTest.DEBUG");

  /**
   * Invokes the main method of AddPerson with the given command line
   * string 
   */
  private void addPerson(String cmdLine) {
    if (DEBUG) {
      System.out.println("\n" + cmdLine + "\n");
    }

    // Create an array of Strings to send to main
    StringTokenizer st = new StringTokenizer(cmdLine);
    String[] args = new String[st.countTokens()];
    for (int i = 0; st.hasMoreTokens(); i++) {
      args[i] = st.nextToken();
    }

    AddPerson.main(args);
    reset(AddPerson.class);
  }

  /**
   * "Resets" all of the static fields in a given class to their
   * default value
   */
  private static void reset(Class c) {
    Field[] fields = c.getDeclaredFields();
    for (int i = 0; i < fields.length; i++) {
      Field field = fields[i];
      if (!Modifier.isFinal(field.getModifiers())) {
        field.setAccessible(true);
        try {
          reset(field);

        } catch (IllegalAccessException ex) {
          StringWriter sw = new StringWriter();
          sw.write("While accessing field " + field + ": ");
          ex.printStackTrace(new PrintWriter(sw, true));
          fail(sw.toString());
        }
      }
    }
  }

  /**
   * "Resets" the value of the given static field to its default value
   */
  private static void reset(Field field)
    throws IllegalAccessException {

    Class type = field.getType();
    if (type.equals(boolean.class)) {
      field.set(null, Boolean.FALSE);

    } else if (type.equals(char.class)) {
      field.set(null, '\0' );

    } else if (type.equals(byte.class)) {
      field.set(null, (byte) 0 );

    } else if (type.equals(short.class)) {
      field.set(null, (short) 0 );

    } else if (type.equals(int.class)) {
      field.set(null, 0 );

    } else if (type.equals(long.class)) {
      field.set(null, 0L );

    } else if (type.equals(float.class)) {
      field.set(null, 0.0f );

    } else if (type.equals(double.class)) {
      field.set(null, 0.0 );

    } else {
      assert Object.class.isAssignableFrom(type) : type;
      field.set(null, null);
    }
  }
  
  /**
   * Invokes the main method of NoteMarriage with the given command line
   * string 
   */
  private void noteMarriage(String cmdLine) {
    if (DEBUG) {
      System.out.println("\n" + cmdLine + "\n");
    }

    // Create an array of Strings to send to main
    StringTokenizer st = new StringTokenizer(cmdLine);
    String[] args = new String[st.countTokens()];
    for (int i = 0; st.hasMoreTokens(); i++) {
      args[i] = st.nextToken();
    }

    NoteMarriage.main(args);
    reset(NoteMarriage.class);
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

    
    addPerson(fileOption + fileName +
              " 1 male David Michael Whitlock");
    
    addPerson(fileOption + "-parentOf 1 " + fileName + 
              " 2 male Stanley Jay Whitlock Feb 27, 1948");

    addPerson(fileOption + "-parentOf 1 " + fileName + 
              " 3 female Carolyn Joyce Granger May 17, 1945");

    noteMarriage(fileOption + "-date Jul 12, 1969 " + fileName +
                 " 2 3 ");

    // If we get this far, delete the file
    file.delete();
  }

  ////////  Test cases

  @Test
  public void testText() {
    createDavesFamily("");
  }

  @Test
  public void testXml() {
    createDavesFamily("-xml ");
  }
}
