package edu.pdx.cs.joy.family;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 * Data Access Object interface for managing Marriage entities in the database.
 */
public interface MarriageDAO {

  /**
   * Drops the marriages table from the database if it exists.
   *
   * @param connection the database connection to use
   * @throws SQLException if a database error occurs
   */
  static void dropTable(Connection connection) throws SQLException {
    MarriageDAOImpl.dropTable(connection);
  }

  /**
   * Creates the marriages table in the database.
   *
   * @param connection the database connection to use
   * @throws SQLException if a database error occurs
   */
  static void createTable(Connection connection) throws SQLException {
    MarriageDAOImpl.createTable(connection);
  }

  /**
   * Saves a marriage to the database.
   *
   * @param marriage the marriage to save
   * @throws SQLException if a database error occurs
   */
  void save(Marriage marriage) throws SQLException;

  /**
   * Finds all marriages for a specific person ID.
   *
   * @param personId the person ID
   * @return a list of marriages involving the person
   * @throws SQLException if a database error occurs
   */
  List<Marriage> findByPersonId(int personId) throws SQLException;

  /**
   * Finds all marriages in the database.
   *
   * @return a list of all marriages
   * @throws SQLException if a database error occurs
   */
  List<Marriage> findAll() throws SQLException;

  /**
   * Deletes a marriage from the database.
   *
   * @param husbandId the husband's ID
   * @param wifeId the wife's ID
   * @throws SQLException if a database error occurs
   */
  void delete(int husbandId, int wifeId) throws SQLException;
}

