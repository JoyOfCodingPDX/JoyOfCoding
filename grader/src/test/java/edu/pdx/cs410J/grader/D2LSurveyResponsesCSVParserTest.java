package edu.pdx.cs410J.grader;

import org.junit.Test;

import java.io.*;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.MatcherAssert.assertThat;

public class D2LSurveyResponsesCSVParserTest {

  @Test
  public void canParseFirstLineOfCSV() throws IOException {
    Reader reader = createReaderWithLines("Section #,Q #,Q Type,Q Title,Q Text,Bonus?,Difficulty,Answer,Answer Match,# Responses");
    D2LSurveyResponsesCSVParser parser = new D2LSurveyResponsesCSVParser(reader);

    assertThat(parser.questionColumnIndex, equalTo(4));
    assertThat(parser.responseColumnIndex, equalTo(7));

  }

  @Test
  public void canParseOneResponseLine() throws IOException {
    String question = "What is your response?";
    String response = "This is my response";
    Reader reader = createReaderWithLines("Q Text,Answer", question + "," + response);

    D2LSurveyResponsesCSVParser parser = new D2LSurveyResponsesCSVParser(reader);
    SurveyResponsesFromD2L responses = parser.getSurveyResponses();
    assertThat(responses.getResponsesTo(question), hasItem(response));

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
