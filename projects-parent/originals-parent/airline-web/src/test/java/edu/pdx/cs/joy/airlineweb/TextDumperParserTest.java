package edu.pdx.cs.joy.airlineweb;

import edu.pdx.cs.joy.ParserException;
import org.junit.jupiter.api.Test;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.Collections;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class TextDumperParserTest {

  @Test
  void emptyMapCanBeDumpedAndParsed() throws ParserException {
    Dictionary<String, String> dictionary = new Hashtable<>();
    Map<String, String> read = dumpAndParse(dictionary);
    assertThat(read, equalTo(dictionary));
  }

  private Map<String, String> dumpAndParse(Dictionary<String, String> map) throws ParserException {
    StringWriter sw = new StringWriter();
    TextDumper dumper = new TextDumper(sw);
    dumper.dump(map);

    String text = sw.toString();

    TextParser parser = new TextParser(new StringReader(text));
    return parser.parse();
  }

  @Test
  void dumpedTextCanBeParsed() throws ParserException {
    Dictionary<String, String> dictionary = new Hashtable<>();
    dictionary.put("one", "1");
    dictionary.put("two", "2");
    Map<String, String> read = dumpAndParse(dictionary);
    assertThat(read, equalTo(dictionary));
  }
}
