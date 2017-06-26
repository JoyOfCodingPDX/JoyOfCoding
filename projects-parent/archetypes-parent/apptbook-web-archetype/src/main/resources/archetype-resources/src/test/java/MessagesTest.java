#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package};

import org.junit.Test;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
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

  @Test
  public void canParseFormattedKeyValueMap() {
    Map<String, String> map = new HashMap<>();

    for (int i = 0; i < 5; i++) {
      String key = String.valueOf(i);
      String value = "QQ" + key;
      map.put(key, value);
    }

    StringWriter sw = new StringWriter();
    Messages.formatKeyValueMap(new PrintWriter(sw, true), map);

    String formatted = sw.toString();

    Map<String, String> actual = Messages.parseKeyValueMap(formatted);
    assertThat(actual, equalTo(map));
  }
}
