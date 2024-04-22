package edu.pdx.cs.joy.apptbook;

import edu.pdx.cs.joy.ParserException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;

public class TextDumperTest {

  @Test
  void appointmentBookOwnerIsDumpedInTextFormat() {
    String owner = "Test Appointment Book";
    AppointmentBook book = new AppointmentBook(owner);

    StringWriter sw = new StringWriter();
    TextDumper dumper = new TextDumper(sw);
    dumper.dump(book);

    String text = sw.toString();
    assertThat(text, containsString(owner));
  }

  @Test
  void canParseTextWrittenByTextDumper(@TempDir File tempDir) throws IOException, ParserException {
    String owner = "Test Appointment Book";
    AppointmentBook book = new AppointmentBook(owner);

    File textFile = new File(tempDir, "apptbook.txt");
    TextDumper dumper = new TextDumper(new FileWriter(textFile));
    dumper.dump(book);

    TextParser parser = new TextParser(new FileReader(textFile));
    AppointmentBook read = parser.parse();
    assertThat(read.getOwnerName(), equalTo(owner));
  }
}
