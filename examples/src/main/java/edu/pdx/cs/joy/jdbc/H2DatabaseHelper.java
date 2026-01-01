package edu.pdx.cs.joy.jdbc;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Helper class for creating connections to H2 databases.
 * Provides factory methods for both in-memory and file-based H2 databases.
 */
public class H2DatabaseHelper {

  /**
   * Creates a connection to an in-memory H2 database.
   * The database will persist as long as at least one connection remains open
   * due to the DB_CLOSE_DELAY=-1 parameter.
   *
   * @param databaseName the name of the in-memory database
   * @return a connection to the in-memory H2 database
   * @throws SQLException if a database error occurs
   */
  public static Connection createInMemoryConnection(String databaseName) throws SQLException {
    return DriverManager.getConnection("jdbc:h2:mem:" + databaseName + ";DB_CLOSE_DELAY=-1");
  }

  /**
   * Creates a connection to a file-based H2 database.
   * The database will be persisted to a file at the specified path.
   *
   * @param databaseFilesDirectory the database file (without the .mv.db extension)
   * @return a connection to the file-based H2 database
   * @throws SQLException if a database error occurs
   */
  public static Connection createFileBasedConnection(File databaseFilesDirectory) throws SQLException {
    return DriverManager.getConnection("jdbc:h2:" + databaseFilesDirectory.getAbsolutePath());
  }
}
