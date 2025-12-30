package edu.pdx.cs.joy.jdbc;

import edu.pdx.cs.joy.family.Person;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 * Data Access Object interface for managing Person entities in the database.
 */
public interface PersonDAO {

  /**
   * Drops the persons table from the database if it exists.
   *
   * @param connection the database connection to use
   * @throws SQLException if a database error occurs
   */
  static void dropTable(Connection connection) throws SQLException {
    PersonDAOImpl.dropTable(connection);
  }

  /**
   * Creates the persons table in the database.
   *
   * @param connection the database connection to use
   * @throws SQLException if a database error occurs
   */
  static void createTable(Connection connection) throws SQLException {
    PersonDAOImpl.createTable(connection);
  }

  /**
   * Saves a person to the database.
   *
   * @param person the person to save
   * @throws SQLException if a database error occurs
   */
  void save(Person person) throws SQLException;

  /**
   * Finds a person by their ID.
   *
   * @param id the ID to search for
   * @return the person with the given ID, or null if not found
   * @throws SQLException if a database error occurs
   */
  Person findById(int id) throws SQLException;

  /**
   * Finds all persons in the database.
   *
   * @return a list of all persons
   * @throws SQLException if a database error occurs
   */
  List<Person> findAll() throws SQLException;

  /**
   * Updates an existing person in the database.
   *
   * @param person the person to update
   * @throws SQLException if a database error occurs
   */
  void update(Person person) throws SQLException;

  /**
   * Deletes a person from the database by ID.
   *
   * @param id the ID of the person to delete
   * @throws SQLException if a database error occurs
   */
  void delete(int id) throws SQLException;
}

