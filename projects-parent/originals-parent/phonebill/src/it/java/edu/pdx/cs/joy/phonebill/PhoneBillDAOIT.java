package edu.pdx.cs.joy.phonebill;

import edu.pdx.cs.joy.InvokeMainTestCase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Integration tests for {@link PhoneBillDAO#main(String[])}.
 */
class PhoneBillDAOIT extends InvokeMainTestCase {

  private MainMethodResult invokeMain(String... args) {
    return invokeMain(PhoneBillDAO.class, args);
  }

  @Test
  void testNoCommandLineArguments() {
    MainMethodResult result = invokeMain();

    assertThat(result.getTextWrittenToStandardError(),
      containsString("Usage: java PhoneBillDAO <db-file> <customer-name>"));
  }

  @Test
  void testCreatesAndRetrievesPhoneBill(@TempDir Path tempDirectory) {
    String databaseFile = tempDirectory.resolve("phonebill").toString();
    String customerName = "Test Customer";

    MainMethodResult result = invokeMain(databaseFile, customerName);

    assertThat(result.getTextWrittenToStandardOut(),
      containsString("Retrieved PhoneBill for customer: " + customerName));
  }
}
