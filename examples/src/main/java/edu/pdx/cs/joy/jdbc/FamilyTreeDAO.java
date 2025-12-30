package edu.pdx.cs.joy.jdbc;

import edu.pdx.cs.joy.family.FamilyTree;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Data Access Object interface for managing FamilyTree entities in the database.
 */
public interface FamilyTreeDAO {

  /**
   * Drops all family tree related tables from the database if they exist.
   *
   * @param connection the database connection to use
   * @throws SQLException if a database error occurs
   */
  static void dropTables(Connection connection) throws SQLException {
    FamilyTreeDAOImpl.dropTables(connection);
  }

  /**
   * Creates all family tree related tables in the database.
   *
   * @param connection the database connection to use
   * @throws SQLException if a database error occurs
   */
  static void createTables(Connection connection) throws SQLException {
    FamilyTreeDAOImpl.createTables(connection);
  }

  /**
   * Saves a complete family tree to the database.
   * This includes all persons and marriages in the tree.
   *
   * @param familyTree the family tree to save
   * @throws SQLException if a database error occurs
   */
  void save(FamilyTree familyTree) throws SQLException;

  /**
   * Loads a complete family tree from the database.
   * This includes all persons and marriages, with relationships properly resolved.
   *
   * @return the family tree loaded from the database
   * @throws SQLException if a database error occurs
   */
  FamilyTree load() throws SQLException;
}

