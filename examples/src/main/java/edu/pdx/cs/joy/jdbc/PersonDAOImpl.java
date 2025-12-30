package edu.pdx.cs.joy.jdbc;

import edu.pdx.cs.joy.family.Person;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object implementation for managing Person entities in the database.
 */
public class PersonDAOImpl implements PersonDAO {

  private final Connection connection;

  /**
   * Creates a new PersonDAOImpl with the specified database connection.
   *
   * @param connection the database connection to use
   */
  public PersonDAOImpl(Connection connection) {
    this.connection = connection;
  }

  /**
   * Drops the persons table from the database if it exists.
   *
   * @param connection the database connection to use
   * @throws SQLException if a database error occurs
   */
  public static void dropTable(Connection connection) throws SQLException {
    try (Statement statement = connection.createStatement()) {
      statement.execute("DROP TABLE IF EXISTS persons");
    }
  }

  /**
   * Creates the persons table in the database.
   *
   * @param connection the database connection to use
   * @throws SQLException if a database error occurs
   */
  public static void createTable(Connection connection) throws SQLException {
    try (Statement statement = connection.createStatement()) {
      statement.execute(
        "CREATE TABLE IF NOT EXISTS persons (" +
        "  id INTEGER PRIMARY KEY," +
        "  gender VARCHAR(10) NOT NULL," +
        "  first_name VARCHAR(255)," +
        "  middle_name VARCHAR(255)," +
        "  last_name VARCHAR(255)," +
        "  father_id INTEGER," +
        "  mother_id INTEGER," +
        "  date_of_birth TIMESTAMP," +
        "  date_of_death TIMESTAMP," +
        "  FOREIGN KEY (father_id) REFERENCES persons(id)," +
        "  FOREIGN KEY (mother_id) REFERENCES persons(id)" +
        ")"
      );
    }
  }

  /**
   * Saves a person to the database.
   *
   * @param person the person to save
   * @throws SQLException if a database error occurs
   */
  @Override
  public void save(Person person) throws SQLException {
    String sql = "INSERT INTO persons (id, gender, first_name, middle_name, last_name, " +
                 "father_id, mother_id, date_of_birth, date_of_death) " +
                 "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

    try (PreparedStatement statement = connection.prepareStatement(sql)) {
      statement.setInt(1, person.getId());
      statement.setString(2, person.getGender().name());
      statement.setString(3, person.getFirstName());
      statement.setString(4, person.getMiddleName());
      statement.setString(5, person.getLastName());

      if (person.getFatherId() == Person.UNKNOWN) {
        statement.setNull(6, Types.INTEGER);
      } else {
        statement.setInt(6, person.getFatherId());
      }

      if (person.getMotherId() == Person.UNKNOWN) {
        statement.setNull(7, Types.INTEGER);
      } else {
        statement.setInt(7, person.getMotherId());
      }

      if (person.getDateOfBirth() == null) {
        statement.setNull(8, Types.TIMESTAMP);
      } else {
        statement.setTimestamp(8, new Timestamp(person.getDateOfBirth().getTime()));
      }

      if (person.getDateOfDeath() == null) {
        statement.setNull(9, Types.TIMESTAMP);
      } else {
        statement.setTimestamp(9, new Timestamp(person.getDateOfDeath().getTime()));
      }

      statement.executeUpdate();
    }
  }

  /**
   * Finds a person by their ID.
   *
   * @param id the ID to search for
   * @return the person with the given ID, or null if not found
   * @throws SQLException if a database error occurs
   */
  @Override
  public Person findById(int id) throws SQLException {
    String sql = "SELECT id, gender, first_name, middle_name, last_name, " +
                 "father_id, mother_id, date_of_birth, date_of_death " +
                 "FROM persons WHERE id = ?";

    try (PreparedStatement statement = connection.prepareStatement(sql)) {
      statement.setInt(1, id);

      try (ResultSet resultSet = statement.executeQuery()) {
        if (resultSet.next()) {
          return extractPersonFromResultSet(resultSet);
        }
      }
    }

    return null;
  }

  /**
   * Finds all persons in the database.
   *
   * @return a list of all persons
   * @throws SQLException if a database error occurs
   */
  @Override
  public List<Person> findAll() throws SQLException {
    List<Person> persons = new ArrayList<>();
    String sql = "SELECT id, gender, first_name, middle_name, last_name, " +
                 "father_id, mother_id, date_of_birth, date_of_death " +
                 "FROM persons ORDER BY id";

    try (Statement statement = connection.createStatement();
         ResultSet resultSet = statement.executeQuery(sql)) {
      while (resultSet.next()) {
        persons.add(extractPersonFromResultSet(resultSet));
      }
    }

    return persons;
  }

  /**
   * Updates an existing person in the database.
   *
   * @param person the person to update
   * @throws SQLException if a database error occurs
   */
  @Override
  public void update(Person person) throws SQLException {
    String sql = "UPDATE persons SET gender = ?, first_name = ?, middle_name = ?, " +
                 "last_name = ?, father_id = ?, mother_id = ?, " +
                 "date_of_birth = ?, date_of_death = ? WHERE id = ?";

    try (PreparedStatement statement = connection.prepareStatement(sql)) {
      statement.setString(1, person.getGender().name());
      statement.setString(2, person.getFirstName());
      statement.setString(3, person.getMiddleName());
      statement.setString(4, person.getLastName());

      if (person.getFatherId() == Person.UNKNOWN) {
        statement.setNull(5, Types.INTEGER);
      } else {
        statement.setInt(5, person.getFatherId());
      }

      if (person.getMotherId() == Person.UNKNOWN) {
        statement.setNull(6, Types.INTEGER);
      } else {
        statement.setInt(6, person.getMotherId());
      }

      if (person.getDateOfBirth() == null) {
        statement.setNull(7, Types.TIMESTAMP);
      } else {
        statement.setTimestamp(7, new Timestamp(person.getDateOfBirth().getTime()));
      }

      if (person.getDateOfDeath() == null) {
        statement.setNull(8, Types.TIMESTAMP);
      } else {
        statement.setTimestamp(8, new Timestamp(person.getDateOfDeath().getTime()));
      }

      statement.setInt(9, person.getId());

      int rowsAffected = statement.executeUpdate();
      if (rowsAffected == 0) {
        throw new SQLException("Update failed, no person found with ID: " + person.getId());
      }
    }
  }

  /**
   * Deletes a person from the database by ID.
   *
   * @param id the ID of the person to delete
   * @throws SQLException if a database error occurs
   */
  @Override
  public void delete(int id) throws SQLException {
    String sql = "DELETE FROM persons WHERE id = ?";

    try (PreparedStatement statement = connection.prepareStatement(sql)) {
      statement.setInt(1, id);
      int rowsAffected = statement.executeUpdate();

      if (rowsAffected == 0) {
        throw new SQLException("Delete failed, no person found with ID: " + id);
      }
    }
  }

  /**
   * Extracts a Person object from the current row of a ResultSet.
   * Note: This creates a Person without resolving parent references.
   * Parent IDs are stored in the database but parent Person objects
   * must be resolved by the caller.
   *
   * @param resultSet the result set positioned at a person row
   * @return a Person object with data from the result set
   * @throws SQLException if a database error occurs
   */
  private Person extractPersonFromResultSet(ResultSet resultSet) throws SQLException {
    int id = resultSet.getInt("id");
    String genderStr = resultSet.getString("gender");
    Person.Gender gender = Person.Gender.valueOf(genderStr);

    Person person = new Person(id, gender);
    person.setFirstName(resultSet.getString("first_name"));
    person.setMiddleName(resultSet.getString("middle_name"));
    person.setLastName(resultSet.getString("last_name"));

    // Note: Parent relationships will be resolved by FamilyTreeDAO
    // We cannot call package-protected setFatherId/setMotherId from here

    Timestamp dob = resultSet.getTimestamp("date_of_birth");
    if (dob != null) {
      person.setDateOfBirth(new java.util.Date(dob.getTime()));
    }

    Timestamp dod = resultSet.getTimestamp("date_of_death");
    if (dod != null) {
      person.setDateOfDeath(new java.util.Date(dod.getTime()));
    }

    return person;
  }

  /**
   * Helper method to get father ID from result set.
   * Package-protected to allow FamilyTreeDAO to resolve relationships.
   *
   * @param resultSet the result set
   * @return the father ID or Person.UNKNOWN if null
   * @throws SQLException if a database error occurs
   */
  int getFatherIdFromResultSet(ResultSet resultSet) throws SQLException {
    int fatherId = resultSet.getInt("father_id");
    return resultSet.wasNull() ? Person.UNKNOWN : fatherId;
  }

  /**
   * Helper method to get mother ID from result set.
   * Package-protected to allow FamilyTreeDAO to resolve relationships.
   *
   * @param resultSet the result set
   * @return the mother ID or Person.UNKNOWN if null
   * @throws SQLException if a database error occurs
   */
  int getMotherIdFromResultSet(ResultSet resultSet) throws SQLException {
    int motherId = resultSet.getInt("mother_id");
    return resultSet.wasNull() ? Person.UNKNOWN : motherId;
  }
}

