package edu.pdx.cs410J.grader;

import org.junit.Test;

import java.io.*;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.hasItems;
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

  @Test
  public void canParseTwoResponseLines() throws IOException {
    String question = "What is your response?";
    String response1 = "This is my response";
    String response2 = "This is my other response";
    Reader reader = createReaderWithLines("Q Text,Answer", question + "," + response1, question + "," + response2);

    D2LSurveyResponsesCSVParser parser = new D2LSurveyResponsesCSVParser(reader);
    SurveyResponsesFromD2L responses = parser.getSurveyResponses();
    assertThat(responses.getResponsesTo(question), hasItems(response1, response2));
  }

  @Test
  public void canParseMultipleQuestion() throws IOException {
    String question1 = "This is my first question?";
    String response1 = "This is my response";
    String question2 = "This is my second question?";
    String response2 = "This is my other response";
    Reader reader = createReaderWithLines("Q Text,Answer", question1 + "," + response1, question2 + "," + response2);

    D2LSurveyResponsesCSVParser parser = new D2LSurveyResponsesCSVParser(reader);
    SurveyResponsesFromD2L responses = parser.getSurveyResponses();
    assertThat(responses.getResponsesTo(question1), hasItems(response1));
    assertThat(responses.getResponsesTo(question2), hasItems(response2));
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
