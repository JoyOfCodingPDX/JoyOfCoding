package edu.pdx.cs410J.grader;

import org.junit.Test;

import java.io.*;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

public class D2LSurveyResponsesCSVParserTest {

  @Test
  public void canParseFirstLineOfCSV() throws IOException {
    Reader reader = createReaderWithLines("Section #,Q #,Q Type,Q Title,Q Text,Bonus?,Difficulty,Answer,Answer Match,# Responses");
    D2LSurveyResponsesCSVParser parser = new D2LSurveyResponsesCSVParser(reader);

    assertThat(parser.questionColumnIndex, equalTo(4));
    assertThat(parser.responseColumnIndex, equalTo(7));

  }

  private Reader createReaderWithLines(String... lines) {
    StringWriter sw = new StringWriter();
    PrintWriter pw = new PrintWriter(sw);
    for (String line : lines) {
      pw.println(line);
    }

    return new StringReader(sw.toString());
  }
}
