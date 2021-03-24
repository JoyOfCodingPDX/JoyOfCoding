package edu.pdx.cs410J.apptbookweb;

import org.junit.jupiter.api.Test;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

class MessagesTest {

  @Test
  void malformedWordAndDefinitionReturnsNull() {
    assertThat(Messages.parseDictionaryEntry("blah"), nullValue());
  }

  @Test
  void canParseFormattedDictionaryEntryPair() {
    String word = "testWord";
    String definition = "testDefinition";
    String formatted = Messages.formatDictionaryEntry(word, definition);
    Map.Entry<String, String> parsed = Messages.parseDictionaryEntry(formatted);
    assertThat(parsed, notNullValue());
    assertThat(parsed.getKey(), equalTo(word));
    assertThat(parsed.getValue(), equalTo(definition));
  }

  @Test
  void canParseFormattedDictionaryEntryWithoutLeadingSpaces() {
    String word = "testWord";
    String definition = "testDefinition";
    String formatted = Messages.formatDictionaryEntry(word, definition);
    String trimmed = formatted.trim();
    Map.Entry<String, String> parsed = Messages.parseDictionaryEntry(trimmed);
    assertThat(parsed, notNullValue());
    assertThat(parsed.getKey(), equalTo(word));
    assertThat(parsed.getValue(), equalTo(definition));

  }

  @Test
  void nullDefinitionIsParsedAsNull() {
    String word = "testWord";
    String formatted = Messages.formatDictionaryEntry(word, null);
    Map.Entry<String, String> parsed = Messages.parseDictionaryEntry(formatted);
    assertThat(parsed, notNullValue());
    assertThat(parsed.getKey(), equalTo(word));
    assertThat(parsed.getValue(), equalTo(null));
  }

  @Test
  void canParseFormattedDictionary() {
    Map<String, String> dictionary = new HashMap<>();

    for (int i = 0; i < 5; i++) {
      String word = String.valueOf(i);
      String definition = "QQ" + word;
      dictionary.put(word, definition);
    }

    StringWriter sw = new StringWriter();
    Messages.formatDictionaryEntries(new PrintWriter(sw, true), dictionary);

    String formatted = sw.toString();

    Map<String, String> actual = Messages.parseDictionary(formatted);
    assertThat(actual, equalTo(dictionary));
  }
}
