package edu.pdx.cs410J.grader;

import au.com.bytecode.opencsv.CSVReader;
import com.google.common.annotations.VisibleForTesting;

import java.io.IOException;
import java.io.Reader;

public class D2LSurveyResponsesCSVParser {

  @VisibleForTesting
  int questionColumnIndex = -1;

  @VisibleForTesting
  int responseColumnIndex = -1;

  private final SurveyResponsesFromD2L responses;

  public D2LSurveyResponsesCSVParser(Reader reader) throws IOException {
    CSVReader csv = new CSVReader( reader );
    String[] firstLine = csv.readNext();
    extractColumnNamesFromFirstLineOfCsv(firstLine);

    responses = new SurveyResponsesFromD2L();

    String[] surveyLine;
    while ((surveyLine = csv.readNext()) != null) {
      addQuestionAndResponseFromLineOfCsv(surveyLine);
    }
  }

  private void addQuestionAndResponseFromLineOfCsv(String[] surveyLine) {
    String question = getQuestionFromSurveyLine(surveyLine);
    String response = getResponseFromSurveyLine(surveyLine);

    this.responses.noteQuestionAndResponse(question, response);
  }

  private String getResponseFromSurveyLine(String[] surveyLine) {
    return surveyLine[responseColumnIndex];
  }

  private String getQuestionFromSurveyLine(String[] surveyLine) {
    return surveyLine[questionColumnIndex];
  }

  private void extractColumnNamesFromFirstLineOfCsv(String[] firstLine) {
    for (int columnIndex = 0; columnIndex < firstLine.length; columnIndex++) {
      String columnHeading = firstLine[columnIndex];
      if ("Q Text".equals(columnHeading)) {
        this.questionColumnIndex = columnIndex;

      } else if ("Answer".equals(columnHeading)) {
        this.responseColumnIndex = columnIndex;
      }
    }
  }

  public SurveyResponsesFromD2L getSurveyResponses() {
    return this.responses;
  }
}
