package edu.pdx.cs.joy.phonebillweb;

import edu.pdx.cs.joy.ParserException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextParser {
  private final Reader reader;

  public TextParser(Reader reader) {
    this.reader = reader;
  }

  public Map<String, String> parse() throws ParserException {
    Pattern pattern = Pattern.compile("(.*) : (.*)");

    Map<String, String> map = new HashMap<>();

    try (
      BufferedReader br = new BufferedReader(this.reader)
    ) {

      for (String line = br.readLine(); line != null; line = br.readLine()) {
        Matcher matcher = pattern.matcher(line);
        if (!matcher.find()) {
          throw new ParserException("Unexpected text: " + line);
        }

        String word = matcher.group(1);
        String definition = matcher.group(2);

        map.put(word, definition);
      }

    } catch (IOException e) {
      throw new ParserException("While parsing dictionary", e);
    }

    return map;
  }
}
