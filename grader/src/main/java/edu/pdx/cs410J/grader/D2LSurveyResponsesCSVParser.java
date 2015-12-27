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

  public D2LSurveyResponsesCSVParser(Reader reader) throws IOException {
    CSVReader csv = new CSVReader( reader );
    String[] firstLine = csv.readNext();
    extractColumnNamesFromFirstLineOfCsv(firstLine);


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
    return null;
  }
}
