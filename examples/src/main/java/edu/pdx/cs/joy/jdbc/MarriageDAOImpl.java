package edu.pdx.cs.joy.jdbc;

import edu.pdx.cs.joy.family.Marriage;
import edu.pdx.cs.joy.family.Person;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Data Access Object implementation for managing Marriage entities in the database.
 */
public class MarriageDAOImpl implements MarriageDAO {

  private final Connection connection;
  private final Map<Integer, Person> personCache;

  /**
   * Creates a new MarriageDAOImpl with the specified database connection.
   *
   * @param connection the database connection to use
   * @param personCache a cache of persons to resolve marriage partners
   */
  public MarriageDAOImpl(Connection connection, Map<Integer, Person> personCache) {
    this.connection = connection;
    this.personCache = personCache;
  }

  /**
   * Drops the marriages table from the database if it exists.
   *
   * @param connection the database connection to use
   * @throws SQLException if a database error occurs
   */
  public static void dropTable(Connection connection) throws SQLException {
    try (Statement statement = connection.createStatement()) {
      statement.execute("DROP TABLE IF EXISTS marriages");
    }
  }

  /**
   * Creates the marriages table in the database.
   *
   * @param connection the database connection to use
   * @throws SQLException if a database error occurs
   */
  public static void createTable(Connection connection) throws SQLException {
    try (Statement statement = connection.createStatement()) {
      statement.execute(
        "CREATE TABLE IF NOT EXISTS marriages (" +
        "  husband_id INTEGER NOT NULL," +
        "  wife_id INTEGER NOT NULL," +
        "  marriage_date TIMESTAMP," +
        "  location VARCHAR(255)," +
        "  PRIMARY KEY (husband_id, wife_id)," +
        "  FOREIGN KEY (husband_id) REFERENCES persons(id)," +
        "  FOREIGN KEY (wife_id) REFERENCES persons(id)" +
        ")"
      );
    }
  }

  /**
   * Saves a marriage to the database.
   *
   * @param marriage the marriage to save
   * @throws SQLException if a database error occurs
   */
  @Override
  public void save(Marriage marriage) throws SQLException {
    String sql = "INSERT INTO marriages (husband_id, wife_id, marriage_date, location) " +
                 "VALUES (?, ?, ?, ?)";

    try (PreparedStatement statement = connection.prepareStatement(sql)) {
      statement.setInt(1, marriage.getHusband().getId());
      statement.setInt(2, marriage.getWife().getId());

      if (marriage.getDate() == null) {
        statement.setNull(3, Types.TIMESTAMP);
      } else {
        statement.setTimestamp(3, new Timestamp(marriage.getDate().getTime()));
      }

      statement.setString(4, marriage.getLocation());

      statement.executeUpdate();
    }
  }

  /**
   * Finds all marriages for a specific person ID.
   *
   * @param personId the person ID
   * @return a list of marriages involving the person
   * @throws SQLException if a database error occurs
   */
  @Override
  public List<Marriage> findByPersonId(int personId) throws SQLException {
    List<Marriage> marriages = new ArrayList<>();
    String sql = "SELECT husband_id, wife_id, marriage_date, location " +
                 "FROM marriages WHERE husband_id = ? OR wife_id = ?";

    try (PreparedStatement statement = connection.prepareStatement(sql)) {
      statement.setInt(1, personId);
      statement.setInt(2, personId);

      try (ResultSet resultSet = statement.executeQuery()) {
        while (resultSet.next()) {
          marriages.add(extractMarriageFromResultSet(resultSet));
        }
      }
    }

    return marriages;
  }

  /**
   * Finds all marriages in the database.
   *
   * @return a list of all marriages
   * @throws SQLException if a database error occurs
   */
  @Override
  public List<Marriage> findAll() throws SQLException {
    List<Marriage> marriages = new ArrayList<>();
    String sql = "SELECT husband_id, wife_id, marriage_date, location FROM marriages";

    try (Statement statement = connection.createStatement();
         ResultSet resultSet = statement.executeQuery(sql)) {
      while (resultSet.next()) {
        marriages.add(extractMarriageFromResultSet(resultSet));
      }
    }

    return marriages;
  }

  /**
   * Deletes a marriage from the database.
   *
   * @param husbandId the husband's ID
   * @param wifeId the wife's ID
   * @throws SQLException if a database error occurs
   */
  @Override
  public void delete(int husbandId, int wifeId) throws SQLException {
    String sql = "DELETE FROM marriages WHERE husband_id = ? AND wife_id = ?";

    try (PreparedStatement statement = connection.prepareStatement(sql)) {
      statement.setInt(1, husbandId);
      statement.setInt(2, wifeId);
      int rowsAffected = statement.executeUpdate();

      if (rowsAffected == 0) {
        throw new SQLException("Delete failed, no marriage found for husband ID: " +
                              husbandId + " and wife ID: " + wifeId);
      }
    }
  }

  /**
   * Extracts a Marriage object from the current row of a ResultSet.
   *
   * @param resultSet the result set positioned at a marriage row
   * @return a Marriage object with data from the result set
   * @throws SQLException if a database error occurs
   */
  private Marriage extractMarriageFromResultSet(ResultSet resultSet) throws SQLException {
    int husbandId = resultSet.getInt("husband_id");
    int wifeId = resultSet.getInt("wife_id");

    Person husband = personCache.get(husbandId);
    Person wife = personCache.get(wifeId);

    if (husband == null || wife == null) {
      throw new SQLException("Cannot create marriage: Person not found in cache. " +
                            "Husband ID: " + husbandId + ", Wife ID: " + wifeId);
    }

    Marriage marriage = new Marriage(husband, wife);

    Timestamp marriageDate = resultSet.getTimestamp("marriage_date");
    if (marriageDate != null) {
      marriage.setDate(new java.util.Date(marriageDate.getTime()));
    }

    marriage.setLocation(resultSet.getString("location"));

    return marriage;
  }
}

