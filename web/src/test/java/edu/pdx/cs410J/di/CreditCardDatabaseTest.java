package edu.pdx.cs410J.di;

import org.junit.Test;

import javax.xml.bind.JAXBException;
import java.io.File;
import java.io.IOException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;

public class CreditCardDatabaseTest {

  final File testDirectory = new File(System.getProperty("java.io.tmpdir"));

  @Test
  public void setBalanceWritesToFile() throws IOException, JAXBException {
    String fileName = "setBalanceWriteToFile" + System.currentTimeMillis() + ".xml";
    double balance = 100.00;

    CreditCardDatabase db = new CreditCardDatabase(testDirectory, fileName);
    CreditCard card = new CreditCard("12345");
    db.setBalance(card, balance);

    db = new CreditCardDatabase(testDirectory, fileName);
    assertThat(db.getBalances().get(card), equalTo(balance));
    db.getDatabaseFile().deleteOnExit();
  }

}
