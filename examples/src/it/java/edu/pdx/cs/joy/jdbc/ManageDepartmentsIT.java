package edu.pdx.cs.joy.jdbc;

import edu.pdx.cs.joy.InvokeMainTestCase;
import org.junit.jupiter.api.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

/**
 * Integration tests for the ManageDepartments command-line program.
 * These tests verify that all CRUD operations work correctly when invoked via the main method.
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ManageDepartmentsIT extends InvokeMainTestCase {

  private static File tempDbFile;
  private static String dbFilePath;

  @BeforeAll
  public static void setUp() throws IOException {
    tempDbFile = Files.createTempFile("ManageDepartmentsIT", ".db").toFile();
    dbFilePath = tempDbFile.getAbsolutePath();
    // Remove the .db extension since H2 will add .mv.db
    if (dbFilePath.endsWith(".db")) {
      dbFilePath = dbFilePath.substring(0, dbFilePath.length() - 3);
    }
  }

  @AfterAll
  public static void tearDown() {
    // Clean up database files
    deleteIfExists(new File(dbFilePath + ".mv.db"));
    deleteIfExists(new File(dbFilePath + ".trace.db"));
    deleteIfExists(tempDbFile);
  }

  private static void deleteIfExists(File file) {
    if (file.exists()) {
      file.delete();
    }
  }

  @Test
  @Order(1)
  public void testCreateDepartment() {
    MainMethodResult result = invokeMain(ManageDepartments.class, dbFilePath, "create", "Computer Science");

    String output = result.getTextWrittenToStandardOut();
    assertThat(output, containsString("Successfully created department:"));
    assertThat(output, containsString("Computer Science"));
    assertThat(output, containsString("Auto-generated ID:"));
  }

  @Test
  @Order(2)
  public void testCreateSecondDepartment() {
    MainMethodResult result = invokeMain(ManageDepartments.class, dbFilePath, "create", "Mathematics");

    String output = result.getTextWrittenToStandardOut();
    assertThat(output, containsString("Successfully created department:"));
    assertThat(output, containsString("Mathematics"));
    assertThat(output, containsString("Auto-generated ID:"));
  }

  @Test
  @Order(3)
  public void testRetrieveDepartmentById() {
    MainMethodResult result = invokeMain(ManageDepartments.class, dbFilePath, "retrieve", "1");

    String output = result.getTextWrittenToStandardOut();
    assertThat(output, containsString("Found department:"));
    assertThat(output, containsString("id=1"));
    assertThat(output, containsString("Computer Science"));
  }

  @Test
  @Order(4)
  public void testRetrieveNonExistentDepartment() {
    MainMethodResult result = invokeMain(ManageDepartments.class, dbFilePath, "retrieve", "999");

    String output = result.getTextWrittenToStandardOut();
    assertThat(output, containsString("No department found with ID: 999"));
  }

  @Test
  @Order(5)
  public void testListAllDepartments() {
    MainMethodResult result = invokeMain(ManageDepartments.class, dbFilePath, "list");

    String output = result.getTextWrittenToStandardOut();
    assertThat(output, containsString("Found 2 department(s)"));
    assertThat(output, containsString("Computer Science"));
    assertThat(output, containsString("Mathematics"));
  }

  @Test
  @Order(6)
  public void testUpdateDepartment() {
    MainMethodResult result = invokeMain(ManageDepartments.class, dbFilePath, "update", "1", "CS Department");

    String output = result.getTextWrittenToStandardOut();
    assertThat(output, containsString("Successfully updated department:"));
    assertThat(output, containsString("id=1"));
    assertThat(output, containsString("CS Department"));
  }

  @Test
  @Order(7)
  public void testRetrieveUpdatedDepartment() {
    MainMethodResult result = invokeMain(ManageDepartments.class, dbFilePath, "retrieve", "1");

    String output = result.getTextWrittenToStandardOut();
    assertThat(output, containsString("CS Department"));
    assertThat(output, not(containsString("Computer Science")));
  }

  @Test
  @Order(8)
  public void testDeleteDepartment() {
    MainMethodResult result = invokeMain(ManageDepartments.class, dbFilePath, "delete", "2");

    String output = result.getTextWrittenToStandardOut();
    assertThat(output, containsString("Successfully deleted department:"));
    assertThat(output, containsString("id=2"));
    assertThat(output, containsString("Mathematics"));
  }

  @Test
  @Order(9)
  public void testListAfterDelete() {
    MainMethodResult result = invokeMain(ManageDepartments.class, dbFilePath, "list");

    String output = result.getTextWrittenToStandardOut();
    assertThat(output, containsString("Found 1 department(s)"));
    assertThat(output, containsString("CS Department"));
    assertThat(output, not(containsString("Mathematics")));
  }

  @Test
  public void testMissingArguments() {
    MainMethodResult result = invokeMain(ManageDepartments.class, dbFilePath);

    String errorOutput = result.getTextWrittenToStandardError();
    assertThat(errorOutput, containsString("Usage: java ManageDepartments"));
  }

  @Test
  public void testUnknownCommand() {
    MainMethodResult result = invokeMain(ManageDepartments.class, dbFilePath, "invalid");

    String errorOutput = result.getTextWrittenToStandardError();
    assertThat(errorOutput, containsString("Unknown command: invalid"));
    assertThat(errorOutput, containsString("Usage:"));
  }

  @Test
  public void testCreateWithoutName() {
    MainMethodResult result = invokeMain(ManageDepartments.class, dbFilePath, "create");

    String errorOutput = result.getTextWrittenToStandardError();
    assertThat(errorOutput, containsString("Missing department name for create command"));
  }

  @Test
  public void testRetrieveWithoutId() {
    MainMethodResult result = invokeMain(ManageDepartments.class, dbFilePath, "retrieve");

    String errorOutput = result.getTextWrittenToStandardError();
    assertThat(errorOutput, containsString("Missing department ID for retrieve command"));
  }

  @Test
  public void testUpdateWithMissingArguments() {
    MainMethodResult result = invokeMain(ManageDepartments.class, dbFilePath, "update", "1");

    String errorOutput = result.getTextWrittenToStandardError();
    assertThat(errorOutput, containsString("Missing arguments for update command"));
  }

  @Test
  public void testDeleteWithoutId() {
    MainMethodResult result = invokeMain(ManageDepartments.class, dbFilePath, "delete");

    String errorOutput = result.getTextWrittenToStandardError();
    assertThat(errorOutput, containsString("Missing department ID for delete command"));
  }
}
