package edu.pdx.cs.joy.jdbc;

import java.io.File;
import java.sql.*;

/**
 * A command-line program that uses the JDBC DatabaseMetaData API to print
 * information about the tables in an H2 database file.
 */
public class PrintH2DatabaseSchema {

  /**
   * Prints information about all tables in the database.
   *
   * @param connection the database connection
   * @throws SQLException if a database error occurs
   */
  private static void printDatabaseSchema(Connection connection) throws SQLException {
    DatabaseMetaData metaData = connection.getMetaData();

    System.out.println("=== Database Information ===");
    System.out.println("Database Product: " + metaData.getDatabaseProductName());
    System.out.println("Database Version: " + metaData.getDatabaseProductVersion());
    System.out.println("Driver Name: " + metaData.getDriverName());
    System.out.println("Driver Version: " + metaData.getDriverVersion());
    System.out.println();

    // Get all tables
    System.out.println("=== Tables ===");
    try (ResultSet tables = metaData.getTables(null, null, "%", new String[]{"TABLE"})) {
      boolean foundTables = false;

      while (tables.next()) {
        foundTables = true;
        String tableName = tables.getString("TABLE_NAME");
        String tableType = tables.getString("TABLE_TYPE");
        String remarks = tables.getString("REMARKS");

        System.out.println("\nTable: " + tableName);
        System.out.println("  Type: " + tableType);
        if (remarks != null && !remarks.isEmpty()) {
          System.out.println("  Remarks: " + remarks);
        }

        // Print columns for this table
        printTableColumns(metaData, tableName);

        // Print primary keys
        printPrimaryKeys(metaData, tableName);

        // Print foreign keys
        printForeignKeys(metaData, tableName);

        // Print indexes
        printIndexes(metaData, tableName);
      }

      if (!foundTables) {
        System.out.println("No tables found in the database.");
      }
    }
  }

  /**
   * Prints information about columns in a table.
   */
  private static void printTableColumns(DatabaseMetaData metaData, String tableName) throws SQLException {
    System.out.println("  Columns:");
    try (ResultSet columns = metaData.getColumns(null, null, tableName, "%")) {
      while (columns.next()) {
        String columnName = columns.getString("COLUMN_NAME");
        String columnType = columns.getString("TYPE_NAME");
        int columnSize = columns.getInt("COLUMN_SIZE");
        String nullable = columns.getString("IS_NULLABLE");
        String defaultValue = columns.getString("COLUMN_DEF");

        System.out.print("    - " + columnName + " " + columnType);
        if (columnSize > 0) {
          System.out.print("(" + columnSize + ")");
        }
        System.out.print(" [" + (nullable.equals("YES") ? "NULL" : "NOT NULL") + "]");
        if (defaultValue != null) {
          System.out.print(" DEFAULT " + defaultValue);
        }
        System.out.println();
      }
    }
  }

  /**
   * Prints information about primary keys in a table.
   */
  private static void printPrimaryKeys(DatabaseMetaData metaData, String tableName) throws SQLException {
    System.out.println("  Primary Keys:");
    try (ResultSet primaryKeys = metaData.getPrimaryKeys(null, null, tableName)) {
      boolean foundKeys = false;
      while (primaryKeys.next()) {
        foundKeys = true;
        String columnName = primaryKeys.getString("COLUMN_NAME");
        String pkName = primaryKeys.getString("PK_NAME");
        int keySeq = primaryKeys.getInt("KEY_SEQ");

        System.out.println("    - " + columnName + " (Key: " + pkName + ", Sequence: " + keySeq + ")");
      }
      if (!foundKeys) {
        System.out.println("    None");
      }
    }
  }

  /**
   * Prints information about foreign keys in a table.
   */
  private static void printForeignKeys(DatabaseMetaData metaData, String tableName) throws SQLException {
    System.out.println("  Foreign Keys:");
    try (ResultSet foreignKeys = metaData.getImportedKeys(null, null, tableName)) {
      boolean foundKeys = false;
      while (foreignKeys.next()) {
        foundKeys = true;
        String fkColumnName = foreignKeys.getString("FKCOLUMN_NAME");
        String pkTableName = foreignKeys.getString("PKTABLE_NAME");
        String pkColumnName = foreignKeys.getString("PKCOLUMN_NAME");
        String fkName = foreignKeys.getString("FK_NAME");

        System.out.println("    - " + fkColumnName + " -> " + pkTableName + "(" + pkColumnName + ")" +
                          (fkName != null ? " [" + fkName + "]" : ""));
      }
      if (!foundKeys) {
        System.out.println("    None");
      }
    }
  }

  /**
   * Prints information about indexes in a table.
   */
  private static void printIndexes(DatabaseMetaData metaData, String tableName) throws SQLException {
    System.out.println("  Indexes:");
    try (ResultSet indexes = metaData.getIndexInfo(null, null, tableName, false, false)) {
      boolean foundIndexes = false;
      String lastIndexName = null;
      StringBuilder indexColumns = new StringBuilder();

      while (indexes.next()) {
        String indexName = indexes.getString("INDEX_NAME");
        String columnName = indexes.getString("COLUMN_NAME");
        boolean nonUnique = indexes.getBoolean("NON_UNIQUE");

        if (indexName == null) {
          continue; // Skip table statistics
        }

        if (lastIndexName != null && !lastIndexName.equals(indexName)) {
          // Print the previous index
          System.out.println("    - " + lastIndexName + " (" + indexColumns + ")");
          indexColumns.setLength(0);
        }

        if (indexColumns.length() > 0) {
          indexColumns.append(", ");
        }
        indexColumns.append(columnName);
        lastIndexName = indexName;
        foundIndexes = true;
      }

      // Print the last index
      if (lastIndexName != null) {
        System.out.println("    - " + lastIndexName + " (" + indexColumns + ")");
      }

      if (!foundIndexes) {
        System.out.println("    None");
      }
    }
  }

  /**
   * Main method that takes a database file path and prints the schema.
   *
   * @param args command line arguments where args[0] is the path to the H2 database file
   * @throws SQLException if a database error occurs
   */
  public static void main(String[] args) throws SQLException {
    if (args.length < 1) {
      System.err.println("Missing database file path argument");
      System.err.println("Usage: java PrintH2DatabaseSchema <database-file-path>");
      return;
    }

    String dbFilePath = args[0];
    File dbFile = new File(dbFilePath);

    System.out.println("Reading schema from H2 database: " + dbFile.getAbsolutePath());
    System.out.println();

    try (Connection connection = H2DatabaseHelper.createFileBasedConnection(dbFile)) {
      printDatabaseSchema(connection);
    }
  }
}
