package edu.pdx.cs.joy.grader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GitConfigParser {
  static final Pattern PROPERTY_PATTERN = Pattern.compile(" {8}(.*) = (.*)");
  static final Pattern REMOTE_PATTERN = Pattern.compile("\\[remote \"(.*)\"]");
  private final Reader reader;
  private String currentSection;

  public GitConfigParser(Reader reader) {
    this.reader = reader;
  }

  public void parse(Callback callback) throws IOException {
    try (BufferedReader br = new BufferedReader(this.reader)) {
      for (String line = br.readLine(); line != null; line = br.readLine()) {
        parseLine(line, callback);
      }

      endCurrentSection(callback);
    }

  }

  private void endCurrentSection(Callback callback) {
    callback.endSection(currentSection);
  }

  private void parseLine(String line, Callback callback) {
    if (line.startsWith("[core]")) {
      endCurrentSection(callback);
      startCoreSection(callback);

    } else if (line.startsWith("[remote ")) {
      endCurrentSection(callback);
      startRemoteSection(line, callback);

    } else if (line.contains(" = ")) {
      parseProperty(line, callback);
    }

  }

  private void startCoreSection(Callback callback) {
    this.currentSection = "core";
    callback.startCoreSection();
  }

  private void startRemoteSection(String line, Callback callback) {
    Matcher matcher = REMOTE_PATTERN.matcher(line);
    if (!matcher.matches()) {
      throw new IllegalStateException("Cannot parse line with remote: \"" + line + "\"");
    }

    String remoteName = matcher.group(1);
    this.currentSection = remoteName;
    callback.startRemoteSection(remoteName);
  }

  private void parseProperty(String line, Callback callback) {
    Matcher matcher = PROPERTY_PATTERN.matcher(line);
    if (!matcher.matches()) {
      throw new IllegalStateException("Cannot parse line with property: \"" + line + "\"");
    }

    String propertyName = matcher.group(1);
    String propertyValue = matcher.group(2);

    callback.property(propertyName, propertyValue);
  }

  public interface Callback {
    void startCoreSection();

    void property(String name, String value);

    void endSection(String name);

    void startRemoteSection(String name);
  }
}
