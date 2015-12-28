package edu.pdx.cs410J.grader;

import org.junit.Test;

import java.io.StringWriter;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;

public class SurveyResponsesFromD2LGeneratorTest {

  @Test
  public void generateHtmlFromSurveyResponses() {
    String questionA = "This is question A";
    String responseA1 = "This is response 1 to question A";
    String responseA2 = "This is response 2 to question A";

    String questionB = "This is question B";
    String responseB1 = "This is response 1 to question B";
    String responseB2 = "This is response 2 to question B";

    SurveyResponsesFromD2L responses = new SurveyResponsesFromD2L();
    responses.noteQuestionAndResponse(questionA, responseA1);
    responses.noteQuestionAndResponse(questionA, responseA2);
    responses.noteQuestionAndResponse(questionB, responseB1);
    responses.noteQuestionAndResponse(questionB, responseB2);

    StringWriter writer = new StringWriter();
    HtmlGenerator generator = new HtmlGenerator(writer);
    SurveyResponsesFromD2LGenerator.generateHtmlForSurveyResponses(responses, generator);

    String html = writer.toString();
    assertThat(html, containsString("      <li>" + questionA + "</li>\n"));
    assertThat(html, containsString("        <li>" + responseA1+ "</li>\n"));
    assertThat(html, containsString("        <li>" + responseA2+ "</li>\n"));
    assertThat(html, containsString("      <li>" + questionB + "</li>\n"));
    assertThat(html, containsString("        <li>" + responseB1+ "</li>\n"));
    assertThat(html, containsString("        <li>" + responseB2+ "</li>\n"));
  }
}
