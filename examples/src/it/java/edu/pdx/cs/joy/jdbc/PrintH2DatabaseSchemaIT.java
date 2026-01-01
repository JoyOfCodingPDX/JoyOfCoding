package edu.pdx.cs.joy.jdbc;

import edu.pdx.cs.joy.InvokeMainTestCase;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

/**
 * Integration test for PrintH2DatabaseSchema that validates the program correctly
 * displays database schema information using the JDBC DatabaseMetaData API.
 */
public class PrintH2DatabaseSchemaIT extends InvokeMainTestCase {

  private static File tempDbFile;
  private static String dbFilePath;

  @BeforeAll
  public static void setUp() throws IOException, SQLException {
    // Create a temporary file for the database
    tempDbFile = Files.createTempFile("PrintH2DatabaseSchemaIT", ".db").toFile();
    dbFilePath = tempDbFile.getAbsolutePath();

    // Remove the .db extension since H2 will add .mv.db
    if (dbFilePath.endsWith(".db")) {
      dbFilePath = dbFilePath.substring(0, dbFilePath.length() - 3);
    }

    // Create a test database with Department, AcademicTerm, and Course tables
    createTestDatabase(dbFilePath);
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

  /**
   * Creates a test database with Department, AcademicTerm, and Course tables
   * and populates them with sample data.
   */
  private static void createTestDatabase(String dbPath) throws SQLException {
    File dbFile = new File(dbPath);

    try (Connection connection = H2DatabaseHelper.createFileBasedConnection(dbFile)) {
      // Create tables
      DepartmentDAOImpl.createTable(connection);
      AcademicTermDAOImpl.createTable(connection);
      CourseDAOImpl.createTable(connection);

      // Create DAOs
      DepartmentDAO departmentDAO = new DepartmentDAOImpl(connection);
      AcademicTermDAO termDAO = new AcademicTermDAOImpl(connection);
      CourseDAO courseDAO = new CourseDAOImpl(connection);

      // Insert sample departments
      Department csDept = new Department("Computer Science");
      departmentDAO.save(csDept);

      Department mathDept = new Department("Mathematics");
      departmentDAO.save(mathDept);

      // Insert sample academic terms
      AcademicTerm fall2024 = new AcademicTerm("Fall 2024",
          LocalDate.of(2024, 9, 1), LocalDate.of(2024, 12, 15));
      termDAO.save(fall2024);

      AcademicTerm spring2025 = new AcademicTerm("Spring 2025",
          LocalDate.of(2025, 1, 15), LocalDate.of(2025, 5, 30));
      termDAO.save(spring2025);

      // Insert sample courses
      Course javaCourse = new Course("Introduction to Java", csDept.getId(), 4);
      courseDAO.save(javaCourse);

      Course dataStructures = new Course("Data Structures", csDept.getId(), 4);
      courseDAO.save(dataStructures);

      Course calculus = new Course("Calculus I", mathDept.getId(), 4);
      courseDAO.save(calculus);
    }
  }

  @Test
  public void testPrintDatabaseSchema() {
    // Invoke the main method with the database file path
    MainMethodResult result = invokeMain(PrintH2DatabaseSchema.class, dbFilePath);

    String output = result.getTextWrittenToStandardOut();

    // Validate database information is printed
    assertThat(output, containsString("=== Database Information ==="));
    assertThat(output, containsString("Database Product: H2"));
    assertThat(output, containsString("Driver Name: H2 JDBC Driver"));

    // Validate tables section is printed
    assertThat(output, containsString("=== Tables ==="));

    // Validate DEPARTMENTS table is shown
    assertThat(output, containsString("Table: DEPARTMENTS"));
    assertThat(output, containsString("ID"));
    assertThat(output, containsString("NAME"));
    assertThat(output, containsString("Primary Keys:"));

    // Validate ACADEMIC_TERMS table is shown
    assertThat(output, containsString("Table: ACADEMIC_TERMS"));
    assertThat(output, containsString("START_DATE"));
    assertThat(output, containsString("END_DATE"));

    // Validate COURSES table is shown
    assertThat(output, containsString("Table: COURSES"));
    assertThat(output, containsString("TITLE"));
    assertThat(output, containsString("DEPARTMENT_ID"));
    assertThat(output, containsString("CREDITS"));

    // Validate foreign key relationship is shown
    assertThat(output, containsString("Foreign Keys:"));
    assertThat(output, containsString("DEPARTMENTS"));
  }

  @Test
  public void testColumnsAreDisplayed() {
    MainMethodResult result = invokeMain(PrintH2DatabaseSchema.class, dbFilePath);
    String output = result.getTextWrittenToStandardOut();

    // Verify that column details are displayed
    assertThat(output, containsString("Columns:"));
    assertThat(output, containsString("NOT NULL"));

    // Check for specific column types
    assertThat(output, containsString("BIGINT"));
    assertThat(output, containsString("CHARACTER VARYING") );
    assertThat(output, containsString("DATE"));
    assertThat(output, containsString("INTEGER"));
  }

  @Test
  public void testIndexesAreDisplayed() {
    MainMethodResult result = invokeMain(PrintH2DatabaseSchema.class, dbFilePath);
    String output = result.getTextWrittenToStandardOut();

    // Verify that index information is displayed
    assertThat(output, containsString("Indexes:"));
    assertThat(output, containsString("PRIMARY_KEY"));
  }

  @Test
  public void testMissingArgumentShowsUsage() {
    // Invoke without arguments
    MainMethodResult result = invokeMain(PrintH2DatabaseSchema.class);

    String errorOutput = result.getTextWrittenToStandardError();

    // Validate error message and usage are shown
    assertThat(errorOutput, containsString("Missing database file path argument"));
    assertThat(errorOutput, containsString("Usage: java PrintH2DatabaseSchema"));
  }

  @Test
  public void testDatabaseFilePathIsDisplayed() {
    MainMethodResult result = invokeMain(PrintH2DatabaseSchema.class, dbFilePath);
    String output = result.getTextWrittenToStandardOut();

    // Verify the database file path is shown
    assertThat(output, containsString("Reading schema from H2 database:"));
    assertThat(output, containsString(dbFilePath));
  }
}
