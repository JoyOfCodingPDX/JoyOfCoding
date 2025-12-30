package edu.pdx.cs.joy.jdbc;

import edu.pdx.cs.joy.family.FamilyTree;
import edu.pdx.cs.joy.family.Marriage;
import edu.pdx.cs.joy.family.Person;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Data Access Object implementation for managing FamilyTree entities in the database.
 * Coordinates persistence of Person and Marriage objects.
 */
public class FamilyTreeDAOImpl implements FamilyTreeDAO {

  private final Connection connection;
  private final PersonDAO personDAO;

  /**
   * Creates a new FamilyTreeDAOImpl with the specified database connection.
   *
   * @param connection the database connection to use
   */
  public FamilyTreeDAOImpl(Connection connection) {
    this.connection = connection;
    this.personDAO = new PersonDAOImpl(connection);
  }

  /**
   * Drops all family tree related tables from the database if they exist.
   * Note: Must drop marriages first due to foreign key constraints.
   *
   * @param connection the database connection to use
   * @throws SQLException if a database error occurs
   */
  public static void dropTables(Connection connection) throws SQLException {
    MarriageDAO.dropTable(connection);
    PersonDAO.dropTable(connection);
  }

  /**
   * Creates all family tree related tables in the database.
   * Note: Must create persons first due to foreign key constraints.
   *
   * @param connection the database connection to use
   * @throws SQLException if a database error occurs
   */
  public static void createTables(Connection connection) throws SQLException {
    PersonDAO.createTable(connection);
    MarriageDAO.createTable(connection);
  }

  /**
   * Saves a complete family tree to the database.
   * This includes all persons and marriages in the tree.
   *
   * @param familyTree the family tree to save
   * @throws SQLException if a database error occurs
   */
  @Override
  public void save(FamilyTree familyTree) throws SQLException {
    // First, save all persons
    for (Person person : familyTree.getPeople()) {
      personDAO.save(person);
    }

    // Build a person cache for marriage persistence
    Map<Integer, Person> personCache = new HashMap<>();
    for (Person person : familyTree.getPeople()) {
      personCache.put(person.getId(), person);
    }

    // Then, save all marriages
    MarriageDAO marriageDAO = new MarriageDAOImpl(connection, personCache);
    for (Person person : familyTree.getPeople()) {
      for (Marriage marriage : person.getMarriages()) {
        // Only save each marriage once (from husband's perspective to avoid duplicates)
        if (person.equals(marriage.getHusband())) {
          marriageDAO.save(marriage);
        }
      }
    }
  }

  /**
   * Loads a complete family tree from the database.
   * This includes all persons and marriages, with relationships properly resolved.
   *
   * @return the family tree loaded from the database
   * @throws SQLException if a database error occurs
   */
  @Override
  public FamilyTree load() throws SQLException {
    FamilyTree familyTree = new FamilyTree();

    // First pass: Load all persons and build cache
    Map<Integer, Person> personCache = new HashMap<>();
    Map<Integer, ParentIds> parentIdsMap = new HashMap<>();

    String sql = "SELECT id, gender, first_name, middle_name, last_name, " +
                 "father_id, mother_id, date_of_birth, date_of_death FROM persons ORDER BY id";

    try (java.sql.Statement statement = connection.createStatement();
         java.sql.ResultSet resultSet = statement.executeQuery(sql)) {

      while (resultSet.next()) {
        int id = resultSet.getInt("id");
        String genderStr = resultSet.getString("gender");
        Person.Gender gender = Person.Gender.valueOf(genderStr);

        Person person = new Person(id, gender);
        person.setFirstName(resultSet.getString("first_name"));
        person.setMiddleName(resultSet.getString("middle_name"));
        person.setLastName(resultSet.getString("last_name"));

        // Store parent IDs for later resolution
        int fatherId = resultSet.getInt("father_id");
        boolean fatherIsNull = resultSet.wasNull();
        int motherId = resultSet.getInt("mother_id");
        boolean motherIsNull = resultSet.wasNull();

        if (!fatherIsNull || !motherIsNull) {
          parentIdsMap.put(id, new ParentIds(
            fatherIsNull ? Person.UNKNOWN : fatherId,
            motherIsNull ? Person.UNKNOWN : motherId
          ));
        }

        java.sql.Timestamp dob = resultSet.getTimestamp("date_of_birth");
        if (dob != null) {
          person.setDateOfBirth(new java.util.Date(dob.getTime()));
        }

        java.sql.Timestamp dod = resultSet.getTimestamp("date_of_death");
        if (dod != null) {
          person.setDateOfDeath(new java.util.Date(dod.getTime()));
        }

        familyTree.addPerson(person);
        personCache.put(id, person);
      }
    }

    // Second pass: Resolve parent relationships
    for (Map.Entry<Integer, ParentIds> entry : parentIdsMap.entrySet()) {
      Person person = personCache.get(entry.getKey());
      ParentIds parentIds = entry.getValue();

      if (parentIds.fatherId != Person.UNKNOWN) {
        Person father = personCache.get(parentIds.fatherId);
        if (father != null) {
          person.setFather(father);
        }
      }

      if (parentIds.motherId != Person.UNKNOWN) {
        Person mother = personCache.get(parentIds.motherId);
        if (mother != null) {
          person.setMother(mother);
        }
      }
    }

    // Third pass: Load all marriages
    MarriageDAO marriageDAO = new MarriageDAOImpl(connection, personCache);
    List<Marriage> marriages = marriageDAO.findAll();

    for (Marriage marriage : marriages) {
      // Add marriage to both spouses
      marriage.getHusband().addMarriage(marriage);
      marriage.getWife().addMarriage(marriage);
    }

    return familyTree;
  }

  /**
   * Helper class to store parent IDs during loading.
   */
  private static class ParentIds {
    final int fatherId;
    final int motherId;

    ParentIds(int fatherId, int motherId) {
      this.fatherId = fatherId;
      this.motherId = motherId;
    }
  }
}

