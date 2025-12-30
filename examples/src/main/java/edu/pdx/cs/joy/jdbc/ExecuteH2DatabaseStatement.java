package edu.pdx.cs.joy.jdbc;

import java.io.File;
import java.sql.*;

/**
 * A command-line program that executes SQL statements against an H2 database.
 * Supports all CRUD operations (INSERT, SELECT, UPDATE, DELETE) and displays
 * results in a human-readable format.
 */
public class ExecuteH2DatabaseStatement {

  /**
   * Main method that takes a database file path and SQL statement as arguments.
   *
   * @param args command line arguments: args[0] = database file path, args[1] = SQL statement
   * @throws SQLException if a database error occurs
   */
  public static void main(String[] args) throws SQLException {
    if (args.length < 2) {
      System.err.println("Missing required arguments");
      System.err.println("Usage: java ExecuteH2DatabaseStatement <database-file-path> <sql-statement>");
      System.err.println();
      System.err.println("Examples:");
      System.err.println("  SELECT: java ExecuteH2DatabaseStatement mydb.db \"SELECT * FROM users\"");
      System.err.println("  INSERT: java ExecuteH2DatabaseStatement mydb.db \"INSERT INTO users (name) VALUES ('John')\"");
      System.err.println("  UPDATE: java ExecuteH2DatabaseStatement mydb.db \"UPDATE users SET name='Jane' WHERE id=1\"");
      System.err.println("  DELETE: java ExecuteH2DatabaseStatement mydb.db \"DELETE FROM users WHERE id=1\"");
      return;
    }

    String dbFilePath = args[0];
    String sqlStatement = args[1];

    File dbFile = new File(dbFilePath);
    System.out.println("Connecting to H2 database: " + dbFile.getAbsolutePath());
    System.out.println("Executing SQL: " + sqlStatement);
    System.out.println();

    try (Connection connection = H2DatabaseHelper.createFileBasedConnection(dbFile)) {
      executeStatement(connection, sqlStatement);
    }
  }

  /**
   * Executes a SQL statement and displays the results.
   *
   * @param connection the database connection
   * @param sql the SQL statement to execute
   * @throws SQLException if a database error occurs
   */
  private static void executeStatement(Connection connection, String sql) throws SQLException {
    try (Statement statement = connection.createStatement()) {
      // Determine if this is a query (SELECT) or update (INSERT/UPDATE/DELETE)
      boolean isQuery = statement.execute(sql);

      if (isQuery) {
        // Handle SELECT queries
        try (ResultSet resultSet = statement.getResultSet()) {
          displayResultSet(resultSet);
        }
      } else {
        // Handle INSERT, UPDATE, DELETE
        int rowsAffected = statement.getUpdateCount();
        System.out.println("Statement executed successfully");
        System.out.println("Rows affected: " + rowsAffected);
      }
    }
  }

  /**
   * Displays a ResultSet in a formatted table.
   *
   * @param resultSet the result set to display
   * @throws SQLException if a database error occurs
   */
  private static void displayResultSet(ResultSet resultSet) throws SQLException {
    ResultSetMetaData metaData = resultSet.getMetaData();
    int columnCount = metaData.getColumnCount();

    // Calculate column widths
    int[] columnWidths = new int[columnCount];
    String[] columnNames = new String[columnCount];

    for (int i = 1; i <= columnCount; i++) {
      columnNames[i - 1] = metaData.getColumnLabel(i);
      columnWidths[i - 1] = Math.max(columnNames[i - 1].length(), 10);
    }

    // Collect all rows to calculate proper column widths
    java.util.List<String[]> rows = new java.util.ArrayList<>();
    while (resultSet.next()) {
      String[] row = new String[columnCount];
      for (int i = 1; i <= columnCount; i++) {
        Object value = resultSet.getObject(i);
        row[i - 1] = value != null ? value.toString() : "NULL";
        columnWidths[i - 1] = Math.max(columnWidths[i - 1], row[i - 1].length());
      }
      rows.add(row);
    }

    // Print header
    printSeparator(columnWidths);
    printRow(columnNames, columnWidths);
    printSeparator(columnWidths);

    // Print data rows
    if (rows.isEmpty()) {
      System.out.println("No rows returned");
    } else {
      for (String[] row : rows) {
        printRow(row, columnWidths);
      }
      printSeparator(columnWidths);
      System.out.println(rows.size() + " row(s) returned");
    }
  }

  /**
   * Prints a separator line.
   *
   * @param columnWidths array of column widths
   */
  private static void printSeparator(int[] columnWidths) {
    System.out.print("+");
    for (int width : columnWidths) {
      for (int i = 0; i < width + 2; i++) {
        System.out.print("-");
      }
      System.out.print("+");
    }
    System.out.println();
  }

  /**
   * Prints a data row.
   *
   * @param values array of values to print
   * @param columnWidths array of column widths
   */
  private static void printRow(String[] values, int[] columnWidths) {
    System.out.print("|");
    for (int i = 0; i < values.length; i++) {
      System.out.print(" ");
      System.out.print(padRight(values[i], columnWidths[i]));
      System.out.print(" |");
    }
    System.out.println();
  }

  /**
   * Pads a string to the right with spaces.
   *
   * @param str the string to pad
   * @param length the desired length
   * @return the padded string
   */
  private static String padRight(String str, int length) {
    if (str.length() >= length) {
      return str;
    }
    StringBuilder sb = new StringBuilder(str);
    while (sb.length() < length) {
      sb.append(' ');
    }
    return sb.toString();
  }
}
