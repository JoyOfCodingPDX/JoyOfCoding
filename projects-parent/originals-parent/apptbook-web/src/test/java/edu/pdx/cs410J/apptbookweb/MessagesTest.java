package edu.pdx.cs410J.apptbookweb;

import org.junit.Test;

import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.nullValue;

public class MessagesTest {

  @Test
  public void malformedKeyAndPairReturnsNull() {
    assertThat(Messages.parseKeyValuePair("blah"), nullValue());
  }

  @Test
  public void canParseFormattedKeyAndValuePair() {
    String key = "testKey";
    String value = "testValue";
    String formatted = Messages.formatKeyValuePair(key, value);
    Map.Entry<String, String> parsed = Messages.parseKeyValuePair(formatted);
    assertThat(parsed.getKey(), equalTo(key));
    assertThat(parsed.getValue(), equalTo(value));
  }

  @Test
  public void canParseFormattedKeyValueWithoutLeadingSpaces() {
    String key = "testKey";
    String value = "testValue";
    String formatted = Messages.formatKeyValuePair(key, value);
    String trimmed = formatted.trim();
    Map.Entry<String, String> parsed = Messages.parseKeyValuePair(trimmed);
    assertThat(parsed.getKey(), equalTo(key));
    assertThat(parsed.getValue(), equalTo(value));

  }

  @Test
  public void nullValueIsParsedAsNull() {
    String key = "testKey";
    String value = null;
    String formatted = Messages.formatKeyValuePair(key, value);
    Map.Entry<String, String> parsed = Messages.parseKeyValuePair(formatted);
    assertThat(parsed.getKey(), equalTo(key));
    assertThat(parsed.getValue(), equalTo(value));
  }
}
