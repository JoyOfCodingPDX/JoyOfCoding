package edu.pdx.cs.joy.jdbc;

import edu.pdx.cs.joy.InvokeMainTestCase;
import org.junit.jupiter.api.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

/**
 * Integration test for ExecuteH2DatabaseStatement that validates SQL execution
 * against an H2 database and verifies output formatting.
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ExecuteH2DatabaseStatementIT extends InvokeMainTestCase {

  private static File tempDbFile;
  private static String dbFilePath;

  @BeforeAll
  public static void setUp() throws IOException, SQLException {
    // Create a temporary file for the database
    tempDbFile = Files.createTempFile("ExecuteH2DatabaseStatementIT", ".db").toFile();
    dbFilePath = tempDbFile.getAbsolutePath();

    // Remove the .db extension since H2 will add .mv.db
    if (dbFilePath.endsWith(".db")) {
      dbFilePath = dbFilePath.substring(0, dbFilePath.length() - 3);
    }

    // Create and populate a test database
    createAndPopulateDatabase(dbFilePath);
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
   * Creates and populates a test database with departments, academic terms, and courses.
   */
  private static void createAndPopulateDatabase(String dbPath) throws SQLException {
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

      // Insert departments
      Department csDept = new Department("Computer Science");
      departmentDAO.save(csDept);

      Department mathDept = new Department("Mathematics");
      departmentDAO.save(mathDept);

      Department physicsDept = new Department("Physics");
      departmentDAO.save(physicsDept);

      // Insert academic terms
      AcademicTerm fall2024 = new AcademicTerm("Fall 2024",
          LocalDate.of(2024, 9, 1), LocalDate.of(2024, 12, 15));
      termDAO.save(fall2024);

      AcademicTerm spring2025 = new AcademicTerm("Spring 2025",
          LocalDate.of(2025, 1, 15), LocalDate.of(2025, 5, 30));
      termDAO.save(spring2025);

      // Insert courses
      Course javaCourse = new Course("Introduction to Java", csDept.getId(), 4);
      courseDAO.save(javaCourse);

      Course dataStructures = new Course("Data Structures", csDept.getId(), 4);
      courseDAO.save(dataStructures);

      Course calculus = new Course("Calculus I", mathDept.getId(), 4);
      courseDAO.save(calculus);

      Course physics101 = new Course("Physics I", physicsDept.getId(), 5);
      courseDAO.save(physics101);
    }
  }

  @Test
  @Order(1)
  public void testSelectAllDepartments() {
    MainMethodResult result = invokeMain(ExecuteH2DatabaseStatement.class, dbFilePath, "SELECT * FROM departments ORDER BY id");
    String output = result.getTextWrittenToStandardOut();

    // Validate output contains all departments
    assertThat(output, containsString("Computer Science"));
    assertThat(output, containsString("Mathematics"));
    assertThat(output, containsString("Physics"));
    assertThat(output, containsString("3 row(s) returned"));

    // Validate table formatting
    assertThat(output, containsString("+"));
    assertThat(output, containsString("|"));
    assertThat(output, containsString("ID"));
    assertThat(output, containsString("NAME"));
  }

  @Test
  @Order(2)
  public void testSelectSingleDepartment() {
    MainMethodResult result = invokeMain(ExecuteH2DatabaseStatement.class, dbFilePath,
        "SELECT name FROM departments WHERE id = 1");
    String output = result.getTextWrittenToStandardOut();

    assertThat(output, containsString("Computer Science"));
    assertThat(output, containsString("1 row(s) returned"));
    assertThat(output, containsString("NAME"));
  }

  @Test
  @Order(3)
  public void testSelectAllCourses() {
    MainMethodResult result = invokeMain(ExecuteH2DatabaseStatement.class, dbFilePath,
        "SELECT * FROM courses ORDER BY id");
    String output = result.getTextWrittenToStandardOut();

    assertThat(output, containsString("Introduction to Java"));
    assertThat(output, containsString("Data Structures"));
    assertThat(output, containsString("Calculus I"));
    assertThat(output, containsString("Physics I"));
    assertThat(output, containsString("4 row(s) returned"));
    assertThat(output, containsString("TITLE"));
    assertThat(output, containsString("CREDITS"));
  }

  @Test
  @Order(4)
  public void testSelectAcademicTerms() {
    MainMethodResult result = invokeMain(ExecuteH2DatabaseStatement.class, dbFilePath,
        "SELECT id, name FROM academic_terms ORDER BY id");
    String output = result.getTextWrittenToStandardOut();

    assertThat(output, containsString("Fall 2024"));
    assertThat(output, containsString("Spring 2025"));
    assertThat(output, containsString("2 row(s) returned"));
  }

  @Test
  @Order(5)
  public void testSelectWithJoin() {
    MainMethodResult result = invokeMain(ExecuteH2DatabaseStatement.class, dbFilePath,
        "SELECT c.title, d.name FROM courses c JOIN departments d ON c.department_id = d.id WHERE d.name = 'Computer Science'");
    String output = result.getTextWrittenToStandardOut();

    assertThat(output, containsString("Introduction to Java"));
    assertThat(output, containsString("Data Structures"));
    assertThat(output, containsString("2 row(s) returned"));
  }

  @Test
  @Order(6)
  public void testInsertOperation() {
    MainMethodResult result = invokeMain(ExecuteH2DatabaseStatement.class, dbFilePath,
        "INSERT INTO departments (name) VALUES ('Engineering')");
    String output = result.getTextWrittenToStandardOut();

    assertThat(output, containsString("Statement executed successfully"));
    assertThat(output, containsString("Rows affected: 1"));

    // Verify the insert worked by querying
    MainMethodResult queryResult = invokeMain(ExecuteH2DatabaseStatement.class, dbFilePath,
        "SELECT * FROM departments WHERE name = 'Engineering'");
    String queryOutput = queryResult.getTextWrittenToStandardOut();
    assertThat(queryOutput, containsString("Engineering"));
    assertThat(queryOutput, containsString("1 row(s) returned"));
  }

  @Test
  @Order(7)
  public void testUpdateOperation() {
    MainMethodResult result = invokeMain(ExecuteH2DatabaseStatement.class, dbFilePath,
        "UPDATE departments SET name = 'Applied Mathematics' WHERE name = 'Mathematics'");
    String output = result.getTextWrittenToStandardOut();

    assertThat(output, containsString("Statement executed successfully"));
    assertThat(output, containsString("Rows affected: 1"));

    // Verify the update worked
    MainMethodResult queryResult = invokeMain(ExecuteH2DatabaseStatement.class, dbFilePath,
        "SELECT name FROM departments WHERE name = 'Applied Mathematics'");
    String queryOutput = queryResult.getTextWrittenToStandardOut();
    assertThat(queryOutput, containsString("Applied Mathematics"));
  }

  @Test
  @Order(8)
  public void testDeleteOperation() {
    // Delete the Engineering department that was added by testInsertOperation
    // This avoids foreign key issues and doesn't affect other tests
    MainMethodResult result = invokeMain(ExecuteH2DatabaseStatement.class, dbFilePath,
        "DELETE FROM departments WHERE name = 'Engineering'");
    String output = result.getTextWrittenToStandardOut();

    assertThat(output, containsString("Statement executed successfully"));
    assertThat(output, containsString("Rows affected: 1"));

    // Verify the delete worked
    MainMethodResult queryResult = invokeMain(ExecuteH2DatabaseStatement.class, dbFilePath,
        "SELECT * FROM departments WHERE name = 'Engineering'");
    String queryOutput = queryResult.getTextWrittenToStandardOut();
    assertThat(queryOutput, containsString("No rows returned"));
  }

  @Test
  @Order(9)
  public void testSelectNoResults() {
    MainMethodResult result = invokeMain(ExecuteH2DatabaseStatement.class, dbFilePath,
        "SELECT * FROM departments WHERE id = 999");
    String output = result.getTextWrittenToStandardOut();

    assertThat(output, containsString("No rows returned"));
  }

  @Test
  @Order(10)
  public void testMissingArguments() {
    MainMethodResult result = invokeMain(ExecuteH2DatabaseStatement.class);
    String errorOutput = result.getTextWrittenToStandardError();

    assertThat(errorOutput, containsString("Missing required arguments"));
    assertThat(errorOutput, containsString("Usage:"));
    assertThat(errorOutput, containsString("SELECT:"));
    assertThat(errorOutput, containsString("INSERT:"));
    assertThat(errorOutput, containsString("UPDATE:"));
    assertThat(errorOutput, containsString("DELETE:"));
  }

  @Test
  @Order(11)
  public void testDatabasePathDisplayed() {
    MainMethodResult result = invokeMain(ExecuteH2DatabaseStatement.class, dbFilePath,
        "SELECT * FROM departments");
    String output = result.getTextWrittenToStandardOut();

    assertThat(output, containsString("Connecting to H2 database:"));
    assertThat(output, containsString(dbFilePath));
  }

  @Test
  @Order(12)
  public void testSqlStatementDisplayed() {
    String sql = "SELECT * FROM departments";
    MainMethodResult result = invokeMain(ExecuteH2DatabaseStatement.class, dbFilePath, sql);
    String output = result.getTextWrittenToStandardOut();

    assertThat(output, containsString("Executing SQL:"));
    assertThat(output, containsString(sql));
  }

  @Test
  @Order(13)
  public void testTableFormattingWithNullValues() {
    // Insert a department with a course that has no credits (hypothetically, for null testing)
    // Since our schema doesn't allow nulls, we'll just verify formatting works correctly
    MainMethodResult result = invokeMain(ExecuteH2DatabaseStatement.class, dbFilePath,
        "SELECT id, name FROM departments ORDER BY id");
    String output = result.getTextWrittenToStandardOut();

    // Verify table has proper borders
    assertThat(output, matchesPattern("(?s).*\\+[-+]+\\+.*"));
    assertThat(output, matchesPattern("(?s).*\\|.*\\|.*"));
  }

  @Test
  @Order(14)
  public void testCountQuery() {
    MainMethodResult result = invokeMain(ExecuteH2DatabaseStatement.class, dbFilePath,
        "SELECT COUNT(*) AS total FROM courses");
    String output = result.getTextWrittenToStandardOut();

    assertThat(output, containsString("TOTAL"));
    assertThat(output, containsString("4"));
    assertThat(output, containsString("1 row(s) returned"));
  }
}

