package edu.pdx.cs.joy.jdbc;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class AcademicTermDAOTest {

  private Connection connection;
  private AcademicTermDAO termDAO;

  @BeforeEach
  public void setUp() throws SQLException {
    // Create an in-memory H2 database
    connection = H2DatabaseHelper.createInMemoryConnection("test");

    // Drop and create the academic_terms table
    AcademicTermDAO.dropTable(connection);
    AcademicTermDAO.createTable(connection);

    // Initialize the DAO with the connection
    termDAO = new AcademicTermDAO(connection);
  }

  @AfterEach
  public void tearDown() throws SQLException {
    if (connection != null && !connection.isClosed()) {
      AcademicTermDAO.dropTable(connection);
      connection.close();
    }
  }

  @Test
  public void testPersistAndFetchAcademicTerm() throws SQLException {
    // Create an academic term
    String termName = "Fall 2024";
    LocalDate startDate = LocalDate.of(2024, 9, 1);
    LocalDate endDate = LocalDate.of(2024, 12, 15);
    AcademicTerm term = new AcademicTerm(termName, startDate, endDate);

    // Persist the term
    termDAO.save(term);

    // Verify that an ID was auto-generated
    int generatedId = term.getId();
    assertThat(generatedId, is(greaterThan(0)));

    // Fetch the term by ID
    AcademicTerm fetchedTerm = termDAO.findById(generatedId);

    // Validate the fetched term using Hamcrest assertions
    assertThat(fetchedTerm, is(notNullValue()));
    assertThat(fetchedTerm.getId(), is(equalTo(generatedId)));
    assertThat(fetchedTerm.getName(), is(equalTo(termName)));
    assertThat(fetchedTerm.getStartDate(), is(equalTo(startDate)));
    assertThat(fetchedTerm.getEndDate(), is(equalTo(endDate)));
  }

  @Test
  public void testFindByName() throws SQLException {
    // Create and persist an academic term
    String termName = "Spring 2025";
    LocalDate startDate = LocalDate.of(2025, 1, 15);
    LocalDate endDate = LocalDate.of(2025, 5, 30);
    AcademicTerm term = new AcademicTerm(termName, startDate, endDate);
    termDAO.save(term);

    // Fetch the term by name
    AcademicTerm fetchedTerm = termDAO.findByName(termName);

    // Validate the fetched term
    assertThat(fetchedTerm, is(notNullValue()));
    assertThat(fetchedTerm.getName(), is(equalTo(termName)));
    assertThat(fetchedTerm.getStartDate(), is(equalTo(startDate)));
    assertThat(fetchedTerm.getEndDate(), is(equalTo(endDate)));
  }

  @Test
  public void testFindNonExistentTerm() throws SQLException {
    // Try to fetch a term that doesn't exist
    AcademicTerm fetchedTerm = termDAO.findById(999);
    assertThat(fetchedTerm, is(nullValue()));

    AcademicTerm fetchedByName = termDAO.findByName("Nonexistent Term");
    assertThat(fetchedByName, is(nullValue()));
  }

  @Test
  public void testFindAllTerms() throws SQLException {
    // Create and persist multiple academic terms
    AcademicTerm fall2024 = new AcademicTerm("Fall 2024",
        LocalDate.of(2024, 9, 1), LocalDate.of(2024, 12, 15));
    AcademicTerm spring2025 = new AcademicTerm("Spring 2025",
        LocalDate.of(2025, 1, 15), LocalDate.of(2025, 5, 30));
    AcademicTerm summer2025 = new AcademicTerm("Summer 2025",
        LocalDate.of(2025, 6, 15), LocalDate.of(2025, 8, 30));

    termDAO.save(fall2024);
    termDAO.save(spring2025);
    termDAO.save(summer2025);

    // Fetch all terms
    List<AcademicTerm> allTerms = termDAO.findAll();

    // Validate using Hamcrest matchers
    assertThat(allTerms, hasSize(3));
    assertThat(allTerms, hasItem(hasProperty("name", is("Fall 2024"))));
    assertThat(allTerms, hasItem(hasProperty("name", is("Spring 2025"))));
    assertThat(allTerms, hasItem(hasProperty("name", is("Summer 2025"))));

    // Verify terms are ordered by start date
    assertThat(allTerms.get(0).getName(), is(equalTo("Fall 2024")));
    assertThat(allTerms.get(1).getName(), is(equalTo("Spring 2025")));
    assertThat(allTerms.get(2).getName(), is(equalTo("Summer 2025")));
  }

  @Test
  public void testUpdateAcademicTerm() throws SQLException {
    // Create and persist an academic term
    AcademicTerm term = new AcademicTerm("Fall 2024",
        LocalDate.of(2024, 9, 1), LocalDate.of(2024, 12, 15));
    termDAO.save(term);
    int termId = term.getId();

    // Update the term
    term.setName("Fall 2024 (Extended)");
    term.setEndDate(LocalDate.of(2024, 12, 20));
    termDAO.update(term);

    // Fetch the updated term
    AcademicTerm updatedTerm = termDAO.findById(termId);

    // Validate the update
    assertThat(updatedTerm, is(notNullValue()));
    assertThat(updatedTerm.getId(), is(equalTo(termId)));
    assertThat(updatedTerm.getName(), is(equalTo("Fall 2024 (Extended)")));
    assertThat(updatedTerm.getEndDate(), is(equalTo(LocalDate.of(2024, 12, 20))));
  }

  @Test
  public void testDeleteAcademicTerm() throws SQLException {
    // Create and persist an academic term
    AcademicTerm term = new AcademicTerm("Fall 2024",
        LocalDate.of(2024, 9, 1), LocalDate.of(2024, 12, 15));
    termDAO.save(term);
    int termId = term.getId();

    // Verify the term exists
    assertThat(termDAO.findById(termId), is(notNullValue()));

    // Delete the term
    termDAO.delete(termId);

    // Verify the term no longer exists
    assertThat(termDAO.findById(termId), is(nullValue()));
  }

  @Test
  public void testDatesArePersisted() throws SQLException {
    // Create terms with different dates
    AcademicTerm term1 = new AcademicTerm("Term 1",
        LocalDate.of(2024, 1, 1), LocalDate.of(2024, 3, 31));
    AcademicTerm term2 = new AcademicTerm("Term 2",
        LocalDate.of(2024, 6, 1), LocalDate.of(2024, 8, 31));

    termDAO.save(term1);
    termDAO.save(term2);

    // Fetch and verify dates
    AcademicTerm fetched1 = termDAO.findByName("Term 1");
    AcademicTerm fetched2 = termDAO.findByName("Term 2");

    assertThat(fetched1.getStartDate(), is(equalTo(LocalDate.of(2024, 1, 1))));
    assertThat(fetched1.getEndDate(), is(equalTo(LocalDate.of(2024, 3, 31))));
    assertThat(fetched2.getStartDate(), is(equalTo(LocalDate.of(2024, 6, 1))));
    assertThat(fetched2.getEndDate(), is(equalTo(LocalDate.of(2024, 8, 31))));
  }
}

