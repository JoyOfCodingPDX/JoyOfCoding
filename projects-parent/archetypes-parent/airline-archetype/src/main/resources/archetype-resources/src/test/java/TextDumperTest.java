#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package};

import edu.pdx.cs410J.ParserException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;

public class TextDumperTest {

  @Test
  void ${artifactId}NameIsDumpedInTextFormat() {
    String ${artifactId}Name = "Test Airline";
    Airline ${artifactId} = new Airline(${artifactId}Name);

    StringWriter sw = new StringWriter();
    TextDumper dumper = new TextDumper(sw);
    dumper.dump(${artifactId});

    String text = sw.toString();
    assertThat(text, containsString(${artifactId}Name));
  }

  @Test
  void canParseTextWrittenByTextDumper(@TempDir File tempDir) throws IOException, ParserException {
    String ${artifactId}Name = "Test Airline";
    Airline ${artifactId} = new Airline(${artifactId}Name);

    File textFile = new File(tempDir, "${artifactId}.txt");
    TextDumper dumper = new TextDumper(new FileWriter(textFile));
    dumper.dump(${artifactId});

    TextParser parser = new TextParser(new FileReader(textFile));
    Airline read = parser.parse();
    assertThat(read.getName(), equalTo(${artifactId}Name));
  }
}
